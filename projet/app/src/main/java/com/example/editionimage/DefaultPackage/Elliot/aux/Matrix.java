package com.example.editionimage.DefaultPackage.Elliot.aux;

public class Matrix {
    protected int w,h;
    protected int[][] values;

    protected Matrix(int w, int h){
        this.w = w;
        this.h = h;
        values = new int[w][h];
    }

    public Matrix(int w, int h, int value){
        this(w,h);
        for(int i=0; i<w; i++){
            for(int j=0; j<h; j++){
                this.setValue(i,j,value);
            }
        }
    }

    public Matrix(int w, int h, int[] values){
        this(w,h);
        for(int i=0; i<w; i++){
            for(int j=0; j<h; j++){
                this.setValue(i,j,values[i+j*w]);
            }
        }
    }

    public void setValue(int i, int j, int value){
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
