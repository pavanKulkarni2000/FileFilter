package com.example.filefilter.fileFetcher;

import android.util.Log;

import com.example.filefilter.FileType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FileUtil {
    public static final long UNIT_KILO_BYTE = 1024;
    public static final long UNIT_MEGA_BYTE = UNIT_KILO_BYTE * UNIT_KILO_BYTE;
    public static final long UNIT_GIGA_BYTE = UNIT_KILO_BYTE * UNIT_KILO_BYTE * UNIT_KILO_BYTE;
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
    private static final String TAG = "FileUtil";

    public static FileData createFileListItem(File file) {

        String name = file.getName();
        String size = null;
        String date = fileDateToString(file.length());
        FileType type;
        if (file.isDirectory()) {
            type = FileType.DIRECTORY;
            //calculate file size later
        } else {
            size = fileSizeToString(file.length());
            type = getFileType(file.toPath());
        }
        return new FileData(name, size, date, type);
    }

    public static FileType getFileType(Path path) {
        if (Files.isDirectory(path))
            return FileType.DIRECTORY;
        String mime;
        try {
            String ext = Files.probeContentType(path);
            if (ext == null) {
                mime = "OTHER";
            } else {
                mime = ext.substring(0, 1).toUpperCase() + ext.substring(1);
            }
        } catch (IOException e) {
            Log.e(TAG, "getFileFilterFromFlags: ", e);
            mime = "OTHER";
        }
        return FileType.valueOf(mime);
    }

    public static long getDirectorySize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += getDirectorySize(file);
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
