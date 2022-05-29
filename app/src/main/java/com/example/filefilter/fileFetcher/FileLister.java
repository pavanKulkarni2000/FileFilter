package com.example.filefilter.fileFetcher;

import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileLister implements Runnable {

    private static final String TAG = "FileLister";
    private final IFileListReadyCallback callback;
    private final String currentFolder;
    private final FileFilterInfo fileFilterInfo;
    public FileLister(String currentFolder, FileFilterInfo fileFilterInfo, IFileListReadyCallback fileListReadyCallback){
        this.currentFolder=currentFolder;
        this.callback=fileListReadyCallback;
        this.fileFilterInfo = fileFilterInfo;
    }
    @Override
    public void run() {
        File parentFolder=new File(currentFolder);
        List<FileListItem> files = Arrays.stream(parentFolder.listFiles(getFileFilterFromFlags())).map(FileUtil::createFileListItem).collect(Collectors.toList());
//        Log.d(TAG, "run: Finished listing, "+Arrays.toString(files.toArray()));
        callback.onFileListReady(files);
    }

    private FileFilter getFileFilterFromFlags() {
        return file -> {
            if (fileFilterInfo.isDateFlag()) {
                if (file.lastModified() < fileFilterInfo.getStartDate() || file.lastModified() > fileFilterInfo.getEndDate())
                    return false;
            }
            if(fileFilterInfo.isTypeFlag()){
                String ext = null;
                try {
                    String mime = Files.probeContentType(file.toPath());
                    ext=((mime==null)?"other":mime.split("/")[0]);
                    if(ext.equals("text")) ext="application";
                } catch (IOException e) {
                    Log.e(TAG, "getFileFilterFromFlags: ",e );
                    ext="other";
                }
                return ext.equals(fileFilterInfo.getFileType());
            }
            return true;
        };
    }

}
