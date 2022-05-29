package com.example.filefilter.fileFetcher;

import com.example.filefilter.R;

import java.util.Date;

public class FileSearchData {

    private boolean dateFlag=false;
    private Date startDate;
    private Date endDate;
    private int fileType= R.id.mime_all;

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public boolean isDateFlag() {
        return dateFlag;
    }

    public void setDateFlag(boolean dateFlag) {
        this.dateFlag = dateFlag;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileTypeId(int fileType) {
        this.fileType = fileType;
    }
}
