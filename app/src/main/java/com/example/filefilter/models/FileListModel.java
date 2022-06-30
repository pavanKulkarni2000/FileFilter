package com.example.filefilter.models;

import android.util.Log;

import com.example.filefilter.callbacks.IFileListChangeListener;
import com.example.filefilter.callbacks.IListStateChangeListener;
import com.example.filefilter.callbacks.IPathChangeListener;
import com.example.filefilter.callbacks.ISelectionChangeListener;
import com.example.filefilter.utils.FileItem;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class FileListModel {

    private static final String TAG = "FileListModel";
    private final List<IFileListChangeListener> fileListChangeListeners = new LinkedList<>();
    private final List<IPathChangeListener> pathChangeListeners = new LinkedList<>();
    private final List<IListStateChangeListener> fileListStateChangeListeners = new LinkedList<>();
    private final List<ISelectionChangeListener> selectionChangeListeners = new LinkedList<>();
    private int selectedCount=0;
    //has to be initialized by controller
    private Path currentPath;
    private List<FileItem> allDirectories, allFiles, fileList;
    private State state;

    public FileListModel() {
        Log.d(TAG, "FileListModel: created model");
    }

    /*
    Getters
     */
    public List<FileItem> getAllDirectories() {
        return allDirectories;
    }

    public List<FileItem> getAllFiles() {
        return allFiles;
    }

    public Path getCurrentPath() {
        return currentPath;
    }

    public List<FileItem> getFileList() {
        return fileList;
    }

    public State getState() {
        return state;
    }

    public boolean isItemSelected(int itemIndex){
        return fileList.get(itemIndex).isSelected();
    }

    public int getSelectedCount(){
        return selectedCount;
    }

    /*
    Setters
     */

    public void setAllDirectories(List<FileItem> allDirectories) {
        this.allDirectories = allDirectories;
    }

    public void setAllFiles(List<FileItem> allFiles) {
        this.allFiles = allFiles;
    }

    public void setCurrentPath(Path currentPath) {
        this.currentPath = currentPath;
        this.pathChangeListeners.forEach(pathChangeListener -> pathChangeListener.onPathChanged(this.currentPath));
    }

    public void setFileListAndUpdateStateAndCount(List<FileItem> fileList) {
        this.selectedCount=0;
        this.selectionChangeListeners.forEach(selectionChangeListener -> selectionChangeListener.onSelectionChange(selectedCount));
        this.fileList = fileList;
        this.state = fileList.isEmpty() ? FileListModel.State.EMPTY : FileListModel.State.LIST;
        this.fileListChangeListeners.forEach(fileListChangeListener -> fileListChangeListener.onFilesAndStateChanged(this.fileList, this.state));
    }

    public void setState(State state) {
        if (this.state != state) {
            this.state = state;
            fileListStateChangeListeners.forEach(fileListStateChangeListener -> fileListStateChangeListener.onFileListStateChanged(this.state));
        }
    }

    public void setFileSizes(List<String> sizes, int offset) {
        int i;
        ListIterator<FileItem> iterator = fileList.listIterator();
        for (i = 0; i < offset; i++)
            iterator.next();
        for (String size : sizes)
            iterator.next().setFileSize(size);
        fileListChangeListeners.forEach(fileListChangeListener -> fileListChangeListener.onFileRangeChanged(offset, sizes.size()));
    }

    public void setItemSelected(boolean selected, int itemIndex ){
        fileList.get(itemIndex).setSelected(selected);
        if(selected) selectedCount++;
        else selectedCount--;
        this.selectionChangeListeners.forEach(selectionChangeListener -> selectionChangeListener.onSelectionChange(selectedCount));
    }

    public void unSelectAllFiles() {
        selectedCount=0;
        this.selectionChangeListeners.forEach(selectionChangeListener -> selectionChangeListener.onSelectionChange(selectedCount));
        fileList.forEach(fileItem -> fileItem.setSelected(false));
        this.fileListChangeListeners.forEach(fileListChangeListener->fileListChangeListener.onFileRangeChanged(0,fileList.size()));
    }

    public void selectAllFiles() {
        selectedCount=fileList.size();
        this.selectionChangeListeners.forEach(selectionChangeListener->selectionChangeListener.onSelectionChange(selectedCount));
        fileList.forEach(fileItem -> fileItem.setSelected(true));
        this.fileListChangeListeners.forEach(fileListChangeListener -> fileListChangeListener.onFileRangeChanged(0,fileList.size()));
    }

    /*
    Registers
     */

    public void registerFileListStateChangeListeners(IListStateChangeListener fileListStateChangeListener) {
        this.fileListStateChangeListeners.add(fileListStateChangeListener);
    }

    public void registerPathChangeListeners(IPathChangeListener pathChangeListener) {
        this.pathChangeListeners.add(pathChangeListener);
    }

    public void registerFileListChangeListener(IFileListChangeListener listener) {
        this.fileListChangeListeners.add(listener);
    }

    public void registerSelectionChangeListeners(ISelectionChangeListener listener) {
        this.selectionChangeListeners.add(listener);
    }

    public enum State {
        LIST, LOADING, EMPTY
    }
}
