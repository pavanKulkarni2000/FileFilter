package com.example.filefilter.fileFetcher;

public class FileListItem {
    private String fileName;
    private String fileSize;
    private String fileDate;

    public FileListItem(String fileName, String fileSize, String fileDate) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileDate = fileDate;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getFileDate() {
        return fileDate;
    }

    @Override
    public String toString() {
        return "FileListItem{" +
                "fileName='" + fileName + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", fileDate='" + fileDate + '\'' +
                '}';
    }
}
