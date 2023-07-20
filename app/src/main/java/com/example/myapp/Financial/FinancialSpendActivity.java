package com.example.myapp.Financial;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FinancialSpendActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    Spinner spinner2;
    Button spend_save, spend_cancel;
    Button income_save, income_cancel, dayChoice;

    RadioGroup group;
    RadioButton cardRadio, billRadio;
    EditText moneyedit, descriptedit;
    TextView dayText;
    String[] spinner_items={"식비", "교통비", "여가생활비", "기타"};

    private SimpleDateFormat dateFormatter;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_spend);
        getSupportActionBar().setTitle("지출 등록");

        spend_save = findViewById(R.id.spend_saveBtn);
        spend_cancel = findViewById(R.id.spend_cancelBtn);
        dayChoice = findViewById(R.id.day_choice_btn);
        spinner2 = findViewById(R.id.spinner_spend);
        group = findViewById(R.id.radioGroup);
        cardRadio = findViewById(R.id.card_Radio);
        billRadio = findViewById(R.id.bill_Radio);
        moneyedit = findViewById(R.id.moneyEdit);
        descriptedit = findViewById(R.id.descriptEdit);
        dayText = findViewById(R.id.dayViewText);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = Calendar.getInstance();

        dbHelper = new DBHelper(this);



        //스피너 아이템 등록, 선택 이벤트------------------------------------------------------------
        spinner2 = findViewById(R.id.spinner_spend);

        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(
                this, R.layout.category_spinner_item, spinner_items
        );
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner2.setAdapter(spinner_adapter);

        dayChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        //저장 버튼 클릭
        spend_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dayText.getText().toString().equals("지출이 발생한 날짜") || TextUtils.isEmpty(moneyedit.getText().toString()) || TextUtils.isEmpty(descriptedit.getText().toString())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(FinancialSpendActivity.this);
                    builder.setTitle("경고!").setMessage("빼먹은게 없나 확인해주세요");
                    builder.setPositiveButton("OK", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else {
                    //날짜 받아오기
                    String day = dayText.getText().toString();
                    //거래수단 받아오기
                    String method = "카드";
                    if (cardRadio.isChecked()) {
                        method = "카드";
                    } else if (billRadio.isChecked()) {
                        method = "현금";
                    }
                    //분류 받아오기
                    String category = spinner2.getSelectedItem().toString();
                    //금액 받아오기
                    int amounts = Integer.parseInt(moneyedit.getText().toString());
                    //세부내용 받아오기
                    String details = descriptedit.getText().toString();
                    //지출 이니까 checks = 0;
                    int checks = 0;
                    Log.v("값이 잘 들어갔나 확인", day + method + category + amounts + details + checks);

                    dbHelper.insertExpense(day, method, category, amounts, details, checks);
                    Toast.makeText(getApplicationContext(), "지출내역 저장 완료", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

        spend_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    //날짜 선택 버튼을 누르면, DatePickerDialog가 뜨고, 선택하고 확인 누르면, textView로 값이 들어감.
    private void showDatePickerDialog() {
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int dayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String formattedDate = dateFormatter.format(selectedDate.getTime());
                        dayText.setText(formattedDate);
                    }
                },
                year,
                month,
                dayOfMonth
        );

        datePickerDialog.show();
    }

}