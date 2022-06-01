package com.example.filefilter.controllers;

import static com.example.filefilter.utils.FileUtil.fileDateToString;
import static com.example.filefilter.utils.FileUtil.fileSizeToString;
import static com.example.filefilter.utils.FileUtil.getDirectorySize;
import static com.example.filefilter.utils.FileUtil.getFileType;

import android.util.Log;

import com.example.filefilter.models.FileType;
import com.example.filefilter.models.FileData;
import com.example.filefilter.models.FileFilterData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileManager {
    private static final String TAG = "FileManager";
    //resource
    private final ExecutorService executorService;
    private FileListViewManager fileListViewManager;
    private FileFilterManager fileFilterManager;
    private Future<?> searchResult;

    public FileManager() {
        //initialize executor services for background work
        executorService = Executors.newFixedThreadPool(4);
    }

    public void setFileListViewManager(FileListViewManager fileListViewManager) {
        this.fileListViewManager = fileListViewManager;
    }

    public void setFileFilterManager(FileFilterManager fileFilterManager) {
        this.fileFilterManager = fileFilterManager;
    }

    void search() {

        if (searchResult != null && !searchResult.isDone()) {
            searchResult.cancel(true);
        }

        Log.d(TAG, "search: sending search requests for path " + fileListViewManager.getCurrentPath());
        searchResult = executorService.submit(() -> {
            Path searchPath = fileListViewManager.getCurrentPath();
            List<FileData> directories = new LinkedList<>();
            List<FileData> files = new LinkedList<>();
            try {
                Files.list(searchPath)
                        .forEach(filePath -> {
                            FileFilterData fileFilterData = fileFilterManager.getFileFilterData();

                            FileType fileType = getFileType(filePath);
                            if (fileFilterData.getFileType() != FileType.ALL && fileFilterData.getFileType() != fileType)
                                return;

                            File file = filePath.toFile();
                            long lastModifiedTime = file.lastModified();
                            if (fileFilterData.isDateFlag() && (fileFilterData.getStartDate() > lastModifiedTime || fileFilterData.getEndDate() < lastModifiedTime))
                                return;

                            if (fileType == FileType.DIRECTORY) {
                                directories.add(new FileData(file.getName(), null, fileDateToString(lastModifiedTime), fileType));
                            } else {
                                files.add(new FileData(file.getName(), fileSizeToString(file.length()), fileDateToString(lastModifiedTime), fileType));
                            }
                        });
            } catch (IOException e) {
                Log.e(TAG, "search: Couldn't list path", e);
            }
            fileListViewManager.clearFiles();
            fileListViewManager.addFiles(directories);
            fileListViewManager.addFiles(files);
            fileListViewManager.onFilesChanged();
            List<String> directorySizes=new ArrayList<>(directories.size());
            directories.forEach(directory -> {
                Path dirPath = searchPath.resolve(directory.getFileName());
                directorySizes.add(fileSizeToString(getDirectorySize(dirPath.toFile())));
            });

            for(int i=0;i<directories.size();i++){
                directories.get(i).setFileSize(directorySizes.get(i));
            }
            fileListViewManager.onFileRangeChanged(0, directories.size());
        });
    }
}
