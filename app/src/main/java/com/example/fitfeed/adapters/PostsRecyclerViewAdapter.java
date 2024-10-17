package com.example.fitfeed.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fitfeed.R;

import java.util.List;

/**
 * RecyclerViewAdapter for Social tab's feed.
 */
public class PostsRecyclerViewAdapter extends RecyclerView.Adapter<PostsRecyclerViewAdapter.ViewHolder> {

    private List<String> data;
    private List<Drawable> drawables;
    private LayoutInflater inflater;

    public PostsRecyclerViewAdapter(Context context, List<String> data, List<Drawable> drawables) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
        this.drawables = drawables;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_row_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // set text and drawable for each post
        holder.textView.setText(data.get(position));
        holder.imageView.setImageDrawable(drawables.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.postTextView);
            imageView = itemView.findViewById(R.id.postImageView);
        }
    }
}
