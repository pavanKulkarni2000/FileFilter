package com.example.filefilter;

public interface IFileChangeListener {
    void onFilesChanged();

    void onFileRangeChanged(int start, int end);
}
