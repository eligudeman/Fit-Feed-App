package com.example.fitfeed.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.fitfeed.CameraActivity;
import com.example.fitfeed.FriendsActivity;
import com.example.fitfeed.R;
import com.example.fitfeed.adapters.PostsRecyclerViewAdapter;
import com.example.fitfeed.common.Post;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SocialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocialFragment extends Fragment {

    public static final String ARG_PARAM1 = "posts";
    public static final int SOCIAL_NEW_POST_REQUEST_CODE = 32;

    private LinearLayoutManager postsLayoutManager;
    private PostsRecyclerViewAdapter postsRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private ArrayList<Post> posts;

    public SocialFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param posts The list of posts to display in the feed.
     * @return A new instance of fragment SocialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SocialFragment newInstance(ArrayList<Post> posts) {
        SocialFragment fragment = new SocialFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, posts);
        fragment.setArguments(args);
        return fragment;
    }

    /** @noinspection deprecation*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ArrayList<Post> postsReceived = getArguments().getParcelableArrayList(ARG_PARAM1);
            //Log.d("SocialFragment.onCreate", String.format("Received %d posts", (postsReceived != null) ? postsReceived.size() : 0));
            posts = (postsReceived != null) ? postsReceived : new ArrayList<>();
        } else {
            if (posts == null) {
                posts = new ArrayList<>();
            }
        }
        setHasOptionsMenu(true);
    }

    /** @noinspection deprecation*/
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.social_menu, menu);
    }

    /** @noinspection deprecation*/
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.socialMenuAddFriends: {
                Intent intent = new Intent(getContext(), FriendsActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.socialMenuNewPost: {
                Intent intent = new Intent(getContext(), CameraActivity.class);
                intent.putExtra("post", new Post("", ""));
                startActivityForResult(intent, SOCIAL_NEW_POST_REQUEST_CODE);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** @noinspection deprecation*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // called after returning from camera activity, check request and result codes for success
        if (requestCode == SOCIAL_NEW_POST_REQUEST_CODE && resultCode == CameraActivity.RESULT_OK) {
            if (data != null) {
                Post newPost = data.getParcelableExtra("post");
                if (newPost != null) {
                    if (!newPost.getPostUser().isEmpty() || !newPost.getPostText().isEmpty() || newPost.getPostDrawable() != null) {
                        postsRecyclerViewAdapter.addPost(newPost);
                        posts = postsRecyclerViewAdapter.getPosts();
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO implement actual posts instead of placeholder values
        if (postsLayoutManager == null) {
            postsLayoutManager = new LinearLayoutManager(view.getContext());
        }

        if (postsRecyclerViewAdapter == null) {
            postsRecyclerViewAdapter = new PostsRecyclerViewAdapter(view.getContext(), posts);
        } else {
            if (postsRecyclerViewAdapter.getPosts().size() != posts.size()) {
                postsRecyclerViewAdapter.restorePostsState(posts);
            }
        }

        // set up the RecyclerView
        if (recyclerView == null) {
            recyclerView = view.findViewById(R.id.recyclerViewPosts);
            recyclerView.setLayoutManager(postsLayoutManager);
            recyclerView.setAdapter(postsRecyclerViewAdapter);
            recyclerView.setSaveEnabled(true);
        }
    }
}