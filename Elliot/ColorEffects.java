package com.example.myapplication.effects;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.myapplication.effects.aux.InnerMethods;

public class ColorEffects {

    /**
     * Put an image into grey scale on given image
     * @param bmp the bitmap image
     */
    public static void toGray(Bitmap bmp){
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
     * @param color the color defining the interval's center (H value from 0 to 360)
     * @param range the range defining the interval's width
     */
    public static void keepColor(Bitmap bmp, int color, int range){
        color = (color%360);
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int size = w*h;
        int[] pixels = new int[size];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        float[] hsv= new float[3];
        int cmin = (color - range + 360)%360, cmax = (color+range)%360;
        boolean discontinue = cmax<cmin;
        for(int i=0; i<size; i++){
            InnerMethods.rgb_to_hsv(pixels[i],hsv);

            boolean inRange = discontinue?hsv[0]<cmax || hsv[0]>cmin:hsv[0]<cmax && hsv[0]>cmin;
            if(!inRange) {
                hsv[1] = 0;
            }
            pixels[i] = InnerMethods.hsv_to_rgb(hsv);

        }
        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    /**
     * Map all H color value of a given image to determined interval
     * @param bmp the bitmap image
     * @param color the color defining the interval's center (H value from 0 to 360)
     * @param range the range defining the interval's width
     */
    public static void colorize(Bitmap bmp, int color, int range){
        color = color%360;
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int size = w*h;
        int[] pixels = new int[size];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        float[] hsv = new float[3];
        int min = (color-range+360)%360, max = (color+range)%360;

        for(int i=0; i<size; i++){
            InnerMethods.rgb_to_hsv(pixels[i], hsv);
            hsv[0] = InnerMethods.mapColor(hsv[0],min,max);
            pixels[i] = InnerMethods.hsv_to_rgb(hsv);
        }

        bmp.setPixels(pixels,0,w,0,0,w,h);
    }
}
