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

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {
    private List<Slider> sliderList;
    private Context context;
    private String baseUrl = "http://172.20.2.196:5000/uploads/";

    public SliderAdapter(List<Slider> sliderList, Context context) {
        this.sliderList = sliderList;
        this.context = context;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        Slider slider = sliderList.get(position);

        // Matn maydonlarini to‘ldirish
        holder.schoolName.setText(slider.getSchoolName());
        holder.description.setText(slider.getDescription());
        holder.createdAt.setText(slider.getCreatedAt());

        // Rasmni yuklash
        String imagePath = slider.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            String fullImageUrl = baseUrl + imagePath.replace("\\", "/");
            Log.d("SliderImageURL", fullImageUrl);

            Glide.with(context)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.error_image)
                    .error(R.drawable.banner_image2)
                    .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            Log.e("GlideError", "Slider rasm yuklanmadi: " + (e != null ? e.getMessage() : "Noma’lum xato"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            Log.d("GlideSuccess", "Slider rasm yuklandi: " + fullImageUrl);
                            return false;
                        }
                    })
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.banner_image1);
        }
    }

    @Override
    public int getItemCount() {
        return sliderList.size();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        TextView schoolName, description, createdAt;
        ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            schoolName = itemView.findViewById(R.id.tvSchoolTitle);
            description = itemView.findViewById(R.id.tvSchoolDescription);
            createdAt = itemView.findViewById(R.id.tvSchoolCreatedAt);
            imageView = itemView.findViewById(R.id.ivSchoolImage);
        }
    }
}