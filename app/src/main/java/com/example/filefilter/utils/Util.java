package com.example.filefilter.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Util {

    public static List<String> getExternalMounts() {
        File file = new File("/storage");
        String[] list = file.list((parent, childFile) -> !(childFile.equals("emulated") || childFile.equals("self")));
        List<String> out = new ArrayList<>();
        if (list == null || list.length == 0)
            return out;
        for (String fileName : list) {
            out.add("/storage/" + fileName);
        }
        Log.d("TAG", "getExternalMounts: " + out);
        return out;
    }

    public static boolean isBetween(long date, long after, long before) {
        return date > after && date < before;
    }

    public static void managePermissions(Context context, Activity activity) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
}
