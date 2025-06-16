package com.example.sportmaktab.Admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportmaktab.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.util.List;

public class CoachAdapter extends RecyclerView.Adapter<CoachAdapter.CoachViewHolder> {
    private  final String TAG = "CoachAdapter";
    private final List<Coach> coachList;
    private final Context context;

    public CoachAdapter(List<Coach> coachList, Context context) {
        this.coachList = coachList;
        this.context = context;
    }

    @NonNull
    @Override
    public CoachViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_coach, parent, false);
        return new CoachViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CoachViewHolder holder, int position) {
       Coach coach = coachList.get(position);
        holder.textViewName.setText(coach.getLastName()+" "+coach.getFirstName());
        holder.textViewPhone.setText(coach.getPhone());
        holder.textViewSportType.setText(coach.getLogin());

//        // Set click listener for edit
//        holder.cardView.setOnClickListener(v -> {
//            Intent intent = new Intent(context, EditCoachActivity.class);
//            intent.putExtra("COACH_ID", coach.getId());
//            intent.putExtra("COACH_NAME", coach.getFullName());
//            intent.putExtra("COACH_PHONE", coach.getPhone());
//            intent.putExtra("COACH_SPORT_TYPE_ID", coach.getSportTypeId());
//            context.startActivity(intent);
//        });
    }

    @Override
    public int getItemCount() {
        return coachList.size();
    }

    static class CoachViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewPhone;
        MaterialCardView cardView;
        MaterialButton imageViewEdit;
        Chip textViewSportType;

        public CoachViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewCoachName);
            textViewPhone = itemView.findViewById(R.id.textViewCoachPhone);
            textViewSportType = itemView.findViewById(R.id.chipCoachSportType);
            cardView = itemView.findViewById(R.id.cardViewCoach);
            imageViewEdit = itemView.findViewById(R.id.btnEditCoach);
        }
    }
}