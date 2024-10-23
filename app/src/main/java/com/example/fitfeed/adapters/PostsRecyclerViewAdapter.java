package com.example.fitfeed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitfeed.R;
import com.example.fitfeed.common.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerViewAdapter for Social tab's feed.
 */
public class PostsRecyclerViewAdapter extends RecyclerView.Adapter<PostsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Post> posts;
    private final LayoutInflater inflater;

    public PostsRecyclerViewAdapter(Context context, List<Post> posts) {
        this.inflater = LayoutInflater.from(context);
        this.posts = (ArrayList<Post>) posts;
    }

    public void restorePostsState(List<Post> posts) {
        int oldSize = this.posts.size();
        this.posts = new ArrayList<>();
        notifyItemRangeRemoved(0, oldSize);
        this.posts.addAll(posts);
        notifyItemRangeInserted(0, this.posts.size());
    }

    public void addPosts(List<Post> posts) {
        this.posts.addAll(0, posts);
        notifyItemRangeInserted(0, posts.size());
    }

    public void addPost(Post post) {
        this.posts.add(0, post);
        notifyItemInserted(0);
    }

    public ArrayList<Post> getPosts() {
        return this.posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_row_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // set text and drawable for each post
        holder.textView.setText(posts.get(position).getPostText());
        holder.imageView.setImageDrawable(posts.get(position).getPostDrawable());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.postTextView);
            imageView = itemView.findViewById(R.id.postImageView);
        }
    }
}