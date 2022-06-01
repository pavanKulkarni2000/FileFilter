package com.example.filefilter.callbacks;

public interface IFolderChangeListener {
    void onChildDirectoryResolved(String folder);
    void onParentDirectoryResolved();
}
