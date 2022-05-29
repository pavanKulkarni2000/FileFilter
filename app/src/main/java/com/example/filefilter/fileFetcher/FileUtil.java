package com.example.filefilter.fileFetcher;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtil {
    public static final long UNIT_KILO_BYTE =1024;
    public static final long UNIT_MEGA_BYTE=UNIT_KILO_BYTE*UNIT_KILO_BYTE;
    public static final long UNIT_GIGA_BYTE=UNIT_KILO_BYTE*UNIT_KILO_BYTE*UNIT_KILO_BYTE;
    private static final String TAG = "FileUtil";

    public static FileListItem createFileListItem(File file){


            return new FileListItem(
                    file.getName(),
                    fileSizeToString(file.isDirectory()?folderSize(file):file.length()),
                    getFileDate(file)
            );
    }
    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }
    public static String fileSizeToString(long bytes){
        if(bytes>UNIT_GIGA_BYTE){
            double gbs= (double) bytes/(double) UNIT_GIGA_BYTE;
            return String.format("%.2f GB",gbs);
        }else if(bytes>UNIT_MEGA_BYTE){
            double mbs= (double) bytes/(double) UNIT_MEGA_BYTE;
            return String.format("%.2f MB",mbs);
        }else if(bytes>UNIT_KILO_BYTE){
            double kbs= (double) bytes/(double) UNIT_MEGA_BYTE;
            return String.format("%.2f KB",kbs);
        }else{
            return String.format("%d B",bytes);
        }
    }
    public static String getFileDate(File file){
        return new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(file.lastModified());
    }
}
