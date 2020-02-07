package com.example.editionimage.DefaultPackage.imagehandling;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Random;

public class ColorEffect {


    /**
     * Put an image into grey scale on given image
     * @param bmp the bitmap image
     */
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


    /**
     * Put all color S value to 0 except those in a given interval of color on given image
     * @param bmp the bitmap image
     */
    public void colorize(BitmapPlus bmp) {

        Random r = new Random();
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        float rand = (float) r.nextInt(360);

        float[][] tabs = bmp.getHSVPixels(h, w);

        for (int i = 0; i < w * h; i++) {
            tabs[0][i] = rand;
        }
        bmp.setHSVPixels(tabs);
    }

    /**
     * Put all color S value to 0 except those in a given interval of color on given image
     *
     * @param bmp   the bitmap image
     * @param color the color defining the interval's center (H value from 0 to 360)
     * @param range the range defining the interval's width
     */
    public static void keepColor(BitmapPlus bmp, int color, int range) {
        color = (color % 360);
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int size = w * h;

        float[][] tabs = bmp.getHSVPixels(h, w);

        float[] hsv = new float[3];
        int cmin = (color - range + 360) % 360, cmax = (color + range) % 360;
        boolean discontinue = cmax < cmin;

        for (int i = 0; i < size; i++) {
            boolean inRange = discontinue ? hsv[0] < cmax || hsv[0] > cmin : hsv[0] < cmax && hsv[0] > cmin;
            if (!inRange) {
                tabs[0][1] = 0;
            }
        }
        bmp.setHSVPixels(tabs);
    }
}

