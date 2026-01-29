package cs.digital_watch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import java.util.TimeZone;

public class ClockFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private TextClock localTimeText;
    private TextClock localDateText;
    private TextView localTimezoneLabel;

    private TextClock selectedTimeText;
    private TextClock selectedDateText;
    private TextView selectedTimezoneLabel;

    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clock, container, false);

        // Local Clock
        localTimeText = view.findViewById(R.id.local_time_text);
        localDateText = view.findViewById(R.id.local_date_text);
        localTimezoneLabel = view.findViewById(R.id.local_timezone_label);

        // Selected Clock
        selectedTimeText = view.findViewById(R.id.selected_time_text);
        selectedDateText = view.findViewById(R.id.selected_date_text);
        selectedTimezoneLabel = view.findViewById(R.id.selected_timezone_label);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        updateClocks();
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ("timezone".equals(key)) {
            updateClocks();
        }
    }

    private void updateClocks() {
        // --- Update First Clock (Local Time) ---
        TimeZone localTimeZone = TimeZone.getDefault();
        String localTimezoneId = localTimeZone.getID();
        localTimeText.setTimeZone(localTimezoneId);
        localDateText.setTimeZone(localTimezoneId);
        localTimezoneLabel.setText(localTimeZone.getDisplayName());

        // --- Update Second Clock (Selected Time) ---
        String selectedTimezoneId = sharedPreferences.getString("timezone", "America/New_York");
        selectedTimeText.setTimeZone(selectedTimezoneId);
        selectedDateText.setTimeZone(selectedTimezoneId);
        selectedTimezoneLabel.setText(getTimezoneDisplayName(selectedTimezoneId));
    }
    
    private String getTimezoneDisplayName(String timezoneId) {
        TimeZone tz = TimeZone.getTimeZone(timezoneId);
        return tz.getDisplayName();
    }
}
