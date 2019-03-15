package br.com.jpttrindade.calendarview.holders;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.jpttrindade.calendarview.R;
import br.com.jpttrindade.calendarview.view.CalendarView;

/**
 * Created by joaotrindade on 06/09/16.
 */
public class MonthHolder extends RecyclerView.ViewHolder {

    private CalendarView.OnDayClickListener mOnDayClickListener;
    public Context mContext;
    public TextView label_month;
    public LinearLayout weeks_container;
    public ArrayList<WeekDayView[]> weeksColumns;
    public int weekRowsCount;
    public int mMonth;
    public int mYear;

    public CalendarView.Attributes attrs;


    public MonthHolder(View itemView, int weekRowsCount, CalendarView.Attributes attrs, CalendarView.OnDayClickListener onDayClickListener) {
        super(itemView);
        ((RecyclerView.LayoutParams) itemView.getLayoutParams()).setMargins(0, attrs.monthDividerSize, 0, 0);
        this.weekRowsCount = weekRowsCount;
        this.attrs = attrs;

        mContext = itemView.getContext();
        label_month = (TextView) itemView.findViewById(R.id.label_month);
        label_month.getLayoutParams().height = attrs.monthLabelHeight;

        weeks_container = (LinearLayout) itemView.findViewById(R.id.weeks_container);
        weeksColumns = new ArrayList<WeekDayView[]>();
        //generateWeekRows();

        mOnDayClickListener = onDayClickListener;
    }

    public void generateWeekRows() {
        LinearLayout linearLayout;
        //weeks_container.setWeightSum(weekRowsCount);


        LinearLayout.LayoutParams layoutParams;
        for (int i = 0; i < weekRowsCount; i++) {
            linearLayout = new LinearLayout(mContext);
            layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    attrs.dayHeight);
            layoutParams.setMargins(0, attrs.dayspace, 0, attrs.dayspace);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.CENTER);

            //linearLayout.setBackgroundColor(Color.GREEN);
            generateWeekColumns(linearLayout);

            LinearLayout line = new LinearLayout(mContext);
            LinearLayout.LayoutParams lineparam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2);
            line.setLayoutParams(lineparam);
            line.setOrientation(LinearLayout.HORIZONTAL);
            line.setBackgroundColor(ContextCompat.getColor(mContext, R.color.font1));


            weeks_container.addView(linearLayout);
            weeks_container.addView(line);

        }
    }

    void generateWeekColumns(LinearLayout linearLayout) {
        WeekDayView[] columns = new WeekDayView[7];


        LayoutInflater inflater = LayoutInflater.from(mContext);

        TextView tv_dayValue;
        View container;
        for (int i = 0; i < 7; i++) {
            container = inflater.inflate(R.layout.day_view, linearLayout, false);
            container.setTag(i);
            container.getLayoutParams().width = attrs.dayWidth;
            container.getLayoutParams().height = attrs.dayHeight;


            //tv = new TextView(mContext);
            View event_circle = container.findViewById(R.id.circle);
            View today_circle = container.findViewById(R.id.today_circle);
            View every_first = container.findViewById(R.id.every_first);
            View blocked = container.findViewById(R.id.blocked);


            ((GradientDrawable) event_circle.getBackground()).setColor(attrs.eventCircleColor);
            ((GradientDrawable) today_circle.getBackground()).setColor(attrs.todayCircleColor);
            blocked.setBackgroundColor(attrs.dayblockedcolor);


            today_circle.getLayoutParams().width = attrs.todayCircleSize;
            today_circle.getLayoutParams().height = attrs.todayCircleSize;

            every_first.getLayoutParams().width = attrs.todayCircleSize;
            every_first.getLayoutParams().height = attrs.todayCircleSize;

            tv_dayValue = (TextView) container.findViewById(R.id.tv_day);
            tv_dayValue.getLayoutParams().width = attrs.todayCircleSize;
            tv_dayValue.getLayoutParams().height = attrs.todayCircleSize;






          /*
*/

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int day = (Integer) view.getTag();//Integer.parseInt(((TextView)view).getText().toString());
                    if (day > 0) {
                        mOnDayClickListener.onClick(day, mMonth, mYear, false);
                    }
                }
            });

            linearLayout.addView(container);

            columns[i] = new WeekDayView(container, tv_dayValue, event_circle, today_circle, every_first, blocked);
        }
        weeksColumns.add(columns);

    }


    public void setLabelMonthHeight(int labelMonthHeight) {
        this.label_month.getLayoutParams().height = labelMonthHeight;
    }

    public class WeekDayView {
        public View container;
        public TextView tv_value;
        public View v_today_circle;
        public View v_event_circle;
        public View every_first;
        public View blocked;

        public WeekDayView(View container, TextView value, View circle, View v_today_circle, View every_first, View blocked) {
            this.container = container;
            this.tv_value = value;
            this.blocked = blocked;
            this.v_event_circle = circle;
            this.every_first = every_first;
            this.v_today_circle = v_today_circle;
            this.v_event_circle.setVisibility(View.INVISIBLE);
            this.v_today_circle.setVisibility(View.INVISIBLE);
            this.every_first.setVisibility(View.INVISIBLE);
            this.blocked.setVisibility(View.INVISIBLE);
        }
    }
}
