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

        // find take picture button and set listener
        Button takePictureButton = findViewById(R.id.cameraActivityTakePictureButton);
        takePictureButton.setOnClickListener(this::openCamera);

        // find image view
        imageView = findViewById(R.id.cameraActivityImageView);

        // handle image file creation if needed
        if (imageFile == null) {
            // check for file system permissions and handle error gracefully
            try {
                imageFile = getImageFile(FILENAME);
                fileError = false;
            } catch (IOException e) {
                Toast.makeText(this, getString(R.string.camera_file_error), Toast.LENGTH_SHORT).show();
                fileError = true;
            }
        }
    }


    private File getImageFile(String filename) throws IOException {
        return File.createTempFile(filename, ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
    }


    /**
     * On click listener for Take Picture button.
     * @param view context of click event.
     */
    public void openCamera(View view) {
        // don't attempt to take picture if file permissions arent granted
        if (!fileError) {
            // intent targets system camera application
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // use file provider to allow for higher resolution images
            Uri fileProvider = FileProvider.getUriForFile(this, "com.example.fitfeed.fileprovider", imageFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

            // handle errors opening camera (no permissions, no camera)
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
        // called after returning from camera application, check request and result codes for success
        if (requestCode == REQUEST_CODE && resultCode == CameraActivity.RESULT_OK) {
            Bitmap image = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            imageView.setImageBitmap(image);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // save file and bitmap to bundle
        outState.putString("cameraActivityCachedImageFilename", imageFile.getAbsolutePath());
        outState.putParcelable("cameraActivityCachedImage", BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // reload file from bundle
        String restoredFilename = savedInstanceState.getString("cameraActivityCachedImageFilename");
        if (restoredFilename != null) {
            File restoredFile = new File(restoredFilename);
            if (restoredFile.exists()) {
                imageFile = new File(restoredFilename);
            }
        }

        // reload bitmap from bundle and set image
        Bitmap imageBitmap = savedInstanceState.getParcelable("cameraActivityCachedImage");
        if (imageBitmap != null) {
            imageView.setImageBitmap(imageBitmap);
        }
    }
}