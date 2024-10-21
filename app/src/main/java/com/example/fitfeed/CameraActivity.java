package com.example.fitfeed;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 23;
    private final String FILENAME = "photo";
    private File imageFile;
    private ImageView imageView;
    private boolean fileError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camera);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainCameraContainer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button takePictureButton = findViewById(R.id.cameraActivityTakePictureButton);
        takePictureButton.setOnClickListener(this::openCamera);

        imageView = findViewById(R.id.cameraActivityImageView);
        if (imageFile == null) {
            try {
                imageFile = getImageFile(FILENAME);
                fileError = false;
            } catch (IOException e) {
                Toast.makeText(this, getString(R.string.camera_file_error), Toast.LENGTH_SHORT).show();
                fileError = true;
            }
        } else {
            Bitmap image = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            imageView.setImageBitmap(image);
        }
    }

    private File getImageFile(String filename) throws IOException {
        return File.createTempFile(filename, ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
    }

    public void openCamera(View view) {
        if (!fileError) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            Uri fileProvider = FileProvider.getUriForFile(this, "com.example.fitfeed.fileprovider", imageFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(cameraIntent, REQUEST_CODE);
            } else {
                Toast.makeText(this, getString(R.string.camera_error), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.camera_file_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == CameraActivity.RESULT_OK) {
            Bitmap image = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            imageView.setImageBitmap(image);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("cameraActivityCachedImageFilename", imageFile.getAbsolutePath());
        outState.putParcelable("cameraActivityCachedImage", BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String restoredFilename = savedInstanceState.getString("cameraActivityCachedImageFilename");
        if (restoredFilename != null) {
            File restoredFile = new File(restoredFilename);
            if (restoredFile.exists()) {
                imageFile = new File(restoredFilename);
            }
        }

        Bitmap imageBitmap = savedInstanceState.getParcelable("cameraActivityCachedImage");
        if (imageBitmap != null) {
            imageView.setImageBitmap(imageBitmap);
        }
    }
}