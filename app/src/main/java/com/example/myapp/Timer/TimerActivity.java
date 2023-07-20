package com.example.myapp.Timer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapp.MainActivity;
import com.example.myapp.R;

import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity {

    EditText hourText, minText, secText;
    TextView hourTextView, minTextView, secTextView, dotTextView1, dotTextView2, timeTextView;
    Button startBtn, pauseBtn, cancelBtn, resumeBtn;
    ProgressBar circleBar;
    CountDownTimer countDownTimer;

    //입력 받은 시간을 밀리 초로 저장하는 변수
    long timeCountInMilliSeconds = 1 * 60000;
    //일시정지 시, 남은 시간을 받아두는 변수
    long latestRemainingTime = 0;

    Boolean checktimer = true; //시간을 입력 했는지 체크


    enum TimerStatus { //타이머의 상태
        Started,
        Stopped,
        Paused
    }
    TimerStatus timerStatus = TimerStatus.Stopped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        getSupportActionBar().setTitle("타이머");

        hourText = findViewById(R.id.HourText);
        minText = findViewById(R.id.MinText);
        secText = findViewById(R.id.SecText);
        hourTextView = findViewById(R.id.HourTextView);
        minTextView = findViewById(R.id.MinTextView);
        secTextView = findViewById(R.id.SecTextView);
        dotTextView1 = findViewById(R.id.DotText1);
        dotTextView2 = findViewById(R.id.DotText2);
        timeTextView = findViewById(R.id.TimeTextView);
        startBtn = findViewById(R.id.StartBtn);
        pauseBtn = findViewById(R.id.PauseBtn);
        cancelBtn = findViewById(R.id.CancelBtn);
        resumeBtn = findViewById(R.id.ResumeBtn);
        circleBar = findViewById(R.id.CircleProgressBar);
    }

    //설정 버튼을 타이틀 바에 집어 넣기
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_res, menu);
        return true;
    }

    //설정 버튼을 클릭 했을 때의 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (R.id.Settings): //설정 버튼을 눌렀다면,
                Intent settingIntent = new Intent(this, TimerMenuActivity.class); //Menu 액티비티를 intent해준다.
                startActivity(settingIntent);//intent한 액티비티로 화면 전환
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //시작 버튼 이벤트
    public void StartBtnClick(View view) {
        setTimerValues(); //타이머 값을 설정해 주는 함수
        setProgressBarValues(); //프로그레스 바에 값 세팅
        timerStatus = TimerStatus.Started;

        startCountDownTimer();//타이머 시작
    }
    //일시정지 버튼 이벤트
    public void PauseBtnClick(View view) {
        timerStatus = TimerStatus.Paused; //타이머를 일시정지 상태로 변환
        pauseBtn.setVisibility(View.INVISIBLE);
        resumeBtn.setVisibility(View.VISIBLE);
    }
    //재개 버튼 이벤트
    public void ResumeBtnClick(View view) {
        resumeBtn.setVisibility(View.INVISIBLE);
        pauseBtn.setVisibility(View.VISIBLE);

        timeCountInMilliSeconds = latestRemainingTime;//밀리초 값 변수를 남은 시간으로 초기화

        timerStatus = TimerStatus.Started; //타이머를 시작 상태로 변환

        startCountDownTimer();//타이머 시작
    }
    //취소 버튼 이벤트
    public void CancelBtnClick(View view) {
        countDownTimer.cancel();//타이머 취소
        setNotTimerVisible();
    }
    //카운트 다운을 시작하는 함수
    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            //수행 간격마다 호출되는 함수 millisUntilFinished=남은 시간 밀리 초 단위로 표시
            @Override
            public void onTick(long millisUntilFinished) {
                if(timerStatus == TimerStatus.Paused){ //일시정지를 눌렀다면
                    latestRemainingTime = millisUntilFinished; //남은 시간 저장
                    countDownTimer.cancel();//타이머 종료
                }
                timeTextView.setText(timeFormatter(millisUntilFinished));//타이머 텍스트 업데이트
                circleBar.setProgress((int) (millisUntilFinished / 1000));//프로그레스 바 업데이트
            }
            //타이머가 모두 수행되면 호출되는 함수.
            @Override
            public void onFinish() {
                timeTextView.setText(timeFormatter(timeCountInMilliSeconds));
                setProgressBarValues(); //프로그레스 바에 값 세팅
                setNotTimerVisible(); //뷰 Visible 설정
                if(checktimer == true){
                    //팝업 알림 설정
                    Intent intent = new Intent(TimerActivity.this, TimerPopupActivity.class);
                    startActivity(intent);
                }

            }
        }.start();
        countDownTimer.start();//카운트 다운 시작
    }

    //입력 받은 타이머 값을 밀리 초로 바꿔 주는 함수, 입력 안했을 때 예외처리
    private void setTimerValues() {
        int hour = 0, min = 0, sec = 0;
        //시간을 입력하지 않았다면 메세지 출력
        if(hourText.getText().toString().isEmpty() && minText.getText().toString().isEmpty() && secText.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "시간을 설정해주세요", Toast.LENGTH_SHORT).show();
            checktimer = false; //입력하지 않았다는 것을 표시
        }
        else{
            checktimer = true; //입력 했다는 것을 표시
            if(hourText.getText().toString().isEmpty()){
                hour = 0;
            }
            else{
                hour = Integer.parseInt(hourText.getText().toString().trim());
            }
            if(minText.getText().toString().isEmpty()){
                min = 0;
            }
            else{
                min = Integer.parseInt(minText.getText().toString().trim());
            }
            if(secText.getText().toString().isEmpty()){
                sec = 0;
            }
            else{
                sec = Integer.parseInt(secText.getText().toString().trim());
            }
            setTimerVisible(); //뷰 Visible 설정
        }
        //입력 받은 시간을 밀리 초로 변환
        timeCountInMilliSeconds = (hour * 60 * 60 * 1000) + (min * 60 * 1000) + (sec * 1000);
    }

    //시간 포맷을 설정 해주는 함수
    private String timeFormatter(long milliSeconds) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
    }

    //원형 프로그레스 바에 입력 받은 시간 값으로 세팅하는 함수
    private void setProgressBarValues() {
        circleBar.setMax((int) timeCountInMilliSeconds / 1000);
        circleBar.setProgress((int) timeCountInMilliSeconds / 1000);
    }

    //타이머 시작 시 뷰 Visible 설정
    private void setTimerVisible() {
        //시작 버튼 안보이게
        startBtn.setVisibility(View.INVISIBLE);
        //시간, 분, 초 텍스트 안보이게
        hourTextView.setVisibility(View.INVISIBLE);
        minTextView.setVisibility(View.INVISIBLE);
        secTextView.setVisibility(View.INVISIBLE);
        //시간 입력 받는 텍스트 안보이게
        hourText.setVisibility(View.INVISIBLE);
        minText.setVisibility(View.INVISIBLE);
        secText.setVisibility(View.INVISIBLE);
        //:표시 안보이게
        dotTextView1.setVisibility(View.INVISIBLE);
        dotTextView2.setVisibility(View.INVISIBLE);

        //일시정지, 취소 버튼 보이게
        pauseBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
        //프로그레스 바 보이게
        circleBar.setVisibility(View.VISIBLE);
        //타이머 보이게
        timeTextView.setVisibility(View.VISIBLE);
    }

    //타이머 끝났을 때 뷰 Visible 설정
    private void setNotTimerVisible() {
        //타이머 끝나면 안보이게
        timeTextView.setVisibility(View.INVISIBLE);
        circleBar.setVisibility(View.INVISIBLE);
        pauseBtn.setVisibility(View.INVISIBLE);
        cancelBtn.setVisibility(View.INVISIBLE);
        resumeBtn.setVisibility(View.INVISIBLE);
        //타이머 끝나면 보이게
        startBtn.setVisibility(View.VISIBLE);
        hourTextView.setVisibility(View.VISIBLE);
        minTextView.setVisibility(View.VISIBLE);
        secTextView.setVisibility(View.VISIBLE);
        hourText.setVisibility(View.VISIBLE);
        minText.setVisibility(View.VISIBLE);
        secText.setVisibility(View.VISIBLE);
        dotTextView1.setVisibility(View.VISIBLE);
        dotTextView2.setVisibility(View.VISIBLE);
    }
}