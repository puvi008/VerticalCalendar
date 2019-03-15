package br.com.jpttrindade.calendarview.view;

import android.app.Service;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import br.com.jpttrindade.calendarview.R;
import br.com.jpttrindade.calendarview.adapters.CalendarAdapter;
import br.com.jpttrindade.calendarview.bottomsheet.MonthBottomSheet;
import br.com.jpttrindade.calendarview.bottomsheet.YearBottomSheet;
import br.com.jpttrindade.calendarview.data.Month;
import br.com.jpttrindade.calendarview.listener.onDateChangedListener;
import br.com.jpttrindade.calendarview.listener.onMonthChangedListener;


public class CalendarView extends FrameLayout implements onDateChangedListener, onMonthChangedListener {

    private Context mContext;
    private int mYear;
    private String[] mMonths;

    private RecyclerView rl_calendar;
    private RecyclerView.LayoutManager mLayoutManager;
    private CalendarAdapter mCalendarAdapter;
    private OnDayClickListener mOnDayClickListener;
    private Attributes calendarAttrs;


    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 1; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private TextView tvYear, tvMonth;

    private List<String> mWeekLabels;
    ArrayList<String> years = new ArrayList<String>();
    List<String> monthsList = Arrays.asList(getContext().getResources().getStringArray(R.array.months));

    private String previousYear;
    private String previousMonth;


    public CalendarView(Context context) {
        super(context);
        init(null, 0);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mContext = getContext();

        calendarAttrs = new Attributes();
        getAttrs(attrs, defStyle);

        mWeekLabels = Arrays.asList(this.getResources().getStringArray(R.array.weeks));

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        View content = layoutInflater.inflate(R.layout.calendar_view, null, false);

        addView(content);

        LinearLayout weekDayNames = (LinearLayout) findViewById(R.id.label_days);
        weekDayNames.getLayoutParams().height = calendarAttrs.weekdayHeight;
        weekDayNames.setBackgroundColor(calendarAttrs.weekdayBackgroundColor);
        tvYear = (TextView) findViewById(R.id.tvyear);
        tvMonth = (TextView) findViewById(R.id.tvmonth);


        for (int i = 0; i < weekDayNames.getChildCount(); i++) {
            weekDayNames.getChildAt(i).getLayoutParams().width = calendarAttrs.dayWidth;
        }

        for (int i = 0; i < weekDayNames.getChildCount(); i++) {
            if (calendarAttrs.weekLabelFormat == 1) {
                ((TextView) weekDayNames.getChildAt(i)).setText(mWeekLabels.get(i));
            }
            if (calendarAttrs.weekendDifferenctColor) {
                if (i == 0 || i == 6) {
                    ((TextView) weekDayNames.getChildAt(i)).setTextColor(calendarAttrs.weekEndColor);
                    continue;
                }
                ((TextView) weekDayNames.getChildAt(i)).setTextColor(calendarAttrs.weekLabelColor);
            } else {
                ((TextView) weekDayNames.getChildAt(i)).setTextColor(calendarAttrs.weekLabelColor);
            }
        }

        rl_calendar = (RecyclerView) findViewById(R.id.rl_calendar);
        mLayoutManager = new LinearLayoutManager(mContext);
        rl_calendar.setLayoutManager(mLayoutManager);

        setAdapter();

        mLayoutManager.scrollToPosition(3);

        rl_calendar.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

               /* visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mCalendarAdapter.getItemCount();
                firstVisibleItem = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();

                Month month=mCalendarAdapter.mMonths.get(firstVisibleItem);
                tvMonth.setText(monthsList.get(month.value-1));
                tvYear.setText(String.valueOf(month.year));

                if (loading) {
                   *//* if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }*//*
                    Log.i("abcmaxReachedEnd", String.valueOf(mCalendarAdapter.maxReachedEnd));
                    Log.i("abcminReachedEnd", String.valueOf(mCalendarAdapter.minReachedEnd));

                    if (!mCalendarAdapter.maxReachedEnd || !mCalendarAdapter.minReachedEnd) {
                        loading = false;
                    }
                }
                if (!loading && !mCalendarAdapter.maxReachedEnd) {
                    // End has been reached
                    mCalendarAdapter.getNextMonths();
                    loading = true;
                }

                if (!loading && !mCalendarAdapter.minReachedEnd) {
                    // Start has been reached
                    mCalendarAdapter.getPreviousMonth();
                    loading = true;
                }*/
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mCalendarAdapter.getItemCount();
                firstVisibleItem = ((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition();

                Month month=mCalendarAdapter.mMonths.get(firstVisibleItem);
                tvMonth.setText(monthsList.get(month.value-1));
                tvYear.setText(String.valueOf(month.year));

                Log.i("loading",loading+"");
                if (loading) {
                    if (totalItemCount >= previousTotal) {
                        loading = false;
                        Log.i("loading", loading + "");
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold) && !mCalendarAdapter.maxReachedEnd) {
                    // End has been reached
                    mCalendarAdapter.getNextMonths();
                    loading = true;
                    Log.i("loading",loading+"");
                }

                if (!loading && firstVisibleItem==0 && !mCalendarAdapter.minReachedEnd) {
                    /*if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) {*/
                        // Start has been reached
                        mCalendarAdapter.getPreviousMonth();
                        loading = true;
                        Log.i("loading",loading+"");
                    }

            }
        });

//        a.recycle();

