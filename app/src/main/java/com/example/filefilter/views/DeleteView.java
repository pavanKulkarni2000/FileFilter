package com.example.filefilter.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.filefilter.MainActivity;
import com.example.filefilter.R;
import com.example.filefilter.callbacks.IDeleteListener;
import com.example.filefilter.callbacks.ISelectionChangeListener;
import com.example.filefilter.controllers.FileController;
import com.example.filefilter.models.FileListModel;
import com.example.filefilter.models.SelectionModel;

import java.util.Locale;

public class DeleteView implements ISelectionChangeListener, IDeleteListener {
    private final View parentView;
    private final TextView textView;
    private final ImageButton deleteButton;
    private final FileController controller;
    private final ProgressBar progressBar;
    private final TextView selectAllButton;

    public DeleteView(View parentView, FileController fileController, FileListModel fileListModel) {
        this.parentView = parentView;
        this.textView = parentView.findViewById(R.id.delete_count);
        this.deleteButton = parentView.findViewById(R.id.delete_button);
        this.selectAllButton = parentView.findViewById(R.id.delete_select_all);
        this.progressBar = parentView.findViewById(R.id.delete_progress_bar);
        this.controller = fileController;
        deleteButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getContext());
            builder.setMessage("Continue deleting " + fileListModel.getSelectedCount() + " files?")
                    .setTitle("Delete");
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    controller.deleteSelected();
                }
            });
            builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        selectAllButton.setOnClickListener(view -> fileListModel.selectAllFiles());
    }

    @Override
    public void onSelectionChange(int count) {
        ((Activity) MainActivity.getContext()).runOnUiThread(() -> {
            if (count == 0)
                parentView.setVisibility(View.GONE);
            else {
                if (count == 1)
                    parentView.setVisibility(View.VISIBLE);
                this.textView.setText(String.format(Locale.ROOT, "Delete %d files", count));
            }
        });
    }

    @Override
    public void onDelete(int deletedCount, int totalCount) {
        ((Activity) MainActivity.getContext()).runOnUiThread(() -> {
            this.textView.setText(String.format(Locale.ROOT, "Deleted %d/%d", deletedCount, totalCount));
        });
    }

    @Override
    public void onStartDelete(int totalCount) {
        ((Activity) MainActivity.getContext()).runOnUiThread(() -> {
            this.deleteButton.setVisibility(View.GONE);
            this.textView.setText(String.format(Locale.ROOT, "Deleting... %d/%d", 0, totalCount));
            this.progressBar.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onFinishDelete(int totalCount) {
        ((Activity) MainActivity.getContext()).runOnUiThread(() -> {
            this.textView.setText(String.format(Locale.ROOT, "Deleted %d files", 0, totalCount));
            this.progressBar.setVisibility(View.GONE);
            this.deleteButton.setVisibility(View.VISIBLE);
        });
    }
}
