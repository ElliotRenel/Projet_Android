package com.example.editionimage.DefaultPackage.Seb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.renderscript.Allocation;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import androidx.renderscript.RenderScript;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;

import com.example.q.renderscriptexample.ScriptC_histEq;

import java.util.Random;

import static android.graphics.Color.HSVToColor;
import static android.graphics.Color.RGBToHSV;
import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;
import static android.graphics.Color.rgb;
import static com.example.td1.R.id.imagetest;

import static com.example.td1.Tp1.*;
import static com.example.td1.Tp2.*;
import static com.example.td1.Tp3.*;
import static com.example.td1.Tp4.*;
import static com.example.td1.Tp5.*;



public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private void toGrayRS ( Bitmap bmp ) {

        // 1) Creer un contexte RenderScript
        androidx.renderscript.RenderScript rs = RenderScript.create(this) ;
        // 2) Creer des Allocations pour passer les donnees
        Allocation input = Allocation. createFromBitmap (rs, bmp ) ;
        Allocation output = Allocation . createTyped ( rs , input.getType () ) ;

        // 3) Creer le script
        ScriptC_gray grayScript = new ScriptC_gray(rs);

        // 4) Copier les donnees dans les Allocations
        // ...

        // 5) Initialiser les variables globales potentielles
        // ...

        // 6) Lancer le noyau
        grayScript . forEach_toGray(input,output);

        // 7) Recuperer les donnees des Allocation (s)
        output . copyTo ( bmp ) ;

        // 8) Detruire le context , les Allocation (s) et le script
        input . destroy () ; output . destroy () ;
        grayScript . destroy () ; rs . destroy () ;
    }

    private void invertRS ( Bitmap bmp ) {
        androidx.renderscript.RenderScript rs = RenderScript.create(this) ;
        Allocation input = Allocation. createFromBitmap (rs, bmp ) ;
        Allocation output = Allocation . createTyped ( rs , input.getType () ) ;
        ScriptC_invert invertScript = new ScriptC_invert(rs);
        invertScript . forEach_invert(input,output);
        output . copyTo ( bmp ) ;
        input . destroy () ; output . destroy () ;
        invertScript . destroy () ; rs . destroy () ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button resetButton = (Button) findViewById(R.id.buttonReset);

        //TextView tv = (TextView) findViewById(R.id.txtHello);
        //MenuItem itemToGray = (MenuItem) findViewById(R.id.itemToGray);

        final ImageView img = (ImageView) findViewById(imagetest);
        BitmapDrawable d = (BitmapDrawable) img.getDrawable();
        final Bitmap bitmap = d.getBitmap();
        //Bitmap b = BitmapFactory.decodeFile("res/drawable/test.png");
        //String width = Integer.toString(bitmap.getWidth());
        //String height = Integer.toString(bitmap.getHeight());
        //tv.setText("Width: "+width+" Height: "+height);
        final Bitmap bitmap2 = bitmap.copy(bitmap.getConfig(),true);

        img.setImageBitmap(bitmap2);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                copy(bitmap,bitmap2);
                img.setImageBitmap(bitmap2);
            }
        });
    }

    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }
    public void showPopup2(View v){
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu2);
        popup.show();
    }
    public void showPopup3(View v){
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu3);
        popup.show();
    }
    public void showPopup4(View v){
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu4);
        popup.show();
    }
    public void showPopup5(View v){
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu5);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        ImageView img = (ImageView) findViewById(imagetest);
        BitmapDrawable d = (BitmapDrawable) img.getDrawable();
        final Bitmap bitmap = d.getBitmap();
        final Bitmap bitmap2 = bitmap.copy(bitmap.getConfig(),true);
        switch (item.getItemId()){
            case R.id.itemToGray:
                toGray(bitmap2);
                img.setImageBitmap(bitmap2);
                return true;
            case R.id.itemToGray2:
                toGray(bitmap2);
                img.setImageBitmap(bitmap2);
                return true;
            case R.id.itemColorize:
                colorize(bitmap2);
                img.setImageBitmap(bitmap2);
                return true;
            case R.id.itemKeepRed:
                keepRed(bitmap2);
                img.setImageBitmap(bitmap2);
                return true;
            case R.id.itemContrasteExtensionLineaire:
                contrasteExtensionLineaire(bitmap2);
                img.setImageBitmap(bitmap2);
                return true;
            case R.id.itemHsvContraste:
                hsvContraste(bitmap2);
                img.setImageBitmap(bitmap2);
                return true;
            case R.id.itemEgalisationHistogramme:
                egalisationHistogramme(bitmap2);
                img.setImageBitmap(bitmap2);
                return true;
            case R.id.itemDisplayHistogramme:
                displayHistogramme(bitmap2);
                img.setImageBitmap(bitmap2);
                return true;
            case R.id.itemFiltreMoyeneur3x3:
                filtreMoyeneur3x3(bitmap2);
                img.setImageBitmap(bitmap2);
                return true;
            case R.id.itemFiltreMoyeneur:
                EditText editText = (EditText) findViewById(R.id.editText);
                String message = editText.getText().toString();
                int size = Integer.parseInt(message);
                if((size%2)==1){
                    filtreMoyeneur(bitmap2,size);
                    img.setImageBitmap(bitmap2);
                }
                return true;
            case R.id.itemPrewitt:
                filtrePrewitt3x3(bitmap2);
                img.setImageBitmap(bitmap2);
                return true;

            case R.id.itemToGrayRS:
                toGrayRS(bitmap2);
                img.setImageBitmap(bitmap2);
                return true;
            case R.id.itemInvertRS:
                invertRS(bitmap2);
                img.setImageBitmap(bitmap2);
                return true;
            case R.id.itemSobel:
                filtreSobel3x3(bitmap2);
                img.setImageBitmap(bitmap2);
                return true;
            default:
                return false;
        }
    }
}
