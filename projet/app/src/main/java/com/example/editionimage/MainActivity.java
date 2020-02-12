package com.example.editionimage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.editionimage.DefaultPackage.imagehandling.BitmapPlus;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;

    ImageView photoView;
    Button openGallery;
    BitmapPlus usedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoView = findViewById(R.id.main_view);
        openGallery = findViewById(R.id.gallery_b);

        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    /**
     * Start an ActivityForResult to get an image Uri from the gallery, which is then used
     * in the override of onActivityResult to create the BitmapPlus that will be used
     */
    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE && data!=null){
            // Reading the Uri of the picked image
            Uri imageUri = data.getData();

            try {
                // MediaStore is used to "convert" the uri to a Bitmap, which is then used to create our BitmapPlus
                usedImage = new BitmapPlus(MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri));
                usedImage.setAsImageView(photoView);
            }catch (IOException e){
                Log.i("Errors", "IOException while trying to load file from gallery");
                e.printStackTrace();
            }
        }
    }
}
