package com.example.fitfeed;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.fitfeed.fragments.HomeFragment;
import com.example.fitfeed.fragments.SocialFragment;
import com.example.fitfeed.fragments.WorkoutsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView titleBarTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        titleBarTextView = findViewById(R.id.mainToolbarTitle);

        setTitleBarText(bottomNavigationView.getSelectedItemId());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer, new HomeFragment()).commit();
        }
        bottomNavigationView.setSelectedItemId(R.id.nav_bar_home);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);

        Log.d("MainActivity.onCreate", String.format("Fragments: %d", getSupportFragmentManager().getFragments().size()));
    }

    // State saving for selected menu item.
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        outState.putInt("bottomNavSelectedItemId", bottomNavigationView.getSelectedItemId());
        super.onSaveInstanceState(outState, outPersistentState);
    }

    // State restoring for menu item dependent on what was selected.
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        int itemId = savedInstanceState.getInt("bottomNavSelectedItem");
        bottomNavigationView.setSelectedItemId(itemId);
        setTitleBarText(bottomNavigationView.getSelectedItemId());

        Fragment fragment = getSupportFragmentManager().findFragmentById(bottomNavigationView.getSelectedItemId());

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer, fragment).commit();
        }
    }

    /**
     * Used as OnItemSelectedListener for bottom navigation bar.
     * @param item the nav bar item that was selected
     * @return true on success
     */
    private boolean onNavigationItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        Fragment fragment = getSupportFragmentManager().findFragmentById(bottomNavigationView.getSelectedItemId());

        Log.d("MainActivity.onNavigationItemSelected", String.format("Fragments: %d", getSupportFragmentManager().getFragments().size()));

        if (fragment == null) {
            if (itemId == R.id.nav_bar_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.nav_bar_workouts) {
                fragment = new WorkoutsFragment();
            } else if (itemId == R.id.nav_bar_social) {
                fragment = new SocialFragment();
            }
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer, fragment).commit();
        setTitleBarText(item.getItemId());

        return true;
    }

    /**
     * Helper to set the title bar text when switching tabs.
     * @param itemId menu item id
     */
    private void setTitleBarText(int itemId) {
        int titleId = 0;

        if (itemId == R.id.nav_bar_home) {
            titleId = R.string.home_nav_bar_title;
        } else if (itemId == R.id.nav_bar_workouts) {
            titleId = R.string.workouts_nav_bar_title;
        } else if (itemId == R.id.nav_bar_social) {
            titleId = R.string.social_nav_bar_title;
        }

        titleBarTextView.setText(titleId);
    }
}