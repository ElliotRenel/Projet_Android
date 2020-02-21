package com.example.effectsapp.kernel.styles;

import com.example.effectsapp.kernel.Kernel;

public class Gauss extends Kernel {

    public Gauss(){
        super(5, new int[]{1, 4, 6, 4, 1,
                             4, 16 ,24, 16 ,4,
                             6, 24, 36, 24, 6,
                             4, 16, 24, 16, 4,
                             1, 4, 6, 4, 1});
    }
}
