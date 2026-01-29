package cs.digital_watch;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar topAppBar = findViewById(R.id.top_app_bar);
        setSupportActionBar(topAppBar);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Set the initial fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ClockFragment())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_clock) {
                selectedFragment = new ClockFragment();
            } else if (id == R.id.nav_stopwatch) {
                selectedFragment = new StopwatchFragment();
            } else if (id == R.id.nav_alarm) {
                selectedFragment = new AlarmFragment();
            } else if (id == R.id.nav_settings) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true; // Return true to show the item as selected
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Handle reselection of the same item
        bottomNav.setOnItemReselectedListener(item -> {
            // Do nothing to prevent recreating the fragment
        });
    }
}