package com.example.fitfeed;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    private FriendsSearchRecyclerViewAdapter friendsSearchRecyclerViewAdapter;
    private EditText searchEditText;
    private Button searchButton;
    private ArrayList<String> postText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_friends);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the search input and button
        searchEditText = findViewById(R.id.editTextSearch);
        searchButton = findViewById(R.id.buttonFriendsSearch);

        // Initialize the RecyclerView and adapter
        RecyclerView recyclerView = findViewById(R.id.recyclerViewFriendsSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postText = new ArrayList<>();
        postText.add("Username1");
        postText.add("Username2");
        postText.add("Username3");

        friendsSearchRecyclerViewAdapter = new FriendsSearchRecyclerViewAdapter(this, postText);
        recyclerView.setAdapter(friendsSearchRecyclerViewAdapter);

        // Set the search button click listener
        searchButton.setOnClickListener(this::performSearch);
    }

    private void performSearch(View view) {
        String query = searchEditText.getText().toString().trim();

        if (query.isEmpty()) {
            // Show an error if the search query is empty
            searchEditText.setError("Search cannot be empty");
            Toast.makeText(this, "Please enter a search query", Toast.LENGTH_SHORT).show();
        } else {
            // Perform the search and update the RecyclerView
            ArrayList<String> filteredResults = filterSearchResults(query);
            friendsSearchRecyclerViewAdapter.updateData(filteredResults);
        }
    }

    // Mock search logic to filter results (replace with actual search logic)
    private ArrayList<String> filterSearchResults(String query) {
        ArrayList<String> results = new ArrayList<>();
        for (String user : postText) {
            if (user.toLowerCase().contains(query.toLowerCase())) {
                results.add(user);
            }
        }
        if (results.isEmpty()) {
            results.add("No results found for \"" + query + "\"");
        }
        return results;
    }
}
