package com.example.editionimage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.editionimage.DefaultPackage.imagehandling.BitmapHandler;
import com.example.editionimage.DefaultPackage.imagehandling.ToasterNoImage;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.IOException;

/**
 * MainActivity manages starting and stopping of the application in general, and provides the link between modifying functions, and their respective button or other tools on the graphic side.
 *
 * It allows the existence of functions like onCreate that will be called on specific moments of the application.
 * It also creates variables that will be used in the whole application, like buttons, bitmaps, or parameters for functions.
 *
 */
public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    private static final int REQUEST_TAKE_PHOTO = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 101;



    PhotoView photoView;
    Button openGallery, openCamera,buttonSave;
    Button buttonColorize, buttonKeepColor, buttonShift, buttonContrast, buttonLight;
    BitmapHandler usedImage;
    int barValue_keepcolor = 180, barValue_colorize = 180 ,barValue_shift = 180, barValue_contrast = 0, barValue_lighlevel = 0;
    int pictureHeight, pictureWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting up the app
        usedImage = null;
        final ToasterNoImage toastNoImage = new ToasterNoImage(getApplicationContext());
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

        // Effects buttons

        // Save
        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = usedImage.saveImage();
                if(file == null){
                    CharSequence text = "A problem occured, image could not be saved";
                    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(file);
                    mediaScanIntent.setData(contentUri);
                    sendBroadcast(mediaScanIntent);

                    CharSequence text = "Image saved";
                    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        // Undo
        Button buttonUndo = findViewById(R.id.buttonUndo);
        buttonUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!toastNoImage.isToastShowed(usedImage))
                    usedImage.undo();
            }
        });

        // Reset
        Button buttonReset = findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!toastNoImage.isToastShowed(usedImage)) usedImage.reset();
            }
        });

        // Gray
        Button buttonToGray = findViewById(R.id.buttonToGray);
        buttonToGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {if(!toastNoImage.isToastShowed(usedImage)) usedImage.toGrayRS(MainActivity.this);
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
                if(!toastNoImage.isToastShowed(usedImage)) usedImage.colorize(barValue_colorize);
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
                    barValue_colorize = 180;
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
                if(!toastNoImage.isToastShowed(usedImage)) usedImage.keepColor(barValue_keepcolor);
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
                    barValue_keepcolor = 180;
                }else{
                    keepColorView.setVisibility(View.GONE);
                    buttonKeepColor.setText("Keep Color");
                }

            }
        });

        // Shift
        buttonShift = findViewById(R.id.buttonShift);

        final ScrollView shiftView = findViewById(R.id.Shift_sv);
        final Button buttonApplyShift = findViewById(R.id.applyShift_b);
        final SeekBar seekbar_shift = findViewById(R.id.Shift_sb);
        seekbar_shift.setOnSeekBarChangeListener(seekListenerShift);


        buttonApplyShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!toastNoImage.isToastShowed(usedImage)) usedImage.shift(barValue_shift);
                shiftView.setVisibility(View.GONE);
                buttonShift.setText("Shift");
            }
        });

        buttonShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shiftView.getVisibility()==View.GONE) {
                    shiftView.setVisibility(View.VISIBLE);
                    seekbar_shift.setProgress(0);
                    barValue_shift = 0;
                }else{
                    shiftView.setVisibility(View.GONE);
                    buttonShift.setText("Shift");
                }

            }
        });

        // Linear Contrast
        Button buttonContrastLinear = findViewById(R.id.buttonContrastLinear);
        buttonContrastLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { if(!toastNoImage.isToastShowed(usedImage)) usedImage.contrastLinear(); }
        });

        // Crayon Effect
        Button buttonCrayonEffect = findViewById(R.id.buttonCrayonEffect);
        buttonCrayonEffect .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { if(!toastNoImage.isToastShowed(usedImage)) usedImage.crayonEffect(MainActivity.this); }
        });

        // Cartoon Effect
        Button buttonCartoonEffect = findViewById(R.id.buttonCartoonEffect);
        buttonCartoonEffect .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { if(!toastNoImage.isToastShowed(usedImage)) usedImage.cartoonEffect(MainActivity.this); }
        });

        // Equal Contrast
        Button buttonContrastEqual = findViewById(R.id.buttonContrastEqual);
        buttonContrastEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { if(!toastNoImage.isToastShowed(usedImage)) usedImage.contrastEqualRS(MainActivity.this); }
        });

        // Modify Contrast
        buttonContrast = findViewById(R.id.buttonContrast);

        final ScrollView contrastView = findViewById(R.id.contrast_sv);
        final SeekBar seekbar_contrast = findViewById(R.id.contrast_sb);
        seekbar_contrast.setOnSeekBarChangeListener(seekListenerContrast);
        Button buttonApplyContrast = findViewById(R.id.applyContrast_b);

        buttonApplyContrast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!toastNoImage.isToastShowed(usedImage)) usedImage.modifContrast(barValue_contrast);
                contrastView.setVisibility(View.GONE);
                buttonContrast.setText("Modify Contrast");
            }
        });

        buttonContrast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contrastView.getVisibility()==View.GONE) {
                    contrastView.setVisibility(View.VISIBLE);
                    seekbar_contrast.setProgress(0);
                    barValue_contrast = 0;

                }else{
                    contrastView.setVisibility(View.GONE);
                    buttonContrast.setText("Modify Contrast");
                }

            }
        });

        // Modify Lightlevel
        buttonLight = findViewById(R.id.buttonLight);

        final ScrollView lightView = findViewById(R.id.light_sv);
        final SeekBar seekbar_light = findViewById(R.id.light_sb);
        seekbar_light.setOnSeekBarChangeListener(seekListenerLight);
        Button buttonApplyLight = findViewById(R.id.applyLight_b);

        buttonApplyLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!toastNoImage.isToastShowed(usedImage)) usedImage.modifLight(barValue_lighlevel);
                lightView.setVisibility(View.GONE);
                buttonLight.setText("Modify Lightlevel");
            }
        });

        buttonLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lightView.getVisibility()==View.GONE) {
                    lightView.setVisibility(View.VISIBLE);
                    seekbar_light.setProgress(0);
                    barValue_lighlevel = 0;

                }else{
                    lightView.setVisibility(View.GONE);
                    buttonLight.setText("Modify Lightlevel");
                }

            }
        });

        // Gaussian Blur
        Button buttonGaussian = findViewById(R.id.buttonGaussian);
        buttonGaussian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { if(!toastNoImage.isToastShowed(usedImage)) usedImage.gaussianBlur(); }
        });

        // Laplace's edge detection
        Button buttonLaplaceEdge = findViewById(R.id.buttonLaplacianEdge);
        buttonLaplaceEdge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { if(!toastNoImage.isToastShowed(usedImage)) usedImage.laplaceEdgeDetection(); }
        });

        // Sobel edge detection
        Button buttonSobelEdge = findViewById(R.id.buttonSobelEdge);
        buttonSobelEdge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { if(!toastNoImage.isToastShowed(usedImage)) usedImage.sobelEdgeDetection(); }
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

    SeekBar.OnSeekBarChangeListener seekListenerShift = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            barValue_shift = progress;
            buttonShift.setText("Shift "+progress+"°");
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

    SeekBar.OnSeekBarChangeListener seekListenerContrast = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            barValue_contrast = progress;
            buttonContrast.setText("Modify Contrast "+(progress>0?"+":"")+progress);
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
            barValue_lighlevel = progress;
            buttonLight.setText("Set Lightlevel "+(progress>0?"+":"")+progress);
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            findViewById(R.id.scrollMain).setVisibility(View.GONE);
            openCamera.setVisibility(View.GONE);
            openGallery.setVisibility(View.GONE);
            buttonSave.setVisibility(View.GONE);

            Point phoneSize = new Point();
            getWindowManager().getDefaultDisplay().getSize(phoneSize);
            ViewGroup.LayoutParams params = photoView.getLayoutParams();
            pictureHeight = params.height;
            pictureWidth = params.width;
            float ratioPhone = (float)phoneSize.x / (float)phoneSize.y;
            float ratioPicture = (float)pictureWidth/(float)pictureHeight;

            if(ratioPhone<=ratioPicture){
                params.width = (int)(phoneSize.x*0.8);
                params.height = (int)(params.width/ratioPhone);
            }else{
                params.height = (int)(phoneSize.y*0.8);
                params.width = (int)(params.height * ratioPhone);
            }

            photoView.setLayoutParams(params);
        }else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            findViewById(R.id.scrollMain).setVisibility(View.VISIBLE);
            openCamera.setVisibility(View.VISIBLE);
            openGallery.setVisibility(View.VISIBLE);
            buttonSave.setVisibility(View.VISIBLE);

            ViewGroup.LayoutParams params = photoView.getLayoutParams();
            params.width = pictureWidth;
            params.height = pictureHeight;
            photoView.setLayoutParams(params);
        }
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
                    usedImage = new BitmapHandler(MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri), photoView);
                    usedImage.setAsImageView();
                } catch (IOException e) {
                    Log.i("Errors", "IOException while trying to load file from gallery");
                    e.printStackTrace();
                }
            }
            if(requestCode == REQUEST_TAKE_PHOTO){
                try {
                    usedImage = new BitmapHandler(MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI), photoView);
                    usedImage.setAsImageView();
                }catch (IOException e){
                Log.i("Errors", "IOException while trying to load the picture taken in camera");
                e.printStackTrace();
            }
        }
    }
}
}
