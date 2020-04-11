package com.example.editionimage.DefaultPackage.imagehandling;

import java.util.function.Function;

class Effect{
    private Function<Void, Void> finalModifier;
    private Function<Void, Void> modifier;

    Effect(Function<Void,Void> finalModifier, Function<Void,Void> modifier){
        this.finalModifier = finalModifier;
        this.modifier = modifier;
    }

    void applyFinalModifier(){
        Void aVoid = null;
        finalModifier.apply(aVoid);
    }

    void applyModifier(){
        Void aVoid = null;
        modifier.apply(aVoid);
    }
}
