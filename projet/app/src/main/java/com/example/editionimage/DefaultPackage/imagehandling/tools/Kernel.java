package com.example.editionimage.DefaultPackage.imagehandling.tools;

/**
 * Kernel is the matrix representation of the masks used for the convolutions.
 */
public class Kernel {
    private int w,h;
    private int[][] values;
    private double inverse;

    /**
     * Kernel constructor initializing an empty kernel
     * @param w the kernel's width
     * @param h the kernel's height
     */
    private Kernel(int w, int h){
        this.w = w;
        this.h = h;
        values = new int[w][h];
        this.inverse = 0;
    }

    /**
     * Kernel constructor initializing a kernel with a set of values
     * @param w the kernel's width
     * @param h the kernel's height
     * @param values the set of values to initialize the matrix
     */
    public Kernel(int w, int h, int[] values){
        this(w,h);
        for(int i=0; i<w; i++){
            for(int j=0; j<h; j++){
                this.setValue(i,j,values[i+j*w]);
                this.inverse += values[i+j*w];
            }
        }
    }

    /**
     * Setter for one of the values of the {@link com.example.editionimage.DefaultPackage.imagehandling.tools.Kernel#values kernel's inner values}
     * @param i the column index
     * @param j the row index
     * @param value the value to put in the given position
     */
    private void setValue(int i, int j, int value){
        values[i][j] = value;
    }

    /**
     * Getter for one of the values of the {@link com.example.editionimage.DefaultPackage.imagehandling.tools.Kernel#values kernel's inner values}
     * @param i
     * @param j
     * @return
     */
    public int getValue(int i, int j){
        return values[i][j];
    }

    /**
     * Getter for the kernel's width
     * @return {@link com.example.editionimage.DefaultPackage.imagehandling.tools.Kernel#w the width}
     */
    public int getW() {
        return w;
    }

    /**
     * Getter for the kernel's height
     * @return {@link com.example.editionimage.DefaultPackage.imagehandling.tools.Kernel#h the height}
     */
    public int getH() {
        return h;
    }

    /**
     * Getter for the kernel's inverse
     * @return {@link com.example.editionimage.DefaultPackage.imagehandling.tools.Kernel#inverse the inverse}
     */
    public double getInverse(){
        return inverse;
    }

    @Override
    public String toString(){
        String str = "";
        for(int i=0; i<w; i++){
            for(int j=0; j<h; j++){
                str += values[i][j]+" ";
            }
            str+= "\n";
        }

        return str;
    }
}
