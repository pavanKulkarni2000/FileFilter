package com.example.filefilter.views;

import static com.example.filefilter.utils.FileUtil.simpleDateFormat;

import android.app.DatePickerDialog;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.UiThread;

import com.example.filefilter.MainActivity;
import com.example.filefilter.R;
import com.example.filefilter.callbacks.ISelectionChangeListener;
import com.example.filefilter.controllers.FileController;
import com.example.filefilter.models.FileFilterModel;

import java.util.Calendar;

@UiThread
public class FileFilterView {

    private static final String TAG = "FileFilterManager";
    private final CheckBox dateCheckBox;
    private final RadioGroup fileTypeRadioGroup;
    private final TextView dateStartTextView;
    private final TextView dateEndTextView;
    private final FileController fileController;
    private final View parentView;

    public FileFilterView(View parentView, FileController fileController, FileFilterModel fileFilterModel) {

        Log.d(TAG, "FileFilterManager: initializing File filter manager");
        this.parentView = parentView;
        this.dateCheckBox = parentView.findViewById(R.id.date_check);
        this.dateStartTextView = parentView.findViewById(R.id.date_start);
        this.dateEndTextView = parentView.findViewById(R.id.date_end);
        this.fileTypeRadioGroup = parentView.findViewById(R.id.file_type_radio_group);
        this.fileController = fileController;

        //filtering
        fileTypeRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            String selectedFileType = ((RadioButton) fileTypeRadioGroup.findViewById(i)).getText().toString().toUpperCase();
            try {
                fileController.onFileFilterTypeSelected(FileFilterModel.FileType.valueOf(selectedFileType));
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "getFileType: got mime type " + selectedFileType);
            }
        });
        dateStartTextView.setOnClickListener(textView -> {
            long date = fileFilterModel.getAfterDate();
            Calendar cldr = Calendar.getInstance();
            if (date != 0) {
                cldr.setTimeInMillis(date);
            }
            int now_day = cldr.get(Calendar.DAY_OF_MONTH);
            int now_month = cldr.get(Calendar.MONTH);
            int now_year = cldr.get(Calendar.YEAR);
            // date picker dialog
            DatePickerDialog picker = new DatePickerDialog(MainActivity.getContext(),
                    (datePickerView, year, month, day) -> {
                        cldr.set(year, month, day);
                        long selectedStartDate = cldr.getTimeInMillis();
                        if (fileFilterModel.isDateFlag() && fileFilterModel.getBeforeDate() != 0 && selectedStartDate > fileFilterModel.getBeforeDate()) {
                            Toast.makeText(MainActivity.getContext(), "Start date cannot be after end date.", Toast.LENGTH_SHORT).show();
                        } else {
                            fileController.onFileFilterDateAfterSet(selectedStartDate);
                            dateStartTextView.setText(String.format("From: %s", simpleDateFormat.format(fileFilterModel.getAfterDate())));
                        }
                    }, now_year, now_month, now_day);
            picker.show();
        });
        dateEndTextView.setOnClickListener(textView -> {
            long date = fileFilterModel.getBeforeDate();
            Calendar cldr = Calendar.getInstance();
            if (date != 0) {
                cldr.setTimeInMillis(date);
            }
            int now_day = cldr.get(Calendar.DAY_OF_MONTH);
            int now_month = cldr.get(Calendar.MONTH);
            int now_year = cldr.get(Calendar.YEAR);
            // date picker dialog
            DatePickerDialog picker = new DatePickerDialog(MainActivity.getContext(),
                    (datePickerView, year, month, day) -> {
                        cldr.set(year, month, day);
                        long selectedEndDate = cldr.getTimeInMillis();
                        if (fileFilterModel.isDateFlag() && fileFilterModel.getAfterDate() != 0 && selectedEndDate < fileFilterModel.getAfterDate()) {
                            Toast.makeText(MainActivity.getContext(), "End date cannot be before start date.", Toast.LENGTH_SHORT).show();
                        } else {
                            fileController.onFileFilterDateBeforeSet(selectedEndDate);
                            dateEndTextView.setText(String.format("To: %s", simpleDateFormat.format(selectedEndDate)));
                        }
                    }, now_year, now_month, now_day);
            picker.show();
        });
        dateCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                if (fileFilterModel.getAfterDate() == 0) {
                    Toast.makeText(MainActivity.getContext(), "Select start date!", Toast.LENGTH_SHORT).show();
                    compoundButton.setChecked(false);
                } else if (fileFilterModel.getBeforeDate() == 0) {
                    Toast.makeText(MainActivity.getContext(), "Select end date!", Toast.LENGTH_SHORT).show();
                    compoundButton.setChecked(false);
                } else if (fileFilterModel.getAfterDate() >= fileFilterModel.getBeforeDate()) {
                    Toast.makeText(MainActivity.getContext(), "Start date should be before end date!", Toast.LENGTH_SHORT).show();
                    compoundButton.setChecked(false);
                } else {
                    fileController.onFileFilterDateFlagSet(true);
                }
            } else {
                fileController.onFileFilterDateFlagSet(false);
            }
        });

        Log.d(TAG, "FileFilterView: created view");
    }
}
