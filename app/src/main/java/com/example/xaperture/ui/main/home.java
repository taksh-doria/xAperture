package com.example.xaperture.ui.main;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.xaperture.ImageAdapter;
import com.example.xaperture.R;
import com.example.xaperture.Utility.ShowAlert;
import com.example.xaperture.WallpapersModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class home extends Fragment {

    private RecyclerView recView;
    DocumentSnapshot lastloaded;
    private ArrayList<WallpapersModel> data;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View root= inflater.inflate(R.layout.fragment_home, container, false);
        if (data!=null)
        {
            ProgressBar progressBar=(ProgressBar)root.findViewById(R.id.progressbar_home);
            progressBar.setVisibility(View.INVISIBLE);
        }
        final ConnectivityManager connectivityManager=(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (connectivityManager.getActiveNetwork()!=null)
            {
                if (data==null)
                {
                    data=getAllImageData();
                    System.out.print("List retrived");
                }
                recView=(RecyclerView)root.findViewById(R.id.recyclerview);
                recView.setLayoutManager(new LinearLayoutManager(root.getContext()));
                recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
                    {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_DRAGGING)
                        {
                            data=getAllImageData();
                        }
                    }
                });
            }
            else
            {
                AlertDialog a1=new ShowAlert().getMessage(root.getContext());
                a1.show();
            }
        }
        final SwipeRefreshLayout swipeRefreshLayout=(SwipeRefreshLayout)root.findViewById(R.id.swiprerefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lastloaded=null;
                if (connectivityManager.getActiveNetwork()!=null)
                {
                    data=getAllImageData();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    },2000);
                    Snackbar.make(recView.getRootView(),"Updated Photos!",Snackbar.LENGTH_LONG).show();
                }
                else
                {
                    AlertDialog a1=new ShowAlert().getMessage(root.getContext());
                    a1.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    },2000);
                }
            }
        });
        return root;
    }
    public ArrayList<WallpapersModel> getAllImageData()
    {
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        final ArrayList<WallpapersModel> list=new ArrayList<>();
        if (lastloaded==null)
        {
            db.collection("UserImg")
                    .orderBy("date", Query.Direction.DESCENDING).limit(20)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            queryDocumentSnapshots.size();
                            List<DocumentSnapshot> snapshots=queryDocumentSnapshots.getDocuments();
                            lastloaded=snapshots.get(snapshots.size()-1);
                            for (DocumentSnapshot document:queryDocumentSnapshots.getDocuments())
                            {
                                final WallpapersModel wall=new WallpapersModel(document.getString("email"),document.getString("orignal_img"),document.getString("thumbnail_img"),document.getString("category"),(Timestamp)document.get("date"),getImageUrl(document));
                                list.add(wall);
                            }
                            ImageAdapter imageAdapter=new ImageAdapter(data);
                            recView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recView.setAdapter(imageAdapter);
                        }
                    });
        }
        else
        {
            db.collection("UserImg")
                    .orderBy("date", Query.Direction.DESCENDING).limit(10)
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
                            System.out.println("");
                            for (DocumentSnapshot document:queryDocumentSnapshots.getDocuments())
                            {
                                final WallpapersModel wall=new WallpapersModel(document.getString("email"),document.getString("orignal_img"),document.getString("thumbnail_img"),document.getString("category"),(Timestamp)document.get("date"),getImageUrl(document));
                                list.add(wall);
                            }
                            ImageAdapter imageAdapter=new ImageAdapter(data);
                            recView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                            recView.setAdapter(imageAdapter);
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