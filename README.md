# CalendarView


## Layout 設定
```xml
<com.example.calendarviewtest.CalendarView
    android:id="@+id/calendar"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:isMonthAlignFirstData="false"
    app:month="12"
    app:showYearMonthLabel="true"
    app:year="2020"/>
```

- `app:year`: 年份
- `app:month`: 月份
- `app:isMonthAlignFirstData`: 標示月份標籤是否對齊月份第一天
`app:showYearMonthLabel="true"`
![](https://bitbucket.askey.com.tw:8443/projects/DBMOB/repos/calendarview/browse/type3.png) 

- `app:showYearMonthLabel`: 是否顯示月份標籤還是星期標籤

`app:showYearMonthLabel="true"`
![](https://bitbucket.askey.com.tw:8443/projects/DBMOB/repos/calendarview/browse/type2.png)  

`app:showYearMonthLabel="false"`
![](https://bitbucket.askey.com.tw:8443/projects/DBMOB/repos/calendarview/browse/type1.png)  

## 程式碼
```java
CalendarView calendar = findViewById(R.id.calendar);
calendar.setDateSelectedListener(new CalendarView.OnDateSelectedListener() {
    @Override
    public void onDateSelected(int year, int month, int date) {
        // 當日期被選擇  
    }
});
```


