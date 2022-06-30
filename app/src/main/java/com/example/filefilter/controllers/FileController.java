package com.example.filefilter.controllers;

import static com.example.filefilter.utils.FileUtil.fileDateToString;
import static com.example.filefilter.utils.FileUtil.fileSizeToString;
import static com.example.filefilter.utils.FileUtil.getDirectorySize;
import static com.example.filefilter.utils.FileUtil.getFileType;
import static com.example.filefilter.utils.FileUtil.simpleDateFormat;

import android.app.Activity;
import android.util.Log;

import com.example.filefilter.MainActivity;
import com.example.filefilter.models.DeleteModel;
import com.example.filefilter.models.FileFilterModel;
import com.example.filefilter.models.FileListModel;
import com.example.filefilter.utils.FileItem;
import com.example.filefilter.utils.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileController {
    private static final String TAG = "FileListViewController";
    private final List<FileItem> rootFolders;
    private final ExecutorService executorService;
    private final FileFilterModel fileFilterModel;
    private final FileListModel fileListModel;
    private final DeleteModel deleteModel;
    private final Path rootPath = Paths.get("/");
    private Future<?> searchResult, filterResult;

    public FileController(ExecutorService executorService, FileListModel fileListModel, FileFilterModel fileFilterModel, DeleteModel deleteModel) {

        //dependency injection
        this.executorService = executorService;
        this.fileListModel = fileListModel;
        this.fileFilterModel = fileFilterModel;
        this.deleteModel = deleteModel;

        //initialize currentFolder variable to internal storage root folder
        String internalStorage = System.getenv("EXTERNAL_STORAGE");
        //exit activity if can't find internal storage
        if (internalStorage == null) {
            ((Activity) MainActivity.getContext()).finish();
        }

        rootFolders = new LinkedList<>();
        rootFolders.add(new FileItem(internalStorage.replaceFirst("/", ""), null, null, FileFilterModel.FileType.DIRECTORY));
        rootFolders.addAll(Util.getExternalMounts().stream().map(mount -> new FileItem(mount.replaceFirst("/", ""), null, null, FileFilterModel.FileType.DIRECTORY)).collect(Collectors.toList()));

        Log.d(TAG, " created controller");
    }

    void searchPath() {

        if (fileListModel.getCurrentPath().toString().equals("/")) {
            fileListModel.setFileListAndUpdateStateAndCount(rootFolders);
            return;
        }

        fileListModel.setState(FileListModel.State.LOADING);

        if (searchResult != null && !searchResult.isDone()) {
            searchResult.cancel(true);
        }

        Log.d(TAG, "search: sending search requests for path " + fileListModel.getCurrentPath());
        searchResult = executorService.submit(() -> {
            Path searchPath = fileListModel.getCurrentPath();
            List<FileItem> directories = new LinkedList<>();
            List<FileItem> files = new LinkedList<>();
            List<FileItem> allDirectories = new LinkedList<>();
            List<FileItem> allFiles = new LinkedList<>();
            try {
                Files.list(searchPath).forEach(filePath -> {

                    FileFilterModel.FileType fileType = getFileType(filePath);
                    File file = filePath.toFile();
                    long lastModifiedTime = file.lastModified();
                    FileItem item = new FileItem(
                            file.getName(),
                            fileType == FileFilterModel.FileType.DIRECTORY ? null : fileSizeToString(file.length()),
                            fileDateToString(lastModifiedTime),
                            fileType
                    );
                    if (fileType == FileFilterModel.FileType.DIRECTORY) allDirectories.add(item);
                    else allFiles.add(item);

                    if (fileFilterModel.getFileType() != FileFilterModel.FileType.ALL && fileFilterModel.getFileType() != fileType)
                        return;

                    if (fileFilterModel.isDateFlag() && (fileFilterModel.getAfterDate() > lastModifiedTime || fileFilterModel.getBeforeDate() < lastModifiedTime))
                        return;

                    if (fileType == FileFilterModel.FileType.DIRECTORY) directories.add(item);
                    else files.add(item);
                });
                fileListModel.setAllDirectories(allDirectories);
                fileListModel.setAllFiles(allFiles);
                fileListModel.setFileListAndUpdateStateAndCount(Stream.concat(directories.stream(), files.stream()).collect(Collectors.toList()));
                List<String> directorySizes = new ArrayList<>(directories.size());
                Log.d(TAG, "searchPath: Calculating size..");
                long startTime = System.currentTimeMillis();

                try {
                    directories.forEach(directory -> {
                        Path dirPath = searchPath.resolve(directory.getFileName());
                        directorySizes.add(fileSizeToString(getDirectorySize(dirPath)));
                    });
                } catch (Exception e) {
                    Log.e(TAG, "searchPath: failed to calculate sizes",e );
                }
                long difference = System.currentTimeMillis() - startTime;
                Log.d(TAG, "searchPath: Finished calculating size in "+(difference/1000)+"s.");
                fileListModel.setFileSizes(directorySizes, 0);
            } catch (IOException e) {
                Log.e(TAG, "search: Couldn't list path", e);
            }
        });
    }

    private void applyFilters() {
        Log.d(TAG, "applyFilters: Applyig filter");
        if(fileListModel.getCurrentPath().toString().equals("/")){
            return;
        }
        if (searchResult != null && !searchResult.isDone()) {
            searchResult.cancel(true);
            searchPath();
        } else if (fileListModel.getAllDirectories() == null || fileListModel.getAllFiles() == null) {
            searchPath();
        } else {

            fileListModel.setState(FileListModel.State.LOADING);

            if (filterResult != null && !filterResult.isDone()) {
                filterResult.cancel(true);
            }

            filterResult = executorService.submit(() -> {
                List<FileItem> allDirectories = fileListModel.getAllDirectories();
                List<FileItem> allFiles = fileListModel.getAllFiles();
                Stream<FileItem> list1;
                if (fileFilterModel.getFileType() == FileFilterModel.FileType.DIRECTORY)
                    list1 = allDirectories.stream();
                else if (fileFilterModel.getFileType() == FileFilterModel.FileType.ALL)
                    list1 = Stream.concat(allDirectories.stream(), allFiles.stream());
                else
                    list1 = allFiles.stream().filter(fileItem -> fileItem.getFileType() == fileFilterModel.getFileType());
                if (fileFilterModel.isDateFlag()) {
                    list1 = list1.filter(fileItem -> {
                        long lastUpdated = 0;
                        try {
                            lastUpdated = simpleDateFormat.parse(fileItem.getFileDate()).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return Util.isBetween(lastUpdated, fileFilterModel.getAfterDate(), fileFilterModel.getBeforeDate());
                    });
                }
                fileListModel.setFileListAndUpdateStateAndCount(list1.collect(Collectors.toList()));
            });
        }
    }

    public void onFileClicked(String fileName) {
        Path path = fileListModel.getCurrentPath().resolve(fileName);
        if (Files.isDirectory(path)) {
            setCurrentPath(path);
        } else {
            //TODO
        }
    }

    public void onBackPressed() {
        if(fileListModel.getSelectedCount()!=0){
            fileListModel.unSelectAllFiles();
        }else {
            Path path = fileListModel.getCurrentPath();
            if (rootFolders.stream().map(FileItem::getFileName).anyMatch(folder -> path.toString().replaceFirst("/", "").equals(folder)))
                setCurrentPath(rootPath);
            else if (Files.isDirectory(path.getParent())) setCurrentPath(path.getParent());
        }
    }

    public void setCurrentPath(Path newPath) {
        fileListModel.setCurrentPath(newPath);
        searchPath();
    }

    public void onFileFilterTypeSelected(FileFilterModel.FileType newType) {
        if (fileFilterModel.getFileType() != newType) {
            fileFilterModel.setFileType(newType);
                applyFilters();
        }
    }

    public void onFileFilterDateAfterSet(long after) {
        fileFilterModel.setAfterDate(after);
        if (fileFilterModel.isDateFlag()) {
                applyFilters();
        }
    }

    public void onFileFilterDateBeforeSet(long before) {
        fileFilterModel.setBeforeDate(before);
        if (fileFilterModel.isDateFlag()) {
                applyFilters();
        }
    }

    public void onFileFilterDateFlagSet(boolean newFlag) {
        fileFilterModel.setDateFlag(newFlag);
            applyFilters();
    }

    public void setSelection(boolean selected, int itemIndex) {
        fileListModel.setItemSelected(selected, itemIndex);
    }

    public void deleteSelected() {
        if (searchResult != null && !searchResult.isDone()) {
            searchResult.cancel(true);
        }

        if (filterResult != null && !filterResult.isDone()) {
            filterResult.cancel(true);
        }

        deleteModel.set(fileListModel.getSelectedCount());

        fileListModel.setState(FileListModel.State.LOADING);

        executorService.submit(() -> {
            List<FileItem> allDirectories = fileListModel.getAllDirectories();
            List<FileItem> allFiles = fileListModel.getAllFiles();
            List<FileItem> fileList = new LinkedList<>(fileListModel.getFileList());
            Iterator<FileItem> iterator = fileList.listIterator();
            int totalCount = fileListModel.getSelectedCount();
            int steps = Math.min(totalCount, 1000);
            int stepSize = totalCount / steps;
            int currentStepProgress = 0;
            int index=0;
            while (iterator.hasNext()) {
                FileItem fileItem = iterator.next();
                if (fileListModel.isItemSelected(index++)) {
                    try {
                        Path fileToDelete = fileListModel.getCurrentPath().resolve(fileItem.getFileName());
                        Files.delete(fileToDelete);
                        iterator.remove();
                        if(fileItem.getFileType()== FileFilterModel.FileType.DIRECTORY)
                            allDirectories.remove(fileItem);
                        else
                            allFiles.remove(fileItem);
                        currentStepProgress++;
                        if (currentStepProgress == stepSize) {
                            deleteModel.addDeleted(currentStepProgress);
                            currentStepProgress = 0;
                        }
                        Log.d(TAG, "deleteSelected: deleted "+fileItem.getFileName());
                    } catch (Exception e) {
                        Log.e(TAG, "deleteSelected: couldn't delete file " + fileItem.getFileName(), e);
                    }
                }
            }
            Log.d(TAG, "deleteSelected: finished");
            if (currentStepProgress != 0) {
                deleteModel.setDeletedCount(currentStepProgress);
            }
            deleteModel.reset();
            Log.d(TAG, "deleteSelected: reset");
            fileListModel.setFileListAndUpdateStateAndCount(fileList);
        });

    }
}
