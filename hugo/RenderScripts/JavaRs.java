package com.example.projet_tech.RenderScripts;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.renderscript.Allocation;
import androidx.renderscript.RenderScript;

import com.example.projet_tech.ScriptC_gray;
import com.example.projet_tech.ScriptC_gray2;

import com.example.projet_tech.Main.*;

public class JavaRs {

    static public void toGrayRS(Bitmap bmp, Context c) {
        RenderScript rs = RenderScript.create(c);

        Allocation input = Allocation.createFromBitmap(rs, bmp);
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptC_gray grayScript = new ScriptC_gray(rs);

        grayScript.forEach_toGray(input, output);
        output.copyTo(bmp);
        input.destroy();
        output.destroy();
        grayScript.destroy();
        rs.destroy();
    }


    static public void toGrayRS2(Bitmap bmp, Context c) {
        RenderScript rs = RenderScript.create(c);

        Allocation input = Allocation.createFromBitmap(rs, bmp);
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptC_gray2 gray2Script = new ScriptC_gray2(rs);

        gray2Script.forEach_toGray(input, output);
        output.copyTo(bmp);
        input.destroy();
        output.destroy();
        gray2Script.destroy();
        rs.destroy();
    }
}
