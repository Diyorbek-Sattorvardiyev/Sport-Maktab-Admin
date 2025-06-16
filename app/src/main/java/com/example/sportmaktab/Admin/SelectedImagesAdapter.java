package com.example.sportmaktab.Admin;



import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportmaktab.R;
// Loyihangizdagi R faylini to'g'ri import qiling

import java.util.List;

public class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesAdapter.ViewHolder> {

    private Context context;
    private List<String> imagePaths; // Rasmlar ro'yxati
    private OnItemClickListener onItemClickListener; // Itemni olib tashlash uchun listener

    public SelectedImagesAdapter(AddNewsActivity context, List<Uri> selectedImages, OnItemClickListener onItemClickListener) {
    }

    public interface OnItemClickListener {
        void onItemClick(int position); // Elementni olib tashlash uchun metod
    }

    public SelectedImagesAdapter(Context context, List<String> imagePaths, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.imagePaths = imagePaths;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_selected_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imagePath = imagePaths.get(position);

        Glide.with(context)
                .load(imagePath)
                .placeholder(R.drawable.banner_image1)
                .error(R.drawable.drawable)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position); // Elementni olib tashlashni chaqiradi
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivSelectedImage);
        }
    }
}

