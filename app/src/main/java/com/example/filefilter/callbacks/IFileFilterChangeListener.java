package com.example.filefilter.callbacks;

import com.example.filefilter.models.FileFilterModel;

public interface IFileFilterChangeListener {
    void onFileTypeFilterChange(FileFilterModel.FileType fileType);

    void onFileDateFilterChange(long createdAfter, long createdBefore, boolean applyFilter);
}
