package com.example.filefilter.models;

import com.example.filefilter.callbacks.ISelectionChangeListener;
import com.example.filefilter.utils.FileItem;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SelectionModel {

    private int selectedCount = 0;
    private final List<ISelectionChangeListener> selectionChangeListeners = new LinkedList<>();
    private boolean[] selectedList;
    private int itemListLength;

    public SelectionModel(){}

    public void initialize(int itemListLength){
        if(selectedCount!=0) {
            selectedCount = 0;
            this.selectionChangeListeners.forEach(selectionChangeListener -> selectionChangeListener.onSelectionChange(selectedCount));
        }
        this.itemListLength=itemListLength;
        this.selectedList=new boolean[itemListLength];
        Arrays.fill(selectedList,false);
    }

    public void setItemSelected(boolean selected, int itemIndex) {
        selectedList[itemIndex]=selected;
        if (selected) this.selectedCount++;
        else this.selectedCount--;
        this.selectionChangeListeners.forEach(selectionChangeListener -> selectionChangeListener.onSelectionChange(selectedCount));
    }

    public boolean isItemSelected(int itemIndex){
        return selectedList[itemIndex];
    }

    public int getSelectedCount() {
        return selectedCount;
    }

    public void registerSelectionChangeListeners(ISelectionChangeListener listener) {
        this.selectionChangeListeners.add(listener);
    }
}
