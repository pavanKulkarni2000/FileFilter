package com.example.filefilter;

import static com.example.filefilter.Util.managePermissions;
import static com.example.filefilter.fileFetcher.FileUtil.simpleDateFormat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filefilter.fileFetcher.FileSearchData;
import com.example.filefilter.fileFetcher.FileListAdapter;
import com.example.filefilter.fileFetcher.FileListItem;
import com.example.filefilter.fileFetcher.FileLister;
import com.example.filefilter.fileFetcher.IFileListReadyCallback;
import com.example.filefilter.folderPicker.FolderPickerDialog;
import com.example.filefilter.folderPicker.IFolderSelectedCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity implements IFolderSelectedCallback, IFileListReadyCallback {

    //resource
    private ExecutorService executorService;
    private static final String TAG = "MainActivity";

    //views
    private TextView currentFolderTextView;
    private TextView emptyMessageView;
    private ProgressBar progressBarView;
    private RecyclerView fileListView;
    private View currentView;
    private TextView dateStartTextView,dateEndTextView;
    private CheckBox dateCheckBox;

    //controller
    private FileListAdapter fileListAdapter;
    private RadioGroup fileTypeRadioGroup;

    //state
    private List<FileListItem> fileList;
    private String currentFolder;
    private Future<?> searchResult;
    private FileSearchData fileSearchData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        managePermissions(this, this);

        init();

    }

    private void init() {
        //Initialize views
        //initialize current folder text view
        currentFolderTextView =findViewById(R.id.current_folder);
        currentFolderTextView.setMovementMethod(new ScrollingMovementMethod());
        emptyMessageView =findViewById(R.id.empty_message);
        progressBarView =findViewById(R.id.progress_bar);

        //initialize file list view
        fileListView = findViewById(R.id.file_list);
        fileListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        fileListView.setItemAnimator(new DefaultItemAnimator());
        fileList=new ArrayList<>();
        fileListAdapter=new FileListAdapter(fileList);
        fileListView.setAdapter(fileListAdapter);
        currentView=fileListView;

        //filtering
        fileTypeRadioGroup=findViewById(R.id.file_type_radio_group);
        fileTypeRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if(fileSearchData.getFileType()!=i){
                fileSearchData.setFileType(i);
                searchWithFilters();
            }
        });
        dateStartTextView=findViewById(R.id.date_start);
        dateStartTextView.setOnClickListener(textView -> {
            Date date=fileSearchData.getStartDate();
            Calendar cldr = Calendar.getInstance();
            if(date!=null){
                cldr.setTime(date);
            }
            int now_day = cldr.get(Calendar.DAY_OF_MONTH);
            int now_month = cldr.get(Calendar.MONTH);
            int now_year = cldr.get(Calendar.YEAR);
            // date picker dialog
            DatePickerDialog picker = new DatePickerDialog(MainActivity.this,
                    (datePickerView, year, month, day) -> {
                        cldr.set(year,month,day);
                        Date selectedStartDate=cldr.getTime();
                        if(fileSearchData.getEndDate()!=null && selectedStartDate.getTime()>fileSearchData.getEndDate().getTime()){
                            Toast.makeText(getApplicationContext(),"Start date cannot be after end date.",Toast.LENGTH_SHORT).show();
                        }else {
                            fileSearchData.setStartDate(cldr.getTime());
                            dateStartTextView.setText(String.format("From: %s", simpleDateFormat.format(fileSearchData.getStartDate())));
                        }
                    }, now_year, now_month, now_day);
            picker.show();
        });
        dateEndTextView=findViewById(R.id.date_end);
        dateEndTextView.setOnClickListener(textView -> {
            Date date=fileSearchData.getEndDate();
            Calendar cldr = Calendar.getInstance();
            if(date!=null){
                cldr.setTime(date);
            }
            int now_day = cldr.get(Calendar.DAY_OF_MONTH);
            int now_month = cldr.get(Calendar.MONTH);
            int now_year = cldr.get(Calendar.YEAR);
            // date picker dialog
            DatePickerDialog picker = new DatePickerDialog(MainActivity.this,
                    (datePickerView, year, month, day) -> {
                        cldr.set(year,month,day);
                        Date selectedEndDate = cldr.getTime();
                        if(fileSearchData.getStartDate()!=null && selectedEndDate.getTime() < fileSearchData.getStartDate().getTime()){
                            Toast.makeText(getApplicationContext(),"End date cannot be before start date.",Toast.LENGTH_SHORT).show();
                        }else{
                            fileSearchData.setEndDate(selectedEndDate);
                            dateEndTextView.setText(String.format("To: %s", simpleDateFormat.format(selectedEndDate)));
                        }
                    }, now_year, now_month, now_day);
            picker.show();
        });
        dateCheckBox=findViewById(R.id.date_check);
        dateCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                if(fileSearchData.getStartDate()==null){
                    Toast.makeText(getApplicationContext(),"Select start date!",Toast.LENGTH_SHORT).show();
                    compoundButton.setChecked(false);
                } else if(fileSearchData.getEndDate()==null){
                    Toast.makeText(getApplicationContext(),"Select end date!",Toast.LENGTH_SHORT).show();
                    compoundButton.setChecked(false);
                }else if(fileSearchData.getStartDate().getTime()>=fileSearchData.getEndDate().getTime()){
                    Toast.makeText(getApplicationContext(),"Start date should be before end date!",Toast.LENGTH_SHORT).show();
                    compoundButton.setChecked(false);
                }
                fileSearchData.setDateFlag(true);
                searchWithFilters();
            }else{
                fileSearchData.setDateFlag(false);
                searchWithFilters();
            }
        });

        fileSearchData =new FileSearchData();

        //initialize executor services for background work
        executorService= Executors.newFixedThreadPool(4);

        //initialize currentFolder variable to internal storage root folder
        String internal_storage=System.getenv("EXTERNAL_STORAGE");
        if(internal_storage!=null){
            File internal_storage_file=new File(internal_storage);
            setCurrentFolder(internal_storage_file.getAbsolutePath());
        }else{
            finish();
        }
    }

    private void setCurrentFolder(String folder ) {
        currentFolder =folder;
        currentFolderTextView.setText(currentFolder);
        searchWithFilters();
    }

    private void searchWithFilters() {
        swapCurrentView(progressBarView);
        if( searchResult!=null && !searchResult.isDone() ) {
                searchResult.cancel(true);
        }
        Log.d(TAG, "searchWithFilters: sending search requets for path "+currentFolder);
        searchResult = executorService.submit(new FileLister(currentFolder, fileSearchData, this));
    }

    public void folderDialog(View view) {

        FolderPickerDialog folderPickerDialog=new FolderPickerDialog(currentFolder);
        folderPickerDialog.show(getSupportFragmentManager(),"My  Fragment");
    }

    @Override
    public void onFolderSelected(String folder) {
        setCurrentFolder(folder);
    }

    @Override
    public void onFileListReady(List<FileListItem> files) {
        Log.d(TAG, "onFileListReady: Got result with "+files.size()+" files");
        fileList.clear();
        fileList.addAll(files);
        runOnUiThread(() -> {
            fileListAdapter.notifyDataSetChanged();
            if(fileList.isEmpty()){
                swapCurrentView(emptyMessageView);
            }else{
                swapCurrentView(fileListView);
            }
        });
    }

    private void swapCurrentView(View newView){
        currentView.setVisibility(View.GONE);
        newView.setVisibility(View.VISIBLE);
        currentView=newView;
    }
}