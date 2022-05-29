package com.example.filefilter.fileFetcher;

public class FileFilterInfo {

    private boolean dateFlag=false;
    private long startDate;
    private long endDate;

    private boolean typeFlag=false;
    private String fileType;


    public boolean isTypeFlag() {
        return typeFlag;
    }

    public void setTypeFlag(boolean typeFlag) {
        this.typeFlag = typeFlag;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

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

    public String getFileType() {
        return fileType;
    }

    public void setFileTypeId(String fileType) {
        this.fileType = fileType;
    }
}
