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

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<News> newsList;
    private Context context;
    private String baseUrl = "http://172.20.2.196:5000/uploads/"; // To‘g‘ri yo‘l: uploads/

    public NewsAdapter(List<News> newsList, Context context) {
        this.newsList = newsList;
        this.context = context;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newsList.get(position);
        holder.title.setText(news.getTitle());
        holder.content.setText(news.getContent());
        holder.date.setText(news.getCreatedAt());

        List<String> imageUrls = news.getImageUrls();
        String imageUrl = (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : null;

        if (imageUrl != null && !imageUrl.isEmpty()) {
            String fullImageUrl = baseUrl + imageUrl.replace("\\", "/"); // Backslash’ni slash’ga almashtirish
            Log.d("ImageURL", fullImageUrl);

            Glide.with(context)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.error_image)
                    .error(R.drawable.banner_image1)
                    .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            Log.e("GlideError", "Rasm yuklanmadi: " + (e != null ? e.getMessage() : "Noma’lum xato"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            Log.d("GlideSuccess", "Rasm yuklandi: " + fullImageUrl);
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
        return newsList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, date;
        ImageView imageView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.newsTitle);
            content = itemView.findViewById(R.id.newsContent);
            date = itemView.findViewById(R.id.newsDate);
            imageView = itemView.findViewById(R.id.newsImage);
        }
    }
}