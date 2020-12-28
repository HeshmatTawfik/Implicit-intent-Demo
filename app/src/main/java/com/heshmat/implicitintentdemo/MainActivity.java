package com.heshmat.implicitintentdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    final static int REQUEST_PERMISSIONS = 3;
    final static int OPEN_CAMERA_REQUEST_CODE = 4;
    final static int OPEN_GALLERY_REQUEST_CODE = 5;
    ImageView imageView;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS && grantResults.length > 2) {

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean arePermissionsGranted() {

        boolean areGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) + ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (!areGranted) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
            return false;

        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
        if (!arePermissionsGranted()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);

        }
    }

    File photoFile;

    public void openCamera(View view) {
        if (arePermissionsGranted()) {

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoFile = createImageFile();
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, OPEN_CAMERA_REQUEST_CODE);
            }
        }
    }

    public void openGallery(View view) { // use
        if (arePermissionsGranted()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            startActivityForResult(Intent.createChooser(intent, "Choose Image"), OPEN_GALLERY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case OPEN_CAMERA_REQUEST_CODE:
                    imageView.setImageURI(Uri.parse("file:" + photoFile.getAbsolutePath()));
                    break;
                case OPEN_GALLERY_REQUEST_CODE:
                    if (data != null)
                        imageView.setImageURI(data.getData());
                    break;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public File createImageFile() {
        // Create a unique image file name using date
        String dateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + dateTime + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
            return image;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
