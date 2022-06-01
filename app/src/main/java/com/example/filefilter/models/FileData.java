package com.example.filefilter.models;

public class FileData {
    private String fileName;
    private String fileSize;
    private String fileDate;
    private FileType fileType;

    public FileData(String fileName, String fileSize, String fileDate, FileType fileType) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileDate = fileDate;
        this.fileType = fileType;
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

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
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
