package com.example.filefilter.callbacks;

public interface IFileChangeListener {
    void onFilesChanged();

    void onFileRangeChanged(int start, int end);
}
