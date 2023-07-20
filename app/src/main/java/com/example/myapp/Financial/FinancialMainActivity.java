package com.example.myapp.Financial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapp.R;
import com.example.myapp.Schedule.ScheduleMainActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FinancialMainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Fin_ListAdapter adapter;
    private List<Fin_ListItem> dataList;
    private DBHelper dbHelper;

    Button prev_btn, next_btn, income_btn, spend_btn;
    TextView fin_day;
    String dayText;
    String currentDate;
    boolean flag = true;//true = 월별, false = 연도별
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_main);
        getSupportActionBar().setTitle("가계부");

        prev_btn = findViewById(R.id.prev_Btn);
        next_btn = findViewById(R.id.next_Btn);
        spend_btn = findViewById(R.id.spend_Btn);
        income_btn = findViewById(R.id.income_Btn);
        fin_day = findViewById(R.id.Fin_DayText);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        currentDate = getCurrentMonth();

        fin_day.setText(currentDate);
        dataList = new ArrayList<>();
        adapter = new Fin_ListAdapter(dataList);
        recyclerView.setAdapter(adapter);

        dbHelper = new DBHelper(this);
        fetchExpensesFromDatabase();

        //지출 입력 버튼
        spend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FinancialSpendActivity.class);
                startActivity(intent);
            }
        });
        //수입 입력 버튼
        income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FinancialIncomeActivity.class);
                startActivity(intent);
            }
        });
        //< 버튼
        prev_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag == true){
                    currentDate = getOneMonthLater(currentDate);
                }
                else{
                    currentDate = getOneYearLater(currentDate);
                }
                fin_day.setText(currentDate);
                //쿼리 재가동
                dataList.clear();
                recyclerView.setAdapter(adapter);
                fetchExpensesFromDatabase();
            }
        });
        //> 버튼
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag == true){
                    currentDate = getOneMonthEarlier(currentDate);
                }
                else{
                    currentDate = getOneYearEarlier(currentDate);
                }
                fin_day.setText(currentDate);
                //쿼리 재가동
                dataList.clear();
                recyclerView.setAdapter(adapter);
                fetchExpensesFromDatabase();
            }
        });
    }
    //액티비티가 다시 보이게 되면, 초기화된 리사이클러뷰 보여주기.
    @Override
    public void onStart() {
        super.onStart();
        dataList.clear();
        recyclerView.setAdapter(adapter);
        fetchExpensesFromDatabase();
    }
    //=============================================================================================================
    // 현재 날짜의 년,월을 가져오는 메서드
    private String getCurrentMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    // 날짜를 한 달 뒤로 이동하는 메서드
    private String getOneMonthLater(String currentDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        try {
            Date date = dateFormat.parse(currentDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, -1);
            return dateFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentDate;
    }
    // 날짜를 한 달 앞으로 이동하는 메서드
    private String getOneMonthEarlier(String currentDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        try {
            Date date = dateFormat.parse(currentDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, +1);
            return dateFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentDate;
    }
    // 현재 연도를 가져오는 메서드
    private String getCurrentYear() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
    // 날짜를 1년 뒤로 이동하는 메서드
    private String getOneYearLater(String currentDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        try {
            Date date = dateFormat.parse(currentDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.YEAR, -1);
            return dateFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentDate;
    }
    // 날짜를 1년 앞으로 이동하는 메서드
    private String getOneYearEarlier(String currentDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        try {
            Date date = dateFormat.parse(currentDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.YEAR, +1);
            return dateFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentDate;
    }
    //=============================================================================================================
    //DB에서 데이터를 쿼리해서 리사이클러 뷰에 넘겨주는 함수.
    private void fetchExpensesFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        dayText = fin_day.getText().toString();
        String query = "SELECT * FROM FinancialTable WHERE date LIKE ? ORDER BY date ASC";
        String[] selectionArgs = new String[]{dayText + "%"};
        Cursor cursor = db.rawQuery(query, selectionArgs);
        //Cursor cursor = db.rawQuery("SELECT * FROM FinancialTable", null);

        int date_index = cursor.getColumnIndex("date");
        int method_index = cursor.getColumnIndex("trade_method");
        int category_index = cursor.getColumnIndex("category");
        int amounts_index = cursor.getColumnIndex("amount");
        int descript_index = cursor.getColumnIndex("details");
        int check_index = cursor.getColumnIndex("checks");

        while (cursor.moveToNext()) {
            String date = cursor.getString(date_index);
            Log.v("날짜", date); // 2023-06-02
            String convertdate;
            if(flag == true){
                convertdate = date.substring(8,10) + "일";
            }
            else{
                convertdate = date.substring(5,7) + "월 " + date.substring(8,10) + "일";
            }
            //TODO: 월별, 연도별에 맞게 날짜 표시해주기

            String method = cursor.getString(method_index);
            String category = cursor.getString(category_index);
            int amount = cursor.getInt(amounts_index);
            String details = cursor.getString(descript_index);
            int checks = cursor.getInt(check_index);

            Fin_ListItem expenseItem = new Fin_ListItem(convertdate, method, category, amount, details, checks);
            dataList.add(expenseItem);

        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }
    //menu.xml파일 가져와서 메뉴 만들기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fin_menu, menu);
        return true;
    }

    //메뉴 클릭 이벤트
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.toYear:
                //내역을 연도별로 보여주기
                Toast.makeText(this, "연도별로 변환", Toast.LENGTH_SHORT).show();
                flag = false;
                currentDate = getCurrentYear();
                fin_day.setText(currentDate);
                //쿼리 재가동
                dataList.clear();
                recyclerView.setAdapter(adapter);
                fetchExpensesFromDatabase();
                break;
            case R.id.toMonth:
                //내역을 월별로 보여주기
                Toast.makeText(this, "월별로 변환", Toast.LENGTH_SHORT).show();
                flag = true;
                currentDate = getCurrentMonth();
                fin_day.setText(currentDate);
                //쿼리 재가동
                dataList.clear();
                recyclerView.setAdapter(adapter);
                fetchExpensesFromDatabase();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}