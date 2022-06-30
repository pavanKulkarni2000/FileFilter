package com.example.filefilter;

import static com.example.filefilter.utils.Util.managePermissions;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.filefilter.controllers.FileController;
import com.example.filefilter.models.DeleteModel;
import com.example.filefilter.models.FileFilterModel;
import com.example.filefilter.models.FileListModel;
import com.example.filefilter.views.DeleteView;
import com.example.filefilter.views.FileFilterView;
import com.example.filefilter.views.FileListView;

import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static MainActivity instance;
    //models
    private FileListModel fileListModel;
    private FileFilterModel fileFilterModel;
    private DeleteModel deleteModel;
    //views
    private FileListView fileListView;
    private FileFilterView fileFilterView;
    private DeleteView deleteView;
    //controllers
    private FileController fileController;
    private ExecutorService executorService;

    public static Context getContext() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        managePermissions(this, this);

        init();

    }

    private void init() {

        this.fileFilterModel = new FileFilterModel();
        this.fileListModel = new FileListModel();
        this.deleteModel = new DeleteModel();

        this.executorService = Executors.newFixedThreadPool(8);
        this.fileController = new FileController(executorService, fileListModel, fileFilterModel, deleteModel);

        this.fileFilterView = new FileFilterView(findViewById(R.id.file_filter_parent), fileController, fileFilterModel);
        this.fileListView = new FileListView(findViewById(R.id.file_list_parent), fileController);
        this.deleteView = new DeleteView(findViewById(R.id.delete_layout), fileController, fileListModel);

        this.fileListModel.registerFileListChangeListener(fileListView);
        this.fileListModel.registerPathChangeListeners(fileListView);
        this.fileListModel.registerFileListStateChangeListeners(fileListView);
        this.fileListModel.registerSelectionChangeListeners(deleteView);

        this.deleteModel.registerDeleteListener(deleteView);

        //initialize view and model
        this.fileController.setCurrentPath(Paths.get("/"));
    }

    @Override
    public void onBackPressed() {
        fileController.onBackPressed();
    }
}