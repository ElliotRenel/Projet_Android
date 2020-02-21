package com.example.effectsapp.convolutions;

import android.graphics.Bitmap;

import com.example.effectsapp.kernel.styles.Gauss;

public class ConvolutionGauss extends Convolution {
    public ConvolutionGauss(Bitmap bmp){
        super(bmp, new Gauss());
    }
}
