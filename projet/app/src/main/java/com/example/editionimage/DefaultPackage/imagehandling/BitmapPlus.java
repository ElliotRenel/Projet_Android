package com.example.editionimage.DefaultPackage.imagehandling;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.editionimage.DefaultPackage.imagehandling.tools.Kernel;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class BitmapPlus {
    private Bitmap bit_origin, bit_current;
    private BasicFilter filters;
    private int height, width, size;
    private PhotoView view;

    private final int IMAGE_SIZE = 700;

    public BitmapPlus(Bitmap bit, PhotoView view){
        bit_origin = bit.copy(bit.getConfig(),false);
        bit_current = Bitmap.createScaledBitmap(bit_origin.copy(bit_origin.getConfig(),true),(bit_origin.getWidth()*IMAGE_SIZE)/bit_origin.getHeight(),IMAGE_SIZE,false);

        filters = new BasicFilter(this);
        height = bit_current.getHeight();
        width = bit_current.getWidth();
        size = height*width;
        this.view = view;
    }

    public void setAsImageView(){
        view.setImageBitmap(bit_current);
    }

    public File saveImage() {
        Log.i("v","bit");
        String imgName = "Image-" + (new Random()).nextInt(1000)+".jpg";
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+"/Edition_Image");
        dir.mkdir();

        File file = new File(dir.getAbsolutePath(),imgName);

        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            this.bit_current.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.i("v","e");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return file;
    }

    public double[][] getHSVPixels(){
        double[][] result = new double[3][size];

        int[] pixels = new int[size];
        this.bit_current.getPixels(pixels,0,width,0,0,width,height);

        for(int i=0; i<size; i++)
            rgb_to_hsv(pixels[i], result, i);

        return result;
    }

    public void setHSVPixels(double[][] pixels){
        int[] result = new int[size];

        for(int i=0; i<size; i++)
            result[i] = hsv_to_rgb(pixels,i);

        setPixels(result);
    }

    public void getPixels(int[] pixels){
        bit_current.getPixels(pixels,0,width,0,0,width,height);
    }

    public void setPixels(int[] pixels){
        bit_current.setPixels(pixels,0,width,0,0,width,height);
    }

    public int[] getHSVHist(double[][] tabs){
        int[] hist = new int[101];
        for(int i=0; i<size; i++)
            hist[(int)(tabs[2][i]*100)]++;
        return hist;
    }

    public int[] getHSVCumul(double[][] tabs){
        int[] cumul = new int[101];
        int[] hist = getHSVHist(tabs);
        for(int i=1; i<hist.length;i++){
            cumul[i] = cumul[i-1] + hist[i];
        }
        return cumul;
    }

    private void rgb_to_hsv(int pixel, double[][] hsv_pixels, int index){
        double red_ = (double) Color.red(pixel)/(double)255;
        double blue_ = (double)Color.green(pixel)/(double)255;
        double green_ = (double)Color.blue(pixel)/(double)255;

        double cmax = Math.max(red_, blue_);
        cmax = Math.max(cmax, green_);
        double cmin= Math.min(red_, blue_);
        cmin = Math.min(cmin, green_);

        double delta = cmax - cmin;

        double h;


        if(delta == 0){
            h = 0;
        }else{
            if(cmax == red_) {
                h = ((60 * (((green_ - blue_)/delta)))+360)%360;
            }else if(cmax == green_) {
                h = (60 *(((blue_ - red_)/delta) ))+120;
            }else {
                h = 60 *(((red_ - green_)/delta))+240;
            }
        }

        h = (360-h)%360;


        if(cmax == 0) {
            hsv_pixels[1][index] = 0;
        }else {
            hsv_pixels[1][index] = 1 -(cmin/cmax);
        }
        hsv_pixels[0][index] = h;

        hsv_pixels[2][index] = cmax;
    }

    private int hsv_to_rgb(double[][] hsv, int index){
        double t = (int) (hsv[0][index]/60)%6;
        double f = (hsv[0][index]/60)- t;
        double l = hsv[2][index] *(1 - hsv[1][index]);
        double m = hsv[2][index] * (1- f*hsv[1][index]);
        double n = hsv[2][index] * (1-(1-f) * hsv[1][index]);


        double red =0;
        double green=0;
        double blue=0;

        if(t ==0){
            red = hsv[2][index];
            green = n;
            blue = l;
        }else if(t==1){
            red = m;
            green = hsv[2][index];
            blue = l;
        }else if(t == 2){
            red = l;
            green = hsv[2][index];
            blue = n;
        }else if(t == 3){
            red = l;
            green = m;
            blue = hsv[2][index];
        }else if(t == 4){
            red = n;
            green = l;
            blue = hsv[2][index];
        }else if(t == 5){
            red = hsv[2][index];
            green = l;
            blue = m;
        }
        return Color.rgb((float)red,(float)green,(float)blue);

    }

    public int getSize(){
        return size;
    }

    public int getHeight(){
        return height;
    }

    public int getWidth(){
        return width;
    }

    public Bitmap getBit_current() {
        return bit_current;
    }

    public void setBit_current( Bitmap arg ) {
        this.bit_current=arg;
    }

    /** Button Effects **/

    public void reset(){
        bit_current = Bitmap.createScaledBitmap(bit_origin.copy(bit_origin.getConfig(),true),(bit_origin.getWidth()*IMAGE_SIZE)/bit_origin.getHeight(),IMAGE_SIZE,false);
        setAsImageView();
    }
    public void toGray(){
        filters.toGray();
        setAsImageView();
    }
    public void toGrayRS(Context context){
        filters.toGrayRS(context);
        setAsImageView();
    }
    public void colorize(int color){
        filters.colorize(color);
        setAsImageView();
    }
    public void keepColor(int color){
        filters.keepColor(color,30);
        setAsImageView();
    }
    public void contrastLinear(){
        filters.contrastLinear();
        setAsImageView();
    }
    public void contrastEqual(){
        filters.contrastEqual();
        setAsImageView();
    }
    public void contrastEqualRS(Context context){
        filters.contrastEqualRS(context);
        setAsImageView();
    }

    public void modifContrast(int contrast){
        filters.modifContrast(contrast);
        setAsImageView();
    }

    public void modifLight(int lightvalue){
        filters.modifLight(lightvalue);
        setAsImageView();
    }

    public void gaussianBlur(){
        int[] tmp = {
                1, 4, 6, 4, 1,
                4, 16 ,24, 16 ,4,
                6, 24, 36, 24, 6,
                4, 16, 24, 16, 4,
                1, 4, 6, 4, 1
        };
        Kernel gauss = new Kernel(5 , 5,tmp);

        filters.convolution(gauss);
    }

    public void laplacianEdgeDetection(){
        this.toGray();
        int[] mask = {
                0, 0,-1, 0, 0,
                0,-1,-2,-1, 0,
                -1,-2,16,-2,-1,
                0,-1,-2,-1, 0,
                0, 0,-1, 0, 0
        };
        Kernel laplace = new Kernel(5, 5,mask);

        filters.convolution(laplace);
    }

    public void sobelEdgeDetection(){
        this.toGray();
        int[] mask1 = {
                1, 0, -1,
                2, 0, -2,
                1, 0, -1
        };
        int[] mask2 = {
                1, 2, 1,
                0, 0, 0,
                -1,-2,-1
        };
        Kernel mA = new Kernel(3,3,mask1);
        Kernel mB = new Kernel(3,3,mask2);

        filters.convolutionEdgeDetection(mA,mB);
    }
}
