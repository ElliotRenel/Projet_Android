package com.example.effectsapp.histogramEqualizer;

import android.graphics.Bitmap;
import android.graphics.Color;

public class HistogramEqualizer {

    Bitmap bmp;

    public HistogramEqualizer(Bitmap bmp){
        this.bmp = bmp;
    }

    public void equalizerGray(){
        int pixels[] = new int[bmp.getHeight()*bmp.getWidth()];
        int grey;
        long ngrey;
        int[] hist = new int[256];
        long[] histc = new long[256];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i=0; i<bmp.getHeight()*bmp.getWidth(); i++){
            grey = Color.red(pixels[i]);
            hist[grey]++;
        }
        long sum = 0;
        for (int i=0; i<256; i++) {
            sum += hist[i];
            histc[i] = sum;
        }
        for(int i=0; i<bmp.getHeight()*bmp.getWidth(); i++){
            grey = Color.red(pixels[i]);
            ngrey = (histc[grey]*255)/(long)(bmp.getWidth()*bmp.getHeight());
            pixels[i] = Color.rgb((int)ngrey, (int)ngrey, (int)ngrey);
        }
        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        return;
    }

    public void equalizerColor(){
        int pixels[] = new int[bmp.getHeight()*bmp.getWidth()];
        int[] rgb = new int[3];
        long[] nrgb = new long[3];
        int[][] hist = new int[256][3];
        long[][] histc = new long[256][3];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i=0; i<bmp.getHeight()*bmp.getWidth(); i++){
            rgb[0] = Color.red(pixels[i]);
            rgb[1] = Color.green(pixels[i]);
            rgb[2] = Color.blue(pixels[i]);
            for(int j=0; j<3; j++){
                hist[rgb[j]][j]++;
            }
        }

        long[] sum = new long[3];
        for (int i=0; i<256; i++) {
            for(int j=0; j<3; j++){
                sum[j] += hist[i][j];
                histc[i][j] = sum[j];
            }
        }

        for(int i=0; i<bmp.getHeight()*bmp.getWidth(); i++){
            rgb[0] = Color.red(pixels[i]);
            rgb[1] = Color.green(pixels[i]);
            rgb[2] = Color.blue(pixels[i]);
            for(int j=0; j<3; j++){
                nrgb[j] = (histc[rgb[j]][j]*255)/(long)(bmp.getWidth()*bmp.getHeight());
            }
            pixels[i] = Color.rgb((int)nrgb[0], (int)nrgb[1], (int)nrgb[2]);
        }
        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        return;
    }
}
