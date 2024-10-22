package com.example.fitfeed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        rememberMeCheckBox = findViewById(R.id.checkBox);
        loginButton = findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener(this::attemptLogin);
    }

    private void attemptLogin(View view) {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            // Display error messages on empty fields
            if (username.isEmpty()) {
                usernameEditText.setError("Username is required");
            }
            if (password.isEmpty()) {
                passwordEditText.setError("Password is required");
            }
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
        } else {
            goToHome(view);
        }
    }

    public void goToHome(View view) {
        if (rememberMeCheckBox.isChecked()) {
            skipLogin();
        } else {
            // Proceed to home even without remember me, or handle as needed
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void skipLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
