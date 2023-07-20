package com.example.myapp.Schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapp.R;

import java.util.ArrayList;

//Schedule_listitem의 커스텀 어댑터
public class Sche_ListAdapter extends BaseAdapter {
    ArrayList<Sche_ListItem> items = new ArrayList<Sche_ListItem>();
    Context context;
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    //item을 받아서 리스트뷰에 넘겨줌.
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        context = viewGroup.getContext();
        Sche_ListItem listItem = items.get(i);

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.schedule_listitem, viewGroup, false);
        }
        TextView titleText = view.findViewById(R.id.title_text);
        TextView startText = view.findViewById(R.id.start_text);
        TextView endText = view.findViewById(R.id.end_text);

        TextView alarmText = view.findViewById(R.id.alarm_text);

        titleText.setText(listItem.getTitle());
        startText.setText(listItem.getStart());
        endText.setText(listItem.getEnd());

        alarmText.setText(isAlarmQ(listItem.getAlarm()));

        return view;
    }

    public void addItem(Sche_ListItem item){
        items.add(item);
    }

    public String isAlarmQ(int i){
        if (i == 0){
            return "알림 off";
        }
        else{
            return "알림 on";
        }
    }
}
