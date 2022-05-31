package com.example.filefilter;

import android.app.Activity;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filefilter.fileFetcher.FileData;
import com.example.filefilter.fileFetcher.FileListAdapter;
import com.example.filefilter.folderPicker.IFolderChangeListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListViewManager implements IFileFilterChangeListener, IFolderChangeListener, IFileChangeListener {
    private static final String TAG = "FileListViewManager";
    private RecyclerView fileListView;
    private FileListAdapter fileListAdapter;
    private List<FileData> files;
    private TextView emptyMsg;
    private ProgressBar progressBar;
    private View parentView;
    private View currentView;
    private FileManager fileManager;
    Context context;
    private String currentFolder;
    private TextView currentFolderTextView;

    public FileListViewManager(Context context, View fileListParentView, FileManager fileManager){
        parentView=fileListParentView;
        this.fileManager=fileManager;

        //Initialize views
        //initialize current folder text view
        currentFolderTextView =parentView.findViewById(R.id.current_folder);
        currentFolderTextView.setMovementMethod(new ScrollingMovementMethod());

        emptyMsg =parentView.findViewById(R.id.empty_message);
        progressBar =parentView.findViewById(R.id.progress_bar);

        //initialize file list view
        fileListView = parentView.findViewById(R.id.file_list);
        fileListView.setLayoutManager(new LinearLayoutManager(context));
        fileListView.setItemAnimator(new DefaultItemAnimator());
        files=new ArrayList<>();
        fileListAdapter=new FileListAdapter(files);
        fileListView.setAdapter(fileListAdapter);
        currentView=fileListView;

        //initialize currentFolder variable to internal storage root folder
        String internal_storage=System.getenv("EXTERNAL_STORAGE");
        if(internal_storage!=null){
            File internal_storage_file=new File(internal_storage);
            setCurrentFolder(internal_storage_file.getAbsolutePath());
        }else{
            ((Activity)context).finish();
        }
    }

    private void swapCurrentView(View newView){
        currentView.setVisibility(View.GONE);
        newView.setVisibility(View.VISIBLE);
        currentView=newView;
    }

    public void reloadFileList() {
        Log.d(TAG, "reloadFileList: "+files.size()+" files");
        ((Activity)context).runOnUiThread(() -> {
            fileListAdapter.notifyDataSetChanged();
            if(files.isEmpty()){
                swapCurrentView(emptyMsg);
            }else{
                swapCurrentView(fileListView);
            }
        });
    }

    private void setCurrentFolder(String folder ) {
        currentFolder =folder;
        currentFolderTextView.setText(currentFolder);
//        searchWithFilters();
    }

    @Override
    public void onFileFilterChange() {
        fileManager.search();
    }

    @Override
    public void onFolderChange(String folder) {
        fileManager.search();
    }

    @Override
    public void onFilesChanged() {
        reloadFileList();
    }

    @Override
    public void onFileRangeChanged() {
        reloadFileList();
    }
}
