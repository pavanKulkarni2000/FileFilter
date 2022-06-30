package com.example.filefilter.utils;

import android.util.Log;

import com.example.filefilter.models.FileFilterModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Queue;
import java.util.Stack;

public class FileUtil {
    public static final long UNIT_KILO_BYTE = 1024;
    public static final long UNIT_MEGA_BYTE = UNIT_KILO_BYTE * UNIT_KILO_BYTE;
    public static final long UNIT_GIGA_BYTE = UNIT_KILO_BYTE * UNIT_KILO_BYTE * UNIT_KILO_BYTE;
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
    private static final String TAG = "FileUtil";

    public static FileItem createFileListItem(java.io.File file) {

        String name = file.getName();
        String size = null;
        String date = fileDateToString(file.length());
        FileFilterModel.FileType type;
        if (file.isDirectory()) {
            type = FileFilterModel.FileType.DIRECTORY;
            //calculate file size later
        } else {
            size = fileSizeToString(file.length());
            type = getFileType(file.toPath());
        }
        return new FileItem(name, size, date, type);
    }

    public static FileFilterModel.FileType getFileType(Path path) {
        if (Files.isDirectory(path))
            return FileFilterModel.FileType.DIRECTORY;
        String mime;
        try {
            String mimeTypeSubType = Files.probeContentType(path);
            if (mimeTypeSubType == null) {
                mime = "OTHER";
            } else {
                mime = mimeTypeSubType.split("/")[0].toUpperCase();
            }
        } catch (IOException e) {
            Log.e(TAG, "getFileFilterFromFlags: ", e);
            mime = "OTHER";
        }
        if (Objects.equals(mime, "APPLICATION") || mime.equals("TEXT"))
            mime = "DOCUMENT";
        try {
            return FileFilterModel.FileType.valueOf(mime);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "getFileType: got mime type " + mime);
            return FileFilterModel.FileType.OTHER;
        }
    }


    public static long getDirectorySize(Path directory) {
        long length = 0;
        LinkedList<Path> directoryQueue=new LinkedList<>(Collections.singletonList(directory));
        try {
            while (!directoryQueue.isEmpty()){
                Path directoryPath = directoryQueue.removeFirst();
                if(Files.isDirectory(directoryPath))
                    directoryQueue.addLast(directoryPath);
                else
                    length+=Files.size(directoryPath);
            }
        } catch (IOException e) {
            Log.e(TAG, "getDirectorySize: error listing path "+directory,e);
        }
        return length;
    }

    public static String fileSizeToString(long bytes) {
        if (bytes > UNIT_GIGA_BYTE) {
            double gbs = (double) bytes / (double) UNIT_GIGA_BYTE;
            return String.format("%.2f GB", gbs);
        } else if (bytes > UNIT_MEGA_BYTE) {
            double mbs = (double) bytes / (double) UNIT_MEGA_BYTE;
            return String.format("%.2f MB", mbs);
        } else if (bytes > UNIT_KILO_BYTE) {
            double kbs = (double) bytes / (double) UNIT_MEGA_BYTE;
            return String.format("%.2f KB", kbs);
        } else {
            return String.format("%d B", bytes);
        }
    }

    public static String fileDateToString(long lastModified) {
        return simpleDateFormat.format(lastModified);
    }
}
