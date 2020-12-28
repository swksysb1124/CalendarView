package com.example.calendarviewtest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class CalendarView extends View {

    private static final String TAG = CalendarView.class.getSimpleName();

    private static final String[] weekdayNames = {"S", "M", "T", "W", "T", "F", "S"};
    private static final String[] monthNames = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private static final int COLOR_MAGENTA = Color.parseColor("#e30074");
    private static final int COLOR_LIGHT_BLUE = Color.parseColor("#e3ecf3");
    private static final int COLOR_LIGHT_GRAY = Color.parseColor("#f0f0f0");

    private static final int DEFAULT_YEAR = 1983;
    private static final int DEFAULT_MONTH = 1;
    private static final int DEFAULT_TEXT_SIZE = 18;
    private static final int DEFAULT_VIEW_WIDTH = 300; // dip
    private static final float DEFAULT_HEIGHT_WIDTH_RATIO = 0.8f;
    private static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;

    int year; // attr: year
    int month;// attr: month
    boolean showMonthLabel = false; // attr: showMonthLabel; if show month label, hide weekday label.
    boolean isMonthAlignFirstData = false; // attr: isMonthAlignFirstData

    private int[][] calendarTable; // store calendarTable[week][day] = date

    private Calendar calendar;
    private Date today;

    private Paint paint = new Paint();

    private int textSize = DEFAULT_TEXT_SIZE; // dip
    private int backgroundColor = DEFAULT_BACKGROUND_COLOR;
    private float heightWidthRatio = DEFAULT_HEIGHT_WIDTH_RATIO; // height / width

    private float currentTouchX; // simulate click event
    private float currentTouchY; // simulate click event

    private OnDateSelectedListener dateSelectedListener;

    public CalendarView(Context context) {
        super(context);

        // 初始化可變參數
        this.year = DEFAULT_YEAR;
        this.month = DEFAULT_MONTH;

        initDateSetting();
        setPaints();
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // 初始化可變參數
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CalendarView, 0, 0);
        try {
            year = a.getInteger(R.styleable.CalendarView_year, DEFAULT_YEAR);
            month = a.getInteger(R.styleable.CalendarView_month, DEFAULT_MONTH);
            showMonthLabel = a.getBoolean(R.styleable.CalendarView_showYearMonthLabel, false);
            isMonthAlignFirstData = a.getBoolean(R.styleable.CalendarView_isMonthAlignFirstData, false);
        } finally {
            a.recycle();
        }

        initDateSetting();
        setPaints();
    }

    public void setDateSelectedListener(OnDateSelectedListener dateSelectedListener) {
        this.dateSelectedListener = dateSelectedListener;
    }

    public void setYearAndMonth(int year, int month) {
        this.year = year;
        this.month = month;
        invalidate();
    }

    private void setPaints() {
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(DensityUtil.dip2px(getContext(), textSize));
        paint.setAntiAlias(true);
    }

    private void initDateSetting() {
        calendar = Calendar.getInstance();
        today = new Date(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DATE));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int defaultWidth = DensityUtil.dip2px(getContext(), DEFAULT_VIEW_WIDTH);
        int width = measureDimension(defaultWidth, widthMeasureSpec);

        calendar.set(year, month - 1, 1);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK); // sun(1) ~ sat(7)
        int dayOfFirstDate = weekDay - 1;
        int daysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int lastWeek = (dayOfFirstDate + daysOfMonth - 1) / 7;

        calendarTable = new int[lastWeek + 1][7];

        int defaultHeight = (int) (width / 7 * heightWidthRatio * (2 + lastWeek));

        int height = measureDimension(defaultHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureDimension(int defaultSize, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int result;
        if (specMode == MeasureSpec.EXACTLY) { // match_parent & specified value
            result = specSize;
        } else {
            result = defaultSize;   //UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) { // wrap_content
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackgroundColor(canvas);

        float xGridWidth = (float) getWidth() / 7;
        float yGridWidth = (float) (xGridWidth * heightWidthRatio);
        float xOffset = xGridWidth / 2;
        float yOffset = yGridWidth / 2;

        calendar.set(year, month - 1, 1);

        int weekDay = calendar.get(Calendar.DAY_OF_WEEK); // sun(1) ~ sat(7)
        int dayOfFirstDate = weekDay - 1;
        int daysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        drawLabelBackground(canvas, yGridWidth, COLOR_MAGENTA);

        if (!showMonthLabel) {
            drawWeekDayLabel(canvas, xGridWidth, yGridWidth, xOffset, yOffset, weekdayNames);
        }
        for (int date = 1; date <= daysOfMonth; date++) {
            int day = (dayOfFirstDate + date - 1) % 7; // x
            int week = (dayOfFirstDate + date - 1) / 7; // y

            calendarTable[week][day] = date;

            if (showMonthLabel) {
                if (date == 1) {
                    drawYearMonthLabel(canvas, xGridWidth, xOffset, yOffset, day, Color.WHITE);
                }
            }
            if (day == 0 || day == 6) { // weekend
                drawWeekend(canvas, xGridWidth, yGridWidth, xOffset, yOffset, date, day, week);
            } else {
                drawDay(canvas, xGridWidth, yGridWidth, xOffset, yOffset, date, day, week);
            }
        }
    }

    private void drawBackgroundColor(Canvas canvas) {
        canvas.drawColor(backgroundColor);
    }

    private void drawYearMonthLabel(Canvas canvas, float xGridWidth, float xOffset, float yOffset, int day, int color) {
        paint.setColor(color);
        paint.setTextAlign(Paint.Align.LEFT);
        String text = monthNames[month - 1] + " " + year;
        float x = xOffset;
        float y = yOffset + getTextVerticalOffset(text);
        if (isMonthAlignFirstData) {
            paint.setTextAlign(Paint.Align.CENTER);
            text = monthNames[month - 1];
            x = xOffset + day * xGridWidth;
        }
        canvas.drawText(text, x, y, paint);
    }

    private void drawLabelBackground(Canvas canvas, float yGridWidth, int color) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(new Rect(0, 0, getWidth(), (int) yGridWidth), paint);
    }

    private void drawWeekDayLabel(Canvas canvas, float xGridWidth, float yGridWidth, float xOffset, float yOffset, String[] labels) {
        for (int i = 0; i < 7; i++) {
            String text = labels[i];

            float x = i * xGridWidth + xOffset;
            float y = yOffset;

            int padding = DensityUtil.dip2px(getContext(), 1); // 1
            int roundRadius = DensityUtil.dip2px(getContext(), 10); // 10
            drawGridRoundRectBackground(canvas, xGridWidth, yGridWidth, x, y, padding, roundRadius, COLOR_MAGENTA, false);

            float y1 = getTextVerticalOffset(text);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.WHITE);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText(text, x, y + y1, paint);
        }
    }

    private void drawDay(Canvas canvas, float xGridWidth, float yGridWidth,
                         float xOffset, float yOffset,
                         int date, int day, int week) {

        int backgroundColor = COLOR_LIGHT_BLUE;
        int textColor = Color.BLACK;
        drawDayOfWeek(canvas, xGridWidth, yGridWidth, xOffset, yOffset, date, day, week, backgroundColor, textColor);
    }

    private void drawWeekend(Canvas canvas, float xGridWidth, float yGridWidth,
                             float xOffset, float yOffset,
                             int date, int day, int week) {

        int backgroundColor = COLOR_LIGHT_GRAY;
        int textColor = Color.GRAY;
        drawDayOfWeek(canvas, xGridWidth, yGridWidth, xOffset, yOffset, date, day, week, backgroundColor, textColor);
    }

    private void drawDayOfWeek(Canvas canvas, float xGridWidth, float yGridWidth,
                               float xOffset, float yOffset,
                               int date, int day, int week,
                               int backgroundColor, int textColor) {

        float x = xOffset + day * xGridWidth;
        float y = yOffset + (week + 1) * yGridWidth;

        // today
        if (today.year == year && today.month == month
                && today.date == date) {
            textColor = COLOR_MAGENTA;
        }

        // background
        int padding = DensityUtil.dip2px(getContext(), 2); // 1
        int roundRadius = DensityUtil.dip2px(getContext(), 100); // 10
        drawGridRoundRectBackground(canvas, xGridWidth, yGridWidth, x, y, padding, roundRadius, backgroundColor, true);

        // value
        drawDayValue(canvas, date, x, y, textColor);
    }

    private void drawDayValue(Canvas canvas, int day, float x, float y, int color) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        String text = String.valueOf(day);
        float y1 = getTextVerticalOffset(text);
        canvas.drawText(text, x, y + y1, paint);
    }

    private float getTextVerticalOffset(String text) {
        Rect r = new Rect();
        paint.getTextBounds(text, 0, text.length(), r);
        return (float) (r.height() / 2);
    }

    private void drawGridRoundRectBackground(Canvas canvas, float width, float height, float centerX, float centerY, int padding, int roundRadius, int color, boolean border) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);

        float left = centerX - width / 2 + padding;
        float top = centerY - height / 2 + padding;
        float right = centerX + width / 2 - padding;
        float bottom = centerY + height / 2 - padding;
        canvas.drawRoundRect(left, top, right, bottom,
                roundRadius, roundRadius, paint);
        if(border) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.GRAY);
            canvas.drawRoundRect(left, top, right, bottom,
                    roundRadius, roundRadius, paint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentTouchX = event.getX();
                currentTouchY = event.getY();
                return true;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                processItemClickEvent(event);
                break;
        }
        return false;
    }

    private void processItemClickEvent(MotionEvent event) {
        if (checkSamePointTouch(event)) {
            float xGridWidth = (float) getWidth() / 7;
            float yGridWidth = (xGridWidth * heightWidthRatio);

            int d = (int) (event.getX() / xGridWidth);
            int w = (int) (event.getY() / yGridWidth) - 1; // -1 because of label height

            if (d >= 0 && d < 7
                    && w >= 0 && w <= calendarTable.length) {
                int date = calendarTable[w][d];
                Log.i(TAG, "select date: " + year + "/" + month + "/" + date);
                if (date > 0) {
                    if (dateSelectedListener != null) {
                        dateSelectedListener.onDateSelected(year, month, date);
                    }
                }
            }
            // TODO: maybe add a click effect
        }
    }

    private boolean checkSamePointTouch(MotionEvent event) {
        double deltaX = currentTouchX - event.getX();
        double deltaY = currentTouchY - event.getY();
        return (Math.hypot(deltaX, deltaY) <= DensityUtil.dip2px(getContext(), 2));
    }

    interface OnDateSelectedListener {
        void onDateSelected(int year, int month, int date);
    }
}