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

import java.util.List;

public class SportTypesAdapter extends RecyclerView.Adapter<SportTypesAdapter.SportTypeViewHolder> {

    private Context context;
    private List<SportType> sportTypesList;
    private OnItemClickListener listener;
    private String baseUrl = "http://172.20.2.196:5000/uploads/"; // Serverdagi uploads/ yo‘li

    public interface OnItemClickListener {
        void onItemClick(SportType sportType);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SportTypesAdapter(Context context, List<SportType> sportTypesList) {
        this.context = context;
        this.sportTypesList = sportTypesList;
    }

    @NonNull
    @Override
    public SportTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sport_type, parent, false);
        return new SportTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SportTypeViewHolder holder, int position) {
        SportType sportType = sportTypesList.get(position);

        holder.tvSportName.setText(sportType.getName());

        // Agar tavsif mavjud bo'lsa
        if (sportType.getDescription() != null && !sportType.getDescription().isEmpty()) {
            holder.tvSportDescription.setText(sportType.getDescription());
            holder.tvSportDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvSportDescription.setVisibility(View.GONE);
        }

        // Rasmni yuklash
        String imagePath = sportType.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            String fullImageUrl = baseUrl + imagePath.replace("\\", "/");
            Log.d("SportTypeImageURL", fullImageUrl);

            Glide.with(context)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.drawable)
                    .error(R.drawable.error_image)
                    .centerCrop()
                    .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            Log.e("GlideError", "SportType rasm yuklanmadi: " + (e != null ? e.getMessage() : "Noma’lum xato"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            Log.d("GlideSuccess", "SportType rasm yuklandi: " + fullImageUrl);
                            return false;
                        }
                    })
                    .into(holder.ivSportImage);
        } else {
            holder.ivSportImage.setImageResource(R.drawable.drawable);
        }

        // Element bosilganda
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(sportType);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sportTypesList.size();
    }

    public class SportTypeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSportImage;
        TextView tvSportName;
        TextView tvSportDescription;

        public SportTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSportImage = itemView.findViewById(R.id.ivSportImage);
            tvSportName = itemView.findViewById(R.id.tvSportName);
            tvSportDescription = itemView.findViewById(R.id.tvSportDescription);
        }
    }
}