package cs.digital_watch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ClockFragment extends Fragment {

    private TextClock textClock;
    private TextView textDate;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clock, container, false);

        textClock = view.findViewById(R.id.textClock);
        textDate = view.findViewById(R.id.textDate);
        sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateClockSettings();
    }

    private void updateClockSettings() {
        String tzId = sharedPreferences.getString("selected_timezone", "Local");
        boolean use24h = sharedPreferences.getBoolean("use_24h", false);

        // Set TimeZone
        if (tzId.equals("Local")) {
            textClock.setTimeZone(TimeZone.getDefault().getID());
        } else {
            textClock.setTimeZone(tzId);
        }

        // Set Format
        if (use24h) {
            textClock.setFormat12Hour(null);
            textClock.setFormat24Hour("HH:mm:ss");
        } else {
            textClock.setFormat12Hour("hh:mm:ss a");
            textClock.setFormat24Hour(null);
        }

        updateDate(tzId);
    }

    private void updateDate(String tzId) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
        if (!tzId.equals("Local")) {
            sdf.setTimeZone(TimeZone.getTimeZone(tzId));
        } else {
            sdf.setTimeZone(TimeZone.getDefault());
        }
        String currentDate = sdf.format(new Date());
        textDate.setText(currentDate);
    }
}