package com.example.fitfeed.model;

import android.graphics.drawable.Drawable;
import android.net.Uri;

public class PostItem {
    private String text;
    private Drawable image;
    private Uri videoUri;

    public PostItem(String text, Drawable image, Uri videoUri) {
        this.text = text;
        this.image = image;
        this.videoUri = videoUri;
    }

    // Getters and Setters
    public String getText() { return text; }
    public Drawable getImage() { return image; }
    public Uri getVideoUri() { return videoUri; }
}
