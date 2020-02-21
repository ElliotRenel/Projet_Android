package com.example.td1;

import android.graphics.Bitmap;

import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;
import static android.graphics.Color.rgb;

public class Tp1 {

    public static void toGray(Bitmap bmp)
    {
        for (int i = 0; i < bmp.getWidth();i++)
        {
            for (int j = 0; j < bmp.getHeight();j++)
            {
                int pixel = bmp.getPixel(i,j);
                int red = red(pixel);
                int green = green(pixel);
                int blue = blue(pixel);
                int grey = (int) Math.round(0.3*red + 0.59*green + 0.11*blue);
                bmp.setPixel(i,j,rgb(grey,grey,grey));
            }
        }
    }

    public static void toGray2(Bitmap bmp)
    {
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for (int i = 0; i < pixels.length;i++)
        {
            int pixel = pixels[i];
            int red = red(pixel);
            int green = green(pixel);
            int blue = blue(pixel);
            int grey = (int) Math.round(0.3*red + 0.59*green + 0.11*blue);
            pixels[i] = rgb(grey,grey,grey);
        }
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    public static void copy(Bitmap bmp,Bitmap dest)
    {
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for (int i = 0; i < pixels.length;i++)
        {
            int pixel = pixels[i];
            int red = red(pixel);
            int green = green(pixel);
            int blue = blue(pixel);
            pixels[i] = rgb(red,green,blue);
        }
        dest.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }
}
