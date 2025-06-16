package com.example.sportmaktab.Admin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.sportmaktab.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultViewHolder> {

    private Context context;
    private List<Result> resultsList;
    private OnItemClickListener listener;
    private String baseUrl = "http://172.20.2.196:5000/uploads/"; // Serverdagi uploads/ yo‘li

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ResultsAdapter(Context context, List<Result> resultsList) {
        this.context = context;
        this.resultsList = resultsList;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        return new ResultViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        Result result = resultsList.get(position);
        holder.diskribtionResult.setText(result.getDescription());
        holder.tvCompetitionName.setText(result.getCompetitionName());
        holder.tvDate.setText(result.getDate());

        // Load image using Glide
        String imagePath = result.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            String fullImageUrl = baseUrl + imagePath.replace("\\", "/");
            Log.d("ResultImageURL", fullImageUrl);

            Glide.with(context)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.drawable)
                    .error(R.drawable.error_image)
                    .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            Log.e("GlideError", "Result rasm yuklanmadi: " + (e != null ? e.getMessage() : "Noma’lum xato"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            Log.d("GlideSuccess", "Result rasm yuklandi: " + fullImageUrl);
                            return false;
                        }
                    })
                    .into(holder.ivResultImage);
        } else {
            holder.ivResultImage.setImageResource(R.drawable.drawable);
        }
    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView tvCompetitionName;
        TextView tvDate;
        ImageView ivResultImage;
        MaterialButton ibEdit;
        MaterialButton ibDelete;
        Chip diskribtionResult;

        public ResultViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tvCompetitionName = itemView.findViewById(R.id.tvCompetitionTitle);
            tvDate = itemView.findViewById(R.id.tvCompetitionDate);
            ivResultImage = itemView.findViewById(R.id.ivCompetitionImage);
            ibEdit = itemView.findViewById(R.id.btnEditCompetition);
            ibDelete = itemView.findViewById(R.id.btnDeleteCompetition);
            diskribtionResult = itemView.findViewById(R.id.chipCompetitionRank);

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

            ibEdit.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(position);
                    }
                }
            });

            ibDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                    }
                }
            });
        }
    }
}