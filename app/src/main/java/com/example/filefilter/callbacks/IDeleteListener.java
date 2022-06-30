package com.example.filefilter.callbacks;

public interface IDeleteListener {
    void onDelete(int deletedCount, int totalCount);

    void onStartDelete(int totalCount);

    void onFinishDelete(int totalCount);
}
