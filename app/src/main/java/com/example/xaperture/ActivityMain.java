package com.example.xaperture;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.xaperture.ui.main.SectionsPagerAdapter;

public class ActivityMain extends AppCompatActivity {

    private static int interval=2000;
    private final int requestCode=1;
    static int count=0;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (count==0)
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            },interval);
            count++;
        }
        if(!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(ActivityMain.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},requestCode);
        }
        else
        {
            System.out.println("permission granted");
        }
        setContentView(R.layout.main_activity);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.signup:
                System.out.println("Signup button clicked");
                startActivity(new Intent(getApplicationContext(),SignUp.class));
                return true;
            case R.id.about:
                startActivity(new Intent(getApplicationContext(),About.class));
                return true;
            case R.id.exit :
                finish();
                System.exit(0);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return  true;
    }
}