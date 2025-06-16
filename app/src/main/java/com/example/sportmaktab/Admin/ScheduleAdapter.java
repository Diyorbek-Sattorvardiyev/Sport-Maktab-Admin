package com.example.sportmaktab.Admin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.sportmaktab.R;
import com.google.android.material.chip.Chip;

import java.util.List;

public class ScheduleAdapter extends ArrayAdapter<TrainingSchedule> {

    private List<TrainingSchedule> schedules;

    public ScheduleAdapter(Context context, List<TrainingSchedule> schedules) {
        super(context, 0, schedules);
        this.schedules = schedules;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_schedule, parent, false);
        }

        TrainingSchedule schedule = getItem(position);
        Log.d("ScheduleAdapter", "Date: " + schedule.getDate());
        Log.d("ScheduleAdapter", "Time: " + schedule.getTime());
        Log.d("ScheduleAdapter", "Room: " + schedule.getRoom());
        Log.d("ScheduleAdapter", "Sport Name: " + schedule.getSportName());
        Log.d("ScheduleAdapter", "Coach Name: " + schedule.getCoachName());

        TextView tvDate = convertView.findViewById(R.id.tvDay);
        TextView tvTime = convertView.findViewById(R.id.tvTime);
        TextView tvRoom = convertView.findViewById(R.id.tvRoom);
        Chip tvSportType = convertView.findViewById(R.id.chipSportType);
        TextView tvCoach = convertView.findViewById(R.id.tvCoach);

        tvDate.setText(schedule.getDate());
        tvTime.setText(schedule.getTime());
        tvRoom.setText(schedule.getRoom());
        tvSportType.setText(schedule.getSportName());
        tvCoach.setText(schedule.getCoachName());

        return convertView;
    }
}
