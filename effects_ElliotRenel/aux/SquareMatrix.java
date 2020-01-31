package com.example.myapplication.effects.aux;

public class SquareMatrix extends Matrix {
    private int dim;

    public SquareMatrix(int dim, int value){
        super(dim,dim,value);
        this.dim = dim;
    }

    public SquareMatrix(int dim, int[] values){
        super(dim,dim,values);
        this.dim = dim;
    }

    public int getDim() {
        return dim;
    }
}
