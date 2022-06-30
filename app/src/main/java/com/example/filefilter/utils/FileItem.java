package com.example.filefilter.utils;

import com.example.filefilter.models.FileFilterModel;

public class FileItem {
    private String fileName;
    private String fileSize;
    private String fileDate;
    private FileFilterModel.FileType fileType;
    private boolean selected = false;

    public FileItem(String fileName, String fileSize, String fileDate, FileFilterModel.FileType fileType) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileDate = fileDate;
        this.fileType = fileType;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileDate() {
        return fileDate;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }

    public FileFilterModel.FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileFilterModel.FileType fileType) {
        this.fileType = fileType;
    }

    @Override
    public String toString() {
        return "FileListItem{" +
                "fileName='" + fileName +
                ", fileSize='" + fileSize +
                ", fileDate='" + fileDate +
                ", fileType='" + fileType.name() +
                '}';
    }
}
