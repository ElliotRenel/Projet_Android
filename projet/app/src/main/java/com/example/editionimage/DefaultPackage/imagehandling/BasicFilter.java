package com.example.editionimage.DefaultPackage.imagehandling;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;

import com.example.editionimage.DefaultPackage.imagehandling.tools.Kernel;
import com.example.editionimage.ScriptC_gray;
import com.example.editionimage.ScriptC_histEq;

public class BasicFilter {
    BitmapHandler bmp;

    public BasicFilter(BitmapHandler bit){
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

    public void toGrayRS (Context context) {

        // 1) Creer un contexte RenderScript
        RenderScript rs = RenderScript.create(context) ;
        // 2) Creer des Allocations pour passer les donnees
        Allocation input = Allocation. createFromBitmap (rs, bmp.getBit_current() ) ;
        Allocation output = Allocation . createTyped ( rs , input.getType () ) ;

        // 3) Creer le script
        ScriptC_gray grayScript = new ScriptC_gray(rs);
        // 4) Copier les donnees dans les Allocations
        // ...

        // 5) Initialiser les variables globales potentielles
        // ...

        // 6) Lancer le noyau
        grayScript . forEach_toGray(input,output);

        // 7) Recuperer les donnees des Allocation (s)
        output . copyTo ( bmp.getBit_current()  ) ;

        // 8) Detruire le context , les Allocation (s) et le script
        input . destroy () ; output . destroy () ;
        grayScript . destroy () ; rs . destroy () ;
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


    /** Contrast and Lighlevel **/

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

    private int truncate(float x){
        if(x>255) return 255;
        if(x<0) return 0;
        return Math.round(x);
    }

    public void modifContrast(int contrast){
        int size = bmp.getSize();
        int pixels[] = new int[size];
        bmp.getPixels(pixels);

        float factor = (259* ((float)contrast + 255)) / (255 * (259 - (float)contrast));

        for(int i=0; i<size; i++){
            int r = Color.red(pixels[i]), g = Color.green(pixels[i]), b = Color.blue(pixels[i]), alpha = Color.alpha(pixels[i]);

            r = truncate(factor * (float)(r - 128) +128);
            g = truncate(factor * (float)(g - 128) +128);
            b = truncate(factor * (float)(b - 128) +128);


            pixels[i] = Color.argb(alpha,r,g,b);
        }

        bmp.setPixels(pixels);
    }

    public void modifLight(int lightlevel){
        int size = bmp.getSize();
        int pixels[] = new int[size];
        bmp.getPixels(pixels);

        for(int i=0; i<size; i++){
            int r = Color.red(pixels[i]), g = Color.green(pixels[i]), b = Color.blue(pixels[i]), alpha = Color.alpha(pixels[i]);

            r = truncate(r + lightlevel);
            g = truncate(g + lightlevel);
            b = truncate(b + lightlevel);


            pixels[i] = Color.argb(alpha,r,g,b);
        }

        bmp.setPixels(pixels);
    }

    /** Convolution **/

    public void convolution(Kernel mask){
        double[][] hsv_pixels = bmp.getHSVPixels();

        int m_h = mask.getH()/2;
        int m_w = mask.getW()/2;

        applyMask(mask, hsv_pixels, m_w, bmp.getWidth()-m_w,m_h,bmp.getHeight()-m_h);

        bmp.setHSVPixels(hsv_pixels);

    }

    public void convolutionEdgeDetection(Kernel mA, Kernel mB){
        if(mA.getInverse()!=0 || mB.getInverse()!=0 || mA.getH()!=mB.getH() || mA.getW()!=mB.getW())
            return;
        double[][] hsv_pixels = bmp.getHSVPixels();

        int m_h = mA.getH()/2;
        int m_w = mA.getW()/2;

        applyMaskEdgeDetection(mA, mB, hsv_pixels, m_w, bmp.getWidth()-m_w,m_h,bmp.getHeight()-m_h);

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

    private void applyMaskEdgeDetection(Kernel mA, Kernel mB, double[][] pixels, int x_min, int x_max, int y_min, int y_max){
        int w = bmp.getWidth();
        double[] pixels_copy = (pixels[2]).clone();

        for(int x=x_min; x<x_max; x++){
            for(int y=y_min; y<y_max;y++){
                pixels[2][x + y * w] = applyMaskEdgeDetectionAux(mA, mB, pixels_copy, x, y);
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

    private double applyMaskEdgeDetectionAux(Kernel mA, Kernel mB, double[] pixels, int x, int y){
        int w = bmp.getWidth();
        int mA_w = mA.getW()/2, mA_h = mA.getH()/2;
        int mB_w = mA.getW()/2, mB_h = mA.getH()/2;

        double sommeA = 0, sommeB = 0;

        for(int i=x-mA_w; i<x+mA_w+1; i++) {
            for (int j = y - mA_h; j < y + mA_h + 1; j++) {
                sommeA += pixels[j * w + i] * (double) mA.getValue(i - (x - mA_w), j - (y - mA_h));
                sommeB += pixels[j * w + i] * (double) mB.getValue(i - (x - mB_w), j - (y - mB_h));
            }
        }
        return Math.hypot(sommeA,sommeB);
    }
}
