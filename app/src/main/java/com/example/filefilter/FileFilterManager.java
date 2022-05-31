package com.example.filefilter;

import static com.example.filefilter.fileFetcher.FileUtil.simpleDateFormat;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filefilter.fileFetcher.FileFilterData;

import java.util.Calendar;
import java.util.Date;

public class FileFilterManager {

    private CheckBox dateCheckBox;
    private RadioGroup fileTypeRadioGroup;
    private TextView dateStartTextView, dateEndTextView;
    private FileFilterData fileFilterData;
    private View parentView;

    FileFilterManager(Context context, View fileFilterParentView, IFileFilterChangeListener filterChangeListener){
        this.parentView=fileFilterParentView;

        this.dateCheckBox=parentView.findViewById(R.id.date_check);
        this.dateStartTextView=parentView.findViewById(R.id.date_start);
        this.dateEndTextView=parentView.findViewById(R.id.date_end);

        this.fileTypeRadioGroup =parentView.findViewById(R.id.file_type_radio_group);

        fileFilterData=new FileFilterData();

        //filtering
        fileTypeRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            String selectedFileType=((RadioButton)fileTypeRadioGroup.findViewById(i)).getText().toString();
            if(!fileFilterData.getFileType().name().equals(selectedFileType)){
                fileFilterData.setFileType(FileType.valueOf(selectedFileType));
                filterChangeListener.onFileFilterChange();
            }
        });
        dateStartTextView.setOnClickListener(textView -> {
            Date date=fileFilterData.getStartDate();
            Calendar cldr = Calendar.getInstance();
            if(date!=null){
                cldr.setTime(date);
            }
            int now_day = cldr.get(Calendar.DAY_OF_MONTH);
            int now_month = cldr.get(Calendar.MONTH);
            int now_year = cldr.get(Calendar.YEAR);
            // date picker dialog
            DatePickerDialog picker = new DatePickerDialog(context,
                    (datePickerView, year, month, day) -> {
                        cldr.set(year,month,day);
                        Date selectedStartDate=cldr.getTime();
                        if(fileFilterData.getEndDate()!=null && selectedStartDate.getTime()>fileFilterData.getEndDate().getTime()){
                            Toast.makeText(context,"Start date cannot be after end date.",Toast.LENGTH_SHORT).show();
                        }else {
                            fileFilterData.setStartDate(cldr.getTime());
                            dateStartTextView.setText(String.format("From: %s", simpleDateFormat.format(fileFilterData.getStartDate())));
                        }
                    }, now_year, now_month, now_day);
            picker.show();
        });
        dateEndTextView.setOnClickListener(textView -> {
            Date date=fileFilterData.getEndDate();
            Calendar cldr = Calendar.getInstance();
            if(date!=null){
                cldr.setTime(date);
            }
            int now_day = cldr.get(Calendar.DAY_OF_MONTH);
            int now_month = cldr.get(Calendar.MONTH);
            int now_year = cldr.get(Calendar.YEAR);
            // date picker dialog
            DatePickerDialog picker = new DatePickerDialog(context,
                    (datePickerView, year, month, day) -> {
                        cldr.set(year,month,day);
                        Date selectedEndDate = cldr.getTime();
                        if(fileFilterData.getStartDate()!=null && selectedEndDate.getTime() < fileFilterData.getStartDate().getTime()){
                            Toast.makeText(context,"End date cannot be before start date.",Toast.LENGTH_SHORT).show();
                        }else{
                            fileFilterData.setEndDate(selectedEndDate);
                            dateEndTextView.setText(String.format("To: %s", simpleDateFormat.format(selectedEndDate)));
                        }
                    }, now_year, now_month, now_day);
            picker.show();
        });
        dateCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                if(fileFilterData.getStartDate()==null){
                    Toast.makeText(context,"Select start date!",Toast.LENGTH_SHORT).show();
                    compoundButton.setChecked(false);
                } else if(fileFilterData.getEndDate()==null){
                    Toast.makeText(context,"Select end date!",Toast.LENGTH_SHORT).show();
                    compoundButton.setChecked(false);
                }else if(fileFilterData.getStartDate().getTime()>=fileFilterData.getEndDate().getTime()){
                    Toast.makeText(context,"Start date should be before end date!",Toast.LENGTH_SHORT).show();
                    compoundButton.setChecked(false);
                }
                fileFilterData.setDateFlag(true);
                filterChangeListener.onFileFilterChange();
            }else{
                fileFilterData.setDateFlag(false);
                filterChangeListener.onFileFilterChange();
            }
        });

    }
}
