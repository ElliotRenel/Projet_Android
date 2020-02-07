package com.example.editionimage.DefaultPackage.Elliot;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.myapplication.effects.aux.*;

public class KernelConvolution {

    /**
     * Apply a mask to a pixel
     * @param mask the mask
     * @param pixels array of V pixel value of the image
     * @param x the x coordinate of the pixel
     * @param y the y coordinate of the pixel
     * @param w the width of the image
     * @return the new value V for the pixel
     */
    private static double applyMask (Matrix mask, double[] pixels, int x, int y, int w){
        int mask_w = mask.getW()/2, mask_h = mask.getH()/2;

        double somme = 0;
        double inverse =0;

        for(int i=x-mask_w; i<x+mask_w+1; i++) {
            for (int j = y - mask_h; j < y + mask_h + 1; j++) {
                somme += pixels[j * w + i] * (double) mask.getValue(i - (x - mask_w), j - (y - mask_h));
                inverse += (double) mask.getValue(i - (x - mask_w), j - (y - mask_h));
            }
        }
        if(inverse==0){
            return Math.sqrt(somme*somme);
        }
        return somme/inverse;
    }

    /**
     * Apply a convolution with the given mask on the given image
     * @param bmp the bitmap image
     * @param mask the mask
     */
    private static void convolution(Bitmap bmp, SquareMatrix mask){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int size = w*h;
        int[] rgb_pixels = new int[size];
        bmp.getPixels(rgb_pixels ,0,w,0,0,w,h);
        double[] hsv_pixels = InnerMethods.rgb_to_v(rgb_pixels);

        int dim = mask.getDim()/2;

        for(int i=dim; i<w-dim; i++){
            for(int j=dim; j<h-dim;j++){
                rgb_pixels[i+j*w] = InnerMethods.v_to_rgb(rgb_pixels[i+j*w],applyMask(mask,hsv_pixels,i,j,w));
            }
        }

        bmp.setPixels(rgb_pixels,0,w,0,0,w,h);

    }

    private static int separableConvolution(Bitmap bmp, Matrix row, Matrix column) {
        if (row.getH() > 1 || column.getW() > 1) return -1;

        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int size = w * h;
        int[] rgb_pixels = new int[size];
        bmp.getPixels(rgb_pixels, 0, w, 0, 0, w, h);
        double[] hsv_pixels = InnerMethods.rgb_to_v(rgb_pixels);

        int dim = row.getW() / 2;

        for (int i = dim; i < w - dim; i++) {
            for (int j = 0; j < h; j++) {
                rgb_pixels[i + j * w] = InnerMethods.v_to_rgb(rgb_pixels[i + j * w], applyMask(row, hsv_pixels, i, j, w));
            }
        }

        dim = column.getH() / 2;

        for (int i = 0; i < w; i++) {
            for (int j = dim; j < h - dim; j++) {
                rgb_pixels[i + j * w] = InnerMethods.v_to_rgb(rgb_pixels[i + j * w], applyMask(column, hsv_pixels, i, j, w));
            }
        }

        bmp.setPixels(rgb_pixels, 0, w, 0, 0, w, h);

        return 1;
    }

    /** Convolution applications **/

    /**
     * Apply a simple average convolution on given image
     * @param bmp the bitmap image
     */
    public static void simpleBlur(Bitmap bmp){
        SquareMatrix simpleMask = new SquareMatrix(7 ,1);

        convolution(bmp,simpleMask);
    }

    /**
     * Apply a more pronounced blur on the given image with a separable
     * convolution with matrices of variable sizes depending on the image size
     * @param bmp
     */
    public static void advancedBlur(Bitmap bmp){
        int size = (bmp.getWidth()+bmp.getHeight())/2;
        size /= 150;
        Matrix row = new Matrix(2*size+1,1,1);
        Matrix column = new Matrix(1,2*size+1,1);

        separableConvolution(bmp,row,column);
    }

    /**
     * Apply a gaussian mask convolution on given image (gaussian blur)
     * @param bmp the bitmap image
     */
    public static void Gaussian(Bitmap bmp){
        int[] tmp = {
                1, 4, 6, 4, 1,
                4, 16 ,24, 16 ,4,
                6, 24, 36, 24, 6,
                4, 16, 24, 16, 4,
                1, 4, 6, 4, 1
        };
        SquareMatrix gauss = new SquareMatrix(5,tmp);

        convolution(bmp,gauss);
    }

    /**
     * Apply a LaplaceGaussian mask convolution on given image (Laplace edge detection)
     * @param bmp the bitmap image
     */
    public static void LaplacianGaussian(Bitmap bmp){
        ColorEffects.toGray(bmp);
        int[] mask = {
                0, 0,-1, 0, 0,
                0,-1,-2,-1, 0,
                -1,-2,16,-2,-1,
                0,-1,-2,-1, 0,
                0, 0,-1, 0, 0
        };
        SquareMatrix custom = new SquareMatrix(5,mask);

        convolution(bmp,custom);
    }
}
