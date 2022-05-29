package com.example.filefilter.fileFetcher;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileLister implements Runnable {
    private static final String TAG = "FileLister";
    IFileListReadyCallback callback;
    String currentFolder;
    public FileLister(String currentFolder, IFileListReadyCallback fileListReadyCallback){
        this.currentFolder=currentFolder;
        this.callback=fileListReadyCallback;
    }
    @Override
    public void run() {
        File parentFolder=new File(currentFolder);
        List<FileListItem> files = Arrays.stream(parentFolder.listFiles()).map(FileUtil::createFileListItem).collect(Collectors.toList());
        Log.d(TAG, "run: Finished listing, "+Arrays.toString(files.toArray()));
        callback.onFileListReady(files);
    }
}