        tvYear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    previousYear = tvYear.getText().toString();
                    years = new ArrayList<String>();
                    ArrayList<String> years = getDifferenceYear(mCalendarAdapter.min.get(Calendar.YEAR), mCalendarAdapter.max.get(Calendar.YEAR));
                    Bundle bundle = new Bundle();
                    YearBottomSheet yearBottomSheet = new YearBottomSheet().newInstance(bundle);
                    FragmentManager fm = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                    yearBottomSheet.setListener(CalendarView.this);
                    yearBottomSheet.setYears(years);
                    yearBottomSheet.show(fm, "showLocation");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        tvMonth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    previousMonth = tvMonth.getText().toString();
                    ArrayList<String> months = new ArrayList<String>();
                    months.addAll(differenctMonthBetween(tvYear.getText().toString()));
                    Bundle bundle = new Bundle();
                    MonthBottomSheet monthBottomSheet = new MonthBottomSheet().newInstance(bundle);
                    FragmentManager fm = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                    monthBottomSheet.setListener(CalendarView.this);
                    monthBottomSheet.setMonths(months);
                    monthBottomSheet.show(fm, "showLocation");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        invalidate();

    }

    private List<String> differenctMonthBetween(String selectedYear) {
        try {
            List<String> filterMonthList = new ArrayList<>();

            if (selectedYear.toLowerCase().equals(String.valueOf(mCalendarAdapter.min.get(Calendar.YEAR)).toLowerCase()) || selectedYear.toLowerCase().equals(String.valueOf(mCalendarAdapter.max.get(Calendar.YEAR)).toLowerCase())) {
                if (selectedYear.toLowerCase().equals(String.valueOf(mCalendarAdapter.min.get(Calendar.YEAR)).toLowerCase())) {
                    for (int i = mCalendarAdapter.min.get(Calendar.MONTH) - 1; i <= monthsList.size() - 1; i++) {
                        filterMonthList.add(monthsList.get(i));
                    }
                    return filterMonthList;
                } else if (selectedYear.toLowerCase().equals(String.valueOf(mCalendarAdapter.max.get(Calendar.YEAR)).toLowerCase())) {
                    for (int j = mCalendarAdapter.max.get(Calendar.MONTH); j > 0; j--) {
                        filterMonthList.add(monthsList.get(j - 1));
                    }
                    Collections.reverse(filterMonthList);
                    return filterMonthList;
                }
            }
            return monthsList;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private ArrayList<String> getDifferenceYear(int startYear, int endYear) {
        if (startYear == endYear + 1) {
            return years;
        } else {
            years.add(String.valueOf(startYear));
            startYear++;
            getDifferenceYear(startYear, endYear);
        }
        return years;
    }

    private void getAttrs(AttributeSet attrs, int defStyle) {
        final TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CalendarView, defStyle, 0);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        calendarAttrs.weekdayHeight = (int) a.getDimension(R.styleable.CalendarView_weekdayNameHeight, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 24, displayMetrics));


        TypedValue typedValue = new TypedValue();

        a.getValue(R.styleable.CalendarView_weekdayNameBackgroundColor, typedValue);
        Log.d("DEBUG", "typedValue = " + typedValue.toString());

        if (typedValue.equals(TypedValue.TYPE_REFERENCE)) {
//        if (TypedValue.TYPE_REFERENCE == a.getType(R.styleable.CalendarView_weekdayNameBackgroundColor)) {
            calendarAttrs.weekdayBackgroundColor = ContextCompat.getColor(mContext, a.getResourceId(R.styleable.CalendarView_weekdayNameBackgroundColor, R.color.default_backgroundColor));
        } else {
            calendarAttrs.weekdayBackgroundColor = a.getColor(R.styleable.CalendarView_weekdayNameBackgroundColor, ContextCompat.getColor(mContext, R.color.default_backgroundColor));
        }

        if (!(getBackground() instanceof ColorDrawable)) {
            setBackgroundResource(R.color.default_backgroundColor);
        }


        calendarAttrs.dayHeight = (int) a.getDimension(R.styleable.CalendarView_dayHeight, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 48, displayMetrics));
        calendarAttrs.dayWidth = (int) a.getDimension(R.styleable.CalendarView_dayWidth, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 48, displayMetrics));

        calendarAttrs.todayCircleSize = (int) a.getDimension(R.styleable.CalendarView_todayCircleSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 30, displayMetrics));

        a.getValue(R.styleable.CalendarView_todayCircleColor, typedValue);
        Log.d("DEBUG", "typedValue = " + typedValue.toString());

        if (typedValue.equals(TypedValue.TYPE_REFERENCE)) {
            // if (TypedValue.TYPE_REFERENCE == a.getType(R.styleable.CalendarView_todayCircleColor)) {
            calendarAttrs.todayCircleColor = ContextCompat.getColor(mContext, a.getResourceId(R.styleable.CalendarView_todayCircleColor, R.color.default_todayCircleColor));
        } else {
            calendarAttrs.todayCircleColor = a.getColor(R.styleable.CalendarView_todayCircleColor, ContextCompat.getColor(mContext, R.color.default_todayCircleColor));
        }

        a.getValue(R.styleable.CalendarView_eventCircleColor, typedValue);
        Log.d("DEBUG", "typedValue = " + typedValue.toString());

        if (typedValue.equals(TypedValue.TYPE_REFERENCE)) {
            //if (TypedValue.TYPE_REFERENCE == a.getType(R.styleable.CalendarView_eventCircleColor)) {
            calendarAttrs.eventCircleColor = ContextCompat.getColor(mContext, a.getResourceId(R.styleable.CalendarView_eventCircleColor, R.color.default_eventCircleColor));
        } else {
            calendarAttrs.eventCircleColor = a.getColor(R.styleable.CalendarView_eventCircleColor, ContextCompat.getColor(mContext, R.color.default_eventCircleColor));
        }

        calendarAttrs.monthDividerSize = (int) a.getDimension(R.styleable.CalendarView_monthDividerSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 48, displayMetrics));

        calendarAttrs.monthLabelSize = a.getDimension(R.styleable.CalendarView_monthLabelSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 14, displayMetrics));

        calendarAttrs.monthLabelHeight = (int) a.getDimension(R.styleable.CalendarView_monthLabelHeight, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 24, displayMetrics));


        calendarAttrs.weekendDifferenctColor = a.getBoolean(R.styleable.CalendarView_weekEndDifferenctColor, false);

        calendarAttrs.weekEndColor = ContextCompat.getColor(mContext, a.getResourceId(R.styleable.CalendarView_weekEndColor, R.color.default_eventCircleColor));

        calendarAttrs.weekLabelColor = ContextCompat.getColor(mContext, a.getResourceId(R.styleable.CalendarView_weekLabelColor, R.color.black));

        calendarAttrs.weekLabelFormat = a.getInt(R.styleable.CalendarView_weekLabelFormat, -1);

        calendarAttrs.monthLabelColor = ContextCompat.getColor(mContext, a.getResourceId(R.styleable.CalendarView_monthLabelColor, R.color.black));

        calendarAttrs.markEveryFirstofMonth = a.getBoolean(R.styleable.CalendarView_markEveryFirstofMonth, false);

        calendarAttrs.minDate = a.getString(R.styleable.CalendarView_minDate);
        calendarAttrs.maxDate = a.getString(R.styleable.CalendarView_maxDate);

        calendarAttrs.daytextsize=(int)a.getFloat(R.styleable.CalendarView_dayTextSize,14);
        calendarAttrs.dayspace=(int)a.getFloat(R.styleable.CalendarView_daySpace,12);
        calendarAttrs.dayblockedcolor= ContextCompat.getColor(mContext, a.getResourceId(R.styleable.CalendarView_dayblockedcolor, R.color.font3));

        a.recycle();
    }

    private void setAdapter() {
        mCalendarAdapter = new CalendarAdapter(mContext, calendarAttrs, new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(String month, String year) {
                tvMonth.setText(month);
                tvYear.setText(year);
            }
        });

        //mCalendarAdapter.setMonthLabelHeight(monthLabelHeight);

        //mCalendarAdapter.setDayHeight(dayHeight);

        rl_calendar.setAdapter(mCalendarAdapter);

        mCalendarAdapter.setOnDayClickListener(new OnDayClickListener() {

            @Override
            public void onClick(int day, int month, int year, boolean hasEvent) {

                //Toast.makeText(getContext(), day+"/"+month+"/"+year, Toast.LENGTH_SHORT).show();
                if (mOnDayClickListener != null) {
                    mOnDayClickListener.onClick(day, month, year, hasEvent);
                }
            }
        });
    }

    public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        mOnDayClickListener = onDayClickListener;
    }


    public void addEvent(int day, int month, int year) {
        mCalendarAdapter.addEvent(day, month, year);
    }

    public void deleteEvent(int day, int month, int year) {
        mCalendarAdapter.deleteEvent(day, month, year);
    }

    @Override
    public void onDateChanged(String year) {
        tvYear.setText(year);
        tvMonth.setText("");
    }

    @Override
    public void onMonthChanged(String month) {
        tvMonth.setText(month);
        moveToMonth(tvYear.getText().toString(), tvMonth.getText().toString());
    }

    public void moveToMonth(String year, String month) {
        try {
            ArrayList<Month> months = mCalendarAdapter.mMonths;
            for (Month mons : months) {
                if (monthsList.get(mons.value - 1).toString().toLowerCase().equals(month.toLowerCase()) && mons.year == Integer.parseInt(year)) {
                    int position = months.indexOf(mons);
                    rl_calendar.getLayoutManager().scrollToPosition(position);
                    return;
                }
            }

            for(String mon:monthsList){
                if(mon.toLowerCase().equals(month.toLowerCase())){
                    mCalendarAdapter.reloadCalendar(Integer.parseInt(year),monthsList.indexOf(mon)+1);
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void moveToSelectedYear(String year, String month) {
         /*---------if the year is greater than current year----------*/
        /*if (Integer.parseInt(year) > Integer.parseInt(previousYear)) {
            mCalendarAdapter.getNextMonths();
            Month monthes = mCalendarAdapter.mMonths.get(mCalendarAdapter.mMonths.size() - 1);
            if (monthes.year != Integer.parseInt(year)) {
                moveToSelectedYear(year);
            }else{
                rl_calendar.getLayoutManager().scrollToPosition(mCalendarAdapter.mMonths.size()-1);
            }
        } else {
            mCalendarAdapter.getPreviousMonth();
        }*/

    }


    public int findMonthPosition(String month) {
        try {
            for (String mon : monthsList) {
                if (mon.toLowerCase().equals(month)) {
                    return monthsList.indexOf(mon);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    /* Classes & Interfaces*/

    public interface OnDayClickListener {
        public void onClick(int day, int month, int year, boolean hasEvent);
    }


    public class Attributes {
        public int weekdayHeight;
        public int weekdayBackgroundColor;

        public int dayWidth;
        public int dayHeight;

        public int todayCircleColor;
        public int todayCircleSize;

        public float monthLabelSize;
        public int monthLabelHeight;

        public int monthDividerSize;


        public int eventCircleColor;

        public boolean weekendDifferenctColor;

        public int weekEndColor;

        public int weekLabelColor;

        public int weekLabelFormat;

        public int monthLabelColor;

        public boolean markEveryFirstofMonth;

        public String minDate;

        public String maxDate;
        public int daytextsize;
        public int dayspace;
        public int dayblockedcolor;
    }

    public interface OnMonthChangedListener {
        public void onMonthChanged(String month, String year);
    }
}

