package com.example.filefilter.models;

import com.example.filefilter.callbacks.IDeleteListener;

import java.util.LinkedList;
import java.util.List;

public class DeleteModel {
    private int totalCount = 0;
    private int deletedCount = 0;
    private List<IDeleteListener> deleteListeners = new LinkedList<>();

    public void set(int totalCount) {
        this.totalCount = totalCount;
        deletedCount = 0;
        deleteListeners.forEach(deleteListener -> deleteListener.onStartDelete(totalCount));
    }

    public void addDeleted(int count) {
        setDeletedCount(deletedCount + count);
    }

    public void setDeletedCount(int count) {
        this.deletedCount = count;
        if (deletedCount != totalCount) {
            this.deleteListeners.forEach(deleteListener -> deleteListener.onDelete(deletedCount, totalCount));
        }
    }

    public void reset() {
        this.deleteListeners.forEach(deleteListener -> deleteListener.onFinishDelete(deletedCount));
        deletedCount = 0;
        totalCount = 0;
    }

    public void registerDeleteListener(IDeleteListener deleteListener) {
        this.deleteListeners.add(deleteListener);
    }
}
