package com.example.filefilter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filefilter.fileFetcher.FileData;
import com.example.filefilter.fileFetcher.FileFilterData;
import com.example.filefilter.fileFetcher.FileListAdapter;
import com.example.filefilter.folderPicker.IFolderChangeListener;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
    private View currentFileTableView;
    private Path currentPath;

    public FileListViewManager(Context context, View fileListParentView, FileManager fileManager) {
        Log.d(TAG, "FileListViewManager: initializing File list view manager");
        parentView = fileListParentView;
        this.fileManager = fileManager;
        this.context = context;

        //Initialize views
        //initialize current folder text view
//        this.currentFolderTextView =parentView.findViewById(R.id.current_folder);
//        currentFolderTextView.setMovementMethod(new ScrollingMovementMethod());

        this.emptyMsg = parentView.findViewById(R.id.empty_message);
        this.progressBar = parentView.findViewById(R.id.progress_bar);

        //initialize file list view
        this.fileListView = parentView.findViewById(R.id.file_list);
        fileListView.setLayoutManager(new LinearLayoutManager(context));
        fileListView.setItemAnimator(new DefaultItemAnimator());

        //initialize currentFolder variable to internal storage root folder
        String internalStorage = System.getenv("EXTERNAL_STORAGE");
        //exit activity if can't find internal storage
        if (internalStorage == null) {
            ((Activity) context).finish();
        }

        this.files = new LinkedList<>(Arrays.asList(new FileData(internalStorage.replaceAll("/", ""), null, null, FileType.DIRECTORY)));
        this.fileListAdapter = new FileListAdapter(files, this);

        fileListView.setAdapter(fileListAdapter);
        currentFileTableView = fileListView;
        currentPath = Paths.get("/");

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

    //new search
    @Override
    public void onFileFilterChange(FileFilterData fileFilterData) {
        submitSearchRequest();
    }

    //new search
    @Override
    public void onChildDirectoryResolved(String folder) {
        Path path = currentPath.resolve(folder);
        if (Files.isDirectory(path)) {
            currentPath = path;
            submitSearchRequest();
        }
    }

    @Override
    public void onParentDirectoryResolved() {
        Path path = currentPath.getParent();
        if(Files.isDirectory(path)){
            currentPath=path;
            submitSearchRequest();
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

    private void setCurrentPath(Path folder) {
        currentPath = folder;
//        currentFolderTextView.setText(currentPath.toString());
//        fileManager.search();
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
