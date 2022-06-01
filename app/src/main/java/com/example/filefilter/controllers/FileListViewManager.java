package com.example.filefilter.controllers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filefilter.models.FileType;
import com.example.filefilter.R;
import com.example.filefilter.callbacks.IFileChangeListener;
import com.example.filefilter.callbacks.IFileFilterChangeListener;
import com.example.filefilter.models.FileData;
import com.example.filefilter.models.FileFilterData;
import com.example.filefilter.callbacks.IFolderChangeListener;
import com.example.filefilter.utils.Util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FileListViewManager implements IFileFilterChangeListener, IFolderChangeListener, IFileChangeListener {
    private static final String TAG = "FileListViewManager";
    private final RecyclerView fileListView;
    private final FileListAdapter fileListAdapter;
    private final List<FileData> files;
    private final TextView emptyMsg;
    private final ProgressBar progressBar;
    private final View parentView;
    private final FileManager fileManager;
    private final Context context;
    private final List<FileData> rootFolders;
    private View currentFileTableView;
    private Path currentPath=Paths.get("/");
    private TextView currentPathView;

    public FileListViewManager(Context context, View fileListParentView, FileManager fileManager) {
        Log.d(TAG, "FileListViewManager: initializing File list view manager");
        parentView = fileListParentView;
        this.fileManager = fileManager;
        this.context = context;

        //Initialize views
        //initialize current folder text view
        this.currentPathView =parentView.findViewById(R.id.current_path);
        currentPathView.setText("/");
//        currentFolderTextView.setMovementMethod(new ScrollingMovementMethod());
        this.emptyMsg = parentView.findViewById(R.id.empty_message);
        this.progressBar = parentView.findViewById(R.id.progress_bar);

        //initialize file list view
        this.fileListView = parentView.findViewById(R.id.file_list);
        fileListView.setLayoutManager(new LinearLayoutManager(context));
        fileListView.setItemAnimator(new DefaultItemAnimator());
        fileListView.setItemViewCacheSize(20);

        //initialize currentFolder variable to internal storage root folder
        String internalStorage = System.getenv("EXTERNAL_STORAGE");
        //exit activity if can't find internal storage
        if (internalStorage == null) {
            ((Activity) context).finish();
        }

        rootFolders = new LinkedList<>();
        rootFolders.add(new FileData(internalStorage.replaceFirst("/", ""), null, null, FileType.DIRECTORY));
        rootFolders.addAll(Util.getExternalMounts().stream().map(mount -> new FileData(mount.replaceFirst("/", ""), null, null, FileType.DIRECTORY)).collect(Collectors.toList()));
        files=new LinkedList<>(rootFolders);
        this.fileListAdapter = new FileListAdapter(files, this);

        fileListView.setAdapter(fileListAdapter);
        currentFileTableView = fileListView;
    }

    private void swapCurrentView(View newView) {
        currentFileTableView.setVisibility(View.GONE);
        newView.setVisibility(View.VISIBLE);
        currentFileTableView = newView;
    }

    private void submitSearchRequest() {
            swapCurrentView(progressBar);
            fileManager.search();
    }

    private void setCurrentPath(Path path) {
        if(currentPath!=path) {
            currentPath=path;
            currentPathView.setText(currentPath.toString());
            if (!currentPath.toString().equals("/")) {
                submitSearchRequest();
            }else{
                files.clear();
                files.addAll(rootFolders);
                onFilesChanged();
            }
        }
    }

    //new search
    @Override
    public void onFileFilterChange(FileFilterData fileFilterData) {
        submitSearchRequest();
    }

    //new search
    @Override
    public void onChildDirectoryResolved(String folder) {
        Path path = getCurrentPath().resolve(folder);
        if (Files.isDirectory(path)) {
            setCurrentPath(path);
        }
    }

    @Override
    public void onParentDirectoryResolved() {
        Path path = currentPath.getParent();
        if(Files.isDirectory(path)){
            setCurrentPath(path);
        }
    }

    //update UI
    @Override
    public void onFilesChanged() {
        Log.d(TAG, "onFilesChanged: " + files.size() + " files");
        ((Activity) context).runOnUiThread(() -> {
            fileListAdapter.notifyDataSetChanged();
            if (files.isEmpty()) {
                swapCurrentView(emptyMsg);
            } else {
                swapCurrentView(fileListView);
            }
        });
    }

    //update UI
    @Override
    public void onFileRangeChanged(int start, int end) {
        Log.d(TAG, "onFileRangeChanged: " + files.size() + " files");
        ((Activity) context).runOnUiThread(() -> {
            fileListAdapter.notifyItemRangeChanged(start, end);
            if (files.isEmpty()) {
                swapCurrentView(emptyMsg);
            } else {
                swapCurrentView(fileListView);
            }
        });
    }

    public Path getCurrentPath() {
        return currentPath;
    }

    public List<FileData> getFiles() {
        return files;
    }

    public void clearFiles() {
        files.clear();
    }

    public void addFiles(List<FileData> fileData) {
        files.addAll(fileData);
    }

}
