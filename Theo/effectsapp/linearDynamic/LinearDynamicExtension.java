package com.example.effectsapp.linearDynamic;

import android.graphics.Bitmap;
import android.graphics.Color;

import static com.example.effectsapp.Colors.HSVtoColor;
import static com.example.effectsapp.Colors.RGBtoHSV;

public class LinearDynamicExtension {

    Bitmap bmp;

    public LinearDynamicExtension(Bitmap bmp){
        this.bmp = bmp;
    }

    public void extendDyn(){
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
        int dynamic = max - min;
        if(dynamic == 255){
            return;
        }
        int[] LUT = new int[256];
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
    }                                   //Seems to work

    public void extendDynColor(){                                   //Passer en hsv puis histo sur h puis traitement puis reconversion en rgb pour affichage
        int pixels[] = new int[bmp.getHeight()*bmp.getWidth()];
        int[] min = new int[]{255, 255, 255};
        int[] max = new int[3];
        int[] rgb = new int[3];
        int[] nrgb = new int[3];

        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i=0; i<bmp.getHeight()*bmp.getWidth(); i++) {
            rgb[0] = Color.red(pixels[i]);
            rgb[1] = Color.green(pixels[i]);
            rgb[2] = Color.blue(pixels[i]);
            for (int j = 0; j < 3; j++) {
                if (rgb[j] > max[j]) {
                    max[j] = rgb[j];
                }
                if (rgb[j] < min[j]) {
                    min[j] = rgb[j];
                }
            }
        }
        int[] dynamic = new int[]{max[0] - min[0], max[1] - min[1], max[2] - min[2]};
        if(dynamic[0] == 255 && dynamic[1] == 255 && dynamic[2] == 255){
            return;
        }
        int[][] LUT = new int[256][3];
        for(int j=0; j<3; j++) {
            for (int ng = 0; ng < 256; ng++) {
                LUT[ng][j] = (255 * (ng - min[j]))/dynamic[j];
            }
        }
        for(int i=0; i<bmp.getHeight()*bmp.getWidth(); i++){
            rgb[0] = Color.red(pixels[i]);
            rgb[1] = Color.green(pixels[i]);
            rgb[2] = Color.blue(pixels[i]);
            for(int j=0; j<3; j++) {
                nrgb[j] = LUT[rgb[j]][j];
            }
            pixels[i] = Color.rgb(nrgb[0], nrgb[1], nrgb[2]);
        }
        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        return;
    }                              //Seems to work

    private void extendDynColorV2() {                                   //Passer en hsv puis histo sur h puis traitement puis reconversion en rgb pour affichage
        int pixels[] = new int[bmp.getHeight() * bmp.getWidth()];
        int min = 255;
        int max = 0;
        float hsv[] = new float[3];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for (int i = 0; i < bmp.getHeight() * bmp.getWidth(); i++) {
            RGBtoHSV(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]), hsv);
            int h = (int) hsv[0];
            if (h > max)
                max = h;
            if (h < min)
                min = h;
        }
        int dynamic = max - min;
        if (dynamic == 255) {
            return;
        }
        int[] LUT = new int[360];
        for (int ng = 0; ng < 360; ng++) {
            LUT[ng] = (360 * (ng - min)) / dynamic;
        }
        for (int i = 0; i < bmp.getHeight() * bmp.getWidth(); i++) {
            RGBtoHSV(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]), hsv);
            //Reverse?
            hsv[0] = LUT[(int)hsv[0]];
            pixels[i] = HSVtoColor(hsv);
        }
        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        return;
    }                           //TRYING TO REPLACE BY ONLY ONE CHANNEL H FROM HSV


}
