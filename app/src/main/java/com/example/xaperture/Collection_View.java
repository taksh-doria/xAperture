package com.example.xaperture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.example.xaperture.Utility.ShowAlert;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Collection_View extends AppCompatActivity {

    RecyclerView view;
    ArrayList<WallpapersModel> data;
    String category;
    DocumentSnapshot lastloaded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection__view);
        Intent intent=getIntent();
        category=intent.getStringExtra("category");
        System.out.print("category: "+category);
        final ConnectivityManager cmgr=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        final SwipeRefreshLayout swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.collection_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lastloaded=null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (cmgr.getActiveNetwork()!=null)
                    {
                        data=getData(category);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        },2000);
                        Snackbar.make(view.getRootView(),"Updated Photos!",Snackbar.LENGTH_LONG).show();
                    }
                    else
                    {
                        AlertDialog a1=new ShowAlert().getMessage(Collection_View.this);
                        a1.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        },2000);
                    }
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cmgr.getActiveNetwork()!=null)
            {
                view=(RecyclerView)findViewById(R.id.collection_recycler);
                data=getData(category);
                view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_DRAGGING)
                        {
                            data=getData(category);
                        }
                    }
                });
            }
            else
            {
                AlertDialog alertDialog=new ShowAlert().getMessage(getApplicationContext());
                alertDialog.show();
            }
        }

    }
    public  ArrayList<WallpapersModel> getData(String category)
    {
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        final ArrayList<WallpapersModel> list=new ArrayList<>();
        if (lastloaded==null)
        {
            db.collection("UserImg")
                    .whereEqualTo("category",category)
                    .limit(20)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            queryDocumentSnapshots.size();
                            List<DocumentSnapshot> snapshots=queryDocumentSnapshots.getDocuments();
                            if (snapshots!=null)
                            {
                                System.out.println("Snapshot not null");
                                lastloaded=snapshots.get(snapshots.size()-1);
                                for (DocumentSnapshot document:queryDocumentSnapshots.getDocuments())
                                {
                                    final WallpapersModel wall=new WallpapersModel(document.getString("email"),document.getString("orignal_img"),document.getString("thumbnail_img"),document.getString("category"),(Timestamp)document.get("date"),getImageUrl(document));
                                    System.out.print(wall.toString());
                                    list.add(wall);
                                }
                            }
                            else
                            {
                                TextView textView=(TextView)findViewById(R.id.error);
                                textView.setText("No Data Available");
                                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            }
                            if (list!=null)
                            {
                                ImageAdapter imageAdapter=new ImageAdapter(data);
                                view.setAdapter(imageAdapter);
                                view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            }
                        }
                    });

        }
        else
        {
            db.collection("UserImg")
                    .whereEqualTo("category",category)
                    .limit(10)
                    .startAfter(lastloaded)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.size()==0)
                            {
                                return;
                            }
                            System.out.println("Size of query snapshot: "+queryDocumentSnapshots.size());
                            List<DocumentSnapshot> snapshots=queryDocumentSnapshots.getDocuments();
                            lastloaded=snapshots.get(snapshots.size()-1);
                            for (DocumentSnapshot document:queryDocumentSnapshots.getDocuments())
                            {
                                final WallpapersModel wall=new WallpapersModel(document.getString("email"),document.getString("orignal_img"),document.getString("thumbnail_img"),document.getString("category"),(Timestamp)document.get("date"),getImageUrl(document));
                                list.add(wall);
                            }
                            if (list!=null)
                            {
                                ImageAdapter imageAdapter=new ImageAdapter(list);
                                view.setAdapter(imageAdapter);
                                view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            }
                        }
                    });
        }
        return list;
    }
    public static StorageReference getImageUrl(DocumentSnapshot document)
    {
        FirebaseStorage storage =FirebaseStorage.getInstance();
        StorageReference reference= storage.getReference();
        System.out.println("storage refrence created");
        StorageReference imgref=reference.child("Images").child(document.getString("category")).child("thumbnails").child(document.getString("thumbnail_img"));
        return imgref;
    }
}