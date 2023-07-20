package com.example.myapp.Timer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.example.myapp.R;

public class TimerMenuActivity extends AppCompatActivity {

    Switch vibrateSwitch, soundSwitch;
    RadioGroup radioGroup;
    RadioButton radio1, radio2;
    Boolean vibrateV, soundV, bell1, bell2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_menu);
        getSupportActionBar().setTitle("타이머 설정");

        vibrateSwitch = findViewById(R.id.VibrateSwitch);
        soundSwitch = findViewById(R.id.SoundSwitch);
        radioGroup = findViewById(R.id.RadioGroup);
        radio1 = findViewById(R.id.RadioBtn1);
        radio2 = findViewById(R.id.RadioBtn2);

        //SharedPreference를 이용해서 Setting_state 파일에 저장된 스위치 상태 값을 불러온다.
        SharedPreferences pref = getSharedPreferences("Setting_state", MODE_PRIVATE); //Setting_state 파일을 사용하겠다.

        //변수 vibrateV에 Switch_state 파일에 저장된 스위치 상태 값을 넣는다.
        vibrateV = pref.getBoolean("vibrate_state", true); // vibrate_state라는 key값을 불러와서 변수 vibrateV에 저장한다. 해당 key에 값이 없으면 true로 불러온다.
        //가져온 값으로 진동 여부 스위치 값 설정
        vibrateSwitch.setChecked(vibrateV);

        //변수 soundV에 Switch_state 파일에 저장된 스위치 상태 값을 넣는다.
        soundV = pref.getBoolean("sound_state", true);//sound_state라는 key값을 불러와서 변수 soundV에 저장한다. 해당 key에 값이 없으면 true로 불러온다.
        //가져온 값으로 소리 여부 스위치 값 설정
        soundSwitch.setChecked(soundV);

        //스위치 버튼 이벤트==================================================================================

        //진동 스위치의 상태가 변경될 때 실행되는 이벤트
        vibrateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SettingStateSave("vibrate_state", isChecked); //vibrate_state라는 key값으로 진동 스위치 상태를 저장하겠다.
            }
        });

        //소리 스위치의 상태가 변경될 때 실행되는 이벤트
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SettingStateSave("sound_state", isChecked); //sound_state라는 key값으로 소리 스위치 상태를 저장하겠다.
            }
        });

        //================================================================================================================================

        //***SharedPreference를 2개 사용하니까 Radio_state 파일은 생기지 않았음! 그래서 삭제.***
        //SharedPreferences Radiopref = getSharedPreferences("Radio_state", MODE_PRIVATE); //Radio_state 파일을 사용하겠다.

        //변수 bell1에 Setting_state 파일에 저장된 라디오 버튼 상태 값을 넣는다.
        bell1 = pref.getBoolean("first_bell", true); // first_bell라는 key값을 불러와서 변수 bell1에 저장한다. 해당 key에 값이 없으면 true로 불러온다.
        //가져온 값으로 라디오 버튼 값 설정
        radio1.setChecked(bell1);

        bell2 = pref.getBoolean("second_bell", false);
        radio2.setChecked(bell2);

        radio1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SettingStateSave("first_bell", isChecked);
            }
        });
        radio2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SettingStateSave("second_bell", isChecked);
            }
        });
    }

    //스위치(진동, 소리)의 상태를 저장하는 함수
    //SharedPreference를 이용해 스위치 on/off 상태 저장하기
    private void SettingStateSave(String key, boolean value){
        //Setting_state라는 이름으로 파일 생성, MODE_PRIVATE는 내 앱 내에서만 사용하도록 설정하는 것
        SharedPreferences pref = getSharedPreferences("Setting_state", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit(); //편집기를 연다.
        editor.putBoolean(key, value);//키와 값 형태로 Boolean으로 받는다.
        editor.apply();//파일에 저장
    }
}