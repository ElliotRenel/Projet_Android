package com.example.editionimage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.SeekBar;

import com.example.editionimage.DefaultPackage.imagehandling.BitmapPlus;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    private static final int REQUEST_TAKE_PHOTO = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 101;

    PhotoView photoView;
    Button openGallery, openCamera;
    Button buttonColorize, buttonKeepColor, buttonLight;
    BitmapPlus usedImage;
    int barValue_keepcolor = 180, barValue_colorize = 180;
    double barValue_lighlevel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Setting up the app **/
        usedImage = null;
        photoView = (PhotoView) findViewById(R.id.main_view);
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

        /** Effects buttons **/

        // Save
        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //usedImage.save();
            }
        });

        // Reset
        Button buttonReset = findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usedImage.reset();
            }
        });

        // Gray
        Button buttonToGray = findViewById(R.id.buttonToGray);
        buttonToGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { usedImage.toGray();
            }
        });

        // Colorize
        buttonColorize = findViewById(R.id.buttonColorize);

        final ScrollView colorizeView = findViewById(R.id.colorize_sv);
        final Button buttonApplyColorize = findViewById(R.id.applyColorize_b);
        final SeekBar seekbar_Colorize = findViewById(R.id.colorize_sb);
        seekbar_Colorize.setOnSeekBarChangeListener(seekListenerColorize);


        buttonApplyColorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usedImage.colorize(barValue_colorize);
                colorizeView.setVisibility(View.GONE);
                buttonColorize.setText("Colorize");
            }
        });

        buttonColorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(colorizeView.getVisibility()==View.GONE) {
                    colorizeView.setVisibility(View.VISIBLE);
                    seekbar_Colorize.setProgress(180);
                }else{
                    colorizeView.setVisibility(View.GONE);
                    buttonColorize.setText("Colorize");
                }

            }
        });

        // KeepColor
        buttonKeepColor = findViewById(R.id.buttonKeepColor);

        final ScrollView keepColorView = findViewById(R.id.keepColor_sv);
        final SeekBar seekbar_keepColor = findViewById(R.id.keepColor_sb);
        seekbar_keepColor.setOnSeekBarChangeListener(seekListenerKeepcolor);
        Button buttonApplyKeepColor = findViewById(R.id.applyKeepColor_b);

        buttonApplyKeepColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usedImage.keepColor(barValue_keepcolor);
                keepColorView.setVisibility(View.GONE);
                buttonKeepColor.setText("Keep Color");
            }
        });

        buttonKeepColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keepColorView.getVisibility()==View.GONE) {
                    keepColorView.setVisibility(View.VISIBLE);
                    seekbar_keepColor.setProgress(180);
                }else{
                    keepColorView.setVisibility(View.GONE);
                    buttonKeepColor.setText("Keep Color");
                }

            }
        });

        // Set Lightlevel
        buttonLight = findViewById(R.id.buttonLight);

        final ScrollView lightView = findViewById(R.id.light_sv);
        final SeekBar seekbar_light = findViewById(R.id.light_sb);
        seekbar_light.setOnSeekBarChangeListener(seekListenerLight);
        Button buttonApplyLight = findViewById(R.id.applyLight_b);

        buttonApplyLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usedImage.modifLight(barValue_lighlevel);
                lightView.setVisibility(View.GONE);
                buttonLight.setText("Set Lightlevel");
            }
        });

        buttonLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lightView.getVisibility()==View.GONE) {
                    lightView.setVisibility(View.VISIBLE);
                    seekbar_light.setProgress(0);

                }else{
                    lightView.setVisibility(View.GONE);
                    buttonLight.setText("Set Lightlevel");
                }

            }
        });


        // Linear Contrast
        Button buttonContrastLinear = findViewById(R.id.buttonContrastLinear);
        buttonContrastLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { usedImage.contrastLinear(); }
        });

        // Equal Contrast
        Button buttonContrastEqual = findViewById(R.id.buttonContrastEqual);
        buttonContrastEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { usedImage.contrastEqual(); }
        });

        // Gaussian Blur
        Button buttonGaussian = findViewById(R.id.buttonGaussian);
        buttonGaussian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { usedImage.gaussianBlur(); }
        });

        // Edge detection
        Button buttonEdge = findViewById(R.id.buttonEdge);
        buttonEdge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { usedImage.simpleEdgeDetection(); }
        });
    }

    SeekBar.OnSeekBarChangeListener seekListenerColorize = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            barValue_colorize = progress;
            buttonColorize.setText("Colorize "+progress+"°");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };

    SeekBar.OnSeekBarChangeListener seekListenerKeepcolor = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            barValue_keepcolor = progress;
            buttonKeepColor.setText("Keep Color "+progress+"°");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };

    SeekBar.OnSeekBarChangeListener seekListenerLight = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            barValue_lighlevel = ((double) progress)/100.0;
            buttonLight.setText("Set Lightlevel "+((float)progress/100));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };



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
