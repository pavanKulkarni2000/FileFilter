package com.example.filefilter.fileFetcher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filefilter.R;
import com.example.filefilter.folderPicker.IFolderChangeListener;

import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    private final List<FileData> files;
    IFolderChangeListener folderChangeListener;

    public FileListAdapter(List<FileData> files, IFolderChangeListener folderChangeListener) {
        this.folderChangeListener = folderChangeListener;
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
        folderChangeListener.onChildDirectoryResolved(fileNameView.getText().toString());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.fileName.setText(files.get(position).getFileName());
        holder.fileSize.setText(files.get(position).getFileSize());
        holder.fileDate.setText(files.get(position).getFileDate());
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView fileName, fileDate, fileSize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            fileDate = itemView.findViewById(R.id.file_date);
            fileSize = itemView.findViewById(R.id.file_size);
        }
    }
}
