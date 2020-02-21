package com.example.effectsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.effectsapp.colors.Colorize;
import com.example.effectsapp.colors.KeepColor;
import com.example.effectsapp.convolutions.ConvolutionGauss;
import com.example.effectsapp.convolutions.ConvolutionMoyenneur;
import com.example.effectsapp.grays.ToGray;
import com.example.effectsapp.histogramEqualizer.HistogramEqualizer;
import com.example.effectsapp.linearDynamic.LinearDynamicExtension;


public class MainActivity extends AppCompatActivity {

    Bitmap bit, bit_copy;
    ImageView iv;
    TextView width_tv, height_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.image);
        width_tv = findViewById(R.id.width);
        height_tv = findViewById(R.id.height);

        bit = BitmapFactory.decodeResource(getResources(), R.drawable.eye);
        bit_copy = bit.copy(bit.getConfig(), true);


        width_tv.setText("Width: " + bit.getWidth());
        height_tv.setText("Height: " + bit.getHeight());
        iv.setImageBitmap(bit_copy);



        Button button_reset = findViewById(R.id.button_reset);
        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bit_copy = bit.copy(bit.getConfig(), true);
                iv.setImageBitmap(bit_copy);
            }
        });

        Button button_toSun = findViewById(R.id.button_toSun);
        button_toSun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bit = BitmapFactory.decodeResource(getResources(), R.drawable.sun);
                width_tv.setText("Width: " + bit.getWidth());
                height_tv.setText("Height: " + bit.getHeight());
                bit_copy = bit.copy(bit.getConfig(), true);
                iv.setImageBitmap(bit_copy);
            }
        });

        Button button_toCircle = findViewById(R.id.button_toHsvCircle);
        button_toCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bit = BitmapFactory.decodeResource(getResources(), R.drawable.hsv_circle);
                width_tv.setText("Width: " + bit.getWidth());
                height_tv.setText("Height: " + bit.getHeight());
                bit_copy = bit.copy(bit.getConfig(), true);
                iv.setImageBitmap(bit_copy);
            }
        });

        Button button_toEye = findViewById(R.id.button_toEye);
        button_toEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bit = BitmapFactory.decodeResource(getResources(), R.drawable.eye);
                width_tv.setText("Width: " + bit.getWidth());
                height_tv.setText("Height: " + bit.getHeight());
                bit_copy = bit.copy(bit.getConfig(), true);
                iv.setImageBitmap(bit_copy);
            }
        });

        Button button_toMario = findViewById(R.id.button_toMario);
        button_toMario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bit = BitmapFactory.decodeResource(getResources(), R.drawable.mario);
                width_tv.setText("Width: " + bit.getWidth());
                height_tv.setText("Height: " + bit.getHeight());
                bit_copy = bit.copy(bit.getConfig(), true);
                iv.setImageBitmap(bit_copy);

            }
        });

        Button button_toRedGradient = findViewById(R.id.button_toRedGradient);
        button_toRedGradient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bit = BitmapFactory.decodeResource(getResources(), R.drawable.red_gradient);
                width_tv.setText("Width: " + bit.getWidth());
                height_tv.setText("Height: " + bit.getHeight());
                bit_copy = bit.copy(bit.getConfig(), true);
                iv.setImageBitmap(bit_copy);
            }
        });

        Button button_to_gray = findViewById(R.id.button_toGray);
        button_to_gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGrayRS(bit_copy);
                iv.setImageBitmap(bit_copy);
            }
        });

        Button button_convolutionGauss = findViewById(R.id.button_convolutionGauss);
        button_convolutionGauss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvolutionGauss convolution_gauss = new ConvolutionGauss(bit_copy);
                convolution_gauss.convolution(bit_copy);
                iv.setImageBitmap(bit_copy);
            }
        });

        Button button_convolutionMoyenneur = findViewById(R.id.button_convolutionMoyenneur);
        button_convolutionMoyenneur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvolutionMoyenneur convolution_moyenneur = new ConvolutionMoyenneur(bit_copy, 7);
                convolution_moyenneur.convolution(bit_copy);
                iv.setImageBitmap(bit_copy);
            }
        });

        Button button_to_grays = findViewById(R.id.button_to_grays);
        button_to_grays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToGray to_gray = new ToGray(bit_copy);
                to_gray.toGrays();
                iv.setImageBitmap(bit_copy);
            }
        });


        Button button_colorize = findViewById(R.id.button_colorize);
        button_colorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Colorize colorize = new Colorize(bit_copy);
                colorize.colorize();
                iv.setImageBitmap(bit_copy);
            }
        });


        Button button_keepColor = findViewById(R.id.button_keepColor);
        button_keepColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeepColor keepColor = new KeepColor(bit_copy);
                keepColor.keepColorRandom(bit_copy, 30);
                iv.setImageBitmap(bit_copy);
            }
        });


        Button button_extDyn = findViewById(R.id.button_extDynG);
        button_extDyn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToGray to_gray = new ToGray(bit_copy);
                to_gray.toGrays();                                          //Transforms in grey in case of colored picture
                LinearDynamicExtension lde = new LinearDynamicExtension(bit_copy);
                lde.extendDyn();
                iv.setImageBitmap(bit_copy);
            }
        });

        Button button_extDyn_Color = findViewById(R.id.button_extDynC);
        button_extDyn_Color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearDynamicExtension lde = new LinearDynamicExtension(bit_copy);
                lde.extendDynColor();
                iv.setImageBitmap(bit_copy);
            }
        });

        Button button_equalizer_grey = findViewById(R.id.button_equalizerg);
        button_equalizer_grey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToGray to_gray = new ToGray(bit_copy);
                to_gray.toGrays();                                          //Transforms in grey in case of colored picture
                HistogramEqualizer he = new HistogramEqualizer(bit_copy);
                he.equalizerGray();
                iv.setImageBitmap(bit_copy);
            }
        });

        Button button_equalizer_color = findViewById(R.id.button_equalizerc);
        button_equalizer_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistogramEqualizer he = new HistogramEqualizer(bit_copy);
                he.equalizerColor();
                iv.setImageBitmap(bit_copy);
            }
        });

    }

    private void black_line(Bitmap bmp ){
        double j = bmp.getHeight()*0.5;
        for(double i =0; i<bmp.getWidth(); i++){
            bmp.setPixel((int) i, (int) j, Color.BLACK);
        }
        return;
    }          // NOT Displayed in application

    private void toGray(Bitmap bmp){
        int p, grey;
        for(int i=0; i<bmp.getWidth(); i++){
            for(int j=0; j< bmp.getHeight(); j++){
                p = bmp.getPixel(i,j);
                grey = (int)(Color.red(p)*0.3) + (int)(Color.green(p)*0.59)+ (int)(Color.blue(p)*0.11);
                bmp.setPixel(i, j, Color.rgb(grey, grey, grey));
            }
        }
        return;
    }               // NOT Displayed in application


    public void toGrayRS(Bitmap bmp){

        RenderScript rs = RenderScript.create(this);

        Allocation input = Allocation.createFromBitmap(rs, bmp);
        Allocation output = Allocation.createTyped(rs, input.getType());


        ScriptC_gray2 grayScript = new ScriptC_gray2(rs);


        grayScript.forEach_toGray2(input, output);

        output.copyTo(bmp);

        input.destroy(); output.destroy();

        grayScript.destroy(); rs.destroy();

    }



    @Override
    protected void onStart(){
        super.onStart();
        Log.i("Deb","Start(Hello world!)");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i("Deb","Resume(Here we go again)");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i("Deb","Pause(Hold on a sec)");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.i("Deb","Stop");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.i("Deb","Restart(It's me, Mario!)");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i("Deb","Destroy(Byebye!)");
    }

}
