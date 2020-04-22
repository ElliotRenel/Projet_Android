package com.example.editionimage.DefaultPackage.imagehandling;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import com.example.editionimage.DefaultPackage.imagehandling.tools.Effect;
import com.example.editionimage.DefaultPackage.imagehandling.tools.Kernel;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Function;

import static java.lang.Math.round;

/**
 * Bitmap Handler is the class used to stock base image and current image (modified), and to apply to the current image the effects given by the user.
 * It's this class that takes care of the interaction between the user and the backend methods from BasicFilter, and takes care of some mid level methods
 * such as saving the image, reset it, or just displaying it on the app.
 */

public class BitmapHandler {
    private Bitmap bit_origin, bit_current, bit_final;
    private BasicFilter filters;
    private int height, width, size, height_final, width_final, size_final;
    private PhotoView view;
    private ArrayList<Effect> effectArray;

    private final int IMAGE_SIZE = 700;


    /**
     * Class BitmapHandler is used to put effect on bitmaps, and to save the current bitmap displayed with bit_final.
     *
     * @param bit Is the original bitmap, extracted from the picture imported or taken. (it will always be stocked in bit_origin in first place)
     * @param view Is the view where the current bitmap should be displayed, since it will be the same for all the duration of the application. (bit_current is displayed)
     */
    public BitmapHandler(Bitmap bit, PhotoView view){
        bit_origin = bit.copy(bit.getConfig(),false);
        bit_current = Bitmap.createScaledBitmap(bit_origin.copy(bit_origin.getConfig(),true),(bit_origin.getWidth()*IMAGE_SIZE)/bit_origin.getHeight(),IMAGE_SIZE,false);
        bit_final = bit_origin.copy(bit_origin.getConfig(),true);
        effectArray = new ArrayList<>();
        filters = new BasicFilter(this);
        height = bit_current.getHeight();
        width = bit_current.getWidth();
        size = height*width;
        height_final = bit_final.getHeight();
        width_final = bit_final.getWidth();
        size_final = height_final*width_final;
        this.view = view;
    }

    /**
     * Displays the {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#bit_current current image} on the view given by parameters
     */
    public void setAsImageView(){
        view.setImageBitmap(bit_current);
    }

    /**
     * Display the {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#bit_final saved image} at the end of the saving process
     */
    private void giveFinalPreview(){
        view.setImageBitmap(bit_final);
    }

    /**
     * Use the {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#effectArray effect's array} to create the final image to be saving by applying each filter on a copy of the original image "bit_final", then creates the image
     * file itself to finally give the preview of the saved image using "giveFinalPreview".
     *
     * @return the image file created
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File saveImage() {
        /* Applying effects to the original image */
        bit_final = bit_origin.copy(bit_origin.getConfig(),true);
        Effect current_effect;
        while(!effectArray.isEmpty()) {
            Log.i("Effect","Effect was applied");
            current_effect = effectArray.get(0);
            effectArray.remove(0);
            current_effect.applyFinalModifier();
        }

