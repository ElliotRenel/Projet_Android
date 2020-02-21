package com.example.effectsapp.linearDynamic;

import android.graphics.Bitmap;
import android.graphics.Color;

public class LinearDynamicContraction {

    Bitmap bmp;

    LinearDynamicContraction(Bitmap bmp){
        this.bmp = bmp;
    }

    public void contractDyn(Bitmap bmp){
        int pixels[] = new int[bmp.getHeight()*bmp.getWidth()];
        int min = 255;
        int max = 0;
        int grey;
        int ngrey;
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i=0; i<bmp.getHeight()*bmp.getWidth(); i++){
            grey = Color.red(pixels[i]);
            if(grey>max){
                max = grey;
            }
            if(grey<min){
                min = grey;
            }
        }
        int dynamic = max - min ;
        if(dynamic <= 30){
            return;
        }
        int[] LUT = new int[256];
        dynamic = max - min -10;                    //Modify dynamic or max/min to get a better result (replace that -10)

        for(int ng =0; ng < 256; ng++){
            LUT[ng] = (255*(ng-min))/dynamic;
        }
        for(int i=0; i<bmp.getHeight()*bmp.getWidth(); i++){
            grey = Color.red(pixels[i]);
            ngrey = LUT[grey];
            pixels[i] = Color.rgb(ngrey, ngrey, ngrey);
        }
        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        return;
    }                                 //NOT WORKING
}
