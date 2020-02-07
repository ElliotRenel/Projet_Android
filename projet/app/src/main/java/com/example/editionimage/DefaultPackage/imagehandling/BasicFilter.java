package com.example.editionimage.DefaultPackage.imagehandling;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Random;

public class BasicFilter {


    public void toGray(Bitmap bmp){
        //vérifier si passage en hsv serait pas plus rapide (temps de conversion vs temps de cast int/float)
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int size = w*h;
        int[] pixels = new int[size];
        bmp.getPixels(pixels,0,w,0,0,w,h);

        for(int i=0;i<size;i++){
            int p = pixels[i];
            int val = (int)(Color.red(p)*0.3)+(int)(Color.green(p)*0.59)+(int)(Color.blue(p)*0.11);
            pixels[i] = Color.rgb(val,val,val);
        }
        bmp.setPixels(pixels,0,w,0,0,w,h);
    }


    public void colorize(BitmapPlus bmp) {
        Random r = new Random();
        int size = bmp.getSize();
        float rand = (float) r.nextInt(360);
        float[][] tabs = bmp.getHSVPixels();

        for (int i = 0; i < size; i++) {
            tabs[0][i] = rand;
        }
        bmp.setHSVPixels(tabs);
    }


    public void keepColor(BitmapPlus bmp, int color, int range) {
        color = (color % 360);
        int size = bmp.getSize();
        float[][] tabs = bmp.getHSVPixels();
        int cmin = (color - range + 360) % 360, cmax = (color + range) % 360;
        boolean discontinue = cmax < cmin;

        for (int i = 0; i < size; i++) {
            boolean inRange = discontinue ? tabs[0][i] < cmax || tabs[0][i] > cmin : tabs[0][i] < cmax && tabs[0][i] > cmin;
            if (!inRange) {
                tabs[0][1] = 0;
            }
        }
        bmp.setHSVPixels(tabs);
    }


    /*à  ajouter les augmentations de contrastes et diminution de contrastes*/

    public static void contrastLinear(BitmapPlus bmp){
        int size = bmp.getSize();
        float[][] tabs = bmp.getHSVPixels();
        int[] hist = bmp.getHSVHist(bmp);
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


    public static void contrastEqual(BitmapPlus bmp){
        int size = bmp.getSize();
        float[][] tabs = bmp.getHSVPixels();

        /* faire une seule fonction pour ces lignes*/

        int[] hist = bmp.getHSVHist(bmp);

        int[] C = new int[101];
        bmp.histToCumul(hist,C);

        /* ----------------- */

        for(int i =0; i<size; i++){
            //voir si c est possible de réduire le nombre de conversion int/float
            tabs[2][i] = (((float)C[(int)(tabs[2][i]*100)])*100)/(float)(size*100);
        }
        bmp.setHSVPixels(tabs);
    }
}
