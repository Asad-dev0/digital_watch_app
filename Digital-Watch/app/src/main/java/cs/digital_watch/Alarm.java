package cs.digital_watch;

import java.io.Serializable;

public class Alarm implements Serializable {
    private long id;
    private int hour;
    private int minute;
    private boolean isEnabled;
    private String label;

    public Alarm(int hour, int minute, String label, boolean isEnabled) {
        this.id = System.currentTimeMillis();
        this.hour = hour;
        this.minute = minute;
        this.label = label;
        this.isEnabled = isEnabled;
    }

    public long getId() {
        return id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTimeString() {
        return String.format("%02d:%02d", hour, minute);
    }
}
