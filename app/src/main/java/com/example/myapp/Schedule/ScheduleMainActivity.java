package com.example.myapp.Schedule;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;


import com.example.myapp.Financial.FinancialIncomeActivity;
import com.example.myapp.MainActivity;
import com.example.myapp.R;
import com.example.myapp.schedule_deco.EventDeco;
import com.example.myapp.schedule_deco.OnedayDeco;
import com.example.myapp.schedule_deco.SaturdayDeco;
import com.example.myapp.schedule_deco.SundayDeco;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class ScheduleMainActivity extends AppCompatActivity {
    MaterialCalendarView materialCalendarView;
    ListView dayListview;
    Sche_ListAdapter adapter;

    private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "Channel1";
    //알람 매니저
    int year, month, dayOfMonth;
    private final OnedayDeco onedayDeco = new OnedayDeco(); //오늘 날짜 초록색으로 표시

    Boolean clickcheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_main);
        getSupportActionBar().setTitle("일정");

        materialCalendarView = findViewById(R.id.calendarView);
        dayListview = findViewById(R.id.DayListview);
        adapter = new Sche_ListAdapter();

        //채널 만들어두기
        createNotificationChannel();
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CALENDAR}, 1);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY) //달력에 표시 되는 첫번째 요일을 일요일로.
                .setMinimumDate(CalendarDay.from(2023, 0, 1))//2023년부터
                .setMaximumDate(CalendarDay.from(2030, 11, 31))//2030년까지.
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(
                new SundayDeco(), //일요일 빨간색으로
                new SaturdayDeco(), //토요일 파란색으로
                onedayDeco //오늘 날짜 초록색으로
        );

        //날짜 클릭하면 날짜에 해당하는 이벤트 출력
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                //선택된 날짜 받아옴.
                year = date.getYear();
                month = date.getMonth();
                dayOfMonth = date.getDay();
                clickcheck = true;

                //findAllEvent(); //화면 갱신에 더 좋은 방법을 찾았음. onStart() 이용

                //리스트뷰 초기화
                adapter.items.clear();
                dayListview.setAdapter(adapter);

                //리졸버에서 데이터 가져오기
                fetchEventsFromCalendar(year, month, dayOfMonth);
            }
        });
        //밑에서 만든 특정 날짜에 체크하기 객체. 이벤트가 입력된 모든 날짜를 매개변수로 보냄.
        new ApiSimulator(findAllEvent()).executeOnExecutor(Executors.newSingleThreadExecutor());

        //리스트 뷰에 있는 일정을 클릭하면 상세보기 페이지로 전환.
        dayListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //리스트뷰의 포지션 내용을 가져온다. 포지션이란 리스트뷰에서 보일 아이템의 위치 정보이다. 매개변수 i가 포지션 번호.
                Sche_ListItem data = (Sche_ListItem) dayListview.getItemAtPosition(i);
                String title = data.getTitle();
                String start = data.getStart();
                String end = data.getEnd();
                String descript = data.getDescription();
                int alarmONOFF = data.getAlarm();

                Intent intent = new Intent(ScheduleMainActivity.this, ScheduleInfoActivity.class);

                intent.putExtra("year",year);
                intent.putExtra("month",month);
                intent.putExtra("day",dayOfMonth);

                intent.putExtra("title", title);
                intent.putExtra("start", start);
                intent.putExtra("end", end);
                intent.putExtra("description", descript);
                intent.putExtra("alarm_On_Off", alarmONOFF);
                startActivity(intent);
            }
        });
    }
    //onCreate 끝

    //====================================날짜 클릭시 데이터 리스트뷰로 보내기======================================
    //이벤트를 컨텐츠 리졸버에서 캘린더로 보냄.
    public void fetchEventsFromCalendar(int year, int month, int dayOfMonth) {
        // 선택한 날짜 범위를 계산합니다. (시작 시간과 종료 시간)
        long startTime = getStartTime(year, month, dayOfMonth);
        long endTime = getEndTime(year, month, dayOfMonth);

        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;

        //선택한 날짜에 해당하는 일정의 제목, 시작시간, 종료시간, 일정위치, 알림여부를 가져온다.
        String[] projection = {
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.ALLOWED_REMINDERS, //캘린더 이벤트에서 허용되는 알림설정을 나타내는 상수.
                // 0: 알림X , 1:기본 알림, 2:알림 및 이메일, 3:알림 또는 이메일
                CalendarContract.Events.HAS_ALARM //알림 설정이 되어있는지 여부 확인. int 타입. 1 or 0
        };

        String selection = CalendarContract.Events.DTSTART + " >= ? AND " + CalendarContract.Events.DTEND + " <= ?";
        String[] selectionArgs = {String.valueOf(startTime), String.valueOf(endTime)};

        //커서로 쿼리 내용을 받는다.
        Cursor cursor = cr.query(uri, projection, selection, selectionArgs, null);
        //각 속성의 DB 테이블 인덱스를 변수에 저장.
        int title_index = cursor.getColumnIndex(CalendarContract.Events.TITLE); //title 속성의 열 번호 저장.
        int Start_index = cursor.getColumnIndex(CalendarContract.Events.DTSTART);
        int End_index = cursor.getColumnIndex(CalendarContract.Events.DTEND);
        int Des_index = cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION);
        int Location_index = cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION);
        int AlarmYN_index = cursor.getColumnIndex(CalendarContract.Events.HAS_ALARM);
        //TODO: 아래 꺼는 필요 없을 듯..?
        int AlarmOption_index = cursor.getColumnIndex(CalendarContract.Events.ALLOWED_REMINDERS);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(title_index);
                long eventStartTime = cursor.getLong(Start_index);
                long eventEndTime = cursor.getLong(End_index);
                String description = cursor.getString(Des_index);
                String location = cursor.getString(Location_index);
                String alarmOption = cursor.getString(AlarmOption_index);
                int alarm = cursor.getInt(AlarmYN_index);
                Log.v("HAS_ALARM", String.valueOf(alarm));
                // 가져온 이벤트 데이터를 사용하여 리스트뷰에 일정을 표시.
                adapter.addItem(new Sche_ListItem(title, convertTime(eventStartTime), convertTime(eventEndTime), description, location, alarmOption, alarm));
            } while (cursor.moveToNext());
            dayListview.setAdapter(adapter);
            cursor.close();
        }
    }
    private long getStartTime(int year, int month, int dayOfMonth) {
        // 선택한 날짜의 시작 시간을 계산하여 반환하는 로직을 구현합니다.
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }
    private long getEndTime(int year, int month, int dayOfMonth) {
        // 선택한 날짜의 종료 시간을 계산하여 반환하는 로직을 구현합니다.
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTimeInMillis();
    }
    //UTC밀리초를 시간으로 변경.
    private String convertTime(long UTC){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = new Date(UTC);
        String str = sdf.format(date);

        return str;
    }
    //일정 알림설정이 1이면 on, 0이면 off
    private String alarmState(int alarm){
        if(alarm == 0){
            return "알림 off";
        }
        else {
            return "알림 on";
        }
    }
    //모든 이벤트를 불러와서, 각 일정의 시작날짜를 가져옴., 알림을 보냄.
    //TODO: 따라서 일정 추가 후 findAllEvent를 호출해줘야 알림 정상작동.
    public ArrayList<String> findAllEvent(){
        long startTime = getStartTime(2023, 0, 1);
        long endTime = getEndTime(2030, 11, 31);
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf_alarm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //이벤트의 시작시간, 종료시간을 쿼리함
        String[] projection = {
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.HAS_ALARM
        };
        String selection = CalendarContract.Events.DTSTART + " >= ? AND " + CalendarContract.Events.DTEND + " <= ?";
        String[] selectionArgs = {String.valueOf(startTime), String.valueOf(endTime)};

        Cursor cursor = cr.query(uri, projection, selection, selectionArgs, null);

        //컬럼의 인덱스를 얻는다.
        int startTime_index = cursor.getColumnIndex(CalendarContract.Events.DTSTART);
        //알림을 위해 필요한 컬럼 인덱스들
        int eId = cursor.getColumnIndex(CalendarContract.Events._ID);
        int eTitle = cursor.getColumnIndex(CalendarContract.Events.TITLE);
        int eLocation = cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION);
        int eAlarm = cursor.getColumnIndex(CalendarContract.Events.HAS_ALARM);

        ArrayList<String> result = new ArrayList<String>();

        while (cursor.moveToNext()) { //다음 행으로 이동. 행 포인터의 초기값은 -1.
            long eventStartTime = cursor.getLong(startTime_index);
            //알림을 받기 위한 데이터들 가져옴.
            long eventId = cursor.getLong(eId);
            String eventTitle = cursor.getString(eTitle);
            String eventLocation = cursor.getString(eLocation);
            int eventAlarm = cursor.getInt(eAlarm);

            Date date = new Date(eventStartTime);
            String str = sdf.format(date);
            result.add(str);

            ///////////////////////////////////////////////////////////////
            // 현재 시간과 일정 시작 시간 차이 계산
            long timeDiff = eventStartTime - System.currentTimeMillis();
            //TODO:HAS_ALARM이 1이여야만 알림 설정되게.
            // 일정 시작 시간과 현재 시간의 차이가 양수인 경우에만 알림 예약
            if (timeDiff > 0 && eventAlarm == 1) {
                scheduleNotification(eventId, eventTitle, timeDiff);
            }
        }
        cursor.close();
        return result;
    }
    //알림..
    private void scheduleNotification(long eventId, String eventTitle, long delayInMillis) {
        Intent notificationIntent = new Intent(ScheduleMainActivity.this, NotificationReceiver.class);
        notificationIntent.putExtra("eventId", eventId);
        notificationIntent.putExtra("eventTitle", eventTitle);
        notificationIntent.putExtra("Channel_ID", CHANNEL_ID);

        long reminderId = 0;
        int minutes = 0;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ScheduleMainActivity.this, (int) eventId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) ScheduleMainActivity.this.getSystemService(Context.ALARM_SERVICE);

        //===========================================================================================================
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayInMillis, pendingIntent);
            Log.v("확인용","알림매니저 성공"); //지금 시간보다 앞에 있는 것들을 모두 알림 설정 해줌.
        }
    }
    //채널 만드는 함수
    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Channel description");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
    //int를 milliseconds로 바꿔주는 함수.
    public long convertToMilliseconds(int value) {
        return (long) value * 1000;
    }
    //추가 버튼 클릭 이벤트
    public void addBtn_click(View view) {
        if(clickcheck == false){
            AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleMainActivity.this);
            builder.setTitle("경고!").setMessage("일정을 추가할 날짜를 선택해주세요.");
            builder.setPositiveButton("OK", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else {
            Intent intent = new Intent(ScheduleMainActivity.this, ScheduleAddActivity.class);

            intent.putExtra("year", year);
            intent.putExtra("month", month);
            intent.putExtra("day", dayOfMonth);

            startActivity(intent); //ScheduleAddActivity 로 넘어감
        }
    }
    //onStart는 생명주기에서 곧 화면에 보여질 예정일 때 호출되는 함수.
    //다른 액티비티에서 스케쥴 메인 액티비티로 돌아오면, 알림설정, 리스트뷰, 빨간점 갱신 해줌.
    @Override
    public void onStart() {
        super.onStart();
        //일정 추가하고 화면 보이자마자 리스트뷰 갱신
        adapter.items.clear();
        dayListview.setAdapter(adapter);
        fetchEventsFromCalendar(year, month, dayOfMonth);
        //일정 추가하고 화면 보이자마자 알림 설정
        findAllEvent();
        //일정 추가하고 화면 보이자마자 빨간점 추가.
        new ApiSimulator(findAllEvent()).executeOnExecutor(Executors.newSingleThreadExecutor());
    }
    //=================================================================================================

    //AsyncTask 사용. 특정 날짜에 표시하는 스레드.
    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        ArrayList<String> Time_Result;

        ApiSimulator(ArrayList<String> Time_Result){
            this.Time_Result = Time_Result;
        }

        //스레드에 의해 처리될 내용을 담기 위한 함수
        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점 표시 해주는 곳*/
            /*월은 0이 1월 년,일은 그대로*/

            //string 문자열인 Time_Result 을 받아와서 -를 기준으로 분할하여 string을 int 로 변환
            for(int i = 0 ; i < Time_Result.size(); i++){

                String[] time = Time_Result.get(i).split("-");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                calendar.set(year,month-1,dayy);
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);
            }
            return dates;
        }

        //AsyncTask의 모든 작업이 완료된 후 가장 마지막에 한 번 호출. doInBackground() 함수의 최종 값을 받기 위해 사용
        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }
            materialCalendarView.addDecorator(new EventDeco(Color.RED, calendarDays,ScheduleMainActivity.this));
        }

    }
}
