package com.example.wallpaper.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.artjimlop.altex.AltexImageDownloader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.wallpaper.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.IOException;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class FullScreenWallpaper extends AppCompatActivity {

    String originalUrl = "";
    PhotoView photoView;
    Button setWallpaperButton, downloadWallpaper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_wallpaper);

        Intent intent = getIntent();
        originalUrl = intent.getStringExtra("originalUrl");

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        photoView = findViewById(R.id.photoView);
        Glide.with(this).load(originalUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(photoView);

        setWallpaperButton = findViewById(R.id.buttonSetWallpaper);
        setWallpaperButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {

                WallpaperManager wallpaperManager = WallpaperManager.getInstance(FullScreenWallpaper.this);
                Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
                try {

                    wallpaperManager.setBitmap(bitmap);
                    Toast.makeText(FullScreenWallpaper.this, "Image is set as your wallpaper", Toast.LENGTH_SHORT).show();


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });


        downloadWallpaper = findViewById(R.id.buttonDownloadWallpaper);
        downloadWallpaper.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {


                AltexImageDownloader.writeToDisk(FullScreenWallpaper.this, originalUrl, "pexels");
                Toast.makeText(FullScreenWallpaper.this, "Downloading start...", Toast.LENGTH_SHORT).show();


            }
        });
    }
}