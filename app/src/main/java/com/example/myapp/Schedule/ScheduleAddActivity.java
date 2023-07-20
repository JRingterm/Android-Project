package com.example.myapp.Schedule;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScheduleAddActivity extends AppCompatActivity {

    TextView dayText;
    EditText title_edit, content_edit;
    TimePicker time_picker;
    Button saveBtn, cancelBtn;
    Switch AlarmSwitch;
    int year, month, day;
    int alarmset = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_add);
        getSupportActionBar().setTitle("일정 추가");
        //여기서도 권한을 해줘야 오류가 안뜸.. 왜지
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_CALENDAR}, 1);

        dayText = findViewById(R.id.dayTextview);
        title_edit = findViewById(R.id.title_editText);
        content_edit = findViewById(R.id.content_editText);
        time_picker = findViewById(R.id.timePicker);
        saveBtn = findViewById(R.id.save_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        AlarmSwitch = findViewById(R.id.alarm_Switch);

        //intent에서 받아온 값들을 저장.
        Intent intent = getIntent();

        year = intent.getIntExtra("year", 0000);
        month = intent.getIntExtra("month", 00);
        day = intent.getIntExtra("day", 00);

        dayText.setText(year+"년 "+(month+1)+"월 "+day +"일");
        //저장 버튼
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(title_edit.getText().toString())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleAddActivity.this);
                    builder.setTitle("경고!").setMessage("일정 제목을 입력해주세요");
                    builder.setPositiveButton("OK", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else {
                    ContentResolver resolver = getContentResolver();
                    String title = title_edit.getText().toString();
                    String content = content_edit.getText().toString();
                    long milliseconds = timeFormat();
                    addEvent(resolver, title, content, milliseconds, milliseconds);
                }
            }
        });

        AlarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    alarmset = 1;
                }else{
                    alarmset = 0;
                }
            }
        });

    }

    public void cancelBtn_click(View view) {
        finish();
    }

    //입력받은 시간을 밀리세컨드로 변환
    public long timeFormat() {
        int hour = time_picker.getHour();
        int minute = time_picker.getMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long milliseconds = calendar.getTimeInMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm");
        String time = sdf.format(milliseconds);

        Log.v("시간", hour + ":" + minute); //1:7
        Log.v("밀리세컨드 포멧팅", time);//01:07

        return milliseconds;
    }
    // 일정 추가 메서드
    public void addEvent(ContentResolver resolver, String title, String description, long startMillis, long endMillis) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, 1); // 캘린더 ID
        values.put(CalendarContract.Events.TITLE, title); //제목
        values.put(CalendarContract.Events.DESCRIPTION, description); //세부내용
        values.put(CalendarContract.Events.DTSTART, startMillis); //시작시간
        values.put(CalendarContract.Events.DTEND, endMillis + 3600000); //종료시간
        //values.put(CalendarContract.Events.HAS_ALARM, 1); //알림설정 on
        values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID()); //필수사항

        Uri uri = resolver.insert(CalendarContract.Events.CONTENT_URI, values); //추가

        //HAS_ALARM 필드는 사용자가 직접 설정해주지 못함. Calendar.Contract.Reminders를 이용해서 알림이벤트를 설정해줘야함.
        ContentResolver contentResolver = getContentResolver();
        if (uri != null && alarmset == 1) {
            long eventId = Long.parseLong(uri.getLastPathSegment());

            // 알림 이벤트 추가
            ContentValues reminderValues = new ContentValues();
            reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventId);
            reminderValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            //TODO:그럼 여기서 5분전, 10분전 가능할 듯!!!!
            //reminderValues.put(CalendarContract.Reminders.MINUTES, 5); // 예: 5분 전에 알림

            Uri reminderUri = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues);
            Log.v("알림이벤트 추가.", "추가완료");
        }
        Toast.makeText(getApplicationContext(), "일정 추가 완료", Toast.LENGTH_LONG).show();
        setResult(Activity.RESULT_OK);
        finish();
    }
}