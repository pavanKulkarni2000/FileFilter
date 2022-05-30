package com.example.filefilter.fileFetcher;

import com.example.filefilter.FileType;
import com.example.filefilter.R;

import java.util.Date;

public class FileSearchData {

    private boolean dateFlag=false;
    private Date startDate;
    private Date endDate;
    private FileType fileType= FileType.All;

    public void setFileType(FileType fileType) {
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

    public FileType getFileType() {
        return fileType;
    }

    public void setFileTypeId(FileType fileType) {
        this.fileType = fileType;
    }
}
