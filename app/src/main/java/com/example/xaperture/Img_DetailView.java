package com.example.xaperture;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Img_DetailView extends AppCompatActivity {
    ProgressBar pbar;
    ImageView img;
    Intent intent;
    FloatingActionButton downloadbtn;
    SwipeRefreshLayout refreshLayout;
    String string;
    URL url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img__detail_view);
        img=(ImageView)findViewById(R.id.img_detail);
        pbar=(ProgressBar)findViewById(R.id.img_detail_progress);
        downloadbtn=(FloatingActionButton)findViewById(R.id.downloadbtn);
        intent=getIntent();
        final WallpapersModel model=WallpapersModel.temp;
        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.refresh_detail_view);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                },2000);
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.temp=model;
                startActivity(new Intent(Img_DetailView.this,full_screen_view.class));
            }
        });
        final String img_name=model.thumbnail_img_name;
        final String date=model.timestamp.toDate().toString();
        model.ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
                Glide.with(img.getContext()).load(url).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        SpannableStringBuilder builder=new SpannableStringBuilder();
                        builder.append("",new ImageSpan(getApplicationContext(),R.drawable.ic_baseline_date_range_24),0);
                        builder.append(date);
                        TextView info1=(TextView)findViewById(R.id.info1);
                        info1.setText(builder);
                        TextView info2=(TextView)findViewById(R.id.info2);
                        info2.setText(img_name);
                        pbar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                }).into(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        downloadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("needed: "+model.toString());
                getOrignalImage(model);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public  void getOrignalImage(final WallpapersModel model)
    {
        StorageReference storage=FirebaseStorage.getInstance().getReference("Images");
        System.out.println(model.cat);
        StorageReference reference=storage.child(model.cat).child(model.orignal_img_name);
        System.out.println(reference.toString());
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSuccess(Uri uri) {
                System.out.println("Uri: "+uri.toString());
                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    DownloadManager downloadManager=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                    DownloadManager.Request request=new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,model.orignal_img_name);
                    downloadManager.enqueue(request);
                    Toast.makeText(getApplicationContext(),"Download Complete!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    System.out.println("Permission denied");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Img_DetailView.this,"Something went wrong!",Toast.LENGTH_SHORT);
            }
        });
    }
}

