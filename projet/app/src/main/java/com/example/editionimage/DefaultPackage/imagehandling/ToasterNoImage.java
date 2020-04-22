package com.example.editionimage.DefaultPackage.imagehandling;

import android.content.Context;
import android.widget.Toast;

/**
 * toastNoImage is used to test if there is an image loaded in the app, to prevent errors from happening, and to print this error to the user.
 */
public class ToasterNoImage {

    Toast toast;
    Context context ;
    CharSequence text = "Please load an image before modifying it!";


    /**
     * Constructor for ToasterNoImage
     * @param context The application context.
     */
    public ToasterNoImage(Context context){
        this.context = context;
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
    }


    /**
     * isToastShowed allows to return a value to the application and a message to the user if no image is being displayed.
     *
     * @param usedImage BitmapHandler used in the application
     * @return true if there is no image loaded from the application, false otherwise.
     */
    public boolean isToastShowed(BitmapHandler usedImage){
        if(usedImage == null){
            toast.show();
            return true;
        }
        return false;
    }
}
