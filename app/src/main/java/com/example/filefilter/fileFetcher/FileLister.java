package com.example.filefilter.fileFetcher;

import static com.example.filefilter.fileFetcher.FileUtil.fileSizeToString;
import static com.example.filefilter.fileFetcher.FileUtil.getDirectorySize;
import static com.example.filefilter.fileFetcher.FileUtil.getFileMimeType;

import com.example.filefilter.FileType;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileLister implements Runnable {

    private static final String TAG = "FileLister";
    private final IFileListReadyCallback callback;
    private final String currentFolder;
    private final FileFilterData fileFilterData;
    public FileLister(String currentFolder, FileFilterData fileFilterData, IFileListReadyCallback fileListReadyCallback){
        this.currentFolder=currentFolder;
        this.callback=fileListReadyCallback;
        this.fileFilterData = fileFilterData;
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
            if (fileFilterData.isDateFlag()) {
//                Log.d(TAG, "getFileFilterFromFlags: lastmodified="+file.lastModified()+" start="+fileSearchData.getStartDate().getTime()+" end="+fileSearchData.getEndDate().getTime());
                if (file.lastModified() < fileFilterData.getStartDate().getTime() || file.lastModified() > fileFilterData.getEndDate().getTime())
                    return false;
            }
            return fileFilterData.getFileType() == FileType.All || getFileMimeType(file.toPath()) == fileFilterData.getFileType();
        };
    }

}
