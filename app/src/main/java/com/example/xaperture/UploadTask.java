package com.example.xaperture;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xaperture.Utility.FileUpload;
import com.google.android.material.snackbar.Snackbar;

public class UploadTask extends AppCompatActivity {

    EditText description;
    private  String category,mime;
    private Button button,upload;
    Intent data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_task);
        final Spinner spinner=(Spinner)findViewById(R.id.upload_category);
        final String[] cat = getResources().getStringArray(R.array.categories);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,cat);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category=cat[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        description=(EditText)findViewById(R.id.imgdescription);
        button=(Button)findViewById(R.id.filepicker);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch();
            }
        });
        upload=(Button)findViewById(R.id.upload_btn);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (description.getText().toString().length()==0)
                {
                    description.setError("Field required");
                }
                else if(data==null)
                {
                    Snackbar.make(button.getRootView(),"Select image to upload!",Snackbar.LENGTH_LONG).show();
                }
                else
                {
                    FileUpload upload=new FileUpload(data,category,description.getText().toString(),mime);
                    ConnectivityManager cmgr=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (cmgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()== NetworkInfo.State.CONNECTED||cmgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()==NetworkInfo.State.CONNECTED)
                    {
                        ProgressBar progressBar=(ProgressBar)findViewById(R.id.status);
                        progressBar.setVisibility(View.VISIBLE);
                        boolean va=upload.registerImage();
                        System.out.println("return value"+va);
                        if (!va)
                        {
                            Intent intent=new Intent(getApplicationContext(),Profile.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            UploadTask.this.finish();
                        }
                        else
                        {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Network Unavailable!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    public void startSearch()
    {
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,100);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100&&resultCode== Activity.RESULT_OK)
        {
            if (data!=null)
            {
                this.data=data;
                String type=getContentResolver().getType(data.getData());
                String mime[]=type.split("/");
                System.out.println("image uri: "+mime[1]);
                this.mime=mime[1];
                ImageView img=(ImageView)findViewById(R.id.img_upload);
                Glide.with(getApplicationContext()).load(data.getData()).into(img);
            }
        }
    }
}