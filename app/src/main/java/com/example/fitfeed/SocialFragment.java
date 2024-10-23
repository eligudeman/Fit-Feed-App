package com.example.fitfeed;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SocialFragment extends Fragment {

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int FALLBACK_REQUEST_CODE = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private String currentPhotoPath;

    private List<String> postText;
    private List<Drawable> postDrawable;

    private PostsRecyclerViewAdapter postsRecyclerViewAdapter;
    private FloatingActionButton addFriendsButton;
    private FloatingActionButton cameraButton;

    public SocialFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_social, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the buttons
        addFriendsButton = getView().findViewById(R.id.addFriendsButton);
        cameraButton = getView().findViewById(R.id.cameraButton);

        // Request permissions if needed
        if (!hasCameraPermissions()) {
            requestPermissions(new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_CAMERA_PERMISSION);
        }

        // Set button listeners
        addFriendsButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), FriendsActivity.class);
            startActivity(intent);
        });

        cameraButton.setOnClickListener(v -> {
            if (hasCameraPermissions()) {
                openCamera();
            } else {
                requestPermissions(new String[]{
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_CAMERA_PERMISSION);
            }
        });

        // Initialize post data
        postText = new ArrayList<>();
        postText.add("Your friend just hit a PB in a set!");
        postText.add("You became friends with Josh!");
        postText.add("Josh shared his new workout plan with you");

        postDrawable = new ArrayList<>();
        postDrawable.add(getResources().getDrawable(R.drawable.placeholder1, null));
        postDrawable.add(getResources().getDrawable(R.drawable.placeholder2, null));
        postDrawable.add(getResources().getDrawable(R.drawable.placeholder3, null));

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        postsRecyclerViewAdapter = new PostsRecyclerViewAdapter(view.getContext(), postText, postDrawable);
        recyclerView.setAdapter(postsRecyclerViewAdapter);
    }

    private boolean hasCameraPermissions() {
        return requireContext().checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                requireContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("SocialFragment", "Error creating image file", ex);
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.fitfeed.fileprovider",
                        photoFile
                );
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        } else {
            Log.i("SocialFragment", "Camera not available. Using fallback.");
            startFallbackCapture();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void startFallbackCapture() {
        Bitmap blackBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        blackBitmap.eraseColor(Color.BLACK);

        try {
            File fallbackFile = createImageFile();
            FileOutputStream fos = new FileOutputStream(fallbackFile);
            blackBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            currentPhotoPath = fallbackFile.getAbsolutePath();
            onFallbackCaptureSuccess();

        } catch (IOException e) {
            Log.e("SocialFragment", "Error saving fallback image", e);
        }
    }

    private void onFallbackCaptureSuccess() {
        Drawable newImage = Drawable.createFromPath(currentPhotoPath);
        postText.add(0, "Captured a black screen!");  // Add at the top of the list
        postDrawable.add(0, newImage);                // Add at the top of the list
        postsRecyclerViewAdapter.notifyDataSetChanged();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            displayCapturedImage();
        } else if (requestCode == FALLBACK_REQUEST_CODE) {
            onFallbackCaptureSuccess();
        }
    }

    private void displayCapturedImage() {
        Drawable newImage = Drawable.createFromPath(currentPhotoPath);
        if (newImage != null) {
            postText.add(0, "New photo added!");  // Add at the top of the list
            postDrawable.add(0, newImage);        // Add at the top of the list
            postsRecyclerViewAdapter.notifyDataSetChanged();
        } else {
            Log.e("SocialFragment", "Failed to load image from path: " + currentPhotoPath);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Log.e("SocialFragment", "Camera permission denied");
            }
        }
    }
}
