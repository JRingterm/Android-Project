package com.example.myapp.schedule_deco;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.example.myapp.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

public class EventDeco implements DayViewDecorator {
    //private final Drawable drawable;
    private int color;
    private HashSet<CalendarDay> dates;

    @SuppressLint("UseCompatLoadingForDrawables")
    public EventDeco(int color, Collection<CalendarDay> dates, Activity context) {
        //drawable = context.getResources().getDrawable(R.drawable.check);//그려놓은 체크표시. 날짜를 감싸는 테두리모양
        this.color = color;
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        //view.setSelectionDrawable(drawable); //내가 만든 커스텀 표시
        view.addSpan(new DotSpan(5, color)); // 날자밑에 점
    }
}
