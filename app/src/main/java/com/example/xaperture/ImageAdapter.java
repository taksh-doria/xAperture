package com.example.xaperture;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.WallpapersHolder>
{
    ActivityMain mainActivity;
    ArrayList<WallpapersModel> wallpapersModels;

    public ImageAdapter(ArrayList<WallpapersModel> ref) {
        System.out.println("Adapter created");
        this.wallpapersModels = ref;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WallpapersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
       LayoutInflater inflater= LayoutInflater.from(parent.getContext());
       View view=inflater.inflate(R.layout.img_holder,parent,false);
       return new WallpapersHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull WallpapersHolder holder, int position) {
        holder.bind(wallpapersModels.get(position));
    }
    @Override
    public int getItemCount() {
        return wallpapersModels.size();
    }

    public class WallpapersHolder extends RecyclerView.ViewHolder
    {
        public WallpapersHolder(@NonNull View itemView) {
            super(itemView);
        }
        public  void bind(final WallpapersModel model)
        {
            System.out.println("here");
            final ImageView imgIcon=itemView.findViewById(R.id.imgIcon);
            model.ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String url=uri.toString();
                    System.out.println("obtained url: "+url);
                    Glide.with(imgIcon.getContext()).load(url).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            YoYo.with(Techniques.FadeIn).duration(500).playOn(imgIcon);
                            return false;
                        }
                    }).into(imgIcon);
                    imgIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            model.temp=model;
                            MobileAds.initialize(imgIcon.getContext());
                            InterstitialAd interstitialAd=new InterstitialAd(imgIcon.getContext());
                            interstitialAd.setAdUnitId("ca-app-pub-5460864729583670/4176008689");
                            interstitialAd.loadAd(new AdRequest.Builder().build());
                            if (interstitialAd.isLoaded())
                            {
                                interstitialAd.show();
                            }
                            Intent i=new Intent(imgIcon.getContext(),Img_DetailView.class);
                            imgIcon.getContext().startActivity(i);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }
}
