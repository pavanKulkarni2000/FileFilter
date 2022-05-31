package com.example.filefilter;

import com.example.filefilter.fileFetcher.FileFilterData;

public interface IFileFilterChangeListener {
    void onFileFilterChange(FileFilterData fileFilterData);
}
