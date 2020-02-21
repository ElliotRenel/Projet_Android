package com.example.effectsapp.convolutions;

import android.graphics.Bitmap;

import com.example.effectsapp.kernel.styles.Moyenneur;

public class ConvolutionMoyenneur extends Convolution {

    public ConvolutionMoyenneur(Bitmap bmp, int size){
        super(bmp, new Moyenneur(size));
    }
}
