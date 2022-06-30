package com.example.filefilter.views;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filefilter.R;
import com.example.filefilter.controllers.FileController;
import com.example.filefilter.utils.FileItem;

import java.util.List;

@UiThread
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    private final FileController fileController;
    private final String TAG = "FileListAdapter";
    private List<FileItem> files;

    public FileListAdapter(List<FileItem> files, FileController fileController) {
        this.fileController = fileController;
        this.files = files;
    }

    public void setFiles(List<FileItem> files) {
        this.files = files;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_item, parent, false);
        view.setOnClickListener(this::onFileClick);
        return new ViewHolder(view);
    }

    public void onFileClick(View view) {
        TextView fileNameView = ((TextView) view.findViewById(R.id.file_name));
        fileController.onFileClicked(fileNameView.getText().toString());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileItem fileItem = files.get(position);
        holder.fileName.setText(fileItem.getFileName());
        holder.fileSize.setText(fileItem.getFileSize());
        holder.fileDate.setText(fileItem.getFileDate());
        holder.itemView.setSelected(fileItem.isSelected());
        int res;
        switch (fileItem.getFileType()) {
            case DIRECTORY:
                res = R.mipmap.folder;
                break;
            case AUDIO:
                res = R.mipmap.audio;
                break;
            case VIDEO:
                res = R.mipmap.video;
                break;
            case DOCUMENT:
                res = R.mipmap.document;
                break;
            case IMAGE:
                res = R.mipmap.image;
                break;
            case OTHER:
            default:
                res = R.mipmap.settings;
        }
        holder.fileIcon.setImageResource(res);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView fileName, fileDate, fileSize;
        ImageView fileIcon;
//        View fileRow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            fileDate = itemView.findViewById(R.id.file_date);
            fileSize = itemView.findViewById(R.id.file_size);
            fileIcon = itemView.findViewById(R.id.file_icon);
//            fileRow = itemView.findViewById(R.id.file_item);
            itemView.setOnLongClickListener(view1 -> {
                view1.setSelected(!view1.isSelected());
                fileController.setSelection(view1.isSelected(), this.getLayoutPosition());
                return true;
            });
//            itemView.setOnTouchListener((view12, motionEvent) -> {
//                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                        view12.setBackgroundColor(Color.parseColor("#B4D5FE"));
//                    } else {
//                        view12.setBackgroundColor(view12.isSelected() ? Color.parseColor("#3297FD") : Color.WHITE);
//                    }
//                return false;
//            });
        }
    }
}
