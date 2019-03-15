package br.com.jpttrindade.calendarview.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import br.com.jpttrindade.calendarview.R;
import br.com.jpttrindade.calendarview.data.Day;
import br.com.jpttrindade.calendarview.data.WeekManager;
import br.com.jpttrindade.calendarview.data.Month;
import br.com.jpttrindade.calendarview.holders.MonthHolder;
import br.com.jpttrindade.calendarview.view.CalendarView;

/**
 * Created by joaotrindade on 06/09/16.
 */
public class CalendarAdapter extends RecyclerView.Adapter<MonthHolder> {


    private  List<String> mMonthLabels;
    public  ArrayList<Month> mMonths = new ArrayList<Month>();
    private final Context mContext;

    private int startYear; //ano atual (real)
    private int startMonth; // mes atual (real)
    private int today;


    private int earlyMonthLoaded; //mes mais antigo ja carregado
    private int earlyYearLoaded; //ano mais antigo ja carregado

     public int laterMonthLoaded; //mes mais a frente ja carregado
     public int laterYearLoaded; //ano mais a frente ja carregado

    private WeekManager weekManager;
    public Calendar max = Calendar.getInstance();
    public Calendar min = Calendar.getInstance();


    private int PAYLOAD = 3; // o numero de meses que serao carregados antes e depois do mes atual.
    private CalendarView.OnDayClickListener onDayClickListener;
    private HashMap<String, Boolean> mEvents;
    private HashMap<String, Boolean> selectedDate;
    private CalendarView.Attributes attrs;
    CalendarView.OnMonthChangedListener onMonthChangedListener;
    private HashMap<String, Boolean> blockedDates;
    public boolean minReachedEnd = false;
    public boolean maxReachedEnd = false;


    public CalendarAdapter(Context context, CalendarView.Attributes calendarAttrs, CalendarView.OnMonthChangedListener onMonthChangedListener) {
        mContext = context;
        attrs = calendarAttrs;
        this.onMonthChangedListener = onMonthChangedListener;
        Calendar c = Calendar.getInstance();
        reloadCalendar( c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1);
    }

    public void reloadCalendar(int year,int month){
        mMonthLabels = Arrays.asList(mContext.getResources().getStringArray(R.array.months));
        Calendar c = Calendar.getInstance();
        startYear = year;
        startMonth = month;
        today = c.get(Calendar.DAY_OF_MONTH);


        String[] minarr = attrs.minDate.split("/");
        min.set(Calendar.DAY_OF_MONTH, Integer.parseInt(minarr[0]));
        min.set(Calendar.MONTH, Integer.parseInt(minarr[1]));
        min.set(Calendar.YEAR, Integer.parseInt(minarr[2]));


        String[] maxarr = attrs.maxDate.split("/");
        max.set(Calendar.DAY_OF_MONTH, Integer.parseInt(maxarr[0]));
        max.set(Calendar.MONTH, Integer.parseInt(maxarr[1]));
        max.set(Calendar.YEAR, Integer.parseInt(maxarr[2]));

        if (min.getTimeInMillis() > max.getTimeInMillis()) {
            Toast.makeText(mContext, "end date should be miss matched", Toast.LENGTH_LONG).show();
        }
        /*attrs.minDate
        attrs.maxDate*/

        mMonths.clear();
        notifyDataSetChanged();
        mEvents = new HashMap<>();
        selectedDate = new HashMap<>();
        blockedDates = new HashMap<>();

        earlyMonthLoaded = startMonth;
        earlyYearLoaded = startYear;
        laterYearLoaded = startYear;
        laterMonthLoaded = startMonth;


        onMonthChangedListener.onMonthChanged(String.valueOf(mMonthLabels.get(c.get(Calendar.MONTH) - 1)), String.valueOf(startYear));

        mMonths.add(new Month(startMonth, startYear));
        getPreviousMonth();
        getNextMonths();
    }


    @Override
    public int getItemViewType(int position) {
        return mMonths.get(position).weeks.length;
    }

