package com.example.editionimage.DefaultPackage.Seb;

import android.graphics.Bitmap;

import java.util.Random;

import static android.graphics.Color.HSVToColor;
import static android.graphics.Color.RGBToHSV;
import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;

public class Tp2 {
    public static void colorize (Bitmap bmp)
    {
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        float[] hsv = new float[3];

        Random r = new Random();
        int h = r.nextInt(360 );

        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for (int i = 0; i < pixels.length;i++)
        {
            int pixel = pixels[i];
            int red = red(pixel);
            int green = green(pixel);
            int blue = blue(pixel);
            RGBToHSV(red ,green,blue,hsv);
            //TO DO hue change
            hsv[0]=h;
            //*****************
            pixels[i] = HSVToColor(hsv);
        }
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    public static void keepRed (Bitmap bmp)
    {
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        float[] hsv = new float[3];


        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for (int i = 0; i < pixels.length;i++)
        {
            int pixel = pixels[i];
            int red = red(pixel);
            int green = green(pixel);
            int blue = blue(pixel);
            RGBToHSV(red ,green,blue,hsv);
            //TO DO hue change
            if ( !((hsv[0] < 40 )| (hsv[0] > 320 ) ) ){
                hsv[1] = 0;
            }
            pixels[i] = HSVToColor(hsv);
        }
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }
}
