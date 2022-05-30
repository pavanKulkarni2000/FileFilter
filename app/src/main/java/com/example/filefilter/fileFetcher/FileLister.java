package com.example.filefilter.fileFetcher;

import static com.example.filefilter.fileFetcher.FileUtil.fileSizeToString;
import static com.example.filefilter.fileFetcher.FileUtil.getDirectorySize;
import static com.example.filefilter.fileFetcher.FileUtil.getFileMimeType;

import android.util.Log;

import com.example.filefilter.FileType;
import com.example.filefilter.R;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FileLister implements Runnable {

    private static final String TAG = "FileLister";
    private final IFileListReadyCallback callback;
    private final String currentFolder;
    private final FileSearchData fileSearchData;
    public FileLister(String currentFolder, FileSearchData fileSearchData, IFileListReadyCallback fileListReadyCallback){
        this.currentFolder=currentFolder;
        this.callback=fileListReadyCallback;
        this.fileSearchData = fileSearchData;
    }
    @Override
    public void run() {
        File parentFolder=new File(currentFolder);
        Map<Boolean,List<FileData>> map= Arrays.stream(parentFolder.listFiles(getFileFilterFromFlags())).map(FileUtil::createFileListItem).collect(Collectors.partitioningBy(fileData -> fileData.getFileType()==FileType.Directory));
//        Log.d(TAG, "run: Finished listing, "+Arrays.toString(files.toArray()));
        map.get(Boolean.FALSE).addAll(map.get(Boolean.TRUE));
        callback.onFileListReady(map.get(Boolean.FALSE));

        //calculate folder sizes later
        map.get(Boolean.TRUE).forEach(fileListItem -> fileListItem.setFileSize(fileSizeToString(getDirectorySize(new File(currentFolder+"/"+fileListItem.getFileName())))));

    }

    private FileFilter getFileFilterFromFlags() {
        return file -> {
            if (fileSearchData.isDateFlag()) {
//                Log.d(TAG, "getFileFilterFromFlags: lastmodified="+file.lastModified()+" start="+fileSearchData.getStartDate().getTime()+" end="+fileSearchData.getEndDate().getTime());
                if (file.lastModified() < fileSearchData.getStartDate().getTime() || file.lastModified() > fileSearchData.getEndDate().getTime())
                    return false;
            }
            return fileSearchData.getFileType() == FileType.All || getFileMimeType(file.toPath()) == fileSearchData.getFileType();
        };
    }

}
