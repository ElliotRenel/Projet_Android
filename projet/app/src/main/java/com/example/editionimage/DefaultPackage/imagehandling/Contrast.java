package com.example.editionimage.DefaultPackage.imagehandling;

import android.graphics.Bitmap;
import android.util.Log;

public class Contrast {



    /*à  ajouter les augmentations de contrastes et diminution de contrastes*/


    /**
     * Apply a linear transformation of Histogram on the given image (for all type of images)
     * @param bmp the given image
     */
    public static void contrastLinearColor(BitmapPlus bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int size = w*h;

        float[][] tabs = bmp.getHSVPixels(h, w);

        int[] hist = new int[101];
        bmp.ToHistHSV(bmp,hist);
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

    /**
     * Apply an equalisation of Histogram on the given image (for all type of images)
     * @param bmp the given image
     */
    public static void contrastEqualColor(BitmapPlus bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int size = w*h;

        float[][] tabs = bmp.getHSVPixels(h, w);

        /* faire une seule fonction pour ces lignes*/

        int[] hist = new int[101];
        bmp.ToHistHSV(bmp,hist);

        int[] C = new int[101];
        //import
        bmp.histToCumul(hist,C);

        /* ----------------- */

        for(int i =0; i<size; i++){
            //voir si c est possible de réduire le nombre de conversion int/float
            tabs[2][i] = (((float)C[(int)(tabs[2][i]*100)])*100)/(float)(size*100);
        }
        bmp.setHSVPixels(tabs);
    }
}
