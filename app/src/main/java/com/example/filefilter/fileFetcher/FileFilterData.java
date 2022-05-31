package com.example.filefilter.fileFetcher;

import com.example.filefilter.FileType;

public class FileFilterData {

    private boolean dateFlag = false;
    private long startDate = 0;
    private long endDate = 0;
    private FileType fileType = FileType.ALL;

    public boolean isDateFlag() {
        return dateFlag;
    }

    public void setDateFlag(boolean dateFlag) {
        this.dateFlag = dateFlag;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public void setFileTypeId(FileType fileType) {
        this.fileType = fileType;
    }
}
