package com.example.myapp.Schedule;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.myapp.MainActivity;
import com.example.myapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScheduleInfoActivity extends AppCompatActivity {
    //이 activity_schedule_info.xml을 액티비티로 말고 커스텀 다이얼로그로 하면 좋을듯.
    TextView daytext;
    EditText titletext, starttext, endtext, contenttext;
    Button deleteBtn, updateBtn, prepareupdateBtn;
    Switch alarmBoolean;

    int year, month, day;
    int bool;
    String t_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_info);
        getSupportActionBar().setTitle("상세 일정");

        //ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CALENDAR}, 1);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_CALENDAR}, 1);

        daytext = findViewById(R.id.dateTextView);
        titletext = findViewById(R.id.TitleText);
        starttext = findViewById(R.id.startText);
        endtext = findViewById(R.id.endText);
        contenttext = findViewById(R.id.ContentText);
        deleteBtn = findViewById(R.id.delete_btn);
        updateBtn = findViewById(R.id.update_btn);
        prepareupdateBtn = findViewById(R.id.update_btn2);
        alarmBoolean = findViewById(R.id.Alarm_Switch);

        //intent에서 받아온 값들을 저장.
        Intent intent = getIntent();

        year = intent.getIntExtra("year", 0000);
        month = intent.getIntExtra("month", 00) + 1;
        day = intent.getIntExtra("day", 00);

        String title = intent.getStringExtra("title");
        String start = intent.getStringExtra("start");
        //Log.v("start값", start); //21:12 형식으로 시간만 가져옴.
        String end = intent.getStringExtra("end");
        String description = intent.getStringExtra("description");
        int alarmONOFF = intent.getIntExtra("alarm_On_Off", 1);

        daytext.setText(year + "년 " + month + "월 " + day + "일");
        titletext.setText(title);
        starttext.setText(start);
        endtext.setText(end);
        contenttext.setText(description);
        alarmBoolean.setChecked(isAlarmOn(alarmONOFF));
        //삭제 버튼
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: 정말로 삭제하시겠습니까?
                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleInfoActivity.this);
                builder.setTitle("일정 삭제").setMessage("정말로 삭제하시겠습니까?");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        String delete_title = titletext.getText().toString();
                        long eventid = findEventID(delete_title);

                        ContentResolver cr = getContentResolver();
                        ContentValues values = new ContentValues();

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(2031, Calendar.JANUARY, 1, 0, 0);
                        long newStartTime = calendar.getTimeInMillis();

                        calendar.set(2031, Calendar.JANUARY, 1, 1, 0);
                        long newEndTime = calendar.getTimeInMillis();

                        values.put(CalendarContract.Events.DTSTART, newStartTime);
                        values.put(CalendarContract.Events.DTEND, newEndTime);

                        Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventid);
                        int rows = cr.update(uri, values, null, null);
                        int rows2 = cr.delete(uri, null, null);

                        Toast.makeText(getApplicationContext(), "일정 삭제 완료", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

                builder.setNegativeButton("Cancel", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        //수정 버튼
        prepareupdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBtn.setEnabled(true);
                updateBtn.setVisibility(View.VISIBLE);
                titletext.setEnabled(true);
                starttext.setEnabled(true);
                endtext.setEnabled(true);
                contenttext.setEnabled(true);
                alarmBoolean.setEnabled(true);

                prepareupdateBtn.setEnabled(false);
                prepareupdateBtn.setVisibility(View.INVISIBLE);
                deleteBtn.setEnabled(false);
                deleteBtn.setVisibility(View.INVISIBLE);
                t_text = titletext.getText().toString();
                Log.v("제목전달", t_text);
            }
        });
        //수정 완료 버튼
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //제목으로 id를 찾기 때문에 제목 변경되면 수정이 안돼서 미리 제목 받아놓음.
                if(TextUtils.isEmpty(titletext.getText().toString())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleInfoActivity.this);
                    builder.setTitle("경고!").setMessage("일정 제목을 입력해주세요");
                    builder.setPositiveButton("OK", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else {
                    long eventid = findEventID(t_text);
                    long reminderId = 0;
                    bool = isSwitchChecked(alarmBoolean);
                    // 일정 URI
                    Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventid);
                    // ContentResolver를 사용하여 일정 수정
                    ContentResolver cr = getContentResolver();
                    Log.v("수정ID", String.valueOf(findEventID(titletext.getText().toString())));
                    // 수정할 내용을 ContentValues에 저장
                    ContentValues values = new ContentValues();

                    values.put(CalendarContract.Events.TITLE, titletext.getText().toString());
                    values.put(CalendarContract.Events.DESCRIPTION, contenttext.getText().toString());
                    values.put(CalendarContract.Events.DTSTART, formatTime(starttext)); // 수정할 시작 시간
                    values.put(CalendarContract.Events.DTEND, formatTime(endtext)); // 수정할 종료 시간
                    //values.put(CalendarContract.Events.HAS_ALARM, bool);

                    //HAS_ALARM 필드는 사용자가 직접 설정해주지 못함. Calendar.Contract.Reminders를 이용해서 알림이벤트를 설정해줘야함.
                    ContentResolver contentResolver = getContentResolver();
                    //switch 상태가 on이라면 알림 이벤트 설정.
                    if (bool == 1) {
                        if (updateUri != null) {
                            long eventId = Long.parseLong(updateUri.getLastPathSegment());

                            // 알림 이벤트 추가
                            ContentValues reminderValues = new ContentValues();
                            reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventId);
                            reminderValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                            //TODO:그럼 여기서 5분전, 10분전 가능할 듯!!!!
                            //reminderValues.put(CalendarContract.Reminders.MINUTES, 2); // 예: 2분 전에 알림

                            Uri reminderUri = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues);
                            Log.v("알림이벤트 추가.", "추가완료");
                        }
                    } else {//switch 상태가 off라면 가지고 있던 알림 이벤트 삭제.
                        //eventId로 reminderId를 가져옴.
                        String[] projection = {CalendarContract.Reminders._ID};
                        String selection = CalendarContract.Reminders.EVENT_ID + " = ?";
                        String[] selectionArgs = {String.valueOf(eventid)};

                        Cursor cursor = cr.query(CalendarContract.Reminders.CONTENT_URI, projection, selection, selectionArgs, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int reminder_index = cursor.getColumnIndex(CalendarContract.Reminders._ID);
                            reminderId = cursor.getLong(reminder_index);

                            cursor.close();
                            // 알림 ID 사용
                        }

                        Uri reminderUri = ContentUris.withAppendedId(CalendarContract.Reminders.CONTENT_URI, reminderId);
                        int rowsDeleted = cr.delete(reminderUri, null, null);
                    }

                    int rows = cr.update(updateUri, values, null, null);

                    Log.v("수정", String.valueOf(rows));
                    Toast.makeText(getApplicationContext(), "일정 수정 완료", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
        alarmBoolean.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    bool = 1;
                }else{
                    bool = 0;
                }
            }
        });
    }

    public long formatTime(EditText editText){
        String s = year + ":" + month + ":" + day + " " + editText.getText().toString();
        Log.v("날짜포멧", s);
        DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm"); // 포맷 지정
        Date dateTime = null;
        try {
            dateTime = dateFormat.parse(s); // 문자열을 Date 객체로 변환
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (dateTime != null) {
            long milliseconds = dateTime.getTime(); // Date 객체를 밀리초로 변환
            Log.v("밀리초 변환", String.valueOf(milliseconds));
            return milliseconds;
            // 필요한 처리를 수행하세요.
        } else {
            // 변환 실패
            // 필요한 처리를 수행하세요.
            return 0;
        }
    }
    // 제목으로 이벤트ID를 찾는 메소드
    public long findEventID(String eventTitle) {
        long eventID = -1; // 일치하는 이벤트 ID, 기본값은 -1로 설정

        String[] projection = new String[]{CalendarContract.Events._ID}; //얻고싶은 컬럼
        String selection = CalendarContract.Events.TITLE + " = ?"; //제목을 통해서 찾음.
        String[] selectionArgs = new String[]{eventTitle};
        String sortOrder = null;

        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        Cursor cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(CalendarContract.Events._ID);
            eventID = cursor.getLong(idIndex);
            cursor.close();
        }

        return eventID;
    }
    private long getDateTime(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        return calendar.getTimeInMillis();
    }
    public int isSwitchChecked(Switch switchView) {
        if(switchView.isChecked()){
            return 1;
        }
        else{
            return 0;
        }
    }
    public boolean isAlarmOn(int i){
        if (i == 1){
            bool = 1;
            return true;
        }
        else{
            bool = 0;
            return false;
        }
    }
}