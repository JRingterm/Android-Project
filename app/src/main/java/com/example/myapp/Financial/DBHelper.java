package com.example.myapp.Financial;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    //테이블의 컬럼명들을 선언하는 단계
    private static final String DATABASE_NAME = "FinancialBook.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "FinancialTable";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date"; //날짜
    private static final  String COLUMN_TRADING_METHOD = "trade_method"; //거래방식
    private static final String COLUMN_CATEGORY = "category"; //분류
    private static final String COLUMN_AMOUNT = "amount"; //금액
    private static final String COLUMN_DETAILS = "details"; //세부사항
    private static final String COLUMN_CHECK = "checks"; //0이면 지출, 1이면 수입

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //테이블 생성
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_DATE + " TEXT, " + COLUMN_TRADING_METHOD + " TEXT, " + COLUMN_CATEGORY + " TEXT, "+ COLUMN_AMOUNT + " INTEGER, " + COLUMN_DETAILS + " TEXT, " + COLUMN_CHECK + " INTEGER)";
        db.execSQL(createTable);
    }

    //이미 해당 이름의 테이블이 있다면, 없애버리고, 새로 만든다.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    //DB에 삽입하기
    public void insertExpense(String date, String method, String category, int amount, String details, int checks) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TRADING_METHOD, method);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DETAILS, details);
        values.put(COLUMN_CHECK, checks);
        db.insert(TABLE_NAME, null, values);
        Log.v("DB반영", "완료");
        db.close();
    }
}
