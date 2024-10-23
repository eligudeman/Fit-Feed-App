package com.example.fitfeed;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitfeed.PostsRecyclerViewAdapter;
import com.example.fitfeed.model.PostItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SocialFragment extends Fragment {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_VIDEO_CAPTURE = 2;
    private String currentMediaPath;

    private List<PostItem> postItems;
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

        // Request permissions if needed
        if (!hasCameraPermissions()) {
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            }, REQUEST_CAMERA_PERMISSION);
        }

        // Initialize the buttons
        addFriendsButton = getView().findViewById(R.id.addFriendsButton);
        cameraButton = getView().findViewById(R.id.cameraButton);

        // Request permissions if needed
        if (!hasCameraPermissions()) {
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            }, REQUEST_CAMERA_PERMISSION);
        }

        // Set button listeners
        addFriendsButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), FriendsActivity.class);
            startActivity(intent);
        });

        cameraButton.setOnClickListener(v -> {
            if (hasCameraPermissions()) {
                showMediaOptionDialog();
            } else {
                requestPermissions(new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                }, REQUEST_CAMERA_PERMISSION);
            }
        });

        // Initialize post data
        postItems = new ArrayList<>();
        postItems.add(new PostItem("Your friend just hit a PB in a set!",
                getResources().getDrawable(R.drawable.placeholder1, null), null));
        postItems.add(new PostItem("You became friends with Josh!",
                getResources().getDrawable(R.drawable.placeholder2, null), null));
        postItems.add(new PostItem("Josh shared his new workout plan with you",
                getResources().getDrawable(R.drawable.placeholder3, null), null));

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        postsRecyclerViewAdapter = new PostsRecyclerViewAdapter(view.getContext(), postItems);
        recyclerView.setAdapter(postsRecyclerViewAdapter);
    }

    private boolean hasCameraPermissions() {
        return requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                requireContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                requireContext().checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void showMediaOptionDialog() {
        String[] options = {"Take Photo", "Record Video"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose an option")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openCameraForImage();
                    } else if (which == 1) {
                        openCameraForVideo();
                    }
                });
        builder.create().show();
    }

    private void openCameraForImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createMediaFile(true);
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
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            Toast.makeText(requireContext(), "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCameraForVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (takeVideoIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File videoFile = null;
            try {
                videoFile = createMediaFile(false);
            } catch (IOException ex) {
                Log.e("SocialFragment", "Error creating video file", ex);
            }

            if (videoFile != null) {
                Uri videoURI = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.fitfeed.fileprovider",
                        videoFile
                );

                // Set the output file for the video
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);

                // Set the video quality (0 for low quality, 1 for high quality)
                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                // Ensure the intent is configured to include audio recording
                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60); // optional, limit to 60 seconds

                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
        } else {
            Toast.makeText(requireContext(), "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private File createMediaFile(boolean isImage) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = (isImage ? "IMG_" : "VID_") + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mediaFile = File.createTempFile(
                fileName,
                isImage ? ".jpg" : ".mp4",
                storageDir
        );
        currentMediaPath = mediaFile.getAbsolutePath();
        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Scan the file so it appears in gallery apps
        MediaScannerConnection.scanFile(
                requireContext(),
                new String[]{currentMediaPath},
                null,
                null
        );

        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                displayCapturedImage();
            } else if (requestCode == REQUEST_VIDEO_CAPTURE) {
                displayCapturedVideo();
            }
        }
    }

    private void displayCapturedImage() {
        Drawable newImage = Drawable.createFromPath(currentMediaPath);
        if (newImage != null) {
            postItems.add(0, new PostItem("New photo added!", newImage, null));
            postsRecyclerViewAdapter.notifyDataSetChanged();
        } else {
            Log.e("SocialFragment", "Failed to load image from path: " + currentMediaPath);
        }
    }

    private void displayCapturedVideo() {
        Uri videoUri = Uri.fromFile(new File(currentMediaPath));
        if (videoUri != null) {
            postItems.add(0, new PostItem("New video added!", null, videoUri));
            postsRecyclerViewAdapter.notifyDataSetChanged();
        } else {
            Log.e("SocialFragment", "Failed to load video from path: " + currentMediaPath);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (allPermissionsGranted(grantResults)) {
                showMediaOptionDialog();
            } else {
                Toast.makeText(requireContext(), "Permissions not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean allPermissionsGranted(@NonNull int[] grantResults) {
        for (int res : grantResults) {
            if (res != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }
}
