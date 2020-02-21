package com.example.effectsapp;

import android.graphics.Color;

public abstract class Colors {
    public Colors(){
        }

    public static void RGBtoHSV(int red,int green, int blue, float hsv[] ){
        double r = (double)red/255.0;
        double g = (double)green/255.0;
        double b = (double)blue/255.0;

        double max = Math.max(b,Math.max(r,g));
        double min = Math.min(b,Math.min(r,g));
        double delta = max - min;

        if(delta == 0){
            hsv[0] = 0;
        }else if(max == r){
            hsv[0] = (float) ((60* ((g-b)/delta) + 360)%360 );
        }else if(max == g){
            hsv[0] = (float) (60* ((b-r)/delta) + 120);
        }else if(max == b){
            hsv[0] = (float) (60* ((r-g)/delta) + 240);
        }


        if(max == 0){
            hsv[1] = 0;
        }else{
            hsv[1] = (float)(delta/max);
        }

        hsv[2] = (float)max;
    }

    public static int HSVtoColor(float hsv[]){
        int ti = (int)(Math.abs(hsv[0]/60))%6;

        float f = hsv[0]/60 - ti;
        float l = hsv[2]*(1-hsv[1]);
        float m = hsv[2]*(1- f*hsv[1]);
        float n = hsv[2]*(1- (1-f)*hsv[1]);

        switch(ti){
            case 0:
                return Color.rgb((int)(255*hsv[2]), (int)(255*n), (int)(255*l));
            case 1:
                return Color.rgb((int)(255*m), (int)(255*hsv[2]), (int)(255*l));
            case 2:
                return Color.rgb((int)(255*l), (int)(255*hsv[2]), (int)(255*n));
            case 3:
                return Color.rgb((int)(255*l), (int)(255*m), (int)(255*hsv[2]));
            case 4:
                return Color.rgb((int)(255*n), (int)(255*l), (int)(255*hsv[2]));
            default:
                return Color.rgb((int)(255*hsv[2]), (int)(255*l), (int)(255*m));
        }

    }
}
