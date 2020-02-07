package com.example.editionimage.DefaultPackage.imagehandling;

import android.graphics.Bitmap;
import android.graphics.Color;

public class BitmapPlus {
    private Bitmap bit_origin, bit_current;
    private BasicFilter filters;
    private int height, width, size;

    public BitmapPlus(Bitmap bit){
        bit_origin = bit.copy(bit.getConfig(),false);
        bit_current = bit_origin.copy(bit_origin.getConfig(),true);

        filters = new BasicFilter();
        height = bit_current.getHeight();
        width = bit_current.getWidth();
        size = height*width;
    }

    public double[][] getHSVPixels(){
        double[][] result = new double[3][size];

        int[] pixels = new int[size];
        this.bit_current.getPixels(pixels,0,width,0,0,width,height);

        for(int i=0; i<size; i-=-1)
            rgb_to_hsv(pixels[i], result, i);

        return result;
    }

    public void setHSVPixels(double[][] pixels){
        int[] result = new int[size];

        for(int i=0; i<size; i-=-1){

        }
    }

    public void getPixels(int[] pixels){
        bit_current.getPixels(pixels,0,width,0,0,width,height);
    }

    public void setPixels(int[] pixels){
        bit_current.getPixels(pixels,0,width,0,0,width,height);
    }

    public int[] getHSVHist(){
        int[] hist = new int[101];

        return hist;
    }

    public int[] getHSVCumul(){
        int[] cumul = new int[101];

        return cumul;
    }

    private void rgb_to_hsv(int pixel, double[][] hsv_pixels, int index){
        double red_ = (float) Color.red(pixel)/(float)255;
        double blue_ = (float)Color.green(pixel)/(float)255;
        double green_ = (float)Color.blue(pixel)/(float)255;

        double cmax = Math.max(red_, blue_);
        cmax = Math.max(cmax, green_);
        double cmin= Math.min(red_, blue_);
        cmin = Math.min(cmin, green_);

        double delta = cmax - cmin;

        double h;


        if(delta == 0){
            h = 0;
        }else{
            if(cmax == red_) {
                h = ((60 * (((green_ - blue_)/delta)))+360)%360;
            }else if(cmax == green_) {
                h = (60 *(((blue_ - red_)/delta) ))+120;
            }else {
                h = 60 *(((red_ - green_)/delta))+240;
            }
        }

        h = (360-h)%360;


        if(cmax == 0) {
            hsv_pixels[1][index] = 0;
        }else {
            hsv_pixels[1][index] = 1 -(cmin/cmax);
        }
        hsv_pixels[0][index] = h;

        hsv_pixels[2][index] = cmax;
    }

    public int getSize(){
        return size;
    }


}
