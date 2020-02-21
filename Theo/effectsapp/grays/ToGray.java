package com.example.effectsapp.grays;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;


import com.example.effectsapp.ScriptC_gray2;

public class ToGray extends Activity {

    private Bitmap bmp;

    public ToGray(Bitmap bmp){
        this.bmp = bmp;
    }

    public void toGrays(){
        int p, grey;
        int pixels[] = new int[bmp.getHeight()*bmp.getWidth()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i=0; i<bmp.getHeight()*bmp.getWidth(); i++){
            p = pixels[i];
            grey = (int) (Color.red(p) * 0.3) + (int) (Color.green(p) * 0.59) + (int) (Color.blue(p) * 0.11);
            pixels[i] = Color.rgb(grey, grey, grey);

        }
        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        return;
    }




}