    @Override
    public MonthHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.month_view, parent, false);
        MonthHolder mh = new MonthHolder(v, viewType, attrs, new CalendarView.OnDayClickListener() {
            @Override
            public void onClick(int day, int month, int year, boolean hasEvent) {
                if (onDayClickListener != null) {
                    String key = String.format("%d%d%d", day, month, year);
                    if (!blockedDates.containsKey(key)) {
                        onDayClickListener.onClick(day, month, year, hasEvent(day, month, year));
                        selectedDate.clear();
                        selectedDate.put(key, true);
                        notifyDataSetChanged();
                    }
                }
            }
        });
        mh.generateWeekRows();
        return mh;
    }


    @Override
    public void onBindViewHolder(MonthHolder holder, int position) {

        Month m = mMonths.get(position);
        setLabel(holder, m);
        setWeeks(holder, m);
        if(position==0){
            ((RecyclerView.LayoutParams)  holder.itemView.getLayoutParams()).setMargins(0, 0, 0,0);
        }
        holder.mYear = m.year;
        holder.mMonth = m.value;


    }

    private void setLabel(MonthHolder holder, Month m) {
        String year = (m.year != startYear ? " de " + m.year : "");
       // onMonthChangedListener.onMonthChanged(mMonthLabels.get(m.value - 1), String.valueOf(m.year));
        holder.label_month.setText(mMonthLabels.get(m.value - 1) + year);
        if (m.value == startMonth && m.year == startYear) {
            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
            holder.label_month.setTextSize(TypedValue.COMPLEX_UNIT_PX, (attrs.monthLabelSize + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 6, displayMetrics)));
        } else {
            holder.label_month.setTextSize(TypedValue.COMPLEX_UNIT_PX, (attrs.monthLabelSize));
        }
    }


    private void setWeeks(MonthHolder holder, Month m) {
        MonthHolder.WeekDayView[] weekColumns;
        Day[] days;
        View container;
        TextView tv_day;
        View v_circle;
        for (int i = 0; i < holder.weekRowsCount; i++) {
            weekColumns = holder.weeksColumns.get(i);
            days = m.weeks[i].days;
            String key;
            for (int j = 0; j < 7; j++) {
                v_circle = weekColumns[j].v_event_circle;
                container = weekColumns[j].container;
                tv_day = weekColumns[j].tv_value;
                tv_day.setText("" + days[j].value);

                container.setTag(days[j].value);
                container.setClickable(days[j].value != 0);

                v_circle.setVisibility(hasEvent(days[j].value, m.value, m.year) ? View.VISIBLE : View.INVISIBLE);

                weekColumns[j].every_first.setVisibility(hasSelectedDate(days[j].value, m.value, m.year) ? View.VISIBLE : View.INVISIBLE);

                if (m.year == startYear && m.value == startMonth && days[j].value == today) {
                    tv_day.setTextColor(Color.WHITE);
                    weekColumns[j].v_today_circle.setVisibility(View.VISIBLE);
                } else {
                    if ((days[j].value == 0)) {
                        tv_day.setTextColor(Color.TRANSPARENT);
                    } else {
                        if (j == 0 || j == 6) {
                            if (attrs.weekendDifferenctColor) {
                                tv_day.setTextColor(attrs.weekEndColor);
                            } else {
                                tv_day.setTextColor(attrs.monthLabelColor);
                            }
                        } else {
                            tv_day.setTextColor(attrs.monthLabelColor);
                        }
                    }

                    weekColumns[j].v_today_circle.setVisibility(View.GONE);
                    if (attrs.markEveryFirstofMonth) {
                        if (days[j].value == 1) {
                            weekColumns[j].every_first.setVisibility(View.VISIBLE);
                        }
                    }
                }

                weekColumns[j].blocked.setVisibility(View.GONE);
                if (min.get(Calendar.YEAR) == m.year || max.get(Calendar.YEAR) == m.year) {
                    if (min.get(Calendar.MONTH) == m.value || max.get(Calendar.MONTH) == m.value) {
                        if (min.get(Calendar.DAY_OF_MONTH) < days[j].value || max.get(Calendar.DAY_OF_MONTH) < days[j].value) {
                            weekColumns[j].v_today_circle.setVisibility(View.GONE);
                            weekColumns[j].v_event_circle.setVisibility(View.GONE);
                            weekColumns[j].every_first.setVisibility(View.GONE);
                            weekColumns[j].blocked.setVisibility(View.VISIBLE);
                            String blockeskey = String.format("%d%d%d", days[j].value, m.value, m.year);
                            blockedDates.put(blockeskey, true);
                        }
                    }
                }
            }
        }

    }

    private boolean hasEvent(int day, int month, int year) {
        String key = String.format("%d%d%d", day, month, year);

        return mEvents.containsKey(key);
    }

    private boolean hasSelectedDate(int day, int month, int year) {
        String key = String.format("%d%d%d", day, month, year);
        Log.i("key", String.valueOf(selectedDate.containsKey(key)));
        return selectedDate.containsKey(key);
    }

    @Override
    public int getItemCount() {
        return mMonths.size();
    }


    public void getPreviousMonth() {
        if (earlyMonthLoaded <= PAYLOAD) {
            for (int i = earlyMonthLoaded - 1; i > 0; i--) {
                if (minDateComparsion(earlyMonthLoaded, min.get(Calendar.MONTH), earlyYearLoaded, min.get(Calendar.YEAR))) {
                    mMonths.add(0, new Month(i, earlyYearLoaded));
                }

                //notifyItemRangeInserted(0, 1);

            }

            earlyMonthLoaded = 12 - (PAYLOAD - earlyMonthLoaded);
            earlyYearLoaded--;

            for (int i = 12; i >= earlyMonthLoaded; i--) {
                if (minDateComparsion(i, min.get(Calendar.MONTH), earlyYearLoaded, min.get(Calendar.YEAR))) {
                    mMonths.add(0, new Month(i, earlyYearLoaded));
                }
                //notifyItemRangeInserted(0, 1);
            }
        } else {
            for (int i = earlyMonthLoaded - 1; i >= earlyMonthLoaded - PAYLOAD; i--) {
                if (minDateComparsion(i, min.get(Calendar.MONTH), earlyYearLoaded, min.get(Calendar.YEAR))) {
                    mMonths.add(0, new Month(i, earlyYearLoaded));
                }
                //notifyItemRangeInserted(0, 1);

            }
            earlyMonthLoaded -= PAYLOAD;
        }

        notifyItemRangeInserted(0, PAYLOAD);

    }

    public void getNextMonths() {
        int positionStart = mMonths.size() - 1;
        if (laterMonthLoaded > (12 - PAYLOAD)) {
            for (int i = laterMonthLoaded + 1; i <= 12; i++) {
                if (maxDateComparsion(laterMonthLoaded, max.get(Calendar.MONTH), laterYearLoaded, max.get(Calendar.YEAR))) {
                mMonths.add(new Month(i, laterYearLoaded));
                }
            }
            laterMonthLoaded = laterMonthLoaded + PAYLOAD - 12;
            laterYearLoaded++;
            for (int i = 1; i <= laterMonthLoaded; i++) {
                if (maxDateComparsion(i, max.get(Calendar.MONTH), laterYearLoaded, max.get(Calendar.YEAR))) {
                    mMonths.add(new Month(i, laterYearLoaded));
                }
            }
        } else {
            for (int i = laterMonthLoaded + 1; i <= laterMonthLoaded + PAYLOAD; i++) {
                if (maxDateComparsion(i, max.get(Calendar.MONTH), laterYearLoaded, max.get(Calendar.YEAR))) {
                    mMonths.add(new Month(i, laterYearLoaded));
                }
            }
            laterMonthLoaded += PAYLOAD;
        }
        notifyItemRangeInserted(positionStart, PAYLOAD);
    }

    public void setOnDayClickListener(CalendarView.OnDayClickListener onDayClickListener) {
        this.onDayClickListener = onDayClickListener;
    }

    public void addEvent(int day, int month, int year) {
        //Month m = getMonth(month, year);
        String key = String.format("%d%d%d", day, month, year);
        mEvents.put(key, true);
        notifyDataSetChanged();
    }

    public void deleteEvent(int day, int month, int year) {
        String key = String.format("%d%d%d", day, month, year);
        mEvents.remove(key);
        notifyDataSetChanged();
    }

    public boolean maxDateComparsion(int monthLoaded, int maxMonth, int laterYearLoaded, int maxYear) {
        try {

            if (laterYearLoaded < maxYear) {
                return true;
            } else {
                if (monthLoaded <= maxMonth) {
                    return true;
                } else {
                    maxReachedEnd = true;
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }


    public boolean minDateComparsion(int monthLoaded, int minMonth, int laterYearLoaded, int minYear) {
        try {
            if (laterYearLoaded > minYear) {
                return true;
            } else {
                if (monthLoaded >= minMonth) {
                    return true;
                } else {
                    minReachedEnd = true;
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }



}
