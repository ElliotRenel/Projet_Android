package com.example.editionimage.DefaultPackage.imagehandling;

import android.content.Context;
import android.widget.Toast;

public class ToasterNoImage {

    Toast toast;
    Context context ;
    CharSequence text = "Please load an image before modifying it!";


    public ToasterNoImage(Context context){
        this.context = context;
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
    }



    public boolean isToastShowed(BitmapPlus usedImage){
        if(usedImage == null){
            toast.show();
            return true;
        }
        return false;
    }
}
