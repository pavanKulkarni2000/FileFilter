package com.example.filefilter;

import static com.example.filefilter.Util.managePermissions;
import static com.example.filefilter.fileFetcher.FileUtil.simpleDateFormat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filefilter.fileFetcher.FileFilterData;
import com.example.filefilter.fileFetcher.FileListAdapter;
import com.example.filefilter.fileFetcher.FileData;
import com.example.filefilter.fileFetcher.FileLister;
import com.example.filefilter.folderPicker.FolderPickerDialog;
import com.example.filefilter.folderPicker.IFolderChangeListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity implements IFolderChangeListener {

    private static final String TAG = "MainActivity";

    //views
    private FileManager fileManager;
    private FileListViewManager fileListViewManager;
    private FileFilterManager fileFilterManager;
    private Future<?> searchResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        managePermissions(this, this);

        init();

    }

    private void init() {

        fileListViewManager=new FileListViewManager(this, findViewById(R.id.file_list_parent), fileManager);
        fileFilterManager=new FileFilterManager(this, findViewById(R.id.file_filter_parent), fileListViewManager);
        fileManager=new FileManager(fileListViewManager);
    }

    private void searchWithFilters() {
        swapCurrentView(progressBarView);
        if( searchResult!=null && !searchResult.isDone() ) {
                searchResult.cancel(true);
        }
        Log.d(TAG, "searchWithFilters: sending search requets for path "+currentFolder);
        searchResult = executorService.submit(new FileLister(currentFolder, fileFilterData, this));
    }

    public void folderDialog(View view) {

        FolderPickerDialog folderPickerDialog=new FolderPickerDialog(currentFolder);
        folderPickerDialog.show(getSupportFragmentManager(),"My  Fragment");
    }

    @Override
    public void onFolderChange(String folder) {
        setCurrentFolder(folder);
    }

}