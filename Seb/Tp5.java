package com.example.td1;

import android.graphics.Bitmap;
import android.util.Log;

import static android.graphics.Color.HSVToColor;
import static android.graphics.Color.RGBToHSV;
import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;
import static android.graphics.Color.rgb;

public class Tp5 {
    public static int[][] bitmapToTableau(Bitmap bmp){
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        int[][] tab = new int[bmp.getWidth()][bmp.getHeight()];
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());

        for (int x = 0; x < bmp.getWidth();x++){
            for (int y = 0; y < bmp.getHeight();y++){
                tab[x][y]= pixels[x+bmp.getWidth()*y];
                //Log.i("TP3", "pixels["+x+"]["+y+"]: " + pixels[x+bmp.getWidth()*y]);
                //Log.i("TP3", "tab["+x+"]["+y+"]: " + tab[x][y]);
            }
        }
        return tab;

    }

    public static int[] TableauToList(int[][] tab){
        int[] pixels= new int[tab.length * tab[0].length];

        for (int x = 0; x < tab.length;x++){
            for (int y = 0; y < tab[0].length;y++){
                pixels[x+tab.length*y] = tab[x][y];

            }
        }
        return pixels;
    }
    public static int moyenne(int a,int b,int c,
                       int d,int e,int f,
                       int g,int h,int i){
        return (a+b+c+d+e+g+h+i)/9;

    }

    public static int moyenne(int[] tab){
        int value=0;
        for (int i=0; i < tab.length; i++){
            value+=tab[i];
        }
        return (value)/tab.length;

    }

    public static void filtreMoyeneur3x3 (Bitmap bmp)
    {

        int[][] pixelsSource= bitmapToTableau(bmp);
        int[][] pixelsResultat= new int[bmp.getWidth()][bmp.getHeight()];
        int filterSize = 3;

        for (int x = 0+filterSize; x < pixelsSource.length-filterSize;x++)
        {
            for (int y = 0+filterSize; y < pixelsSource.length-filterSize;y++)
            {

                int moyenneRouge = moyenne(red(pixelsSource[x-1][y-1]),red(pixelsSource[x][y-1]),red(pixelsSource[x+1][y-1]),
                        red(pixelsSource[x-1][y]),red(pixelsSource[x][y]),red(pixelsSource[x+1][y]),
                        red(pixelsSource[x-1][y+1]),red(pixelsSource[x][y+1]),red(pixelsSource[x+1][y+1]));
                int moyenneVert = moyenne(green(pixelsSource[x-1][y-1]),green(pixelsSource[x][y-1]),green(pixelsSource[x+1][y-1]),
                        green(pixelsSource[x-1][y]),green(pixelsSource[x][y]),green(pixelsSource[x+1][y]),
                        green(pixelsSource[x-1][y+1]),green(pixelsSource[x][y+1]),green(pixelsSource[x+1][y+1]));
                int moyenneBleu = moyenne(blue(pixelsSource[x-1][y-1]),blue(pixelsSource[x][y-1]),blue(pixelsSource[x+1][y-1]),
                        blue(pixelsSource[x-1][y]),blue(pixelsSource[x][y]),blue(pixelsSource[x+1][y]),
                        blue(pixelsSource[x-1][y+1]),blue(pixelsSource[x][y+1]),blue(pixelsSource[x+1][y+1]));
                int moyenne=rgb((int)moyenneRouge,(int)moyenneVert,(int)moyenneBleu);
                pixelsResultat[x][y] = moyenne;
                //Log.i("TP3", "val: " + pixelsResultat[x][y]);
            }
        }

        bmp.setPixels(TableauToList(pixelsResultat),0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    public  static int prewitt(int a,int b,int c,
                               int d,int e,int f,
                               int g,int h,int i){
        int value=  (a*(-1))+(c*(1))+
                    (d*(-1))+(f*(1))+
                    (g*(-1))+(i*(1));
        return value;
    }

    public static void filtrePrewitt3x3 (Bitmap bmp)
    {

        int[][] pixelsSource= bitmapToTableau(bmp);
        int[][] pixelsResultat= new int[bmp.getWidth()][bmp.getHeight()];
        int filterSize = 3;

        for (int x = 0+filterSize; x < pixelsSource.length-filterSize;x++)
        {
            for (int y = 0+filterSize; y < pixelsSource.length-filterSize;y++)
            {

                int prewittRouge = prewitt(  red(pixelsSource[x-1][y-1]), red(pixelsSource[x][y-1]), red(pixelsSource[x+1][y-1]),
                                             red(pixelsSource[x-1][y]),   red(pixelsSource[x][y]),   red(pixelsSource[x+1][y]),
                                             red(pixelsSource[x-1][y+1]), red(pixelsSource[x][y+1]), red(pixelsSource[x+1][y+1]) );

                int prewittVert = prewitt(  green(pixelsSource[x-1][y-1]),green(pixelsSource[x][y-1]),green(pixelsSource[x+1][y-1]),
                                            green(pixelsSource[x-1][y]),  green(pixelsSource[x][y]),  green(pixelsSource[x+1][y]),
                                            green(pixelsSource[x-1][y+1]),green(pixelsSource[x][y+1]),green(pixelsSource[x+1][y+1]));

                int prewittBleu = prewitt(  blue(pixelsSource[x-1][y-1]),blue(pixelsSource[x][y-1]),blue(pixelsSource[x+1][y-1]),
                                            blue(pixelsSource[x-1][y]),  blue(pixelsSource[x][y]),  blue(pixelsSource[x+1][y]),
                                            blue(pixelsSource[x-1][y+1]),blue(pixelsSource[x][y+1]),blue(pixelsSource[x+1][y+1]));



                int result=rgb(     Math.max(Math.min((int)prewittRouge,255),0),
                                    Math.max(Math.min((int)prewittVert,255),0),
                                    Math.max(Math.min((int)prewittBleu,255),0));

                pixelsResultat[x][y] = result;
                //Log.i("TP3", "val: " + pixelsResultat[x][y]);
            }
        }

        bmp.setPixels(TableauToList(pixelsResultat),0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    public  static int sobel(int a,int b,int c,
                             int d,int e,int f,
                             int g,int h,int i){
        int value=  (a*(-1))+(c*(1))+
                    (d*(-2))+(f*(2))+
                    (g*(-1))+(i*(1));
        return value;
    }


    public static void filtreSobel3x3 (Bitmap bmp)
    {

        int[][] pixelsSource= bitmapToTableau(bmp);
        int[][] pixelsResultat= new int[bmp.getWidth()][bmp.getHeight()];
        int filterSize = 3;

        for (int x = 0+filterSize; x < pixelsSource.length-filterSize;x++)
        {
            for (int y = 0+filterSize; y < pixelsSource.length-filterSize;y++)
            {

                int sobelRouge = sobel(  red(pixelsSource[x-1][y-1]), red(pixelsSource[x][y-1]), red(pixelsSource[x+1][y-1]),
                        red(pixelsSource[x-1][y]),   red(pixelsSource[x][y]),   red(pixelsSource[x+1][y]),
                        red(pixelsSource[x-1][y+1]), red(pixelsSource[x][y+1]), red(pixelsSource[x+1][y+1]) );

                int sobelVert = sobel(  green(pixelsSource[x-1][y-1]),green(pixelsSource[x][y-1]),green(pixelsSource[x+1][y-1]),
                        green(pixelsSource[x-1][y]),  green(pixelsSource[x][y]),  green(pixelsSource[x+1][y]),
                        green(pixelsSource[x-1][y+1]),green(pixelsSource[x][y+1]),green(pixelsSource[x+1][y+1]));

                int sobelBleu = sobel(  blue(pixelsSource[x-1][y-1]),blue(pixelsSource[x][y-1]),blue(pixelsSource[x+1][y-1]),
                        blue(pixelsSource[x-1][y]),  blue(pixelsSource[x][y]),  blue(pixelsSource[x+1][y]),
                        blue(pixelsSource[x-1][y+1]),blue(pixelsSource[x][y+1]),blue(pixelsSource[x+1][y+1]));



                int result=rgb(     Math.max(Math.min((int)sobelRouge,255),0),
                                    Math.max(Math.min((int)sobelVert,255),0),
                                    Math.max(Math.min((int)sobelBleu,255),0));

                pixelsResultat[x][y] = result;
            }
        }
        bmp.setPixels(TableauToList(pixelsResultat),0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    public static void filtreMoyeneur (Bitmap bmp,int filterSize)
    {

        int[][] pixelsSource= bitmapToTableau(bmp);
        int[][] pixelsResultat= new int[bmp.getWidth()][bmp.getHeight()];
        int filterReach = (filterSize-1)/2;
        for (int x = 0+filterSize; x < pixelsSource.length-filterSize;x++)
        {
            for (int y = 0+filterSize; y < pixelsSource.length-filterSize;y++)
            {
                int arrayRed[] = new int[filterSize*filterSize];
                int arrayGreen[] = new int[filterSize*filterSize];
                int arrayBlue[] = new int[filterSize*filterSize];
                int c=0;
                for(int i=-filterReach; i <filterReach+1;i++){
                    for(int j=-filterReach; j <filterReach+1;j++){
                        arrayRed[c]=red(pixelsSource[i][j]);
                        arrayGreen[c]=green(pixelsSource[i][j]);
                        arrayBlue[c]=blue(pixelsSource[i][j]);
                        c+=1;
                    }
                }
                int moyenneRed = moyenne(arrayRed);
                int moyenneGreen = moyenne(arrayGreen);
                int moyenneBlue = moyenne(arrayBlue);
                int moyenne=rgb((int)moyenneRed,(int)moyenneGreen,(int)moyenneBlue);
                pixelsResultat[x][y] = moyenne;
            }
        }

        bmp.setPixels(TableauToList(pixelsResultat),0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }
}
