package com.example.filefilter;

import static com.example.filefilter.Util.managePermissions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

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

    private static final int REQUEST_DIRECTORY = 0;
    private static final String TAG = "MainActivity";

    TextView currentFolderTextView;
    RecyclerView fileListView;
    FileListAdapter fileListAdapter;
    List<FileListItem> fileList;
    ExecutorService executorService;
    String currentFolder;
    String latestFolderSearch;

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

        //initialize file list view
        fileListView=findViewById(R.id.file_list);
        fileListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        fileListView.setItemAnimator(new DefaultItemAnimator());
        fileList=new ArrayList<>();
        fileListAdapter=new FileListAdapter(fileList);
        fileListView.setAdapter(fileListAdapter);

        //initialize executor services for backgound work
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
        currentFolder =folder;
        currentFolderTextView.setText(currentFolder);
        searchWithFilters();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DIRECTORY) {
            //
//            setCurrentFolder();
        }
    }

    private void searchWithFilters() {
        Future<?> submit = executorService.submit(new FileLister(currentFolder, this));
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
        fileList.clear();
        fileList.addAll(files);
        runOnUiThread(() -> fileListAdapter.notifyDataSetChanged());
    }
}