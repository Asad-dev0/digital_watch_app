package cs.digital_watch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import java.util.Arrays;
import java.util.TimeZone;

public class SettingsFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AppPrefs";
    private TextView textSelectedTimeZone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        MaterialSwitch switch24Hour = view.findViewById(R.id.switch24Hour);
        MaterialSwitch switchVibrate = view.findViewById(R.id.switchVibrate);
        MaterialButton btnResetAlarms = view.findViewById(R.id.btnResetAlarms);
        MaterialButton btnSelectTimeZone = view.findViewById(R.id.btnSelectTimeZone);
        textSelectedTimeZone = view.findViewById(R.id.textSelectedTimeZone);

        // Load saved states
        switch24Hour.setChecked(sharedPreferences.getBoolean("use_24h", false));
        switchVibrate.setChecked(sharedPreferences.getBoolean("vibrate_enabled", true));
        
        String savedTz = sharedPreferences.getString("selected_timezone", "Local");
        textSelectedTimeZone.setText("Selected Country: " + savedTz);

        // 24 Hour Toggle Logic
        switch24Hour.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("use_24h", isChecked).apply();
            String msg = isChecked ? "24-Hour format enabled" : "12-Hour format enabled";
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });

        // TimeZone Selection Logic
        btnSelectTimeZone.setOnClickListener(v -> showTimeZoneDialog());

        // Vibrate Toggle Logic
        switchVibrate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("vibrate_enabled", isChecked).apply();
        });

        // Reset Alarms Logic
        btnResetAlarms.setOnClickListener(v -> {
            SharedPreferences alarmPrefs = requireContext().getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE);
            alarmPrefs.edit().clear().apply();
            Toast.makeText(getContext(), "All alarms have been deleted", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void showTimeZoneDialog() {
        String[] ids = TimeZone.getAvailableIDs();
        Arrays.sort(ids);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Time Zone / Country");
        builder.setItems(ids, (dialog, which) -> {
            String selectedId = ids[which];
            sharedPreferences.edit().putString("selected_timezone", selectedId).apply();
            textSelectedTimeZone.setText("Selected Country: " + selectedId);
            Toast.makeText(getContext(), "Time zone updated to " + selectedId, Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Reset to Local", (dialog, which) -> {
            sharedPreferences.edit().putString("selected_timezone", "Local").apply();
            textSelectedTimeZone.setText("Selected Country: Local");
        });
        builder.show();
    }
}