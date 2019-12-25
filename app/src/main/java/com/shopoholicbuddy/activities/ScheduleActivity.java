package com.shopoholicbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.adapters.BuddySlotsAdapter;
import com.shopoholicbuddy.calendar.CalendarBean;
import com.shopoholicbuddy.calendar.CalendarDateView;
import com.shopoholicbuddy.calendar.CalendarView;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.buddyscheduleresponse.BuddyScheduleResponse;
import com.shopoholicbuddy.models.buddyscheduleresponse.Result;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.shopoholicbuddy.utils.AppSharedPreference;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ScheduleActivity extends AppCompatActivity implements NetworkListener {

    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.menu_right)
    ImageView menuRight;
    @BindView(R.id.menu_second_right)
    ImageView menuSecondRight;
    @BindView(R.id.menu_third_right)
    ImageView menuThirdRight;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.tv_previous_year)
    TextView tvPreviousYear;
    @BindView(R.id.tv_current_year)
    TextView tvCurrentYear;
    @BindView(R.id.tv_next_year)
    TextView tvNextYear;
    @BindView(R.id.ll_year_month)
    LinearLayout llYearMonth;
    @BindView(R.id.ll_week_days)
    LinearLayout llWeekDays;
    @BindView(R.id.calendarDateView)
    CalendarDateView calendarDateView;
    @BindView(R.id.tv_shopper_selected_date)
    CustomTextView tvShopperSelectedDate;
    @BindView(R.id.tv_add_slots)
    CustomTextView tvAddSlots;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
    private String selectedMonth;
    private CalendarBean selectedDay;
    private long lastClickTime;
    private Date currentDate;
    private Calendar mCalendar;
    private int month, year, day;
    private BuddySlotsAdapter buddySlotsAdapter;
    private ArrayList<Result> buddyScheduleList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ButterKnife.bind(this);
        initVariable();
        setAdapter();
        setAdapters();

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        CalendarBean calendarBean = new CalendarBean(year, month + 1, day);
        setCalenderData(calendarBean);
        setAdapter();
        if (AppUtils.getInstance().isInternetAvailable(this)) {
            progressBar.setVisibility(View.VISIBLE);
            filterCalenderList(calendarBean);
        }
    }

    private void initVariable() {

        lastClickTime = 0;
        buddyScheduleList = new ArrayList<>();
        mCalendar = Calendar.getInstance();
        currentDate = mCalendar.getTime();
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        progressBar.setVisibility(View.GONE);
        tvTitle.setText(getString(R.string.schedule));
        ivMenu.setVisibility(View.VISIBLE);
        ivMenu.setImageResource(R.drawable.ic_back);
        noDataAvailable();
        buddySlotsAdapter = new BuddySlotsAdapter(ScheduleActivity.this, buddyScheduleList, new RecyclerCallBack() {
            @Override
            public void onClick(int position, View view) {
                switch (view.getId()) {
                    case R.id.text_view:
                        break;
                    case R.id.iv_options:
                        break;
                    case R.id.tv_edit:
                        startActivityForResult(new Intent(ScheduleActivity.this, AddProductServiceActivity.class)
                                        .putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.PROFILE_SKILLS)
                                        .putExtra(Constants.IntentConstant.DEAL_ID, buddyScheduleList.get(position).getDealId())
                                        .putExtra(Constants.IntentConstant.IS_PRODUCT, false),
                                Constants.IntentConstant.REQUEST_EDIT_DEAL);
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IntentConstant.REQUEST_EDIT_DEAL && resultCode == RESULT_OK) {
            if (AppUtils.getInstance().isInternetAvailable(this)) {
                hitBuddyScheduleApi();
            }
        }
    }

    /**
     * This method sets the recylcer view adapter
     */
    private void setAdapters() {
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        recycleView.setAdapter(buddySlotsAdapter);
    }


    /*This method is used to set the recycler view adapter*/
    private synchronized void setAdapter() {
        //linearLayoutManager.scrollToPositionWithOffset(2,30);
        calendarDateView.setAdapter((convertView, parentView, bean, pos) -> {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.adapter_calendar, null);
                viewHolder = new ViewHolder();
                viewHolder.dayView = (TextView) convertView.findViewById(R.id.tv_day);
                viewHolder.rlDayView = (RelativeLayout) convertView.findViewById(R.id.rl_day_view);
                viewHolder.ivEvent1 = (ImageView) convertView.findViewById(R.id.iv_event_1);
                viewHolder.rlEventMore = (RelativeLayout) convertView.findViewById(R.id.rl_event_4);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.dayView.setText("" + bean.day);
            String eventDate = AppUtils.changeDateFormat(bean.toString(), new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH),
                    new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH));

            if (bean.mothFlag != 0) {
                viewHolder.rlDayView.setBackgroundResource(0);
                viewHolder.ivEvent1.setVisibility(View.GONE);
                viewHolder.dayView.setTextColor(ContextCompat.getColor(ScheduleActivity.this, android.R.color.darker_gray));
            } else if (new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(currentDate).equalsIgnoreCase(eventDate)) {
                viewHolder.rlDayView.setBackground(ContextCompat.getDrawable(ScheduleActivity.this, R.drawable.white_drawable_stroke));
                viewHolder.dayView.setTextColor(ContextCompat.getColor(ScheduleActivity.this, R.color.colorLightPurple));
            } else if (selectedDay != null && selectedDay == bean) {
                viewHolder.rlDayView.setBackgroundResource(R.drawable.round_date_drawable);
                viewHolder.dayView.setTextColor(ContextCompat.getColor(ScheduleActivity.this, R.color.colorLightWhite));
            } else {
                viewHolder.rlDayView.setBackgroundResource(0);
                viewHolder.dayView.setTextColor(ContextCompat.getColor(ScheduleActivity.this, R.color.colorLightPurple));
            }

            return convertView;
        });

        calendarDateView.setOnItemClickListener(new CalendarView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion, final CalendarBean bean) {
                filterCalenderList(bean);
            }

            @Override
            public boolean onItemLongClick(View view, int position, CalendarBean bean) {
                return false;
            }
        });

        calendarDateView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }


            @Override
            public void onPageSelected(int position) {
                if (calendarDateView != null) {
                    CalendarBean bean = calendarDateView.getData(position);
                    filterCalenderList(bean);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    private void filterCalenderList(CalendarBean bean) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 500) {
            return;
        }

        lastClickTime = SystemClock.elapsedRealtime();

        if (bean.mothFlag == 0) {
            selectedMonth = String.valueOf(bean.month - 1);
            selectedDay = bean;
            day = bean.day;
            Log.e("Date", "" + bean.year + "-" + bean.month + "-" + bean.day);
            setCalenderData(bean);
            String date = bean.year + "-" + bean.month + "-" + bean.day;
            if (AppUtils.getInstance().isInternetAvailable(ScheduleActivity.this)) {
                layoutNoDataFound.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                hitBuddyScheduleApi();
            }
            calendarDateView.notifyData();

        }
    }

    /**
     * Method to set the year and month name in calendar
     *
     * @param bean
     */
    private void setCalenderData(CalendarBean bean) {

        switch (bean.month) {
            case 1:
                selectedMonth = getString(R.string.month_january);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_january) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_december) + " " + (bean.year - 1)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_feburary) + " " + bean.year));
                break;

            case 2:
                selectedMonth = getString(R.string.month_feburary);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_feburary) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_january) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_march) + " " + bean.year));
                break;

            case 3:
                selectedMonth = getString(R.string.month_march);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_march) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_feburary) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_april) + " " + bean.year));
                break;

            case 4:
                selectedMonth = getString(R.string.month_april);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_april) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_march) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_may) + " " + bean.year));
                break;

            case 5:
                selectedMonth = getString(R.string.month_may);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_may) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_april) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_june) + " " + bean.year));
                break;

            case 6:
                selectedMonth = getString(R.string.month_june);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_june) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_may) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_july) + " " + bean.year));
                break;

            case 7:
                selectedMonth = getString(R.string.month_july);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_july) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_june) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_august) + " " + bean.year));
                break;

            case 8:
                selectedMonth = getString(R.string.month_august);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_august) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_july) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_september) + " " + bean.year));
                break;

            case 9:
                selectedMonth = getString(R.string.month_september);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_september) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_august) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_october) + " " + bean.year));
                break;

            case 10:
                selectedMonth = getString(R.string.month_october);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_october) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_september) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_november) + " " + bean.year));
                break;

            case 11:
                selectedMonth = getString(R.string.month_november);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_november) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_october) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_december) + " " + bean.year));
                break;

            case 12:
                selectedMonth = getString(R.string.month_december);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_december) + " " + bean.year));
