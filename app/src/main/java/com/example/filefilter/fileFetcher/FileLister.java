package com.example.filefilter.fileFetcher;

import android.util.Log;

import com.example.filefilter.R;

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
    private final FileSearchData fileSearchData;
    public FileLister(String currentFolder, FileSearchData fileSearchData, IFileListReadyCallback fileListReadyCallback){
        this.currentFolder=currentFolder;
        this.callback=fileListReadyCallback;
        this.fileSearchData = fileSearchData;
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
            if (fileSearchData.isDateFlag()) {
                Log.d(TAG, "getFileFilterFromFlags: lastmodified="+file.lastModified()+" start="+fileSearchData.getStartDate().getTime()+" end="+fileSearchData.getEndDate().getTime());
                if (file.lastModified() < fileSearchData.getStartDate().getTime() || file.lastModified() > fileSearchData.getEndDate().getTime())
                    return false;
            }
            if(fileSearchData.getFileType()== R.id.mime_all){
                return true;
            }

            String mime = null;
            try {
                String ext = Files.probeContentType(file.toPath());
                mime=((ext==null)?"other":ext.split("/")[0]);
            } catch (IOException e) {
                Log.e(TAG, "getFileFilterFromFlags: ",e );
            }

            switch (fileSearchData.getFileType()){
                case R.id.mime_other:
                    return mime==null || mime.equals("other");
                case R.id.mime_doc:
                    return mime!=null && (mime.equals("application") || mime.equals("text"));
                case R.id.mime_image:
                    return mime!=null && mime.equals("image");
                case R.id.mime_video:
                    return mime!=null && mime.equals("video") ;
                case R.id.mime_audio:
                    return mime!=null && mime.equals("audio");
                default:
                    Log.e(TAG, "getFileFilterFromFlags: unknown file type set in filters"+fileSearchData.getFileType() );
                    return false;
            }
        };
    }

}
