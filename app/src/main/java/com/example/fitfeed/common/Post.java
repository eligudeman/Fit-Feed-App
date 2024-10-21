package com.example.fitfeed.common;

import android.graphics.drawable.Drawable;

import java.io.File;
import java.util.UUID;

public class Post {
    private String postText;
    private String postUser;
    //private UUID postUserID;
    private Drawable postDrawable;

    public Post(String postText, String postUser) {
        this(postText, postUser, null);
    }

    public Post(String postText, String postUser, Drawable postDrawable) {
        this.postText = postText;
        this.postUser = postUser;
        this.postDrawable = postDrawable;
    }

    public Drawable getPostDrawable() {
        return postDrawable;
    }

    public String getPostText() {
        return postText;
    }

    public String getPostUser() {
        return postUser;
    }

    public void setPostDrawable(Drawable postDrawable) {
        if (postDrawable != null) {
            this.postDrawable = postDrawable;
        }
        // todo throw error?
    }

    public void setPostDrawableFromFile(File file) {
        Drawable drawable = Drawable.createFromPath(file.getAbsolutePath());
        this.setPostDrawable(drawable);
    }

    public void setPostDrawableFromFile(String filename) {
        Drawable drawable = Drawable.createFromPath(filename);
        this.setPostDrawable(drawable);
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public void setPostUser(String postUser) {
        this.postUser = postUser;
    }
}
