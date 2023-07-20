package com.example.myapp.Financial;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;

import java.util.List;

public class Fin_ListAdapter extends RecyclerView.Adapter<Fin_ListAdapter.ViewHolder>{
    private List<Fin_ListItem> dataList;

    // 생성자를 통해서 데이터를 전달받도록 함
    public Fin_ListAdapter (List<Fin_ListItem> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public Fin_ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_fin_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override // ViewHolder안의 내용을 position에 해당되는 데이터로 교체한다.
    public void onBindViewHolder(@NonNull Fin_ListAdapter.ViewHolder holder, int position) {
        Fin_ListItem dataItem = dataList.get(position);

        holder.bind(dataItem);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView fin_day_text, fin_how_text, fin_money_text, fin_descript_text, fin_check_text;

        public ViewHolder(View itemView) {
            super(itemView);

            fin_day_text = itemView.findViewById(R.id.fin_dayText);
            fin_how_text = itemView.findViewById(R.id.fin_howText);
            fin_money_text = itemView.findViewById(R.id.fin_moneyText);
            fin_descript_text = itemView.findViewById(R.id.fin_descriptText);
            fin_check_text = itemView.findViewById(R.id.checkText);
        }
        public void bind(Fin_ListItem expenseItem) {
            fin_day_text.setText(expenseItem.getDay());
            fin_how_text.setText(expenseItem.getHowPay());
            fin_money_text.setText(String.valueOf(expenseItem.getMoney()));
            fin_descript_text.setText(expenseItem.getDescripts());
            fin_money_text.setTextColor(I_P_check(expenseItem.getCheck()));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static int I_P_check(int i){
        if(i == 0){
            return Color.RED;
        }
        else{
            return Color.BLUE;
        }
    }
}
