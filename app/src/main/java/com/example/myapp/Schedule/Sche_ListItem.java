package com.example.myapp.Schedule;

import java.util.Date;

//일정에서 보여지는 커스텀 리스트뷰
public class Sche_ListItem {
    private String title;
    private String start;
    private String end;
    private String location;
    private String alarmOption;
    private int alarm;
    private String description;



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    public String getAlarmOption() {
        return alarmOption;
    }

    public void setAlarmOption(String alarmOption) {
        this.alarmOption = alarmOption;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    Sche_ListItem(String title, String start, String end, String description, String location, String alarmOption, int alarm){
        this.title = title;
        this.start = start;
        this.end = end;
        this.description = description;
        this.location = location;
        this.alarmOption = alarmOption;
        this.alarm = alarm;
    }
}
