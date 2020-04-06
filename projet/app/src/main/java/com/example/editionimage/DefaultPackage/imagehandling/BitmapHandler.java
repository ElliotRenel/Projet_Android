package com.example.editionimage.DefaultPackage.imagehandling;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import com.example.editionimage.DefaultPackage.imagehandling.effectclass.Effect;
import com.example.editionimage.DefaultPackage.imagehandling.tools.Kernel;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.function.Function;

public class BitmapHandler {
    private Bitmap bit_origin, bit_current, bit_final;
    private BasicFilter filters;
    private int height, width, size, height_final, width_final, size_final;
    private PhotoView view;
    Queue<Effect> effectQueue;

    private final int IMAGE_SIZE = 700;

    public BitmapHandler(Bitmap bit, PhotoView view){
        bit_origin = bit.copy(bit.getConfig(),false);
        bit_current = Bitmap.createScaledBitmap(bit_origin.copy(bit_origin.getConfig(),true),(bit_origin.getWidth()*IMAGE_SIZE)/bit_origin.getHeight(),IMAGE_SIZE,false);
        bit_final = bit_origin.copy(bit_origin.getConfig(),true);
        effectQueue = new LinkedList<>();
        filters = new BasicFilter(this);
        height = bit_current.getHeight();
        width = bit_current.getWidth();
        size = height*width;
        height_final = bit_final.getHeight();
        width_final = bit_final.getWidth();
        size_final = height_final*width_final;
        this.view = view;
    }

    public void setAsImageView(){
        view.setImageBitmap(bit_current);
    }

    private void giveFinalPreview(){
        view.setImageBitmap(bit_final);
    }

    public File saveImage() {
        /** Applying effects to the original image */
        bit_final = bit_origin.copy(bit_origin.getConfig(),true);
        Effect current_effect;
        while(!effectQueue.isEmpty()) {
            Log.i("Effect","Effect was applied");
            current_effect = effectQueue.remove();
            current_effect.applyModifier();
        }

        /** Creating the final Image File */
        String imgName = "Image-" + (new Random()).nextInt(1000)+".jpg";
        File parentDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+"/Edition_Image");

        File file = new File(parentDir,imgName);

        file.mkdir();

        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            this.bit_final.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        /** Giving preview of final image */
        giveFinalPreview();

