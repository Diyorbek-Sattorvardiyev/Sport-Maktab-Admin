package com.example.sportmaktab.Talaba;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportmaktab.R;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private Context context;
    private List<BannerItem> bannerItems;

    public BannerAdapter(Context context, List<BannerItem> bannerItems) {
        this.context = context;
        this.bannerItems = bannerItems;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        BannerItem item = bannerItems.get(position);

        // Rasm va tekstni o'rnatish
        holder.imageView.setImageResource(item.getImageResId());
        holder.txtTitle.setText(item.getTitle());
        holder.txtSubtitle.setText(item.getSubtitle());

        // Animatsiyani boshlash
        holder.animateItems();
    }

    @Override
    public int getItemCount() {
        return bannerItems.size();
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtTitle, txtSubtitle;
        ConstraintLayout rootLayout;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bannerImage);
            txtTitle = itemView.findViewById(R.id.txtBannerTitle);
            txtSubtitle = itemView.findViewById(R.id.txtBannerSubtitle);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }

        void animateItems() {
            // Banner elementi animatsiyasi
            txtTitle.setTranslationX(-200);
            txtTitle.setAlpha(0);
            txtSubtitle.setTranslationX(-200);
            txtSubtitle.setAlpha(0);

            txtTitle.animate()
                    .translationX(0)
                    .alpha(1)
                    .setDuration(800)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();

            txtSubtitle.animate()
                    .translationX(0)
                    .alpha(1)
                    .setStartDelay(300)
                    .setDuration(800)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();

            // Rasm animatsiyasi
            imageView.setScaleX(1.1f);
            imageView.setScaleY(1.1f);

            imageView.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(2000)
                    .start();
        }
    }
}
