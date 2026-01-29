package cs.digital_watch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmFragment extends Fragment implements AlarmAdapter.OnAlarmDeleteListener {

    private RecyclerView recyclerView;
    private AlarmAdapter alarmAdapter;
    private List<Alarm> alarmList;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AlarmPrefs";
    private static final String ALARM_LIST_KEY = "alarmList";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerViewAlarms);
        FloatingActionButton fab = view.findViewById(R.id.fabAddAlarm);

        loadAlarms();

        alarmAdapter = new AlarmAdapter(alarmList);
        // Add toggle listener to save changes when user switches an alarm on/off
        alarmAdapter.setOnAlarmToggledListener(this::saveAlarmsAndSchedule);
        alarmAdapter.setOnAlarmDeleteListener(this);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(alarmAdapter);

        fab.setOnClickListener(v -> showTimePicker());

        return view;
    }

    @Override
    public void onAlarmDelete(int position) {
        if (alarmList != null && position >= 0 && position < alarmList.size()) {
            Alarm alarmToDelete = alarmList.get(position);
            
            // Cancel the alarm from AlarmManager
            cancelAlarm(alarmToDelete);
            
            // Remove from the list
            alarmList.remove(position);
            
            // Notify the adapter
            alarmAdapter.notifyItemRemoved(position);
            
            // Save the updated list to SharedPreferences
            saveAlarmsAndSchedule();
        }
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minuteOfHour) -> {
            Alarm newAlarm = new Alarm(hourOfDay, minuteOfHour, "Alarm", true);
            alarmList.add(newAlarm);
            alarmAdapter.notifyItemInserted(alarmList.size() - 1);
            saveAlarmsAndSchedule();
        }, hour, minute, false);

        timePickerDialog.show();
    }

    private void saveAlarmsAndSchedule() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alarmList);
        editor.putString(ALARM_LIST_KEY, json);
        editor.apply();

        // Schedule all enabled alarms
        for (Alarm alarm : alarmList) {
            if (alarm.isEnabled()) {
                scheduleAlarm(alarm);
            } else {
                cancelAlarm(alarm);
            }
        }
    }

    private void loadAlarms() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(ALARM_LIST_KEY, null);
        Type type = new TypeToken<ArrayList<Alarm>>() {}.getType();
        alarmList = gson.fromJson(json, type);

        if (alarmList == null) {
            alarmList = new ArrayList<>();
        }
    }

    private void scheduleAlarm(Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("ALARM_TIME", alarm.getTimeString());
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(), (int) alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());
        calendar.set(Calendar.SECOND, 0);

        // If time has already passed today, schedule for tomorrow
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private void cancelAlarm(Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(), (int) alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
