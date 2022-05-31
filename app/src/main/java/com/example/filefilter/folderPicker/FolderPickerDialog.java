package com.example.filefilter.folderPicker;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.filefilter.R;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FolderPickerDialog extends DialogFragment {

    private static final String TAG = "FolderPickerDialog";
    IFolderChangeListener hostActivity;
    LinkedList<String> currentFolderStack = new LinkedList<>();

    List<String> currentFolderList;
    ArrayAdapter<String> folderListAdapter;
    ListView folderListView;

    public FolderPickerDialog(String current_folder) {
        this.currentFolderStack.addAll(Arrays.asList(current_folder.split("/")));
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View dialog = inflater.inflate(R.layout.folder_picker_dialog, container, false);
        folderListView = dialog.findViewById(R.id.folder_list);
        folderListView.setEmptyView(dialog.findViewById(R.id.empty_message));

        init();

        ImageButton back = dialog.findViewById(R.id.folder_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFolderStack.size() > 2) {
                    currentFolderStack.removeLast();
                    updateCurrentFolderListAndNotify();
                }
            }
        });

        ImageButton select = dialog.findViewById(R.id.folder_select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hostActivity.onChildDirectoryResolved(String.join("/", currentFolderStack));
                dismiss();
            }
        });

        return dialog;
    }

    private void init() {
        currentFolderList = getCurrentFolderList();
        folderListAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, currentFolderList);
        folderListView.setAdapter(folderListAdapter);
        folderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView item = (TextView) view;
                currentFolderStack.add(item.getText().toString());
                updateCurrentFolderListAndNotify();
            }
        });
    }

    private List<String> getCurrentFolderList() {
        String current_folder = String.join("/", currentFolderStack);
//        Log.d(TAG, "getCurrentFolderList: currentfolder="+current_folder);
        File parentFolder = new File(current_folder);
//        Log.d(TAG, "getCurrentFolderList: "+ Arrays.toString(parentFolder.list()));
        return Arrays.stream(parentFolder.listFiles(File::isDirectory)).map(File::getName).collect(Collectors.toList());
    }


    private void updateCurrentFolderListAndNotify() {
        currentFolderList.clear();
        currentFolderList.addAll(getCurrentFolderList());
        folderListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            hostActivity = (IFolderChangeListener) context;
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ", e);
        }
    }
}
