package com.example.editionimage.DefaultPackage.hugo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projet_tech.Commons.HsvCommon;
import com.example.projet_tech.Commons.Matrice;
import com.example.projet_tech.Commons.MatriceType;
import com.example.projet_tech.Commons.inputTreatment;
import com.example.projet_tech.Main.ImageBank;

import java.util.Random;

import static com.example.projet_tech.ImageTreatment.Contrastes.contrasteDyn;
import static com.example.projet_tech.ImageTreatment.Contrastes.contrasteEgal;
import static com.example.projet_tech.ImageTreatment.Contrastes.contrasteEgalGray;
import static com.example.projet_tech.ImageTreatment.Contrastes.contrasteGrayDyn;
import static com.example.projet_tech.ImageTreatment.NotContrasts.colorize;
import static com.example.projet_tech.ImageTreatment.NotContrasts.conserveV2;
import static com.example.projet_tech.ImageTreatment.NotContrasts.conserveV1;
import static com.example.projet_tech.ImageTreatment.NotContrasts.convolutionMatrice;
import static com.example.projet_tech.ImageTreatment.NotContrasts.toGray2;
import static com.example.projet_tech.ImageTreatment.NotContrasts.toGray;
import static com.example.projet_tech.RenderScripts.JavaRs.toGrayRS2;
import static com.example.projet_tech.RenderScripts.JavaRs.toGrayRS;


public class MainActivity extends AppCompatActivity {
    Bitmap btmp; //image centrale
    ImageView image;
    int seekBarValue; //valeur de la seekbar
    SeekBar seekBar;
    ImageBank imgBank; //banque d'image (voir Main.ImageBank)
    Boolean rs; //si l'option renderscript est activée, si oui les fonctions appellées utiliseront le renderscript
    EditText optnText; //valeur du champ de text
    EditText optnNumber; //valeur du champ de text (uniquement des nombres)
    Bitmap cpy;//copy de l'image où les modifications vont etre faites


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.imgMain);

        TextView tv = (TextView) findViewById(R.id.imageDim);
        optnText = (EditText)findViewById(R.id.OptnText);
        optnNumber = (EditText)findViewById(R.id.OptnNumber);

        Button bouttonGray = (Button) findViewById(R.id.buttonGray);
        Button bouttonContrastDyn = (Button) findViewById(R.id.buttonContrastDyn);
        Button bouttonClear = (Button) findViewById(R.id.buttonClear);
        Button bouttonChange = (Button) findViewById(R.id.buttonChange);
        Button bouttonContrastGrayDyn = (Button) findViewById(R.id.buttonGrayDyn);
        Button bouttonEgalGray = (Button) findViewById(R.id.buttonEgalGray);
        Button bouttonEgal = (Button) findViewById(R.id.buttonEgal);
        Button bouttonConserve = (Button) findViewById(R.id.buttonConserve);
        Button buttonConvo = (Button) findViewById(R.id.buttonConvo);
        Button buttonDoubleHSV = (Button) findViewById(R.id.buttonDoubleHSV);
        Button buttonColorize = (Button) findViewById(R.id.buttonColorize);

        Switch renderSwitch = (Switch) findViewById(R.id.RenderSwitch);

        seekBar = (SeekBar) findViewById(R.id.seekBar);


        imgBank = new ImageBank();// voir Main.ImageBank
        btmp = BitmapFactory.decodeResource(getResources(),imgBank.bank[imgBank.index] );

        String w = Integer.toString(btmp.getWidth());//affichage des dimensions de l'image
        String h = Integer.toString(btmp.getHeight());
        final Context c = this;
        rs = false;

        imgBank = new ImageBank();
        tv.setText(w + " " + h);


        //passe du mode renderscript au mode sans renderscript
        renderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                        rs = true;
                } else {
                      rs = false;
                }
            }
        });

        //appel de togray
        bouttonGray.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cpy = btmp.copy(btmp.getConfig(), true);
                if(rs){
                    toGrayRS2(cpy, c);
                }else{
                    toGray2(cpy);
                }

                image.setImageBitmap(cpy);
            }
        });

        //lance la fonction conserve en fonction de la valeur de la seekbar
        bouttonConserve.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cpy = btmp.copy(btmp.getConfig(), true);
                seekBarValue = seekBar.getProgress();
                conserveV2(cpy, (float) seekBarValue);
                image.setImageBitmap(cpy);
            }
        });

        //lance la convolution en fonction des deux champs textuels
        buttonConvo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int tmp =1;
                cpy = btmp.copy(btmp.getConfig(), true);
                MatriceType mt = inputTreatment.textInput(optnText.getText().toString());
                if( (mt == MatriceType.MOYENNE) && !("".equals(optnNumber.getText().toString())) ){//vérifie qui Integer.parsInt ne va pas avoir un String "" en paramtre
                    tmp = Integer.parseInt(optnNumber.getText().toString());
                    if(tmp %2 == 0){//pour etre sur que la valeur ait un "centre"
                        tmp ++;
                    }
                }
                Matrice mat = new Matrice(mt, tmp);// voir Commons.Matrice et Commons.MatriceType
                convolutionMatrice(cpy, mat);
                image.setImageBitmap(cpy);
            }
        });

        //lance une double conversion (dans les deux sens) de hsv, afin de vérifier leurs fonctionnements
        buttonDoubleHSV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cpy = btmp.copy(btmp.getConfig(), true);
                HsvCommon.doubleConvertion(cpy);
                image.setImageBitmap(cpy);
            }
        });

        //lance le contraste linéaire dynamique
        bouttonContrastDyn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cpy = btmp.copy(btmp.getConfig(), true);
                contrasteDyn(cpy);
                image.setImageBitmap(cpy);
            }
        });

        //permet "d'effacer" les modifications de l'image
        bouttonClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                image.setImageBitmap(btmp);
            }
        });

        //lance le contraste linéaire dynamique gris
        bouttonContrastGrayDyn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cpy = btmp.copy(btmp.getConfig(), true);
                contrasteGrayDyn(cpy);
                image.setImageBitmap(cpy);
            }
        });

        //change l'image en suivant l'ordre implémenté dans Main.ImageBank
        bouttonChange.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if( (imgBank.index +1) < imgBank.nbImages){
                    imgBank.index = imgBank.index +1;
                }else{
                    imgBank.index = 0;
                }
                int imageName = imgBank.bank[imgBank.index];
                btmp = BitmapFactory.decodeResource(getResources(), imgBank.bank[imgBank.index]);
                cpy = btmp.copy(btmp.getConfig(), true);
                image.setImageBitmap(btmp);
                String w = Integer.toString(btmp.getWidth());
                String h = Integer.toString(btmp.getHeight());
                TextView tv = (TextView) findViewById(R.id.imageDim);
                tv.setText(w + " " + h);
            }
        });

        //contraste égalisé gris
        bouttonEgalGray.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cpy = btmp.copy(btmp.getConfig(), true);
                contrasteEgalGray(cpy);
                image.setImageBitmap(cpy);
            }
        });

        //contraste égalisé
        bouttonEgal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cpy = btmp.copy(btmp.getConfig(), true);
                contrasteEgal(cpy);
                image.setImageBitmap(cpy);
            }
        });

        //colorize, la valeur à conserver est aléatoire
        buttonColorize.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cpy = btmp.copy(btmp.getConfig(), true);
                colorize(cpy);
                image.setImageBitmap(cpy);
            }
        });

        image.setImageBitmap(btmp);


    }

}
