package com.example.fitfeed;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fitfeed.R;
import com.example.fitfeed.model.PostItem;

import java.util.List;

public class PostsRecyclerViewAdapter extends RecyclerView.Adapter<PostsRecyclerViewAdapter.ViewHolder> {

    private List<PostItem> postItems;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public PostsRecyclerViewAdapter(Context context, List<PostItem> postItems) {
        this.mInflater = LayoutInflater.from(context);
        this.postItems = postItems;
    }

    @Override
    public PostsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.post_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostsRecyclerViewAdapter.ViewHolder holder, int position) {
        PostItem item = postItems.get(position);
        holder.postText.setText(item.getText());

        if (item.getImage() != null) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.GONE);
            holder.imageView.setImageDrawable(item.getImage());
        } else if (item.getVideoUri() != null) {
            holder.imageView.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.VISIBLE);
            holder.videoView.setVideoURI(item.getVideoUri());
            holder.videoView.seekTo(1); // Show a preview frame
        } else {
            holder.imageView.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return postItems.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView postText;
        ImageView imageView;
        VideoView videoView;

        ViewHolder(View itemView) {
            super(itemView);
            postText = itemView.findViewById(R.id.postText);
            imageView = itemView.findViewById(R.id.postImage);
            videoView = itemView.findViewById(R.id.postVideo);
        }
    }
}