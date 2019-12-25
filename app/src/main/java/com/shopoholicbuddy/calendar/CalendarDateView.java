package com.shopoholicbuddy.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


import com.shopoholicbuddy.R;
import com.shopoholicbuddy.utils.AppSharedPreference;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * Created by Akash Jain on 19/07/2017.
 */

public class CalendarDateView extends ViewPager implements CalendarTopView {

    HashMap<Integer, CalendarView> views = new HashMap<>();
    private CalendarTopViewChangeListener mCaledarLayoutChangeListener;
    private CalendarView.OnItemClickListener onItemClickListener;
    private LinkedList<CalendarView> cache = new LinkedList<>();
//    private int MAXCOUNT=5;
    private int row = 6;
    private CalendarAdapter mAdapter;
    private PagerAdapter pagerAdapter;
    private int calendarItemHeight = 0;
    private int mMinHeight;


    public int getRow(){
        return row;
    }
    public void setAdapter(CalendarAdapter adapter) {
        mAdapter = adapter;
        initData();
    }

    public void notifyData(){
        pagerAdapter.notifyDataSetChanged();
    }

    public void setOnItemClickListener(CalendarView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public CalendarDateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CalendarDateView);
        row = a.getInteger(R.styleable.CalendarDateView_cbd_calendar_row, row);
        mMinHeight=2;
        a.recycle();
        init();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                CalendarView calendarView = views.get(getCurrentItem());
                if (calendarView!=null)
                    calendarView.updateUI();
            }
        },50);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int calendarHeight = 0;
        if (getAdapter() != null) {
            CalendarView view = (CalendarView) getChildAt(0);
            if (view != null) {
                calendarHeight = view.getMeasuredHeight();
                calendarItemHeight = view.getItemHeight();
            }
        }
        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(calendarHeight, MeasureSpec.EXACTLY));
    }

    private void updateNumberOfRows(Calendar calendar){
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.setFirstDayOfWeek(firstDayOfWeek);
        int numberOfDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        if(firstDayOfWeek == Calendar.THURSDAY){
           row = 5;
        }
       else if(firstDayOfWeek==Calendar.FRIDAY){
            if(numberOfDaysInMonth<=30){
               row = 5;
            }else{
              row = 6;
            }
        }
       else if(((firstDayOfWeek==Calendar.SATURDAY))&& numberOfDaysInMonth>=30){
            row =6;
        }
        else{
            row = 5;
        }

    }


    private void init() {
       final int[] dateArr= CalendarUtil.getYMD(new Date());
        Calendar calendar=Calendar.getInstance();
        calendar.clear();
        calendar.set(dateArr[0],dateArr[1]-1,1);
        updateNumberOfRows(calendar);
        pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {

                CalendarView view;

                if (!cache.isEmpty()) {
                    view = cache.removeFirst();
                    view.setNumberOfRows(row);
                } else {
                    view = new CalendarView(container.getContext(), row);
                }
                view.setParent(CalendarDateView.this);
                view.setOnItemClickListener(onItemClickListener);
                view.setAdapter(mAdapter);

                view.setData(CalendarFactory.getMonthOfDayList(dateArr[0],dateArr[1]+position-Integer.MAX_VALUE/2),position==Integer.MAX_VALUE/2);
                container.addView(view);
                views.put(position, view);

                return view;
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
                cache.addLast((CalendarView) object);
                views.remove(position);
            }
        };

        setAdapter(pagerAdapter);

        addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //if (!AppSharedPreference.getBoolean(getContext(), AppSharedPreference.PREF_KEY.CALENDAR_PREVIEW)) {
                    super.onPageSelected(position);
                    if (views.containsKey(position)){
                        CalendarBean data = getData(position);
                        Calendar calendar=Calendar.getInstance();
                        if(data.year==calendar.get(Calendar.YEAR)){
                            calendar.set(data.year,data.month-1,1);
                        }else{
                            calendar.set(data.year,data.month-1,data.day);
                        }
                        updateNumberOfRows(calendar);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                notifyData();
                            }
                        },300);
                                        }
                    mCaledarLayoutChangeListener.onLayoutChange(CalendarDateView.this);
            //    }
            }
        });
    }

    public CalendarBean getData(int position){
        CalendarView view = views.get(position);
        Object[] obs = view.getSelect();
        return (CalendarBean) obs[2];
    }

    private void initData() {
        setCurrentItem(Integer.MAX_VALUE/2, false);
        getAdapter().notifyDataSetChanged();
    }

    @Override
    public int[] getCurrentSelectPositon() {
      //  if (!AppSharedPreference.getBoolean(getContext(), AppSharedPreference.PREF_KEY.CALENDAR_PREVIEW)) {
            CalendarView view = views.get(getCurrentItem());
            if (view == null) {
                view = (CalendarView) getChildAt(0);
            }
            if (view != null) {
                return view.getSelectPostion();
            }
       // }
        return new int[4];
    }

    @Override
    public int getItemHeight() {
        return calendarItemHeight*mMinHeight;
    }

    @Override
    public void setCaledarTopViewChangeListener(CalendarTopViewChangeListener listener) {
        mCaledarLayoutChangeListener = listener;
    }
    public void setMinHeight(int minHeight){
        mMinHeight=minHeight;
    }
}
