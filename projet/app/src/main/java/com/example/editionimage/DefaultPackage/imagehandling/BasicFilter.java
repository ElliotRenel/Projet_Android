package com.example.editionimage.DefaultPackage.imagehandling;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.util.Log;

import com.example.editionimage.DefaultPackage.imagehandling.tools.FirstKernel;
import com.example.editionimage.DefaultPackage.imagehandling.tools.Kernel;
import com.example.editionimage.MainActivity;
import com.example.editionimage.ScriptC_histEq;

import java.util.Random;

import static java.lang.Double.NaN;

public class BasicFilter {
    BitmapPlus bmp;

    public BasicFilter(BitmapPlus bit){
        bmp = bit;
    }

    /** Color **/

    public void toGray(){
        //vérifier si passage en hsv serait pas plus rapide (temps de conversion vs temps de cast int/float)
        int size = bmp.getSize();
        int[] pixels = new int[size];
        bmp.getPixels(pixels);

        for(int i=0;i<size;i++){
            int p = pixels[i];
            int val = (int)(Color.red(p)*0.3)+(int)(Color.green(p)*0.59)+(int)(Color.blue(p)*0.11);
            pixels[i] = Color.rgb(val,val,val);
        }
        bmp.setPixels(pixels);
    }


    public void colorize(int color) {
        int size = bmp.getSize();
        double[][] tabs = bmp.getHSVPixels();

        for (int i = 0; i < size; i++) {
            tabs[0][i] = color;
        }
        bmp.setHSVPixels(tabs);
    }


    public void keepColor(int color, int range) {
        color = (color % 360);
        int size = bmp.getSize();
        double[][] tabs = bmp.getHSVPixels();
        int cmin = (color - range + 360) % 360, cmax = (color + range) % 360;
        boolean discontinue = cmax < cmin;

        for (int i = 0; i < size; i++) {
            boolean inRange = discontinue ? tabs[0][i] < cmax || tabs[0][i] > cmin : tabs[0][i] < cmax && tabs[0][i] > cmin;
            if (!inRange) {
                tabs[1][i] = 0;
            }
        }
        bmp.setHSVPixels(tabs);
    }


    /** Contrast **/

    public void contrastLinear(){
        int size = bmp.getSize();
        double[][] tabs = bmp.getHSVPixels();
        int[] hist = bmp.getHSVHist(tabs);
        int min = 0, max = 100;
        boolean b = true;

        for(int i=0; i<101; i++) {
            if (hist[i] != 0 && b) {
                min = i;
                b=false;
            }
        }

        for(int i=min+1; i<101;i++){
            if(hist[i]!=0)
                max=i;
        }

        for(int i=0; i<size; i++){
            tabs[2][i] = ((100/(max-min))*(tabs[2][i]*100)-min)/100;
        }
        bmp.setHSVPixels(tabs);
    }


    public void contrastEqual(){
        int size = bmp.getSize();
        double[][] tabs = bmp.getHSVPixels();

        int[] C = bmp.getHSVCumul(tabs);

        for(int i =0; i<size; i++){
            //voir si c est possible de réduire le nombre de conversion int/float
            tabs[2][i] = (((float)C[(int)(tabs[2][i]*100)])*100)/(float)(size*100);
        }
        bmp.setHSVPixels(tabs);
    }

    public void contrastEqualRS(Context context) {
        //Get image size
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        //Create new bitmap;
        Bitmap res = bmp.getBit_current();
        //Create renderscript
        RenderScript rs = RenderScript.create(context);

        //Create allocation from Bitmap
        Allocation allocationA = Allocation.createFromBitmap(rs, res);

        //Create allocation with same type
        Allocation allocationB = Allocation.createTyped(rs, allocationA.getType());

        //Create script from rs file.
        ScriptC_histEq histEqScript = new ScriptC_histEq(rs);

        //Set size in script
        histEqScript.set_size(width*height);

        //Call the first kernel.
        histEqScript.forEach_root(allocationA, allocationB);

        //Call the rs method to compute the remap array
        histEqScript.invoke_createRemapArray();

        //Call the second kernel
        histEqScript.forEach_remaptoRGB(allocationB, allocationA);

        //Copy script result into bitmap
        allocationA.copyTo(res);

        //Destroy everything to free memory
        allocationA.destroy();
        allocationB.destroy();
        histEqScript.destroy();
        rs.destroy();

        bmp.setBit_current(res);
    }

    public void modifLight(double alpha){
        double[][] hsv_pixels = bmp.getHSVPixels();

        for(int i=0; i<bmp.getSize(); i++){
            double L = hsv_pixels[2][i]*(1.0-hsv_pixels[1][i]/2.0);

            double S_L = (L==0 || L==1)?0:((hsv_pixels[2][i]-L)/Math.min(L,1.0-L));

            L = L + (alpha>0?(1-L):L)*alpha;

            hsv_pixels[2][i] = L + S_L*Math.min(L,1.0-L);
            hsv_pixels[1][i] = (hsv_pixels[2][i]==0)?0:(2*(1-L/hsv_pixels[2][i]));
        }

        bmp.setHSVPixels(hsv_pixels);
    }

    /** Convolution **/

    public void convolution(Kernel mask){
        double[][] hsv_pixels = bmp.getHSVPixels();

        int m_h = mask.getH()/2;
        int m_w = mask.getW()/2;

        applyMask(mask, hsv_pixels, m_w, bmp.getWidth()-m_w,m_h,bmp.getHeight()-m_h);

        bmp.setHSVPixels(hsv_pixels);

    }

    public int separableConvolution(Kernel row, Kernel column) {
        if (row.getH() > 1 || column.getW() > 1) return -1;

        int w = bmp.getWidth();
        int h = bmp.getHeight();
        double[][] hsv_pixels = bmp.getHSVPixels();

        int dim = row.getW() / 2;

        applyMask(row,hsv_pixels,dim,w-dim,0,h);

        dim = column.getH() / 2;

        applyMask(column,hsv_pixels,0,w,dim,h-dim);

        bmp.setHSVPixels(hsv_pixels);

        return 1;
    }

    private void applyMask(Kernel mask, double[][] pixels, int x_min, int x_max, int y_min, int y_max){
        int w = bmp.getWidth();
        double[] pixels_copy = (pixels[2]).clone();

        for(int x=x_min; x<x_max; x++){
            for(int y=y_min; y<y_max;y++){
                pixels[2][x + y * w] = applyMaskAux(mask, pixels_copy, x, y);
            }
        }
    }

    private double applyMaskAux(Kernel mask, double[] pixels, int x, int y){
        int w = bmp.getWidth();
        int mask_w = mask.getW()/2, mask_h = mask.getH()/2;

        double somme = 0;

        for(int i=x-mask_w; i<x+mask_w+1; i++) {
            for (int j = y - mask_h; j < y + mask_h + 1; j++) {
                double tmp = pixels[j * w + i] * (double) mask.getValue(i - (x - mask_w), j - (y - mask_h));
                somme += tmp;
            }
        }
        if(mask.getInverse()==0){
            return Math.sqrt(somme*somme);
        }else {
            return somme / mask.getInverse();
        }
    }
}