        return file;
    }

    public double[][] getHSVPixels(boolean saving){
        double[][] result = new double[3][saving?size_final:size];

        int[] pixels = new int[saving?size_final:size];

        getPixels(pixels,saving);

        for(int i=0; i<(saving?size_final:size); i++)
            rgb_to_hsv(pixels[i], result, i);

        return result;
    }

    public void setHSVPixels(double[][] pixels, boolean saving){
        int[] result = new int[saving?size_final:size];

        for(int i=0; i<(saving?size_final:size); i++)
            result[i] = hsv_to_rgb(pixels,i);

        setPixels(result, saving);
    }

    public void getPixels(int[] pixels, boolean saving){
        if(saving)
            this.bit_final.getPixels(pixels,0,width_final,0,0,width_final,height_final);
        else
            this.bit_current.getPixels(pixels,0,width,0,0,width,height);
    }

    public void setPixels(int[] pixels, boolean saving){
        if(saving)
            bit_final.setPixels(pixels,0,width_final,0,0,width_final,height_final);
        else
            bit_current.setPixels(pixels,0,width,0,0,width,height);
    }

    public int[] getHSVHist(double[][] tabs,boolean saving){
        int[] hist = new int[101];
        for(int i=0; i<(saving?size_final:size); i++)
            hist[(int)(tabs[2][i]*100)]++;
        return hist;
    }

    public int[] getHSVCumul(double[][] tabs,boolean saving){
        int[] cumul = new int[101];
        int[] hist = getHSVHist(tabs,saving);
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

    public int getSize(boolean saving){
        return saving?size_final:size;
    }

    public int getHeight(boolean saving){
        return saving?height_final:height;
    }

    public int getWidth(boolean saving){
        return saving?width_final:width;
    }

    public Bitmap getBit_current() {
        return bit_current;
    }

    public void setBit_current( Bitmap arg ) {
        this.bit_current=arg;
    }

    public Bitmap getBit_final(){
        return bit_final;
    }

    public void setBit_final( Bitmap arg ) {
        this.bit_final=arg;
    }

    private void incrustation(BitmapHandler bmp, boolean saving){
        int pixelsBackground[] = new int[saving?size_final:size];
        int pixelsTopLayer[] = new int[saving?size_final:size];

        this.getPixels(pixelsBackground,saving);
        bmp.getPixels(pixelsTopLayer,saving);

        for(int i=0; i<(saving?size_final:size); i++){
            if (Color.red(pixelsTopLayer[i])<150) {
                pixelsBackground[i] = pixelsTopLayer[i];
            }
        }
        this.setPixels(pixelsBackground,saving);
    }

    //épaissit les contours noirs d'une image en noir et blanc
    private void thicken(int intensity,boolean saving){
        int pixels[] = new int[(saving?size_final:size)];
        this.getPixels(pixels,saving);

        for(int i=1; i<(saving?size_final:size)-1; i++){
            //si pixel est foncé
            if (Color.red(pixels[i])<150) {
                pixels[i] = Color.argb(255,0,0,0);
                //ajouter les pixels autours
            }
        }
    }

    /** Button Effects **/

    public void reset(){
        bit_current = Bitmap.createScaledBitmap(bit_origin.copy(bit_origin.getConfig(),true),(bit_origin.getWidth()*IMAGE_SIZE)/bit_origin.getHeight(),IMAGE_SIZE,false);
        effectQueue.clear();
        setAsImageView();
    }

    public void toGray(){
        filters.toGray(false);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.toGray(true);
                return null;
            }
        }));
        setAsImageView();
    }

    public void toGrayRS(final Context context){
        filters.toGrayRS(context,false);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.toGrayRS(context,true);
                return null;
            }
        }));
        setAsImageView();
    }

    public void invertRS(final Context context){
        filters.invertRS(context,false);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.invertRS(context,true);
                return null;
            }
        }));
        setAsImageView();
    }

    public void colorize(final int color){
        filters.colorize(color,false);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.colorize(color,true);
                return null;
            }
        }));
        setAsImageView();
    }

    public void keepColor(final int color){
        filters.keepColor(color,30,false);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.keepColor(color,30,true);
                return null;
            }
        }));
        setAsImageView();
    }

    public void shift(final int shift){
        filters.shift(shift,false);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.shift(shift,true);
                return null;
            }
        }));
        setAsImageView();
    }

    public void contrastLinear(){
        filters.contrastLinear(false);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.contrastLinear(true);
                return null;
            }
        }));
        setAsImageView();
    }

    public void contrastEqual(){
        filters.contrastEqual(false);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.contrastEqual(true);
                return null;
            }
        }));
        setAsImageView();
    }

    public void contrastEqualRS(final Context context){
        filters.contrastEqualRS(context,false);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.contrastEqualRS(context,true);
                return null;
            }
        }));
        setAsImageView();
    }

    public void modifContrast(final int contrast){
        filters.modifContrast(contrast,false);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.modifContrast(contrast,true);
                return null;
            }
        }));
        setAsImageView();
    }

    public void modifLight(final int lightvalue){
        filters.modifLight(lightvalue,false);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.modifLight(lightvalue,true);
                return null;
            }
        }));
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
        final Kernel gauss = new Kernel(5 , 5,tmp);

        filters.convolution(gauss,false);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.convolution(gauss,true);
                return null;
            }
        }));

        setAsImageView();
    }

    public void laplaceEdgeDetection(){
        int[] mask = {
                0, 0,-1, 0, 0,
                0,-1,-2,-1, 0,
                -1,-2,16,-2,-1,
                0,-1,-2,-1, 0,
                0, 0,-1, 0, 0
        };
        final Kernel laplace = new Kernel(5, 5,mask);

        filters.convolution(laplace,false);

        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.convolution(laplace,true);
                return null;
            }
        }));

        setAsImageView();
    }

    public void sobelEdgeDetection(){
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
        final Kernel mA = new Kernel(3,3,mask1);
        final Kernel mB = new Kernel(3,3,mask2);

        filters.convolutionEdgeDetection(mA,mB,false);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.convolutionEdgeDetection(mA,mB,true);
                return null;
            }
        }));
        setAsImageView();
    }

    public void crayonEffect(Context context){
        laplaceEdgeDetection();
        invertRS(context);
        toGrayRS(context);
        setAsImageView();
    }

    public void cartoonEffect(Context context){
        BitmapHandler border = new BitmapHandler(this.bit_current,this.view);
        border.toGrayRS(context);
        border.crayonEffect(context);

        incrustation(border,false);

        setAsImageView();
    }

}
