package com.example.editionimage.DefaultPackage.hugo.Commons;

import android.graphics.Bitmap;
import android.graphics.Color;

import static java.lang.Math.min;
import static java.lang.StrictMath.max;

public class HsvCommon {//fonctions auxiliaires ou peu importante liées avec des traitement de valeurs en HSV



    static public float[][] hsvTabs(int[] pixels, int h, int w){//renvoit un tableau repésentant 3 tableaux de H, S et V provenant du tableaux pixels
        float[][] tabsHSV = new float[3][h*w];

        float[] hsv;
        hsv = new float[3];

        for (int i = 0; i < w * h; i++) {
            int color = pixels[i];
            int blue = Color.blue(color);
            int red = Color.red(color);
            int green = Color.green(color);
            rgb_to_hsv(red, green, blue, hsv);
            tabsHSV[2][i] = hsv[2];
            tabsHSV[0][i] = hsv[0];
            tabsHSV[1][i] = hsv[1];
        }
        return tabsHSV;
    }

    static public int[] histoHSV(float[] tab, int size, int maxRange) {//calcul un histogramme selon le paramètre tab, une version avec un tableau de int à été fait mais pas utilisée donc supprimée
        int histo[];
        histo = new int[maxRange];

        for (int i = 0; i < size; i++) {
            int tmp = (int) tab[i];
            histo[tmp] = histo[tmp] + 1;
        }
        return (histo);
    }

    static public void rgb_to_hsv(float red, float green, float blue, float[] hsv) {//converion de rgb à hsv conformément aux formules du wikipédia français
        float red_ = red / 255;
        float blue_ = blue / 255;
        float green_ = green / 255;

        float cmax = max(red_, blue_);
        cmax = max(cmax, green_);
        float cmin = min(red_, blue_);
        cmin = min(cmin, green_);

        float delta = cmax - cmin;

        float h;

        if (delta == 0) {
            h = 0;
        } else {
            if (cmax == red_) {
                h = ((60 * (((green_ - blue_) / delta))) + 360) % 360;
            } else if (cmax == green_) {
                h = (60 * (((blue_ - red_) / delta))) + 120;
            } else {
                h = 60 * (((red_ - green_) / delta)) + 240;
            }
        }

        float s;

        if (cmax == 0) {
            s = 0;
        } else {
            s = 1 - (cmin / cmax);
        }

        float v = cmax;

        hsv[0] = h;
        hsv[1] = s;
        hsv[2] = v;
        return;
    }

    static public int hsv_to_rgb(float hsv[]) {//converion de hsv à rgb conformément aux formules du wikipédia français
        float t = (int) (hsv[0] / 60) % 6;
        float f = (hsv[0] / 60) - t;
        float l = hsv[2] * (1 - hsv[1]);
        float m = hsv[2] * (1 - f * hsv[1]);
        float n = hsv[2] * (1 - (1 - f) * hsv[1]);

        float red = 0;
        float green = 0;
        float blue = 0;

        if (t == 0) {
            red = hsv[2];
            green = n;
            blue = l;
        } else if (t == 1) {
            red = m;
            green = hsv[2];
            blue = l;
        } else if (t == 2) {
            red = l;
            green = hsv[2];
            blue = n;
        } else if (t == 3) {
            red = l;
            green = m;
            blue = hsv[2];
        } else if (t == 4) {
            red = n;
            green = l;
            blue = hsv[2];
        } else if (t == 5) {
            red = hsv[2];
            green = l;
            blue = m;
        }
        int rgb = Color.rgb(red, green, blue);

        return rgb;
    }

    static public void doubleConvertion(Bitmap bmp) { //fait une convertion puis l'autre, sert de test
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int pixels[];
        pixels = new int[w * h];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);

        float[] hsv;
        hsv = new float[3];

        float[][] tabs = HsvCommon.hsvTabs(pixels,  h,  w);

        for (int i = 0; i < w * h; i++) {
            hsv[0] = tabs[0][i];
            hsv[1] = tabs[1][i];
            hsv[2] = tabs[2][i];
            pixels[i] = hsv_to_rgb(hsv);
        }
        bmp.setPixels(pixels, 0, w, 0, 0, w, h);
    }



}
