package com.example.editionimage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;

import android.Manifest;

import android.Manifest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.editionimage.DefaultPackage.imagehandling.BitmapPlus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    private static final int REQUEST_TAKE_PHOTO = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 101;

    PhotoView photoView;
    Button openGallery, openCamera;
    BitmapPlus usedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoView = findViewById(R.id.main_view);
        openGallery = findViewById(R.id.galleryButton);
        openCamera = findViewById(R.id.cameraButton);

        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

         openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
         });

        Button buttonToGray = findViewById(R.id.buttonToGray);
        Button buttonColorize = findViewById(R.id.buttonColorize);
        Button buttonKeepColor = findViewById(R.id.buttonKeepColor);
        Button buttonContrastLinear = findViewById(R.id.buttonContrastLinear);
        Button buttonContrastEqual = findViewById(R.id.buttonContrastEqual);
        Button buttonReset = findViewById(R.id.buttonReset);

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usedImage.reset();
            }
        });

        buttonToGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usedImage.toGray();
                Log.i("Deb", "Heyooo out of gray!");
            }
        });


        buttonColorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usedImage.colorize();
            }
        });


        buttonKeepColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usedImage.keepColor();
            }
        });

        buttonContrastLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usedImage.contrastLinear();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
        }
    }
    /**
     * Start an ActivityForResult to get an image Uri from the gallery, which is then used
     * in the override of onActivityResult to create the BitmapPlus that will be used
     */
    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }

        String currentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_TEST";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    Uri photoURI;

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (photoFile != null) {
                //Log.i("Path",BuildConfig.APPLICATION_ID);
                photoURI = FileProvider.getUriForFile(this, "com.example.editionimage" + ".fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE && data != null) {
            // Reading the Uri of the picked image
            Uri imageUri = data.getData();

            try {
                    //MediaStore is used to "convert" the uri to a Bitmap, which is then used to create our BitmapPlus
                    usedImage = new BitmapPlus(MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri), photoView);
                    usedImage.setAsImageView();
                } catch (IOException e) {
                    Log.i("Errors", "IOException while trying to load file from gallery");
                    e.printStackTrace();
                }
            }
            if(requestCode == REQUEST_TAKE_PHOTO){
                try {
                    usedImage = new BitmapPlus(MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI), photoView);
                    usedImage.setAsImageView();
                }catch (IOException e){
                Log.i("Errors", "IOException while trying to load the picture taken in camera");
                e.printStackTrace();
            }
        }
    }
}
}
