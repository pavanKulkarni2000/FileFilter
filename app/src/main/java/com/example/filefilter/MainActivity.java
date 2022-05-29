package com.example.filefilter;

import static com.example.filefilter.Util.managePermissions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filefilter.fileFetcher.FileFilterInfo;
import com.example.filefilter.fileFetcher.FileListAdapter;
import com.example.filefilter.fileFetcher.FileListItem;
import com.example.filefilter.fileFetcher.FileLister;
import com.example.filefilter.fileFetcher.IFileListReadyCallback;
import com.example.filefilter.folderPicker.FolderPickerDialog;
import com.example.filefilter.folderPicker.IFolderSelectedCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity implements IFolderSelectedCallback, IFileListReadyCallback {

    //resource
    private ExecutorService executorService;
    private static final String TAG = "MainActivity";

    //views
    private TextView currentFolderTextView;
    private TextView emptyMessageView;
    private ProgressBar progressBarView;
    private RecyclerView fileListView;
    private View currentView;

    //controller
    private FileListAdapter fileListAdapter;

    //state
    private List<FileListItem> fileList;
    private String currentFolder;
    private String latestFolderSearch;
    private Future<?> searchResult;
    private FileFilterInfo fileFilterInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        managePermissions(this, this);

        init();

    }

    private void init() {
        //Initialize views
        //initialize current folder text view
        currentFolderTextView =findViewById(R.id.current_folder);
        currentFolderTextView.setMovementMethod(new ScrollingMovementMethod());
        emptyMessageView =findViewById(R.id.empty_message);
        progressBarView =findViewById(R.id.progress_bar);

        //initialize file list view
        fileListView = findViewById(R.id.file_list);
        fileListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        fileListView.setItemAnimator(new DefaultItemAnimator());
        fileList=new ArrayList<>();
        fileListAdapter=new FileListAdapter(fileList);
        fileListView.setAdapter(fileListAdapter);
        currentView=fileListView;

        fileFilterInfo =new FileFilterInfo();

        //initialize executor services for background work
        executorService= Executors.newFixedThreadPool(4);

        //initialize currentFolder variable to internal storage root folder
        String internal_storage=System.getenv("EXTERNAL_STORAGE");
        if(internal_storage!=null){
            File internal_storage_file=new File(internal_storage);
            setCurrentFolder(internal_storage_file.getAbsolutePath());
        }else{
            finish();
        }
    }

    private void setCurrentFolder(String folder ) {
        swapCurrentView(progressBarView);
        currentFolder =folder;
        currentFolderTextView.setText(currentFolder);
        searchWithFilters();
    }

    private void searchWithFilters() {
        if( searchResult!=null && !searchResult.isDone() ) {
            if (latestFolderSearch.equals(currentFolder)) {
                Toast.makeText(this, "Search in progress", Toast.LENGTH_SHORT).show();
                return;
            } else {
                searchResult.cancel(true);
            }
        }
        latestFolderSearch = currentFolder;
        Log.d(TAG, "searchWithFilters: sending search requets for path "+currentFolder);
        searchResult = executorService.submit(new FileLister(currentFolder, fileFilterInfo, this));
    }

    public void folderDialog(View view) {

        FolderPickerDialog folderPickerDialog=new FolderPickerDialog(currentFolder);
        folderPickerDialog.show(getSupportFragmentManager(),"My  Fragment");
    }

    @Override
    public void onFolderSelected(String folder) {
        setCurrentFolder(folder);
    }

    @Override
    public void onFileListReady(List<FileListItem> files) {
        Log.d(TAG, "onFileListReady: Got result with "+files.size()+" files");
        fileList.clear();
        fileList.addAll(files);
        runOnUiThread(() -> {
            fileListAdapter.notifyDataSetChanged();
            if(fileList.isEmpty()){
                swapCurrentView(emptyMessageView);
            }else{
                swapCurrentView(fileListView);
            }
        });
    }

    private void swapCurrentView(View newView){
        currentView.setVisibility(View.GONE);
        newView.setVisibility(View.VISIBLE);
        currentView=newView;
    }
}