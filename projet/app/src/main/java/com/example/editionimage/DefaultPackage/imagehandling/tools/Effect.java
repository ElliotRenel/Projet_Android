package com.example.editionimage.DefaultPackage.imagehandling.tools;

import java.util.function.Function;

/**
 * The Effect class is meant to represent a particular filter.
 *
 * It has 2 functions as parameters :
 *  - {@link Effect#modifier modifier}  : store the application of the filter on the currently displayed image (the scaled down one)
 *  - {@link Effect#finalModifier finalModifier}  : store the application of the filter on the final image that will be called when saving the resulting product
 *
 * @author elliotrenel
 */

public class Effect{
    private Function<Void, Void> modifier;
    private Function<Void, Void> finalModifier;

    /**
     * Effect constructor
     * @param modifier the Function object applying the filter to the scaled down image
     * @param finalModifier the Function object applying the filter to the original image for saving
     */
    public Effect(Function<Void,Void> modifier,Function<Void,Void> finalModifier){
        this.finalModifier = finalModifier;
        this.modifier = modifier;
    }

    /**
     * Apply the stored filter on the scaled down image
     */
    public void applyModifier(){
        Void aVoid = null;
        modifier.apply(aVoid);
    }

    /**
     * Apply the stored filter on the final image
     */
    public void applyFinalModifier(){
        Void aVoid = null;
        finalModifier.apply(aVoid);
    }
}
