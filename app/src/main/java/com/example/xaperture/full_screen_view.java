package com.example.xaperture;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class full_screen_view extends AppCompatActivity {

    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_view);
        img=(ImageView)findViewById(R.id.full_screen_view);
        final WallpapersModel model=WallpapersModel.temp;
        model.ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                finish();
            }
        });
        Button home=(Button)findViewById(R.id.homescreen);
        Button lockscreen=(Button)findViewById(R.id.lockscreen);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WallpaperManager manager =WallpaperManager.getInstance(full_screen_view.this);
                BitmapDrawable bitmapDrawable=(BitmapDrawable)img.getDrawable();
                Bitmap bitmap=bitmapDrawable.getBitmap();
                try {
                    manager.setBitmap(bitmap);
                    Snackbar.make(img.getRootView(),"Applied to Homescreen!",Snackbar.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        lockscreen.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                BitmapDrawable bitmapDrawable=(BitmapDrawable)img.getDrawable();
                Bitmap bitmap=bitmapDrawable.getBitmap();
                WallpaperManager manager=WallpaperManager.getInstance(getApplicationContext());
                try {
                    manager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_LOCK);
                    Snackbar.make(img.getRootView(),"Applied to LockScreen!",Snackbar.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}