package com.example.effectsapp.kernel.styles;

import com.example.effectsapp.kernel.Kernel;


public class Moyenneur extends Kernel {

    public Moyenneur(int size){
        super(size);
        for(int j= 0; j<size; j++ ){
            for(int i=0; i<size; i++){
                matrice[i][j] = 1;
            }
        }
        sumKernel = size*size;
    }


}
