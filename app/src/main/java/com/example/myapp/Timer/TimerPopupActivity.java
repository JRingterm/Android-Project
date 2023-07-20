package com.example.myapp.Timer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;

import com.example.myapp.R;

public class TimerPopupActivity extends AppCompatActivity {

    Vibrator vibrator; //진동 기능
    Ringtone ringtone; //소리 기능
    Boolean vibrateV, soundV, bell1, bell2; //sharedpreference에서 상태 값을 받아와 저장할 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_popup);

        SharedPreferences pref = getSharedPreferences("Setting_state", MODE_PRIVATE); //선언부

        //SharedPreference로 저장한 Setting_state파일에서 진동, 소리 스위치의 상태를 받아옴.
        vibrateV = pref.getBoolean("vibrate_state", true); // vibrate_state라는 key값을 불러와서 변수 vibrateV에 저장한다. 해당 key에 값이 없으면 true로 불러온다.
        soundV = pref.getBoolean("sound_state", true); //sound_state라는 key값을 불러와서 변수 soundV에 저장한다. 해당 key에 값이 없으면 true로 불러온다.

        //SharedPreference로 저장한 Setting_state파일에서 선택한 알림음 상태를 받아옴.
        bell1 = pref.getBoolean("first_bell", true);
        bell2 = pref.getBoolean("second_bell", false);

        //진동 사용
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); //진동
        long[] pattern = {1000, 2000}; //진동 패턴 {진동이 오기 전 대기시간, 진동이 지속되는 시간}

        //Ringtone으로 알림 소리 사용
        Uri notification;
        if(soundV == true){
            //어떤 라디오 버튼을 체크했냐에 따라 알림음 종류가 달라지도록
            if(bell1 == true){
                notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
            }
            else{
                notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
            }
            ringtone.play();
        }

        //스위치 버튼 상태에 따라 처리방법이 달라짐.
        if(vibrateV == true){
            vibrator.vibrate(pattern, 0);//무한 반복
        }
    }

    //해제 버튼 이벤트
    public void AlarmCancelClick(View view) {
        if(vibrateV == true){
            vibrator.cancel();
        }
        if(soundV == true){
            ringtone.stop();
        }
        finish();//팝업 액티비티 종료
    }
}