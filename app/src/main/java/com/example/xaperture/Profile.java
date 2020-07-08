package com.example.xaperture;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Profile extends About {

    GoogleSignInClient mGoogleSignInClient;
    ImageView imageView;
    Button signout;
    TextView name,email;
    RecyclerView view;
    ArrayList<WallpapersModel> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient=GoogleSignIn.getClient(this,gso);
        imageView=(ImageView)findViewById(R.id.imageView);
        GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(this);
        Uri img=account.getPhotoUrl();
        Glide.with(this).load(String.valueOf(img)).into(imageView);
        name=(TextView)findViewById(R.id.name);
        email=(TextView)findViewById(R.id.email);
        name.setText(account.getDisplayName());
        email.setText(account.getEmail());
        signout=(Button)findViewById(R.id.signout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    signOut();
            }
        });
        view=(RecyclerView)findViewById(R.id.profile_recycler);
        FloatingActionButton upload=(FloatingActionButton)findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UploadTask.class));
            }
        });
        updateDatabase(account);
        loadUserImages();
        if (data!=null)
        {
            ProgressBar p=findViewById(R.id.profile_bar);
            p.setVisibility(View.INVISIBLE);
        }

    }
    private void signOut()
    {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(),"Signing Out",Toast.LENGTH_SHORT).show();
                Intent  intent=new Intent(getApplicationContext(),ActivityMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Profile.this.finish();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_profile,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.share:
                Toast.makeText(this,"Share",Toast.LENGTH_SHORT);
                return true;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent  intent=new Intent(getApplicationContext(),ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Profile.this.finish();

    }
    public void updateDatabase(GoogleSignInAccount account)
    {
        User user=new User(account.getDisplayName(),account.getEmail());
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("Users").document(account.getEmail()).set(user.user, SetOptions.merge());
    }
    public void loadUserImages()
    {
        final ArrayList<WallpapersModel> userimg=new ArrayList<>();
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        FirebaseFirestore firestore= FirebaseFirestore.getInstance();
        firestore.collection("UserImg").whereEqualTo("email",user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for(QueryDocumentSnapshot document:task.getResult())
                    {
                        WallpapersModel model= new WallpapersModel(document.getString("email"),document.getString("orignal_img"),document.getString("thumbnail_img"),document.getString("category"),(Timestamp)document.get("date"),getImageUrl(document));
                        userimg.add(model);
                    }
                    data=userimg;
                    view.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    ImageAdapter adapter=new ImageAdapter(userimg);
                    view.setAdapter(adapter);
                    final SwipeRefreshLayout refreshLayout=(SwipeRefreshLayout)findViewById(R.id.refresh_profile);
                    refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    refreshLayout.setRefreshing(false);
                                }
                            },3000);
                        }
                    });
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"no data available",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public static StorageReference getImageUrl(QueryDocumentSnapshot document)
    {
        FirebaseStorage storage =FirebaseStorage.getInstance();
        StorageReference reference= storage.getReference();
        System.out.println("storage refrence created");
        StorageReference imgref=reference.child("Images").child(document.getString("category")).child("thumbnails").child(document.getString("thumbnail_img"));
        return imgref;
    }
}