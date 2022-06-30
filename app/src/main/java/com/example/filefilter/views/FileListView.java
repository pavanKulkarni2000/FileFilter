package com.example.filefilter.views;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.UiThread;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filefilter.MainActivity;
import com.example.filefilter.R;
import com.example.filefilter.callbacks.IFileListChangeListener;
import com.example.filefilter.callbacks.IListStateChangeListener;
import com.example.filefilter.callbacks.IPathChangeListener;
import com.example.filefilter.callbacks.ISelectionChangeListener;
import com.example.filefilter.controllers.FileController;
import com.example.filefilter.models.FileListModel;
import com.example.filefilter.utils.FileItem;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

@UiThread
public class FileListView implements IFileListChangeListener, IPathChangeListener, IListStateChangeListener {
    private static final String TAG = "FileListView";
    private final RecyclerView fileListView;
    private final FileListAdapter fileListAdapter;
    private final TextView emptyMsg;
    private final ProgressBar progressBar;
    private final TextView currentPathView;
    private View currentView;

    public FileListView(View parentView, FileController fileController) {
        //initialize file list view
        this.fileListView = parentView.findViewById(R.id.file_list);
        this.fileListView.setLayoutManager(new LinearLayoutManager(MainActivity.getContext()));
        this.fileListView.setItemAnimator(new DefaultItemAnimator());
        this.fileListView.setItemViewCacheSize(20);
        this.fileListAdapter = new FileListAdapter(new LinkedList<>(), fileController);
        this.fileListView.setAdapter(fileListAdapter);

        this.emptyMsg = parentView.findViewById(R.id.empty_message);
        this.progressBar = parentView.findViewById(R.id.progress_bar);
        this.currentPathView = parentView.findViewById(R.id.current_path);

        Log.d(TAG, "FileListView: created view");
    }

    @Override
    public void onFilesAndStateChanged(List<FileItem> newFiles, FileListModel.State newState) {
        Log.d(TAG, "onFilesAndStateChanged: " + newFiles.size() + " files");
        ((Activity) MainActivity.getContext()).runOnUiThread(() -> {
            fileListAdapter.setFiles(newFiles);
            fileListAdapter.notifyDataSetChanged();
            if (newState == FileListModel.State.EMPTY) {
                swapCurrentView(emptyMsg);
            } else {
                swapCurrentView(fileListView);
            }
        });
    }

    @Override
    public void onFileRangeChanged(int start, int count) {
        Log.d(TAG, "onFileRangeChanged: " + count + " files");
        ((Activity) MainActivity.getContext()).runOnUiThread(() -> {
            fileListAdapter.notifyItemRangeChanged(start, count);
        });
    }

    @Override
    public void onPathChanged(Path newPath) {
        Log.d(TAG, "onPathChanged: " + newPath);
        ((Activity) MainActivity.getContext()).runOnUiThread(() -> {
            currentPathView.setText(newPath.toString());
        });
    }

    @Override
    public void onFileListStateChanged(FileListModel.State newState) {
        View newCurrentView;
        switch (newState) {
            case LIST:
                newCurrentView = fileListView;
                break;
            case LOADING:
                newCurrentView = progressBar;
                break;
            case EMPTY:
                newCurrentView = emptyMsg;
                break;
            default:
                return;
        }
        ((Activity) MainActivity.getContext()).runOnUiThread(() -> {
            swapCurrentView(newCurrentView);
        });
    }

    private void swapCurrentView(View newCurrentView) {
        if (currentView != null)
            currentView.setVisibility(View.GONE);
        newCurrentView.setVisibility(View.VISIBLE);
        currentView = newCurrentView;
    }

}
