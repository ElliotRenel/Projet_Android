package com.example.editionimage.DefaultPackage.imagehandling.tools;

public class Kernel {
    private int w,h;
    private int[][] values;
    private double inverse;

    private Kernel(int w, int h){
        this.w = w;
        this.h = h;
        values = new int[w][h];
        this.inverse = 0;
    }

    public Kernel(int w, int h, int[] values){
        this(w,h);
        for(int i=0; i<w; i++){
            for(int j=0; j<h; j++){
                this.setValue(i,j,values[i+j*w]);
                this.inverse += values[i+j*w];
            }
        }
    }

    private void setValue(int i, int j, int value){
        values[i][j] = value;
    }

    public int getValue(int i, int j){
        return values[i][j];
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

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
