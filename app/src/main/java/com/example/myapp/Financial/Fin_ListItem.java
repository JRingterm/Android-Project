package com.example.myapp.Financial;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.myapp.R;

public class Fin_ListItem{

    private String day; //날짜
    private String howPay; //거래수단
    private String category; //분류
    private int money; //금액
    private String descripts; //세부사항
    private int check; //0이면 지출, 1이면 수입.

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHowPay() {
        return howPay;
    }

    public void setHowPay(String howPay) {
        this.howPay = howPay;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getDescripts() {
        return descripts;
    }

    public void setDescripts(String descripts) {
        this.descripts = descripts;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public Fin_ListItem(String day, String howPay, String category, int money, String descripts, int check) {
        this.day = day;
        this.howPay = howPay;
        this.category = category;
        this.money = money;
        this.descripts = descripts;
        this.check = check;
    }
}