package com.example.calendarviewtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CalendarView.OnDateSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    List<YearMonth> yearMonthList = new ArrayList<>();
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("2010");

        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        setCalendarListView();
    }

    private void setCalendarListView() {
        // create data
        for (int year = 2010; year <= 2020; year++) {
            for (int month = 1; month <= 12; month++) {
                yearMonthList.add(new YearMonth(year, month));
            }
        }

        // create calendar adapter
        CalendarAdapter adapter = new CalendarAdapter(yearMonthList);
        adapter.setOnDateSelectedListener(this);

        // create calendar list view
        RecyclerView rcvCalendarList = findViewById(R.id.rcvCalendarList);
        rcvCalendarList.setLayoutManager(new LinearLayoutManager(this));
        rcvCalendarList.setAdapter(adapter);
        // listen calendar list scroll event
        rcvCalendarList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // scroll to update current year
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                    YearMonth ym = yearMonthList.get(position);
                    Log.e(TAG, "position=" + position + ", year=" + ym.year + ", month=" + ym.month);
                    toast.setText(ym.year + "/" + ym.month);
                    toast.show();
                    setTitle(String.valueOf(ym.year));
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    @Override
    public void onDateSelected(int year, int month, int date) {
        toast.setText("" + year + "/" + month + "/" + date);
        toast.show();
    }
}