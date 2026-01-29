package cs.digital_watch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class StopwatchFragment extends Fragment {

    private TextView tvStopwatch;
    private ExtendedFloatingActionButton btnStartPause;
    private MaterialButton btnLap, btnReset;
    private RecyclerView rvLaps;

    private Handler handler = new Handler(Looper.getMainLooper());
    private long startTime = 0L, timeInMilliseconds = 0L, timeSwapBuff = 0L, updateTime = 0L;
    private boolean isRunning = false;

    private List<String> lapList = new ArrayList<>();
    private LapAdapter lapAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);

        tvStopwatch = view.findViewById(R.id.tvStopwatch);
        btnStartPause = view.findViewById(R.id.btnStartPause);
        btnLap = view.findViewById(R.id.btnLap);
        btnReset = view.findViewById(R.id.btnReset);
        rvLaps = view.findViewById(R.id.rvLaps);

        lapAdapter = new LapAdapter(lapList);
        rvLaps.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLaps.setAdapter(lapAdapter);

        btnStartPause.setOnClickListener(v -> {
            if (!isRunning) {
                startTime = SystemClock.uptimeMillis();
                handler.postDelayed(updateTimerThread, 0);
                isRunning = true;
                btnStartPause.setText("Pause");
                btnStartPause.setIconResource(android.R.drawable.ic_media_pause);
            } else {
                timeSwapBuff += timeInMilliseconds;
                handler.removeCallbacks(updateTimerThread);
                isRunning = false;
                btnStartPause.setText("Start");
                btnStartPause.setIconResource(android.R.drawable.ic_media_play);
            }
        });

        btnReset.setOnClickListener(v -> {
            startTime = 0L;
            timeInMilliseconds = 0L;
            timeSwapBuff = 0L;
            updateTime = 0L;
            tvStopwatch.setText("00:00:00");
            lapList.clear();
            lapAdapter.notifyDataSetChanged();
            if (isRunning) {
                handler.removeCallbacks(updateTimerThread);
                isRunning = false;
                btnStartPause.setText("Start");
                btnStartPause.setIconResource(android.R.drawable.ic_media_play);
            }
        });

        btnLap.setOnClickListener(v -> {
            if (isRunning) {
                lapList.add(0, tvStopwatch.getText().toString());
                lapAdapter.notifyDataSetChanged();
                rvLaps.scrollToPosition(0);
            }
        });

        return view;
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updateTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            tvStopwatch.setText(String.format("%02d:%02d:%02d", mins / 60, mins % 60, secs));
            handler.postDelayed(this, 100);
        }
    };

    private static class LapAdapter extends RecyclerView.Adapter<LapAdapter.ViewHolder> {
        private List<String> laps;

        LapAdapter(List<String> laps) { this.laps = laps; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lap, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvLapNumber.setText(String.format(holder.itemView.getContext().getString(R.string.lap_count), laps.size() - position));
            holder.tvLapTime.setText(laps.get(position));
        }

        @Override
        public int getItemCount() { return laps.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvLapNumber, tvLapTime;
            ViewHolder(View v) {
                super(v);
                tvLapNumber = v.findViewById(R.id.tvLapNumber);
                tvLapTime = v.findViewById(R.id.tvLapTime);
            }
        }
    }
}