package com.example.editionimage.DefaultPackage.imagehandling.effectclass;

import android.content.Context;

import java.util.function.Function;

public class Effect{
    Function<Void, Void> modifier;

    public Effect(Function<Void,Void> modifier){
        this.modifier = modifier;
    }

    public void applyModifier(){
        Void aVoid = null;
        modifier.apply(aVoid);
    }
}
