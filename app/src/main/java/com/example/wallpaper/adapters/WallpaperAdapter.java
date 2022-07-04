package com.example.wallpaper.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wallpaper.R;
import com.example.wallpaper.activity.FullScreenWallpaper;
import com.example.wallpaper.models.WallpapersModel;

import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder> {

    private Context context;
    private List<WallpapersModel> wallpapersModelList;

    public WallpaperAdapter(Context context, List<WallpapersModel> wallpapersModelList) {
        this.context = context;
        this.wallpapersModelList = wallpapersModelList;
    }

    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.wallpaper_item, parent, false);
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperAdapter.WallpaperViewHolder holder, int position) {

        WallpapersModel model = wallpapersModelList.get(position);

        Glide.with(context).load(model.getMediumUrl()).into(holder.imageView);
        holder.photographerName.setText(model.getPhotographerName());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.startActivity(new Intent(context, FullScreenWallpaper.class)
                        .putExtra("originalUrl", model.getOriginalUrl()));

            }
        });
    }

    @Override
    public int getItemCount() {
        return wallpapersModelList.size();
    }

    class WallpaperViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView photographerName;


        public WallpaperViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewItem);
            photographerName = itemView.findViewById(R.id.photographerName);
        }
    }
}
