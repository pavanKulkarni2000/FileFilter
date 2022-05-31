package com.example.filefilter.folderPicker;

public interface IFolderChangeListener {
    void onChildDirectoryResolved(String folder);
    void onParentDirectoryResolved();
}