//                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_november) + " " + (bean.year)));
//                tvNextYear.setText(TextUtils.concat(getString(R.string.month_january) + (bean.year + 1)));
                break;

        }
        tvShopperSelectedDate.setText(TextUtils.concat(selectedMonth + " " + bean.day + "," + " " + bean.year));
    }


    /**
     * View holder for calendar date view
     */
    public class ViewHolder {
        TextView dayView;
        ImageView ivEvent1;
        RelativeLayout rlDayView, rlEventMore;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick({R.id.iv_menu, R.id.tv_previous_year, R.id.tv_next_year})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                onBackPressed();
                break;
            case R.id.tv_previous_year:
                try {
                    calendarDateView.setCurrentItem(calendarDateView.getCurrentItem() - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_next_year:
                try {
                    calendarDateView.setCurrentItem(calendarDateView.getCurrentItem() + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * This method is used to hit the api to get the slots for the buddy on the selected date
     */
    private void hitBuddyScheduleApi() {
        ApiInterface apiInterface = RestApi.createServiceAccessToken(this, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();

        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_SCHEDULE_DATE, String.valueOf(selectedDay));
        Call<ResponseBody> call = apiInterface.hitBuddyScheduleApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(ScheduleActivity.this, call, this, Constants.NetworkConstant.REQUEST_BUDDY_SCHEDULE);
    }

    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        progressBar.setVisibility(View.GONE);
        switch (requestCode) {
            case Constants.NetworkConstant.REQUEST_BUDDY_SCHEDULE:
                switch (responseCode) {
                    case Constants.NetworkConstant.SUCCESS_CODE:
                        BuddyScheduleResponse buddyScheduleResponse = new Gson().fromJson(response, BuddyScheduleResponse.class);
                        buddyScheduleList.clear();
                        buddyScheduleList.addAll(buddyScheduleResponse.getResult());
                        buddySlotsAdapter.notifyDataSetChanged();
                        noDataAvailable();
                        break;

                    case Constants.NetworkConstant.NO_DATA:
                        buddyScheduleList.clear();
                        buddySlotsAdapter.notifyDataSetChanged();
                        noDataAvailable();
                        break;
                }
                break;
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        progressBar.setVisibility(View.GONE);
        AppUtils.getInstance().showToast(this, response);
        noDataAvailable();

    }

    @Override
    public void onFailure() {
        progressBar.setVisibility(View.GONE);
        noDataAvailable();
    }

    /**
     * This method checks whether data is present in list or not and update UI accordingly
     */
    public void noDataAvailable() {
        if (buddyScheduleList.size() == 0) {
            layoutNoDataFound.setVisibility(View.VISIBLE);
        } else {
            layoutNoDataFound.setVisibility(View.GONE);
        }
    }

}
