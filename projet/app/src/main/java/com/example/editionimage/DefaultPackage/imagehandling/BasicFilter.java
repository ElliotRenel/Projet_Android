package com.example.editionimage.DefaultPackage.imagehandling;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;

import com.example.editionimage.DefaultPackage.imagehandling.tools.Kernel;
import com.example.editionimage.ScriptC_gray;
import com.example.editionimage.ScriptC_histEq;
import com.example.editionimage.ScriptC_invert;

/**
 * BasicFilter regroups every transformation applicable to an image on the application.
 */
class BasicFilter {
    private BitmapHandler bmp;

    BasicFilter(BitmapHandler bit){
        bmp = bit;
    }

    /** Color **/

    /**
     * toGray transforms a colored bitmap to a grey channel.
     * @param saving
     */
    void toGray(boolean saving){
        //vérifier si passage en hsv serait pas plus rapide (temps de conversion vs temps de cast int/float)
        int size = bmp.getSize(saving);
        int[] pixels = new int[size];
        bmp.getPixels(pixels,saving);

        for(int i=0;i<size;i++){
            int p = pixels[i];
            int val = (int)(Color.red(p)*0.3)+(int)(Color.green(p)*0.59)+(int)(Color.blue(p)*0.11);
            pixels[i] = Color.rgb(val,val,val);
        }
        bmp.setPixels(pixels,saving);
    }

    /**
     * toGrayRS uses renderscript to do the same thing as toGray
     * @param context
     * @param saving
     */
    void toGrayRS (Context context, boolean saving) {

        // 1) Creer un contexte RenderScript
        RenderScript rs = RenderScript.create(context) ;
        // 2) Creer des Allocations pour passer les donnees
        Allocation input;
        if(saving)
            input = Allocation. createFromBitmap (rs, bmp.getBit_final() ) ;
        else
            input = Allocation. createFromBitmap (rs, bmp.getBit_current() ) ;
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
        if(saving)
            output . copyTo ( bmp.getBit_final()  ) ;
        else
            output . copyTo ( bmp.getBit_current()  ) ;

        // 8) Detruire le context , les Allocation (s) et le script
        input . destroy () ; output . destroy () ;
        grayScript . destroy () ; rs . destroy () ;
    }


    /**
     * TODO
     * @param context
     * @param saving
     */
    void invertRS (Context context, boolean saving) {

        // 1) Creer un contexte RenderScript
        RenderScript rs = RenderScript.create(context) ;
        // 2) Creer des Allocations pour passer les donnees
        Allocation input;
        if(saving)
            input = Allocation. createFromBitmap (rs, bmp.getBit_final() ) ;
        else
            input = Allocation. createFromBitmap (rs, bmp.getBit_current() ) ;
        Allocation output = Allocation . createTyped ( rs , input.getType () ) ;

        // 3) Creer le script
        ScriptC_invert invertScript = new ScriptC_invert(rs);
        // 4) Copier les donnees dans les Allocations
        // ...

        // 5) Initialiser les variables globales potentielles
        // ...

        // 6) Lancer le noyau
        invertScript . forEach_invert(input,output);

        // 7) Recuperer les donnees des Allocation (s)
        if(saving)
            output . copyTo ( bmp.getBit_final()  ) ;
        else
            output . copyTo ( bmp.getBit_current()  ) ;

        // 8) Detruire le context , les Allocation (s) et le script
        input . destroy () ; output . destroy () ;
        invertScript . destroy () ; rs . destroy () ;
    }

    /**
     * colorize changes the hue of every pixel and is replaced by color.
     * @param color the new hue to apply.
     * @param saving
     */
    void colorize(int color, boolean saving) {
        int size = bmp.getSize(saving);
        double[][] tabs = bmp.getHSVPixels(saving);

        for (int i = 0; i < size; i++) {
            tabs[0][i] = color;
        }
        bmp.setHSVPixels(tabs,saving);
    }

