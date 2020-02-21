package com.example.effectsapp.colors;

import android.graphics.Bitmap;
import android.graphics.Color;

import static com.example.effectsapp.Colors.HSVtoColor;
import static com.example.effectsapp.Colors.RGBtoHSV;

public class KeepColor {

    Bitmap bmp ;

    public KeepColor(Bitmap bmp){
        this.bmp = bmp;
    }

    public void keepColorRandom(Bitmap bmp, int size_keeping){
        int color;
        float hue;
        double t = Math.random()*360;
        float[] hsvc = new float[3];
        int pixels[] = new int[bmp.getHeight()*bmp.getWidth()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i=0; i<bmp.getHeight()*bmp.getWidth(); i++){
            RGBtoHSV(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]), hsvc);
            hue = hsvc[0];
            boolean to_verify = hue <= t+size_keeping && hue >= t-size_keeping;
            if(t<size_keeping)
                to_verify = ((hue <= t+size_keeping && hue>= 0 ) || hue >= 360-(size_keeping-t));
            if(t>360-size_keeping)
                to_verify = ((hue >= t-size_keeping && hue <=360) || hue <= t-(360-t));
            if(to_verify){
                color = HSVtoColor(hsvc);
                pixels[i] = color;
            }else{
                int grey = (int) (Color.red(pixels[i]) * 0.3) + (int) (Color.green(pixels[i]) * 0.59) + (int) (Color.blue(pixels[i]) * 0.11);
                pixels[i] = Color.rgb(grey, grey, grey);
            }

        }
        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        return;
    }            //WORKS

    private void keepColor(Bitmap bmp, int size_to_keep_color, int color_to_keep){
        int color;
        float hue;
        float[] hsv_old = new float[3];
        RGBtoHSV(Color.red(color_to_keep),Color.green(color_to_keep),Color.blue(color_to_keep), hsv_old);
        double t = Math.random()*360;
        float[] hsv_new = new float[3];
        int pixels[] = new int[bmp.getHeight()*bmp.getWidth()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i=0; i<bmp.getHeight()*bmp.getWidth(); i++){
            RGBtoHSV(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]), hsv_new);
            hue = hsv_new[0];
            if(hue <= t+size_to_keep_color && hue >= t-size_to_keep_color){         //Case around 0 not managed
                color = HSVtoColor(hsv_new);
                pixels[i] = color;
            }else{
                int grey = (int) (Color.red(pixels[i]) * 0.3) + (int) (Color.green(pixels[i]) * 0.59) + (int) (Color.blue(pixels[i]) * 0.11);
                pixels[i] = Color.rgb(grey, grey, grey);
            }

        }
        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        return;
    }
}
