package com.example.editionimage.DefaultPackage.Elliot;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.example.myapplication.effects.aux.InnerMethods;

public class Contrast {

    /**
     * Apply a linear transformation of Histogram on the given image (only grey scale images)
     * @param bmp the given image
     */
    public static void contrastLinearGrey(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int size = w*h;
        int[] pixels = new int[size];
        bmp.getPixels(pixels,0,w,0,0,w,h);

        int[] hist = new int[256];
        InnerMethods.bitmapToHistGray(bmp,hist);

        int min=0, max=255;

        for(int i=0; i<256; i++) {
            if (hist[i] != 0) {
                min = i;
                break;
            }
        }
        for(int i=min+1; i<256;i++){
            if(hist[i]!=0)
                max=i;
        }

        for(int i=0; i<size; i++){
            long r = (long)255*((long) Color.red(pixels[i])-(long)min)/(long)(max-min);
            long g = (long)255*((long)Color.green(pixels[i])-(long)min)/(long)(max-min);
            long b = (long)255*((long)Color.blue(pixels[i])-(long)min)/(long)(max-min);
            pixels[i] = Color.argb(Color.alpha(pixels[i]),(int)r,(int)g,(int)b);
        }
        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    /**
     * Apply an equalisation of Histogram on the given image (only grey scale images)
     * @param bmp the given image
     */
    public static void contrastEqualGrey(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int size = w*h;
        int[] pixels = new int[size];
        bmp.getPixels(pixels,0,w,0,0,w,h);

        int[] hist = new int[256];
        InnerMethods.bitmapToHistGray(bmp,hist);

        int[] C = new int[256];
        InnerMethods.histToCumul(hist,C);

        for(int i =0; i<size; i++){
            long r = (C[Color.red(pixels[i])]*(long)255)/(long)size;
            long g = (C[Color.green(pixels[i])]*(long)255)/(long)size;
            long b = (C[Color.blue(pixels[i])]*(long)255)/(long)size;
            pixels[i] = Color.argb(Color.alpha(pixels[i]),(int)r,(int)g,(int)b);
        }
        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    /**
     * Apply a linear transformation of Histogram on the given image (for all type of images)
     * @param bmp the given image
     */
    public static void contrastLinearColor(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int size = w*h;
        int[] pixels = new int[size];
        bmp.getPixels(pixels,0,w,0,0,w,h);

        int[] hist = new int[101];
        InnerMethods.bitmapToHistHSV(bmp,hist);
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
            float[] hsv = new float[3];
            InnerMethods.rgb_to_hsv(pixels[i],hsv);
            hsv[2] = ((100/(max-min))*(hsv[2]*100)-min)/100;
            pixels[i] = InnerMethods.hsv_to_rgb(hsv);
        }
        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    /**
     * Apply an equalisation of Histogram on the given image (for all type of images)
     * @param bmp the given image
     */
    public static void contrastEqualColor(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int size = w*h;
        int[] pixels = new int[size];
        bmp.getPixels(pixels,0,w,0,0,w,h);

        int[] hist = new int[101];
        InnerMethods.bitmapToHistHSV(bmp,hist);

        int[] C = new int[101];
        InnerMethods.histToCumul(hist,C);

        for(int i =0; i<size; i++){
            float[] hsv = new float[3];
            InnerMethods.rgb_to_hsv(pixels[i],hsv);
            hsv[2] = (((float)C[(int)(hsv[2]*100)])*100)/(float)(size*100);
            if(hsv[2] >1 || hsv[2] <0){
                Log.i("Cont",hsv[2] +"");
            }
            pixels[i] = InnerMethods.hsv_to_rgb(hsv);
        }
        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

}
