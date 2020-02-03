package com.example.projet_tech.ImageTreatment;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.projet_tech.Commons.*;

public class Contrastes {

    static public void contrasteGrayDyn(Bitmap bmp) {//contraste linéaire dynamique en gris
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int pixels[];
        pixels = new int[w * h];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        float[] LUT;
        LUT = new float[100];
        int[] I;
        I = new int[w * h];
        float[] hsv;
        hsv = new float[3];

        float[] tabV;
        tabV = new float[w * h];
        float[] tabH;
        tabH = new float[w * h];
        float[] tabS;
        tabS = new float[w * h];

        float min = 100;
        float max = 0;

        for (int x = 0; x < w * h; x++) {
            int color = pixels[x];
            int blue = Color.blue(color);
            int red = Color.red(color);
            int green = Color.green(color);
            HsvCommon.rgb_to_hsv(red, green, blue, hsv);
            tabV[x] = hsv[2];
            tabH[x] = hsv[0];
            tabS[x] = hsv[1];
            if (min > hsv[2]) {
                min = hsv[2];
            }
            if (max < hsv[2]) {
                max = hsv[2];
            }
        }

        for (int val = 0; val < 100; val++) {
            LUT[val] = (100 * (val - min)) / (max - min);
        }

        for (int z = 0; z < w * h; z++) {
            hsv[2] = tabV[z];
            hsv[0] = tabH[z];
            hsv[1] = tabS[z];
            hsv[2] = LUT[(int) hsv[2]] / 100;
            int color = HsvCommon.hsv_to_rgb(hsv);
            int blue = Color.blue(color);
            int red = Color.red(color);
            int green = Color.green(color);
            int gray = (int) ((0.33 * red) + (0.33 * green) + (0.33 * blue));
            pixels[z] = Color.rgb(gray, gray, gray);
        }
        bmp.setPixels(pixels, 0, w, 0, 0, w, h);
    }

    static public void contrasteDyn(Bitmap bmp) {//contraste linéaire dynamique
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int pixels[];
        pixels = new int[w * h];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        float[] hsv;
        hsv = new float[3];

        float[] tabV;
        tabV = new float[w * h];
        float[] tabH;
        tabH = new float[w * h];
        float[] tabS;
        tabS = new float[w * h];

        float min = 100;
        float max = 0;

        for (int x = 0; x < w * h; x++) {
            int color = pixels[x];
            int blue = Color.blue(color);
            int red = Color.red(color);
            int green = Color.green(color);
            HsvCommon.rgb_to_hsv(red, green, blue, hsv);
            tabV[x] = hsv[2];
            tabH[x] = hsv[0];
            tabS[x] = hsv[1];
            if (min > hsv[2]) {
                min = hsv[2];
            }
            if (max < hsv[2]) {
                max = hsv[2];
            }
        }

        float[] LUT;
        LUT = new float[100];
        for (int val = 0; val < 100; val++) {
            LUT[val] = (100 * (val - min)) / (max - min);
        }

        for (int z = 0; z < w * h; z++) {
            hsv[2] = tabV[z];
            hsv[0] = tabH[z];
            hsv[1] = tabS[z];
            hsv[2] = LUT[(int) hsv[2]] / 100;
            pixels[z] = HsvCommon.hsv_to_rgb(hsv);
        }
        bmp.setPixels(pixels, 0, w, 0, 0, w, h);
    }

    static public void contrasteEgalGray(Bitmap bmp) { //contraste égalisé en gris
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int pixels[];
        pixels = new int[w * h];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        float[] hsv;
        hsv = new float[3];

        float[][] tabs = HsvCommon.hsvTabs(pixels,  h,  w);

        int[] H;
        H = HsvCommon.histoHSV(tabs[2], w * h, 100);

        float[] c;
        c = new float[100];
        c[0] = H[0];

        for (int i = 0; i < (100 - 1); i++) {
            c[i + 1] = c[i] + H[i + 1];
        }

        for (int i = 0; i < w * h; i++) {
            int tmp = (int) tabs[2][i];
            float sum = (c[tmp] * 100);
            sum = sum / (w * h);
            hsv[2] = sum / 100;
            hsv[0] = tabs[0][i];
            hsv[1] = tabs[1][i];
            int color = HsvCommon.hsv_to_rgb(hsv);
            int blue = Color.blue(color);
            int red = Color.red(color);
            int green = Color.green(color);
            int gray = (int) ((0.33 * red) + (0.33 * green) + (0.33 * blue));
            pixels[i] = Color.rgb(gray, gray, gray);
        }
        bmp.setPixels(pixels, 0, w, 0, 0, w, h);
    }

    static public void contrasteEgal(Bitmap bmp) {//contraste égalisé
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int pixels[];
        pixels = new int[w * h];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        float[] hsv;
        hsv = new float[3];

        float[][] tabs = HsvCommon.hsvTabs(pixels,  h,  w);

        int[] H;
        H = HsvCommon.histoHSV(tabs[2], w * h, 100);

        int[] c;
        c = new int[100];
        c[0] = H[0];

        for (int i = 0; i < (100 - 1); i++) {
            c[i + 1] = c[i] + H[i + 1];
        }

        for (int i = 0; i < w * h; i++) {
            int tmp = (int) tabs[2][i];
            float sum = (c[tmp] * 100);
            sum = sum / (w * h);
            hsv[2] = sum / 100;
            hsv[0] = tabs[0][i];
            hsv[1] = tabs[1][i];
            pixels[i] = HsvCommon.hsv_to_rgb(hsv);
        }
        bmp.setPixels(pixels, 0, w, 0, 0, w, h);
    }
}
