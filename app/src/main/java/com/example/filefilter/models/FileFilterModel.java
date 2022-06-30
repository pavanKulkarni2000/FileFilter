package com.example.filefilter.models;

import android.util.Log;

import com.example.filefilter.callbacks.IFileFilterChangeListener;

import java.util.LinkedList;
import java.util.List;

public class FileFilterModel {
    private static final String TAG = "FileFilterModel";
    private final List<IFileFilterChangeListener> fileFilterChangeListeners = new LinkedList<>();
    private boolean dateFlag = false;
    private long afterDate = 0;
    private long beforeDate = 0;
    private FileFilterModel.FileType fileType = FileFilterModel.FileType.ALL;

    public FileFilterModel() {
        Log.d(TAG, "FileFilterModel: created model");
    }

    public boolean isDateFlag() {
        return dateFlag;
    }

    public void setDateFlag(boolean dateFlag) {
        this.dateFlag = dateFlag;
        this.fileFilterChangeListeners.forEach(fileFilterChangeListener -> fileFilterChangeListener.onFileDateFilterChange(this.afterDate, this.beforeDate, this.dateFlag));
    }

    public long getAfterDate() {
        return afterDate;
    }

    public void setAfterDate(long afterDate) {
        this.afterDate = afterDate;
        this.fileFilterChangeListeners.forEach(fileFilterChangeListener -> fileFilterChangeListener.onFileDateFilterChange(this.afterDate, this.beforeDate, this.dateFlag));
    }

    public long getBeforeDate() {
        return beforeDate;
    }

    public void setBeforeDate(long beforeDate) {
        this.beforeDate = beforeDate;
        this.fileFilterChangeListeners.forEach(fileFilterChangeListener -> fileFilterChangeListener.onFileDateFilterChange(this.afterDate, this.beforeDate, this.dateFlag));
    }

    public FileFilterModel.FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
        this.fileFilterChangeListeners.forEach(fileFilterChangeListener -> fileFilterChangeListener.onFileTypeFilterChange(fileType));
    }

    public void registerFileFilterChangeListener(IFileFilterChangeListener fileFilterChangeListener) {
        this.fileFilterChangeListeners.add(fileFilterChangeListener);
    }

    public enum FileType {
        AUDIO, DIRECTORY, DOCUMENT, IMAGE, ALL, OTHER, VIDEO
    }
}