    /**
     * keepColor, transforms every pixel not in the range of the hue color into a grey channel.
     * @param color the base hue to keep
     * @param range the distance accepted to keep elements
     * @param saving
     *
     * For the hue to be preserved, a pixel needs to have a hue at a distance of range maximum from color modulo 360.
     * Example: color = 30, range = 40. 359 and 45 are kept intact, but 250 and 71 are turned into grey.
     */
    void keepColor(int color, int range, boolean saving) {
        color = (color % 360);
        int size = bmp.getSize(saving);
        double[][] tabs = bmp.getHSVPixels(saving);
        int cmin = (color - range + 360) % 360, cmax = (color + range) % 360;
        boolean discontinue = cmax < cmin;

        for (int i = 0; i < size; i++) {
            boolean inRange = discontinue ? tabs[0][i] < cmax || tabs[0][i] > cmin : tabs[0][i] < cmax && tabs[0][i] > cmin;
            if (!inRange) {
                tabs[1][i] = 0;
            }
        }
        bmp.setHSVPixels(tabs,saving);
    }

    /**
     * shift modifies every pixel's hues by shift modulo 360.
     * @param shift the hue to add for modifications
     * @param saving
     *
     * Formula gives newhue = oldhue + shift
     *
     */
    void shift(int shift, boolean saving){
        int size = bmp.getSize(saving);
        double[][] tabs = bmp.getHSVPixels(saving);

        for (int i = 0; i < size; i++) {
            tabs[0][i] = (tabs[0][i] + shift)%360 ;
        }
        bmp.setHSVPixels(tabs,saving);
    }


    /** Contrast and Lightlevel */

