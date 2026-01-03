package cs.digital_watch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private List<Alarm> alarmList;
    private OnAlarmToggledListener onAlarmToggledListener;

    public interface OnAlarmToggledListener {
        void onAlarmToggled();
    }

    public void setOnAlarmToggledListener(OnAlarmToggledListener listener) {
        this.onAlarmToggledListener = listener;
    }

    public AlarmAdapter(List<Alarm> alarmList) {
        this.alarmList = alarmList;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = alarmList.get(position);
        
        int hour = alarm.getHour();
        int minute = alarm.getMinute();
        String amPm = (hour >= 12) ? "PM" : "AM";
        int hourDisplay = (hour > 12) ? hour - 12 : (hour == 0 ? 12 : hour);
        
        holder.tvAlarmTime.setText(String.format("%02d:%02d %s", hourDisplay, minute, amPm));
        holder.tvAlarmLabel.setText(alarm.getLabel());
        
        // Remove listener before setting checked state to avoid infinite loop or wrong triggers
        holder.switchAlarm.setOnCheckedChangeListener(null);
        holder.switchAlarm.setChecked(alarm.isEnabled());

        holder.switchAlarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            alarm.setEnabled(isChecked);
            if (onAlarmToggledListener != null) {
                onAlarmToggledListener.onAlarmToggled();
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView tvAlarmTime, tvAlarmLabel;
        SwitchCompat switchAlarm;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAlarmTime = itemView.findViewById(R.id.tvAlarmTime);
            tvAlarmLabel = itemView.findViewById(R.id.tvAlarmLabel);
            switchAlarm = itemView.findViewById(R.id.switchAlarm);
        }
    }
}