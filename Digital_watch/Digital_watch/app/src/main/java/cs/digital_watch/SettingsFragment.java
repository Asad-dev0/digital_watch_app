package cs.digital_watch;

import android.os.Bundle;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import java.util.TimeZone;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        ListPreference timezonePreference = findPreference("timezone");
        if (timezonePreference != null) {
            String[] timezoneIds = TimeZone.getAvailableIDs();
            timezonePreference.setEntries(timezoneIds);
            timezonePreference.setEntryValues(timezoneIds);
        }
    }
}