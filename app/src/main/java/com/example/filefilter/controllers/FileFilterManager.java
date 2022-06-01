package com.example.filefilter.controllers;

import static com.example.filefilter.utils.FileUtil.simpleDateFormat;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filefilter.models.FileType;
import com.example.filefilter.R;
import com.example.filefilter.callbacks.IFileFilterChangeListener;
import com.example.filefilter.models.FileFilterData;

import java.util.Calendar;

public class FileFilterManager {

    private static final String TAG = "FileFilterManager";
    private final CheckBox dateCheckBox;
    private final RadioGroup fileTypeRadioGroup;
    private final TextView dateStartTextView;
    private final TextView dateEndTextView;
    private final FileFilterData fileFilterData;
    private final View parentView;

    public FileFilterManager(Context context, View fileFilterParentView, IFileFilterChangeListener filterChangeListener) {

        Log.d(TAG, "FileFilterManager: initializing File filter manager");
        this.parentView = fileFilterParentView;

        this.dateCheckBox = parentView.findViewById(R.id.date_check);
        this.dateStartTextView = parentView.findViewById(R.id.date_start);
        this.dateEndTextView = parentView.findViewById(R.id.date_end);

        this.fileTypeRadioGroup = parentView.findViewById(R.id.file_type_radio_group);

        fileFilterData = new FileFilterData();

        //filtering
        fileTypeRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            String selectedFileType = ((RadioButton) fileTypeRadioGroup.findViewById(i)).getText().toString().toUpperCase();
            if (!fileFilterData.getFileType().name().equals(selectedFileType)) {
                fileFilterData.setFileType(FileType.valueOf(selectedFileType));
                filterChangeListener.onFileFilterChange(fileFilterData);
            }
        });
        dateStartTextView.setOnClickListener(textView -> {
            long date = fileFilterData.getStartDate();
            Calendar cldr = Calendar.getInstance();
            if (date != 0) {
                cldr.setTimeInMillis(date);
            }
            int now_day = cldr.get(Calendar.DAY_OF_MONTH);
            int now_month = cldr.get(Calendar.MONTH);
            int now_year = cldr.get(Calendar.YEAR);
            // date picker dialog
            DatePickerDialog picker = new DatePickerDialog(context,
                    (datePickerView, year, month, day) -> {
                        cldr.set(year, month, day);
                        long selectedStartDate = cldr.getTimeInMillis();
                        if (fileFilterData.isDateFlag()) {
                            if (fileFilterData.getEndDate() != 0 && selectedStartDate > fileFilterData.getEndDate()) {
                                Toast.makeText(context, "Start date cannot be after end date.", Toast.LENGTH_SHORT).show();
                            } else {
                                fileFilterData.setStartDate(selectedStartDate);
                                dateStartTextView.setText(String.format("From: %s", simpleDateFormat.format(fileFilterData.getStartDate())));
                                filterChangeListener.onFileFilterChange(fileFilterData);
                            }
                        } else {
                            fileFilterData.setStartDate(selectedStartDate);
                            dateStartTextView.setText(String.format("From: %s", simpleDateFormat.format(fileFilterData.getStartDate())));
                        }
                    }, now_year, now_month, now_day);
            picker.show();
        });
        dateEndTextView.setOnClickListener(textView -> {
            long date = fileFilterData.getEndDate();
            Calendar cldr = Calendar.getInstance();
            if (date != 0) {
                cldr.setTimeInMillis(date);
            }
            int now_day = cldr.get(Calendar.DAY_OF_MONTH);
            int now_month = cldr.get(Calendar.MONTH);
            int now_year = cldr.get(Calendar.YEAR);
            // date picker dialog
            DatePickerDialog picker = new DatePickerDialog(context,
                    (datePickerView, year, month, day) -> {
                        cldr.set(year, month, day);
                        long selectedEndDate = cldr.getTimeInMillis();
                        if (fileFilterData.isDateFlag())
                            if (fileFilterData.getStartDate() != 0 && selectedEndDate < fileFilterData.getStartDate()) {
                                Toast.makeText(context, "End date cannot be before start date.", Toast.LENGTH_SHORT).show();
                            } else {
                                fileFilterData.setEndDate(selectedEndDate);
                                dateEndTextView.setText(String.format("To: %s", simpleDateFormat.format(selectedEndDate)));
                                filterChangeListener.onFileFilterChange(fileFilterData);
                            }
                        else {
                            fileFilterData.setEndDate(selectedEndDate);
                            dateEndTextView.setText(String.format("To: %s", simpleDateFormat.format(selectedEndDate)));
                        }
                    }, now_year, now_month, now_day);
            picker.show();
        });
        dateCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                if (fileFilterData.getStartDate() == 0) {
                    Toast.makeText(context, "Select start date!", Toast.LENGTH_SHORT).show();
                    compoundButton.setChecked(false);
                } else if (fileFilterData.getEndDate() == 0) {
                    Toast.makeText(context, "Select end date!", Toast.LENGTH_SHORT).show();
                    compoundButton.setChecked(false);
                } else if (fileFilterData.getStartDate() >= fileFilterData.getEndDate()) {
                    Toast.makeText(context, "Start date should be before end date!", Toast.LENGTH_SHORT).show();
                    compoundButton.setChecked(false);
                } else {
                    fileFilterData.setDateFlag(true);
                    filterChangeListener.onFileFilterChange(fileFilterData);
                }
            } else {
                fileFilterData.setDateFlag(false);
                filterChangeListener.onFileFilterChange(fileFilterData);
            }
        });

    }

    public FileFilterData getFileFilterData() {
        return fileFilterData;
    }
}