        /* Creating the final Image File */
        String imgName = "Image-" + (new Random()).nextInt(1000)+".jpg";
        File parentDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+"/Edition_Image");

        File file = new File(parentDir,imgName);

        file.mkdirs();

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

        /* Giving preview of final image */
        giveFinalPreview();

        return file;
    }

    /**
     * Clears all effects applied on bit_current by resetting it to bit_origin.
     */
    public void reset(){
        bit_current = Bitmap.createScaledBitmap(bit_origin.copy(bit_origin.getConfig(),true),(bit_origin.getWidth()*IMAGE_SIZE)/bit_origin.getHeight(),IMAGE_SIZE,false);
        effectArray.clear();
        setAsImageView();
    }

    /**
     * Undoes the last effect applied, if there is one.
     */
    public void undo(){
        if(!effectArray.isEmpty()){
            effectArray.remove(effectArray.size()-1);
            bit_current = Bitmap.createScaledBitmap(bit_origin.copy(bit_origin.getConfig(),true),(bit_origin.getWidth()*IMAGE_SIZE)/bit_origin.getHeight(),IMAGE_SIZE,false);
            for(Effect e : effectArray)
                e.applyModifier();
            setAsImageView();
        }
    }

    /**
     * Get an array of pixels in the HSV format
     * @param saving true if the method is used in the context of saving the final product, false otherwise
     * @return the pixel array
     */
    double[][] getHSVPixels(boolean saving){
        double[][] result = new double[3][saving?size_final:size];

        int[] pixels = new int[saving?size_final:size];

        getPixels(pixels,saving);

        for(int i=0; i<(saving?size_final:size); i++)
            rgb_to_hsv(pixels[i], result, i);

        return result;
    }

    /**
     * Set the image pixels from a given array of pixels in the HSV format
     * @param pixels the given array of pixels
     * @param saving true if the method is used in the context of saving the final product, false otherwise
     */
    void setHSVPixels(double[][] pixels, boolean saving){
        int[] result = new int[saving?size_final:size];

        for(int i=0; i<(saving?size_final:size); i++)
            result[i] = hsv_to_rgb(pixels,i);

        setPixels(result, saving);
    }

    /**
     * Get an array of the pixels' V value in the HSV format (used to reduce memory usage where V is the only value used)
     * @param saving true if the method is used in the context of saving the final product, false otherwise
     * @return the array of pixels' V value
     */
    double[] getVPixels(boolean saving){
        double[] result = new double[saving?size_final:size];

        int[] pixels = new int[saving?size_final:size];

        getPixels(pixels,saving);

        for(int i=0; i<(saving?size_final:size); i++)
            result[i] = rgb_to_v(pixels[i]);

        return result;

    }

    /**
     * Set the image pixel values given an array of new V pixel values in the HSV format
     * @param pixels the given V pixel array
     * @param saving true if the method is used in the context of saving the final product, false otherwise
     */
    void setVPixels(double[] pixels, boolean saving){
        int[] result = new int[saving?size_final:size];

        int[] old_pixels = new int[saving?size_final:size];

        getPixels(old_pixels,saving);

        for(int i=0; i<(saving?size_final:size); i++)
            result[i] = v_to_rgb(pixels[i],old_pixels[i]);

        setPixels(result, saving);

    }

    /**
     * Get the image's pixels in the classic RGB format
     * @param pixels the pixel array to be filled with the obtained values
     * @param saving true if the method is used in the context of saving the final product, false otherwise
     */
    void getPixels(int[] pixels, boolean saving){
        if(saving)
            this.bit_final.getPixels(pixels,0,width_final,0,0,width_final,height_final);
        else
            this.bit_current.getPixels(pixels,0,width,0,0,width,height);
    }

    /**
     * Set the image's pixels given an array of pixels in the RGB format
     * @param pixels the given array of RGB pixels
     * @param saving true if the method is used in the context of saving the final product, false otherwise
     */
    void setPixels(int[] pixels, boolean saving){
        if(saving)
            bit_final.setPixels(pixels,0,width_final,0,0,width_final,height_final);
        else
            bit_current.setPixels(pixels,0,width,0,0,width,height);
    }

    /**
     * Calculate the histogram of the given array
     * @param tabs array of V pixel values
     * @param saving true if the method is used in the context of saving the final product, false otherwise
     * @return the calculated histogram
     */
    int[] getHSVHist(double[] tabs,boolean saving){
        int[] hist = new int[101];
        for(int i=0; i<(saving?size_final:size); i++)
            hist[(int)(tabs[i]*100)]++;
        return hist;
    }

    /**
     * Calculate the cumulative histogram of the given array
     * @param tabs array of V pixel values
     * @param saving true if the method is used in the context of saving the final product, false otherwise
     * @return the calculated cumulated histogram
     */
    int[] getHSVCumul(double[] tabs,boolean saving){
        int[] cumul = new int[101];
        int[] hist = getHSVHist(tabs,saving);
        for(int i=1; i<hist.length;i++){
            cumul[i] = cumul[i-1] + hist[i];
        }
        return cumul;
    }

    /**
     * Conversion function from RGB to HSV values
     * @param pixel the pixel RGB value
     * @param hsv_pixels the HSV pixels array to be modified
     * @param index the index of the pixel to be modified in hsv_pixels
     */
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

    /**
     * Conversion function from HSV to RGB values
     * @param hsv the HSV array of pixels
     * @param index the index of the hsv pixel to convert
     * @return the RGB pixel value obtained
     */
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

    /**
     * Conversion function from RGB to the V value in the HSV format
     * @param pixel the RGB pixel to convert
     * @return the calculated V value
     */
    private double rgb_to_v(int pixel){
        double red_ = (double) Color.red(pixel)/(double)255;
        double blue_ = (double)Color.green(pixel)/(double)255;
        double green_ = (double)Color.blue(pixel)/(double)255;

        double V = Math.max(red_, blue_);
        V = Math.max(V, green_);

        return V;
    }

    /**
     * Conversion function from the V value in the HSV format to RGB values
     * @param v_pixel the V value of the pixel
     * @param old_pixel the old RGB value of the pixel
     * @return the calculated RGB pixel
     */
    private int v_to_rgb(double v_pixel, int old_pixel){
        double[][] old = new double[3][1];
        rgb_to_hsv(old_pixel,old,0);
        old[2][0] = v_pixel;
        return hsv_to_rgb(old,0);
    }

    /**
     * Getter for the image size
     * @param saving true if the method is used in the context of saving the final product, false otherwise
     * @return the image size (either current or final)
     */
    int getSize(boolean saving){
        return saving?size_final:size;
    }

    /**
     * Getter for the image height
     * @param saving true if the method is used in the context of saving the final product, false otherwise
     * @return the image height (either current of final)
     */
    int getHeight(boolean saving){
        return saving?height_final:height;
    }

    /**
     * Getter for the image width
     * @param saving true if the method is used in the context of saving the final product, false otherwise
     * @return the image width (either current of final)
     */
    int getWidth(boolean saving){
        return saving?width_final:width;
    }

    /**
     * Getter for the {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#bit_current current image}
     * @return {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#bit_current bit_current}
     */
    Bitmap getBit_current() {
        return bit_current;
    }

    /**
     * Setter for the {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#bit_current current image}
     * @param arg the new Bitmap value for {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#bit_current bit_current}
     */
    void setBit_current( Bitmap arg ) {
        this.bit_current=arg;
    }

    /**
     * Getter for the {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#bit_final final image}
     * @return {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#bit_final bit_final}
     */
    Bitmap getBit_final(){
        return bit_final;
    }

    /**
     * Setter for the {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#bit_final final image}
     * @param arg the new Bitmap value for {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#bit_final bit_final}
     */
    void setBit_final( Bitmap arg ) {
        this.bit_final=arg;
    }

    /**
     * Used to insert another Bitmap image into itself
     * @param bmp the other Bitmap
     * @param saving true if the method is used in the context of saving the final product, false otherwise
     */
    private void incrustation(BitmapHandler bmp, boolean saving){
        int [] pixelsBackground = new int[saving?size_final:size];
        int [] pixelsTopLayer = new int[saving?size_final:size];

        this.getPixels(pixelsBackground,saving);
        bmp.getPixels(pixelsTopLayer,saving);

        for(int i=0; i<(saving?size_final:size); i++){
            if (Color.red(pixelsTopLayer[i])<150) {
                pixelsBackground[i] = pixelsTopLayer[i];
            }
        }
        this.setPixels(pixelsBackground,saving);
    }

    /**
     * Thicken the black edges of a black and white image
     * @param saving true if the method is used in the context of saving the final product, false otherwise
     */
    private void thicken(boolean saving){
        int [] pixels = new int[(saving?size_final:size)];
        int [] result = new int[(saving?size_final:size)];
        this.getPixels(pixels,saving);
        this.getPixels(result,saving);

        for(int i=this.width; i<(saving?size_final:size)-this.width; i++){
            //si pixel est foncé
            if (Color.red(pixels[i])<200) {
                result[i] = Color.argb(255,0,0,0);
                result[i+1] = Color.argb(255,0,0,0);
                result[i-1] = Color.argb(255,0,0,0);
                result[i+this.width] = Color.argb(255,0,0,0);
                result[i-this.width] = Color.argb(255,0,0,0);

            }
        }
        this.setPixels(result,saving);
    }

    /**
     * Takes pseudo continuous color values of the image and discretize them
     * @param saving true if the method is used in the context of saving the final product, false otherwise
     */
    private void discretizeColor(boolean saving){
        int [] pixels = new int[(saving?size_final:size)];
        this.getPixels(pixels,saving);

        for(int i=this.width; i<(saving?size_final:size)-this.width; i++){

            pixels[i] = Color.argb(255,
                    round((Color.red(pixels[i]))/30)*30,
                    round((Color.green(pixels[i]))/30)*30,
                    round((Color.blue(pixels[i]))/30)*30);
        }
        this.setPixels(pixels,saving);
    }

    /* Button Effects */

    /**
     * Apply the {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#toGray(boolean) toGray} filter to the image and add the effect to {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#effectArray effectArray}
     */
    @SuppressWarnings("unused")
    public void toGray(){
        filters.toGray(false);
        effectArray.add(new Effect(
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.toGray(true);
                        return null;
                    }
                },
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.toGray(false);
                        return null;
                    }
                }));
        setAsImageView();
    }

    /**
     * Apply the {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#toGrayRS(Context, boolean) toGrayRS} filter to the image and add the effect to {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#effectArray effectArray}
     * @param context the application context
     */
    public void toGrayRS(final Context context){
        filters.toGrayRS(context,false);
        effectArray.add(new Effect(
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.toGrayRS (context,true);
                        return null;
                    }
                },
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.toGrayRS(context,false);
                        return null;
                    }
                }));
        setAsImageView();
    }

    /**
     * Apply the {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#invertRS(Context, boolean) invertRS} filter to the image and add the effect to {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#effectArray effectArray}
     * @param context the application context
     */
    private void invertRS(final Context context){
        filters.invertRS(context,false);
        effectArray.add(new Effect(
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.invertRS(context,true);
                        return null;
                    }
                },
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.invertRS(context,false);
                        return null;
                    }
                }));
        setAsImageView();
    }

    /**
     * Apply the {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#colorize(int, boolean) colorize} filter to the image and add the effect to {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#effectArray effectArray}
     * @param color the color argument to pass to {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#colorize(int, boolean) colorize}
     */
    public void colorize(final int color){
        filters.colorize(color,false);
        effectArray.add(new Effect(
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.colorize(color,true);
                        return null;
                    }
                },
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.colorize(color,false);
                        return null;
                    }
                }));
        setAsImageView();
    }

    /**
     * Apply the {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#keepColor(int, int, boolean) keepColor} filter to the image and add the effect to {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#effectArray effectArray}
     * @param color the color argument to pass to {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#keepColor(int, int, boolean) keepColor}
     */
    public void keepColor(final int color){
        final int range = 30;
        filters.keepColor(color,range,false);
        effectArray.add(new Effect(
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.keepColor(color,range,true);
                        return null;
                    }
                },
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.keepColor(color,range,false);
                        return null;
                    }
                }));
        setAsImageView();
    }

    /**
     * Apply the {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#shift(int, boolean) shift} filter to the image and add the effect to {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#effectArray effectArray}
     * @param shift the shift argument to pass to {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#shift(int, boolean) shift}
     */
    public void shift(final int shift){
        filters.shift(shift,false);
        effectArray.add(new Effect(
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.shift(shift,true);
                        return null;
                    }
                },
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.shift(shift,false);
                        return null;
                    }
                }));
        setAsImageView();
    }

    /**
     * Apply the {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#contrastLinear(boolean) contrastLinear} filter to the image and add the effect to {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#effectArray effectArray}
     */
    public void contrastLinear(){
        filters.contrastLinear(false);
        effectArray.add(new Effect(
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.contrastLinear(true);
                        return null;
                    }
                },
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.contrastLinear(false);
                        return null;
                    }
                }));
        setAsImageView();
    }

    /**
     * Apply the {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#contrastEqual(boolean) contrastEqual} filter to the image and add the effect to {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#effectArray effectArray}
     */
    @SuppressWarnings("unused")
    public void contrastEqual(){
        filters.contrastEqual(false);
        effectArray.add(new Effect(
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.contrastEqual(true);
                        return null;
                    }
                },
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.contrastEqual(false);
                        return null;
                    }
                }));
        setAsImageView();
    }

    /**
     * Apply the {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#contrastEqualRS(Context, boolean) contrastEqualRS} filter to the image and add the effect to {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#effectArray effectArray}
     * @param context the application context argument to pass to {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#contrastEqualRS(Context, boolean) contrastEqualRS}
     */
    public void contrastEqualRS(final Context context){
        filters.contrastEqualRS(context,false);
        effectArray.add(new Effect(
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.contrastEqualRS(context,true);
                        return null;
                    }
                },
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.contrastEqualRS(context,false);
                        return null;
                    }
                }));
        setAsImageView();
    }

    /**
     * Apply the {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#modifContrast(int, boolean) modifContrast} filter to the image and add the effect to {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#effectArray effectArray}
     * @param contrast the contrast argument to pass to {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#modifContrast(int, boolean) modifContrast}
     */
    public void modifContrast(final int contrast){
        filters.modifContrast(contrast,false);
        effectArray.add(new Effect(
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.modifContrast(contrast,true);
                        return null;
                    }
                },
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.modifContrast(contrast,false);
                        return null;
                    }
                }));
        setAsImageView();
    }

    /**
     * Apply the {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#modifLight(int, boolean) modifLight} filter to the image and add the effect to {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#effectArray effectArray}
     * @param lightvalue the lightlevel argument to pass to {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#modifLight(int, boolean) modifLight}
     */
    public void modifLight(final int lightvalue){
        filters.modifLight(lightvalue,false);
        effectArray.add(new Effect(
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.modifLight(lightvalue,true);
                        return null;
                    }
                },
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.modifLight(lightvalue,false);
                        return null;
                    }
                }));
        setAsImageView();
    }

    /**
     * Apply a {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#convolution(Kernel, boolean) convolution} to the image with a gaussian mask and add the effect to {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#effectArray effectArray}
     */
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
        effectArray.add(new Effect(
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.convolution(gauss,true);
                        return null;
                    }
                },
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.convolution(gauss,false);
                        return null;
                    }
                }));

        setAsImageView();
    }

    /**
     * Apply a {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#convolution(Kernel, boolean) convolution} to the image with a laplacian mask and add the effect to {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#effectArray effectArray}
     */
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

        effectArray.add(new Effect(
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.convolution(laplace,true);
                        return null;
                    }
                },
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.convolution(laplace,false);
                        return null;
                    }
                }));

        setAsImageView();
    }

    /**
     * Apply an {@link com.example.editionimage.DefaultPackage.imagehandling.BasicFilter#convolutionEdgeDetection(Kernel, Kernel, boolean) edge detection convolution} to the image with the Sobel's Kernels and add the effect to {@link com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler#effectArray effectArray}
     */
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
        effectArray.add(new Effect(
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.convolutionEdgeDetection(mA,mB,true);
                        return null;
                    }
                },
                new Function<Void, Void>() {
                    @Override
                    public Void apply(Void aVoid) {
                        filters.convolutionEdgeDetection(mA,mB,false);
                        return null;
                    }
                }));
        setAsImageView();
    }

    /**
     * Apply a crayon effect to the image using several different filters
     * @param context the application context
     */
    public void crayonEffect(Context context){
        laplaceEdgeDetection();
        invertRS(context);
        toGrayRS(context);
        setAsImageView();
    }

    /**
     * Apply a cartoon effect to the image using several different filters
     * @param context the application context
     */
    public void cartoonEffect(Context context){
        BitmapHandler border = new BitmapHandler(this.bit_current,this.view);
        border.toGrayRS(context);
        border.crayonEffect(context);
        border.thicken(true);
        this.discretizeColor(false);
        incrustation(border,false);

        setAsImageView();
    }

}
