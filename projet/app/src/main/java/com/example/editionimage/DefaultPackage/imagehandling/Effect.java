package com.example.editionimage.DefaultPackage.imagehandling;

import java.util.function.Function;

class Effect{
    private Function<Void, Void> modifier;

    Effect(Function<Void,Void> modifier){
        this.modifier = modifier;
    }

    void applyModifier(){
        Void aVoid = null;
        modifier.apply(aVoid);
    }
}
