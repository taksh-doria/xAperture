package com.example.xaperture.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.xaperture.Collection_View;
import com.example.xaperture.R;

public class CollectionFrag extends Fragment {
    String category;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_collection, container, false);
        final ListView listView=(ListView)root.findViewById(R.id.listview);
        final String arr[]=getResources().getStringArray(R.array.categories);
        ArrayAdapter<String> stringArrayAdapter =new ArrayAdapter<String>(root.getContext(),R.layout.customlist,arr);
        listView.setAdapter(stringArrayAdapter);
        listView.setTextAlignment(ListView.TEXT_ALIGNMENT_CENTER);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                category=arr[position];
                Intent i=new Intent(getActivity().getApplicationContext(), Collection_View.class);
                i.putExtra("category",category);
                startActivity(i);
            }
        });
        return root;
    }
}