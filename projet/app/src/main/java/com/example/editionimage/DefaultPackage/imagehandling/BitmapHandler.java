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
    private Bitmap bit_origin, bit_current;
    private BasicFilter filters;
    private int height, width, size;
    private PhotoView view;
    Queue<Effect> effectQueue;

    private final int IMAGE_SIZE = 700;

    public BitmapHandler(Bitmap bit, PhotoView view){
        bit_origin = bit.copy(bit.getConfig(),false);
        bit_current = Bitmap.createScaledBitmap(bit_origin.copy(bit_origin.getConfig(),true),(bit_origin.getWidth()*IMAGE_SIZE)/bit_origin.getHeight(),IMAGE_SIZE,false);
        effectQueue = new LinkedList<>();
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
        /** Applying effects to the original image */
        Bitmap tmp = bit_current;
        this.setBit_current(bit_origin.copy(bit_origin.getConfig(),true));
        Effect current_effect;
        while(!effectQueue.isEmpty()) {
            Log.i("Effect","Effect was applied");
            current_effect = effectQueue.remove();
            current_effect.applyModifier();
        }

        /** Creating the final Image File */
        String imgName = "Image-" + (new Random()).nextInt(1000)+".jpg";
        File parentDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString());
        File dir = new File(parentDir,"/Edition_Image");
        if(!dir.mkdirs()){
            return null;
        }
        File file = new File(dir.getAbsolutePath(),imgName);

        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            this.bit_current.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        /** Restoring bit_current */
        //this.setBit_current(tmp);
        setAsImageView();

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

    private void incrustation(BitmapHandler bmp){
        int pixelsBackground[] = new int[size];
        int pixelsTopLayer[] = new int[size];

        this.getPixels(pixelsBackground);
        bmp.getPixels(pixelsTopLayer);

        for(int i=0; i<size; i++){
            if (Color.red(pixelsTopLayer[i])<150) {
                pixelsBackground[i] = pixelsTopLayer[i];
            }
        }
        this.setPixels(pixelsBackground);
    }

    //épaissit les contours noirs d'une image en noir et blanc
    private void thicken(int intensity){
        int pixels[] = new int[size];
        this.getPixels(pixels);

        for(int i=1; i<size-1; i++){
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
        filters.toGray();
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.toGray();
                return null;
            }
        }));
        setAsImageView();
    }

    public void toGrayRS(final Context context){
        filters.toGrayRS(context);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.toGrayRS(context);
                return null;
            }
        }));
        setAsImageView();
    }

    public void invertRS(final Context context){
        filters.invertRS(context);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.invertRS(context);
                return null;
            }
        }));
        setAsImageView();
    }

    public void colorize(final int color){
        filters.colorize(color);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.colorize(color);
                return null;
            }
        }));
        setAsImageView();
    }

    public void keepColor(final int color){
        filters.keepColor(color,30);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.keepColor(color,30);
                return null;
            }
        }));
        setAsImageView();
    }

    public void shift(final int shift){
        filters.shift(shift);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.shift(shift);
                return null;
            }
        }));
        setAsImageView();
    }

    public void contrastLinear(){
        filters.contrastLinear();
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.contrastLinear();
                return null;
            }
        }));
        setAsImageView();
    }

    public void contrastEqual(){
        filters.contrastEqual();
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.contrastEqual();
                return null;
            }
        }));
        setAsImageView();
    }

    public void contrastEqualRS(final Context context){
        filters.contrastEqualRS(context);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.contrastEqualRS(context);
                return null;
            }
        }));
        setAsImageView();
    }

    public void modifContrast(final int contrast){
        filters.modifContrast(contrast);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.modifContrast(contrast);
                return null;
            }
        }));
        setAsImageView();
    }

    public void modifLight(final int lightvalue){
        filters.modifLight(lightvalue);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.modifLight(lightvalue);
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

        filters.convolution(gauss);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.convolution(gauss);
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

        filters.convolution(laplace);

        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.convolution(laplace);
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

        filters.convolutionEdgeDetection(mA,mB);
        effectQueue.add(new Effect(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                filters.convolutionEdgeDetection(mA,mB);
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

        incrustation(border);

        setAsImageView();
    }

}
