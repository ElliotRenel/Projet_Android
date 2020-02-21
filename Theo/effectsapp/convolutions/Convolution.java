package com.example.effectsapp.convolutions;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.effectsapp.kernel.Kernel;
import static com.example.effectsapp.Colors.HSVtoColor;
import static com.example.effectsapp.Colors.RGBtoHSV;

public class Convolution{

    Bitmap bmp;
    Kernel kernel;

    public Convolution(Bitmap bmp, Kernel kernel){
        this.bmp = bmp;
        this.kernel = kernel;
    }

    public Convolution(Bitmap bmp){
        this.bmp = bmp;
    }

    public void convolution(Bitmap bmp){
        int pixels[] = new int[bmp.getHeight()*bmp.getWidth()];
        int new_pixels[] = new int[bmp.getHeight()*bmp.getWidth()];

        int start_end = (kernel.getSize()-1)/2;

        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int j= start_end; j<bmp.getHeight()-start_end; j++){
            for(int i= start_end; i<bmp.getWidth()-start_end; i++){
                int result = calculate_pixel(i-start_end, j-start_end,kernel, pixels, bmp.getWidth());
                new_pixels[bmp.getWidth()*j + i] = result;
            }
        }
        bmp.setPixels(new_pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        return;
    }

    private int calculate_pixel(int i, int j, Kernel kernel, int pixels[], int width){
        int sum_kernel = kernel.getSumKernel();
        float sum_v = 0;
        int to_act;
        float base_hsv[] = new float[3];
        for(int y=0; y<kernel.getSize(); y++){
            for(int x=0; x<kernel.getSize(); x++){

                to_act = (i+x) + ((j+y)*width);
                float hsv[] = new float[3];

                RGBtoHSV(Color.red(pixels[to_act]), Color.blue(pixels[to_act]), Color.green(pixels[to_act]), hsv);

                if(x==1 && y==1) {
                    RGBtoHSV(Color.red(pixels[to_act]), Color.blue(pixels[to_act]), Color.green(pixels[to_act]), base_hsv);
                }

                float v = hsv[2]*kernel.getMatrice()[x][y];
                sum_v = sum_v + v;
            }
        }

        float new_v = sum_v/sum_kernel;
        base_hsv[2] = new_v;
        base_hsv[0] = (360-base_hsv[0])%360;
        int newPixel = HSVtoColor(base_hsv);

        return newPixel;
    }
}
