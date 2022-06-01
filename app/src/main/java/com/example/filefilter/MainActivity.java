package com.example.filefilter;

import static com.example.filefilter.utils.Util.managePermissions;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.filefilter.controllers.FileFilterManager;
import com.example.filefilter.controllers.FileListViewManager;
import com.example.filefilter.controllers.FileManager;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //views
    private FileManager fileManager;
    private FileListViewManager fileListViewManager;
    private FileFilterManager fileFilterManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        managePermissions(this, this);

        init();

        Log.d(TAG, "onCreate: "+ Arrays.toString(new File("/storage/0A6C-1CF8").list()));

    }

    private void init() {

        fileManager = new FileManager();
        fileListViewManager = new FileListViewManager(this, findViewById(R.id.file_list_parent), fileManager);
        fileFilterManager = new FileFilterManager(this, findViewById(R.id.file_filter_parent), fileListViewManager);
        fileManager.setFileFilterManager(fileFilterManager);
        fileManager.setFileListViewManager(fileListViewManager);
    }

    @Override
    public void onBackPressed() {
        fileListViewManager.onParentDirectoryResolved();
    }
}