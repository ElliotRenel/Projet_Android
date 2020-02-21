package com.example.effectsapp.colors;

import android.graphics.Bitmap;
import android.graphics.Color;

import static com.example.effectsapp.Colors.HSVtoColor;
import static com.example.effectsapp.Colors.RGBtoHSV;

public class Colorize {

    Bitmap bmp ;

    public Colorize(Bitmap bmp){
        this.bmp = bmp;
    }

    public void colorize(){
        int color;
        double t = Math.random()*360;
        float hsvc[] = new float[3];
        int pixels[] = new int[bmp.getHeight()*bmp.getWidth()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i=0; i<bmp.getHeight()*bmp.getWidth(); i++){
            RGBtoHSV(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]), hsvc);
            hsvc[0] = (float)t;
            color = HSVtoColor(hsvc);
            pixels[i] = color;
        }
        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        return;
    }

}
