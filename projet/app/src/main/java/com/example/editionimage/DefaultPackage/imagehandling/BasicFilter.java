package com.example.editionimage.DefaultPackage.imagehandling;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.editionimage.DefaultPackage.imagehandling.tools.FirstKernel;

import java.util.Random;

public class BasicFilter {
    BitmapPlus bmp;

    public BasicFilter(BitmapPlus bit){
        bmp = bit;
    }


    public void toGray(){
        //vérifier si passage en hsv serait pas plus rapide (temps de conversion vs temps de cast int/float)
        int size = bmp.getSize();
        int[] pixels = new int[size];
        bmp.getPixels(pixels);

        for(int i=0;i<size;i++){
            int p = pixels[i];
            int val = (int)(Color.red(p)*0.3)+(int)(Color.green(p)*0.59)+(int)(Color.blue(p)*0.11);
            pixels[i] = Color.rgb(val,val,val);
        }
        bmp.setPixels(pixels);
    }


    public void colorize() {
        Random r = new Random();
        int size = bmp.getSize();
        float rand = (float) r.nextInt(360);
        double[][] tabs = bmp.getHSVPixels();

        for (int i = 0; i < size; i++) {
            tabs[0][i] = rand;
        }
        bmp.setHSVPixels(tabs);
    }


    public void keepColor(int color, int range) {
        color = (color % 360);
        int size = bmp.getSize();
        double[][] tabs = bmp.getHSVPixels();
        int cmin = (color - range + 360) % 360, cmax = (color + range) % 360;
        boolean discontinue = cmax < cmin;

        for (int i = 0; i < size; i++) {
            boolean inRange = discontinue ? tabs[0][i] < cmax || tabs[0][i] > cmin : tabs[0][i] < cmax && tabs[0][i] > cmin;
            if (!inRange) {
                tabs[1][i] = 0;
            }
        }
        bmp.setHSVPixels(tabs);
    }


    /*à  ajouter les augmentations de contrastes et diminution de contrastes*/

    public void contrastLinear(){
        int size = bmp.getSize();
        double[][] tabs = bmp.getHSVPixels();
        int[] hist = bmp.getHSVHist(tabs);
        int min = 0, max = 100;
        boolean b = true;

        for(int i=0; i<101; i++) {
            if (hist[i] != 0 && b) {
                min = i;
                b=false;
            }
        }

        for(int i=min+1; i<101;i++){
            if(hist[i]!=0)
                max=i;
        }

        for(int i=0; i<size; i++){
            tabs[2][i] = ((100/(max-min))*(tabs[2][i]*100)-min)/100;
        }
        bmp.setHSVPixels(tabs);
    }


    public void contrastEqual(){
        int size = bmp.getSize();
        double[][] tabs = bmp.getHSVPixels();

        int[] C = bmp.getHSVCumul(tabs);

        for(int i =0; i<size; i++){
            //voir si c est possible de réduire le nombre de conversion int/float
            tabs[2][i] = (((float)C[(int)(tabs[2][i]*100)])*100)/(float)(size*100);
        }
        bmp.setHSVPixels(tabs);
    }

    public void convolutionMatrice(BitmapPlus bmp, FirstKernel m) {

        int size = bmp.getSize();
        int h = bmp.getHeight();
        int w = bmp.getWidth();

        double[] hsvTmp = new double[3];
        double pixelsBuf[][];
        pixelsBuf = new double[3][size];

        double[][] tabs = bmp.getHSVPixels();

        for (int i = 0; i < size; i++) {
            hsvTmp = convoAux(m, h, w, i, tabs);
            pixelsBuf[0][i] = hsvTmp[0];
            pixelsBuf[1][i] = hsvTmp[1];
            pixelsBuf[2][i] = hsvTmp[2];
        }
        bmp.setHSVPixels(pixelsBuf);
    }

    public double[] convoAux(FirstKernel m, int h, int w, int i, double[][] tabs){
        double[] hsv;
        hsv = new double[3];
        int coefTmp = m.coef;
        int area = ((m.size - 1) / 2);
        double sum = 0;
        for (int x = 0; x < m.size; x++) {
            for (int y = 0; y < m.size; y++) {
                if ( ((i % w) + (x - area) < w && (i % w) + (x - area) >= 0) && ((w * (y - area)) + i > 0 && (w * (y - area)) + i < w * h)) {
                    hsv[2] = tabs[2][i + (x - area) + (w * (y - area))];
                    hsv[0] = tabs[0][i + (x - area) + (w * (y - area))];
                    hsv[1] = tabs[1][i + (x - area) + (w * (y - area))];
                    sum = sum + (hsv[2] * (m.matrice[x][y]));
                }else{
                    if(coefTmp - m.matrice[x][y] >= 1){
                        coefTmp = coefTmp - m.matrice[x][y];
                    }
                }
            }
        }
        hsv[2] = sum / coefTmp;
        return hsv;
    }
}
