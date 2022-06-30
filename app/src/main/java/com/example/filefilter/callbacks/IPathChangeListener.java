package com.example.filefilter.callbacks;

import java.nio.file.Path;

public interface IPathChangeListener {
    void onPathChanged(Path newPath);
}
