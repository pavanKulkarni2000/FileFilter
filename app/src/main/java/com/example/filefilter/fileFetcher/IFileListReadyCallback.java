package com.example.filefilter.fileFetcher;

import java.util.List;

public interface IFileListReadyCallback {
    void onFileListReady(List<FileListItem> files);
}
