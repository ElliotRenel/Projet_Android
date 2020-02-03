package com.example.projet_tech.ImageTreatment;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.projet_tech.Commons.HsvCommon;
import com.example.projet_tech.Commons.Matrice;

import java.util.Random;

public class NotContrasts {//fonctions de traitement d'images qui ne sont pas liés a des contraste

    static public void toGray(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int color = bmp.getPixel(i, j);
                float blue = Color.blue(color);
                float green = Color.green(color);
                float red = Color.red(color);
                int tmp = (int) (0.3 * red) + (int) (0.59 * green) + (int) (0.11 * blue);
                bmp.setPixel(i, j, Color.rgb(tmp, tmp, tmp));
            }
        }
    }

    static public void toGray2(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int pixels[];
        pixels = new int[w * h];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);

        for (int i = 0; i < w * h; i++) {
            int color = pixels[i];
            float blue = Color.blue(color);
            float green = Color.green(color);
            float red = Color.red(color);
            int tmp = (int) (0.3 * red) + (int) (0.59 * green) + (int) (0.11 * blue);
            pixels[i] = Color.rgb(tmp, tmp, tmp);
        }
        bmp.setPixels(pixels, 0, w, 0, 0, w, h);
    }


    static public void colorize(Bitmap bmp) {//change le H (hue) de l'image, la nouvelle valeur est choisie aléatoirement
        Random r = new Random();
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int pixels[];
        pixels = new int[w * h];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        float rand = (float) r.nextInt(360);

        float[] hsv;
        hsv = new float[3];

        float[][] tabs = HsvCommon.hsvTabs(pixels,  h,  w);

        for (int i = 0; i < w * h; i++) {
            tabs[0][i] = rand;
            hsv[0] = tabs[0][i];
            hsv[1] = tabs[1][i];
            hsv[2] = tabs[2][i];
            pixels[i] = HsvCommon.hsv_to_rgb(hsv);
        }
        bmp.setPixels(pixels, 0, w, 0, 0, w, h);
    }

    static public void conserveV1(Bitmap bmp, float mid) { //garde une seule Hue sur l'image, les autres sont mis à 0, mid est la valeur à garder, version tres peu performante comparé à la fonction en dessous
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int pixels[];
        pixels = new int[w * h];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);

        for (int i = 0; i < w * h; i++) {
            int color = pixels[i];
            float blue = Color.blue(color);//-------- debut de la partie changée de la verion suivante
            float green = Color.green(color);
            float red = Color.red(color);
            float[] hsv;
            hsv = new float[3];
            HsvCommon.rgb_to_hsv(red, green, blue, hsv);//------- fin de la partie changée de la verion suivante
            float angle = hsv[0];
            boolean b = false;
            if (mid + 30 >= 360 || mid - 30 < 0) {
                if (angle > mid - 30 && angle < mid + 30) {//conditions lorsque la valeur du mid est proche d'un angle a 0 radian (calcul fait en degré)
                    b = true;
                }
                if (mid - 30 < 0) {
                    mid = mid + 360;
                    if (angle > mid - 30 && angle < mid + 30) {
                        b = true;
                    }
                    mid = mid - 360;
                }
            } else if (angle > mid - 30 && angle < mid + 30) {
                b = true;
            }
            if (b) {
                pixels[i] = HsvCommon.hsv_to_rgb(hsv);
            } else {
                hsv[1] = 0;
                pixels[i] = HsvCommon.hsv_to_rgb(hsv);
            }

        }
        bmp.setPixels(pixels, 0, w, 0, 0, w, h);
    }

    static public void conserveV2(Bitmap bmp, float mid) {//version plus performante de conserveV1, toute les fonctions utilisant : HsvCommon.hsvTabs(pixels,  h,  w); sont en réalités des V2, celle-ci a été gardé afin
        //de voir les différences en temps d'exécution et en mémoires utilisé
        float[] hsv = new float[3];
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int pixels[];
        pixels = new int[w * h];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);

        float[][] tabs = HsvCommon.hsvTabs(pixels,  h,  w);//cette partie remplace la partie de la fonction du dessus

        for (int i = 0; i < w * h; i++) {
            float angle = tabs[0][i];
            boolean b = false;
            if (mid + 30 >= 360 || mid - 30 < 0) {
                if (angle > mid - 30 && angle < mid + 30) {
                    b = true;
                }
                if (mid - 30 < 0) {
                    mid = mid + 360;
                    if (angle > mid - 30 && angle < mid + 30) {
                        b = true;
                    }
                    mid = mid - 360;
                }
            } else if (angle > mid - 30 && angle < mid + 30) {
                b = true;
            }
            if (b) {
                hsv[0] = tabs[0][i];
                hsv[1] = tabs[1][i];
                hsv[2] = tabs[2][i];
                pixels[i] = HsvCommon.hsv_to_rgb(hsv);
            } else {
                hsv[0] = tabs[0][i];
                hsv[1] = 0;
                hsv[2] = tabs[2][i];
                pixels[i] = HsvCommon.hsv_to_rgb(hsv);
            }

        }
        bmp.setPixels(pixels, 0, w, 0, 0, w, h);
    }

    static public void convolutionMatrice(Bitmap bmp, Matrice m) { //fait une covolution avec une matrice m (voir Commons.Matrice)

        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int pixels[];
        pixels = new int[w * h];
        int pixelsBuf[];
        pixelsBuf = new int[w * h];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);

        float[][] tabs = HsvCommon.hsvTabs(pixels,  h,  w);

        for (int i = 0; i < w * h; i++) {
            pixelsBuf[i] = convoAux(m, h, w, i, tabs); //l'application de la matrice sur chaque case est fait ici
        }
        bmp.setPixels(pixelsBuf, 0, w, 0, 0, w, h);
    }

    static public int convoAux(Matrice m, int h, int w, int i, float[][] tabs){//applique la matrice sur un indice i, ici les matrices sont considérés comme carrées
        float[] hsv;
        hsv = new float[3];
        int coefTmp = m.coef; //différent de 1 si il y a un facteur qui multipli la matrice
        int area = ((m.size - 1) / 2); //"demi-largeur" de la mtrice depuis son centre
        float sum = 0; //valeur de sortie de la matrice or coefTmp
        for (int x = 0; x < m.size; x++) {
            for (int y = 0; y < m.size; y++) {
                if ( ((i % w) + (x - area) < w && (i % w) + (x - area) >= 0) && ((w * (y - area)) + i > 0 && (w * (y - area)) + i < w * h)) {//permet de détecter les cases qui ne sont en dehors de l'image mais que la matrice peut atteindre
                    hsv[2] = tabs[2][i + (x - area) + (w * (y - area))];
                    hsv[0] = tabs[0][i + (x - area) + (w * (y - area))];
                    hsv[1] = tabs[1][i + (x - area) + (w * (y - area))];
                    sum = sum + (hsv[2] * (m.matrice[x][y]));
                }else{
                    if(coefTmp - m.matrice[x][y] >= 1){//si la matrice a des cases qui ne sont pas traités (cf la if au-dessus), déduit le coefTmp selon la valeur de cette case, afin de ne pas fausser le résulat final
                        coefTmp = coefTmp - m.matrice[x][y];
                    }
                }
            }
        }
        hsv[2] = sum / coefTmp;
        return HsvCommon.hsv_to_rgb(hsv);
    }
}
