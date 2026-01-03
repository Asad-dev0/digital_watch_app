package cs.digital_watch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class AlarmAlertActivity extends AppCompatActivity {

    private Ringtone ringtone;
    private String alarmTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Make activity show over lock screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        
        setContentView(R.layout.activity_alarm_alert);

        TextView tvTime = findViewById(R.id.tvAlarmTime);
        Button btnSnooze = findViewById(R.id.btnSnooze);
        Button btnStop = findViewById(R.id.btnStop);

        alarmTime = getIntent().getStringExtra("ALARM_TIME");
        if (alarmTime != null) {
            tvTime.setText(alarmTime);
        }

        // Start ringing
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (notification == null) {
            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
        ringtone.play();

        btnStop.setOnClickListener(v -> {
            stopAlarm();
        });

        btnSnooze.setOnClickListener(v -> {
            snoozeAlarm();
        });
    }

    private void stopAlarm() {
        if (ringtone != null) ringtone.stop();
        finish();
    }

    private void snoozeAlarm() {
        if (ringtone != null) ringtone.stop();

        // Schedule snooze for 5 minutes later
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("ALARM_TIME", alarmTime + " (Snoozed)");
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 999, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5); // 5 minute snooze

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }
}
