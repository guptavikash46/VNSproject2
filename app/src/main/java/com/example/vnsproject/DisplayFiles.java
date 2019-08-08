package com.example.vnsproject;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class DisplayFiles extends AppCompatActivity {
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> fileNames;
    private String googlePhotosPackageName;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_files_list);
        listView = findViewById(R.id.files_list);

        final String directory = Environment.getExternalStorageDirectory().toString() +"/DCIM/VNSproject";
        File dir = new File(directory);
        File[] fileList = dir.listFiles();
        fileNames = new ArrayList<>();
        for(int i = 0; i< fileList.length; i++) fileNames.add(fileList[i].getName());
        //Log.e("Files ", fileNames.get(0)+ " directory: "+directory);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);
        listView.setAdapter(arrayAdapter);

        googlePhotosPackageName = "com.google.android.apps.photos";
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // Toast.makeText(getApplicationContext(), "Under progress", Toast.LENGTH_SHORT).show();
                Intent intent =  new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setType("image/*");
                intent.setPackage(googlePhotosPackageName);
                startActivityForResult(intent, 1);
            }
        });
    }
}
