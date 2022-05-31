package com.example.filefilter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileManager {
    //resource
    private ExecutorService executorService;
    private IFileChangeListener fileChangeListener;

    FileManager(IFileChangeListener fileChangeListener){
        this.fileChangeListener=fileChangeListener;

        //initialize executor services for background work
        executorService= Executors.newFixedThreadPool(4);
    }
    void search(){
        fileChangeListener.onFilesChanged();
    }
}
