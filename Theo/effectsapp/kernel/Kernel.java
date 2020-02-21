package com.example.effectsapp.kernel;

public class Kernel {

    protected int[][] matrice;
    protected int sumKernel;
    protected int size;

    public Kernel(int size, int[] values){
        this.size = size;
        this.sumKernel = 0;
        this.matrice = new int[size][size];
        for(int y=0; y<size; y++){
            for(int x=0; x<size; x++){
                int value = values[size*y + x];
                this.matrice[x][y] = value;
                this.sumKernel = sumKernel + value;
            }
        }
    }

    public Kernel(int size){
        this.size = size;
        this.sumKernel = 0;
        this.matrice = new int[size][size];
    }

    public int[][] getMatrice() {
        return matrice;
    }

    public int getSumKernel() {
        return sumKernel;
    }

    public int getSize() {
        return size;
    }

    public void setSumKernel(int sumKernel) {
        this.sumKernel = sumKernel;
    }

    public void setMatrice(int[][] matrice) {
        this.matrice = matrice;
    }



}
