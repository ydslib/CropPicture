package com.crystallake.clippicture;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.crystallake.clippicture.crop.CropImageActivity;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button takePhotoCrop;
    private Button selectPhotoCrop;
    private Uri photoURI;
    private ImageView cropImg;

    private static final String FILE_PROVIDER_NAME = ".image_provider";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePhotoCrop = findViewById(R.id.take_photo_crop);
        selectPhotoCrop = findViewById(R.id.select_photo_crop);
        cropImg = findViewById(R.id.crop_img);


        takePhotoCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        selectPhotoCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x001) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(this, CropImageActivity.class);
                intent.setData(photoURI);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(intent,0x002);
            }
        }else if (requestCode == 0x002){
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            if (data != null) {
                Uri cropPhotoUri = data.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),cropPhotoUri);
                    cropImg.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void takePhoto() {
        if (!hasCamera()) {
            return;
        }
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            File photoFile = createPhotoFile();
            photoURI = FileProvider.getUriForFile(this, getPackageName() + FILE_PROVIDER_NAME, photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(intent, 0x001);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean hasCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        return intent.resolveActivity(getPackageManager()) != null;
    }

    private File createPhotoFile() throws IOException {
        String imageFileName = "IMAGE_PICK_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

//    private fun createPhotoFile(context: Context): File {
//        val imageFileName = "IMAGE_PICK_" + System.currentTimeMillis()
//        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile(imageFileName, ".jpg", storageDir)
//    }

}