package com.example.filefilter.callbacks;

import com.example.filefilter.models.FileListModel;

public interface IListStateChangeListener {
    void onFileListStateChanged(FileListModel.State newState);
}
