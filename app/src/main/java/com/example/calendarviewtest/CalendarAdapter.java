package com.example.calendarviewtest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class CalendarAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<YearMonth> list;
    CalendarView.OnDateSelectedListener onDateSelectedListener;

    public void setOnDateSelectedListener(CalendarView.OnDateSelectedListener onDateSelectedListener) {
        this.onDateSelectedListener = onDateSelectedListener;
    }

    public CalendarAdapter(List<YearMonth> list) {
        this.list = list;
    }

    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        YearMonth ym = list.get(position);
        vh.calendarView.setYearAndMonth(ym.year, ym.month);
        vh.calendarView.requestLayout(); // make view to call onMeasure() again!
        if(onDateSelectedListener != null) {
            vh.calendarView.setDateSelectedListener(onDateSelectedListener);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CalendarView calendarView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            calendarView = itemView.findViewById(R.id.calendarView);
        }
    }
}
