package com.example.filefilter.callbacks;

import com.example.filefilter.models.FileListModel;
import com.example.filefilter.utils.FileItem;

import java.util.List;

public interface IFileListChangeListener {
    void onFilesAndStateChanged(List<FileItem> newFiles, FileListModel.State fileListState);

    void onFileRangeChanged(int start, int count);
}
