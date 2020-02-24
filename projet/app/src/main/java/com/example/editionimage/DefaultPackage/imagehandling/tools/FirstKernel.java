package com.example.editionimage.DefaultPackage.imagehandling.tools;

public class FirstKernel {
    public int[][] matrice;
    public int coef; //coefficient multiplicateur de la matrice
    public int size;//taille de la matrice

    enum MatriceType {
        IDENTITE,
        CONTOUR1,
        CONTOUR2,
        CONTOUR3,
        NET,
        MOYENNE,
        GAUSS3X3,
        GAUSS5X5
    }

    public FirstKernel(MatriceType mt,  int moySize){ //size utilisé uniquement pour la matrice "moyenne", voir MatriceType
        switch(mt){
            case IDENTITE :
                this.size = 3;
                matrice = new int[size][size];
                matrice[0][0] = 0;
                matrice[0][1] = 0;
                matrice[0][2] = 0;
                matrice[1][0] = 0;
                matrice[1][1] = 1;
                matrice[1][2] = 0;
                matrice[2][0] = 0;
                matrice[2][1] = 0;
                matrice[2][2] = 0;
                coef = 1;
                break;
            case CONTOUR1 :
                this.size = 3;
                matrice = new int[size][size];
                matrice[0][0] = 1;
                matrice[0][1] = 0;
                matrice[0][2] = -1;
                matrice[1][0] = 0;
                matrice[1][1] = 0;
                matrice[1][2] = 0;
                matrice[2][0] = -1;
                matrice[2][1] = 0;
                matrice[2][2] = 1;
                coef = 1;
                break;
            case CONTOUR2 :
                this.size = 3;
                matrice = new int[size][size];
                matrice[0][0] = 0;
                matrice[0][1] = 1;
                matrice[0][2] = 0;
                matrice[1][0] = 1;
                matrice[1][1] = -4;
                matrice[1][2] = 1;
                matrice[2][0] = 0;
                matrice[2][1] = 1;
                matrice[2][2] = 0;
                coef = 1;
                break;
            case CONTOUR3:
                this.size = 3;
                matrice = new int[size][size];
                matrice[0][0] = -1;
                matrice[0][1] = -1;
                matrice[0][2] = -1;
                matrice[1][0] = -1;
                matrice[1][1] = 8;
                matrice[1][2] = -1;
                matrice[2][0] = -1;
                matrice[2][1] = -1;
                matrice[2][2] = -1;
                coef = 1;
                break;
            case NET:
                this.size = 3;
                matrice = new int[size][size];
                matrice[0][0] = 0;
                matrice[0][1] = -1;
                matrice[0][2] = 0;
                matrice[1][0] = -1;
                matrice[1][1] = 5;
                matrice[1][2] = -1;
                matrice[2][0] = 0;
                matrice[2][1] = -1;
                matrice[2][2] = 0;
                coef = 1;
                break;
            case MOYENNE:
                size = moySize;
                matrice = new int[size][size];
                for(int i = 0; i< size; i++){
                    for(int j = 0; j<size; j++){
                        matrice[i][j] = 1;
                    }
                }
                coef = size*size;
                break;
            case GAUSS3X3:
                this.size = 3;
                matrice = new int[size][size];
                matrice[0][0] = 1;
                matrice[0][1] = 2;
                matrice[0][2] = 1;
                matrice[1][0] = 2;
                matrice[1][1] = 4;
                matrice[1][2] = 2;
                matrice[2][0] = 1;
                matrice[2][1] = 2;
                matrice[2][2] = 1;
                coef = 16;
                break;
            case GAUSS5X5:
                this.size = 5;
                matrice = new int[size][size];
                matrice[0][0] = 1;
                matrice[0][1] = 4;
                matrice[0][2] = 6;
                matrice[0][3] = 4;
                matrice[0][4] = 1;
                matrice[1][0] = 4;
                matrice[1][1] = 16;
                matrice[1][2] = 24;
                matrice[1][3] = 16;
                matrice[1][4] = 4;
                matrice[2][0] = 6;
                matrice[2][1] = 24;
                matrice[2][2] = 36;
                matrice[2][3] = 24;
                matrice[2][4] = 6;
                matrice[3][0] = 4;
                matrice[3][1] = 16;
                matrice[3][2] = 24;
                matrice[3][3] = 16;
                matrice[3][4] = 4;
                matrice[4][0] = 1;
                matrice[4][1] = 4;
                matrice[4][2] = 6;
                matrice[4][3] = 4;
                matrice[4][4] = 1;
                coef = 256;
                break;
        }
    }