    /**
     * TODO
     *
     * @param saving
     */
    void contrastLinear(boolean saving){
        int size = bmp.getSize(saving);
        double[] tabs = bmp.getVPixels(saving);
        int[] hist = bmp.getHSVHist(tabs,saving);
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
            tabs[i] = ((100.0/(float)(max-min))*(tabs[i]*100)-min)/100;
        }
        bmp.setVPixels(tabs,saving);
    }


    /**
     * TODO
     * @param saving
     */
    void contrastEqual(boolean saving){
        int size = bmp.getSize(saving);
        double[] tabs = bmp.getVPixels(saving);

        int[] C = bmp.getHSVCumul(tabs,saving);

        for(int i =0; i<size; i++){
            //voir si c est possible de réduire le nombre de conversion int/float
            tabs[i] = (((float)C[(int)(tabs[i]*100)])*100)/(float)(size*100);
        }
        bmp.setVPixels(tabs,saving);
    }

    /**
     * contrastEqualRS does the same principle as contrastEqual, with a renderscript version.
     * @param context
     * @param saving
     */
    void contrastEqualRS(Context context, boolean saving) {
        //Create new bitmap;
        Bitmap res = saving?bmp.getBit_final():bmp.getBit_current();

        //Get image size
        int width = res.getWidth();
        int height = res.getHeight();

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

        if(saving)
            bmp.setBit_final(res);
        else
            bmp.setBit_current(res);
    }

    /**
     * truncate function allows to make an int that we will be able to use for modifications.
     * @param x the float to make to int
     * @return the int from x
     */
    private int truncate(float x){
        if(x>255) return 255;
        if(x<0) return 0;
        return Math.round(x);
    }

    /**
     * modifContrast allows to modify the contrast of the image.
     * @param contrast the variation to apply
     * @param saving
     *
     */
    void modifContrast(int contrast,boolean saving){
        int size = bmp.getSize(saving);
        int[] pixels = new int[size];
        bmp.getPixels(pixels,saving);

        float factor = (259* ((float)contrast + 255)) / (255 * (259 - (float)contrast));

        for(int i=0; i<size; i++){
            int r = Color.red(pixels[i]), g = Color.green(pixels[i]), b = Color.blue(pixels[i]), alpha = Color.alpha(pixels[i]);

            r = truncate(factor * (float)(r - 128) +128);
            g = truncate(factor * (float)(g - 128) +128);
            b = truncate(factor * (float)(b - 128) +128);


            pixels[i] = Color.argb(alpha,r,g,b);
        }

        bmp.setPixels(pixels,saving);
    }

    /**
     * modifLight function allows to modify the lightness of an image by lightLevel.
     * @param lightlevel the level of light to add to each pixel.
     * @param saving
     */
    void modifLight(int lightlevel,boolean saving){
        int size = bmp.getSize(saving);
        int [] pixels = new int[size];
        bmp.getPixels(pixels,saving);

        for(int i=0; i<size; i++){
            int r = Color.red(pixels[i]), g = Color.green(pixels[i]), b = Color.blue(pixels[i]), alpha = Color.alpha(pixels[i]);

            r = truncate(r + lightlevel);
            g = truncate(g + lightlevel);
            b = truncate(b + lightlevel);


            pixels[i] = Color.argb(alpha,r,g,b);
        }

        bmp.setPixels(pixels,saving);
    }

    /** Convolution **/

    /**
     * convolution modifies an image with the mask.
     * @param mask the kernel given, used to modify the image.
     * @param saving
     *
     * The convolution method uses a mask, that we will use on every pixel to modify this one. Every pixel will be modified differently with it's neighbours.
     */
    void convolution(Kernel mask,boolean saving){
        double[] hsv_pixels = bmp.getVPixels(saving);

        int m_h = mask.getH()/2;
        int m_w = mask.getW()/2;

        applyMask(mask, hsv_pixels, m_w, bmp.getWidth(saving)-m_w,m_h,bmp.getHeight(saving)-m_h,saving);

        bmp.setVPixels(hsv_pixels,saving);

    }

    /**
     * TODO
     * @param mA
     * @param mB
     * @param saving
     */
    void convolutionEdgeDetection(Kernel mA, Kernel mB, boolean saving){
        if(mA.getInverse()!=0 || mB.getInverse()!=0 || mA.getH()!=mB.getH() || mA.getW()!=mB.getW())
            return;
        double[] hsv_pixels = bmp.getVPixels(saving);

        int m_h = mA.getH()/2;
        int m_w = mA.getW()/2;

        applyMaskEdgeDetection(mA, mB, hsv_pixels, m_w, bmp.getWidth(saving)-m_w,m_h,bmp.getHeight(saving)-m_h,saving);

        bmp.setVPixels(hsv_pixels,saving);
    }

    /**
     * TODO
     * @param row
     * @param column
     * @param saving
     * @return
     */
    int separableConvolution(Kernel row, Kernel column,boolean saving) {
        if (row.getH() > 1 || column.getW() > 1) return -1;

        int w = bmp.getWidth(saving);
        int h = bmp.getHeight(saving);
        double[] hsv_pixels = bmp.getVPixels(saving);

        int dim = row.getW() / 2;

        applyMask(row,hsv_pixels,dim,w-dim,0,h,saving);

        dim = column.getH() / 2;

        applyMask(column,hsv_pixels,0,w,dim,h-dim,saving);

        bmp.setVPixels(hsv_pixels,saving);

        return 1;
    }

    /**
     *
     * applyMask deals with the use of the mask in the convolution method.
     *
     * @param mask used to modify the pixel
     * @param pixels an array of pixels
     * @param x_min
     * @param x_max
     * @param y_min
     * @param y_max
     * @param saving
     *
     * applyMask will modify every pixel by picking the neighbours of each pixel and apply the value of the mask for each neighbour.
     * TODO?
     *
     */
    private void applyMask(Kernel mask, double[] pixels, int x_min, int x_max, int y_min, int y_max,boolean saving){
        int w = bmp.getWidth(saving);
        double[] pixels_copy = (pixels).clone();

        for(int x=x_min; x<x_max; x++){
            for(int y=y_min; y<y_max;y++){
                pixels[x + y * w] = applyMaskAux(mask, pixels_copy, x, y,saving);
            }
        }
    }

    /**
     * TODO
     * @param mA
     * @param mB
     * @param pixels
     * @param x_min
     * @param x_max
     * @param y_min
     * @param y_max
     * @param saving
     */
    private void applyMaskEdgeDetection(Kernel mA, Kernel mB, double[] pixels, int x_min, int x_max, int y_min, int y_max,boolean saving){
        int w = bmp.getWidth(saving);
        double[] pixels_copy = (pixels).clone();

        for(int x=x_min; x<x_max; x++){
            for(int y=y_min; y<y_max;y++){
                pixels[x + y * w] = applyMaskEdgeDetectionAux(mA, mB, pixels_copy, x, y,saving);
            }
        }
    }

    /**
     * TODO
     * @param mask
     * @param pixels
     * @param x
     * @param y
     * @param saving
     * @return
     */
    private double applyMaskAux(Kernel mask, double[] pixels, int x, int y,boolean saving){
        int w = bmp.getWidth(saving);
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

    /**
     * TODO
     * @param mA
     * @param mB
     * @param pixels
     * @param x
     * @param y
     * @param saving
     * @return
     */
    private double applyMaskEdgeDetectionAux(Kernel mA, Kernel mB, double[] pixels, int x, int y,boolean saving){
        int w = bmp.getWidth(saving);
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
