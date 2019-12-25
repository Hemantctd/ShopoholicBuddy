package com.shopoholicbuddy.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.activities.HomeActivity;
import com.shopoholicbuddy.activities.OrderDetailsActivity;
import com.shopoholicbuddy.activities.SpecializedSkillActivity;
import com.shopoholicbuddy.adapters.OrdersAdapter;
import com.shopoholicbuddy.calendar.CalendarAdapter;
import com.shopoholicbuddy.calendar.CalendarBean;
import com.shopoholicbuddy.calendar.CalendarDateView;
import com.shopoholicbuddy.calendar.CalendarView;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.firebasechat.utils.FirebaseConstants;
import com.shopoholicbuddy.interfaces.RecyclerCallBack;
import com.shopoholicbuddy.models.NotificationBean;
import com.shopoholicbuddy.models.orderlistdetailsresponse.OrderListDetailsRespose;
import com.shopoholicbuddy.models.orderlistdetailsresponse.Result;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lal.adhish.gifprogressbar.GifView;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.shopoholicbuddy.utils.Constants.AppConstant.DATE_FORMAT;
import static com.shopoholicbuddy.utils.Constants.AppConstant.SERVICE_DATE_FORMAT;

/**
 * Created by appinventiv-pc on 23/3/18.
 */

public class MyOrdersFragment extends Fragment implements NetworkListener {

    @BindView(R.id.tv_shopper_selected_date)
    CustomTextView tvShopperSelectedDate;
    @BindView(R.id.recycle_view)
    RecyclerView rvOrders;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    @BindView(R.id.gif_progress)
    GifView gifProgress;
    @BindView(R.id.progressBar)
    FrameLayout progressBar;
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
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private AppCompatActivity mActivity;
    Unbinder unbinder;
    private View rootView;
    private OrdersAdapter ordersAdapter;
    private List<Result> ordersList;
    private List<Result> selectedOrdersList;
    private boolean isMoreData;
    private boolean isLoading;
    private LinearLayoutManager linearLayoutManager;
    private int count = 0;
    private String orderDate;

    private long lastClickTime;
    private String selectedMonth;
    private Date currentDate;
    private Calendar mCalendar;
    private CalendarBean selectedDay;
    private int month, year, day;
    private ArrayList<String> orderDayList;
    private int chooseMonth;


    private BroadcastReceiver orderUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getExtras() != null) {
                NotificationBean notificationBean = (NotificationBean) intent.getExtras().getSerializable(Constants.IntentConstant.NOTIFICATION);
                if (notificationBean != null) {
                    for (int i = 0; i < ordersList.size(); i++) {
                        if (ordersList.get(i).getId().equals(notificationBean.getOrderId())) {
                            ordersList.get(i).setOrderStatus(notificationBean.getOrderStatus());
                            break;
                        }
                    }
                    for (int i = 0; i < selectedOrdersList.size(); i++) {
                        if (selectedOrdersList.get(i).getId().equals(notificationBean.getOrderId())) {
                            selectedOrdersList.get(i).setOrderStatus(notificationBean.getOrderStatus());
                            ordersAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }
            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(orderUpdateReceiver, new IntentFilter(Constants.IntentConstant.NOTIFICATION));
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(orderUpdateReceiver);
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_order, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        mActivity = (AppCompatActivity) getActivity();
        initVariable();
        setListeners();
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        CalendarBean calendarBean = new CalendarBean(year, month + 1, day);
        setCalenderData(calendarBean);
        setAdapter();
        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
            progressBar.setVisibility(View.VISIBLE);
            hitOrdersListApi(year, month + 1);
            orderDayList.clear();
            calendarDateView.notifyData();
        }
        getNotificationData();
        return rootView;
    }


    /**
     * Method to get data on notification click
     */
    private void getNotificationData() {
        if (mActivity instanceof HomeActivity) {
            Intent receivedIntent = ((HomeActivity) mActivity).getIntent();
            if (receivedIntent != null && receivedIntent.getExtras() != null && receivedIntent.hasExtra(FirebaseConstants.NOTIFICATION)) {
                final NotificationBean notificationBean = (NotificationBean) receivedIntent.getExtras().getSerializable(FirebaseConstants.NOTIFICATION);
                if (notificationBean != null) {
                    MyOrdersFragment.this.startActivityForResult(new Intent(mActivity, OrderDetailsActivity.class)
                                    .putExtra(Constants.IntentConstant.ORDER_ID, notificationBean.getOrderId())
                                    .putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.MY_ORDERS)
                            , Constants.IntentConstant.REQUEST_PUSH_ORDER_CODE);
                }
                ((HomeActivity) mActivity).setIntent(null);
            }
        }
    }

    /**
     * method to set listener in views
     */
    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                hitOrdersListApi(year, month + 1);
                orderDayList.clear();
                calendarDateView.notifyData();
            }
        });

    }

    /*This method is used to set the recycler view adapter*/
    private synchronized void setAdapter() {
        linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        rvOrders.setLayoutManager(linearLayoutManager);
        rvOrders.setAdapter(ordersAdapter);

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
            viewHolder.dayView.setText(TextUtils.concat("" + bean.day));
            viewHolder.ivEvent1.setVisibility(View.GONE);
            String eventDate = AppUtils.getInstance().formatDate(bean.toString(), SERVICE_DATE_FORMAT, DATE_FORMAT);

            setEventUI(viewHolder, bean, eventDate);

            if (bean.mothFlag != 0) {
                viewHolder.ivEvent1.setVisibility(View.GONE);
                viewHolder.rlDayView.setBackgroundResource(0);
                viewHolder.dayView.setTextColor(ContextCompat.getColor(mActivity, android.R.color.darker_gray));
            } else if (new SimpleDateFormat(DATE_FORMAT).format(currentDate).equalsIgnoreCase(eventDate)) {
                viewHolder.rlDayView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.white_drawable_stroke));
                viewHolder.dayView.setTextColor(ContextCompat.getColor(mActivity, R.color.colorLightPurple));
            } else if (selectedDay != null && selectedDay == bean) {
                viewHolder.rlDayView.setBackgroundResource(R.drawable.round_date_drawable);
                viewHolder.dayView.setTextColor(ContextCompat.getColor(mActivity, R.color.colorLightWhite));
            } else {
                viewHolder.rlDayView.setBackgroundResource(0);
                //viewHolder.ivEvent1.setVisibility(View.GONE);
                viewHolder.dayView.setTextColor(ContextCompat.getColor(mActivity, R.color.colorLightPurple));
            }

            return convertView;
        });

        calendarDateView.setOnItemClickListener(new CalendarView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, final CalendarBean bean) {
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
                try {
                    if (calendarDateView != null) {
                        CalendarBean bean = calendarDateView.getData(position);
                        setCalenderData(bean);
                        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                            ordersList.clear();
                            if (swipeRefreshLayout.isRefreshing())
                                swipeRefreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.VISIBLE);
                            hitOrdersListApi(bean.year, bean.month);
                            orderDayList.clear();
                            calendarDateView.notifyData();

                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * method to filter calendar list
     * @param bean
     */
    private void filterCalenderList(CalendarBean bean) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 500) {
            return;
        }

        lastClickTime = SystemClock.elapsedRealtime();

        if (bean.mothFlag == 0) {
            year = bean.year;
            month = bean.month - 1;
            day = bean.day;
            orderDate = AppUtils.getInstance().convertDate(bean, SERVICE_DATE_FORMAT);
            selectedMonth = String.valueOf(bean.month - 1);
            selectedDay = bean;
            Log.e("Date", "" + bean.year + "/" + bean.month + "/" + bean.day);
            setCalenderData(bean);
            String date = bean.year + "/" + bean.month + "/" + bean.day;


        }

        selectedOrdersList.clear();
        for (int i = 0; i < ordersList.size(); i++) {
            if (ordersList.get(i).getSlotArr().size() == 0) {
                if (AppUtils.getInstance().formatDate(ordersList.get(i).getDileveryDate(), SERVICE_DATE_FORMAT, DATE_FORMAT).equalsIgnoreCase
                        (AppUtils.getInstance().formatDate(String.valueOf(selectedDay), SERVICE_DATE_FORMAT, DATE_FORMAT))) {
                    selectedOrdersList.add(ordersList.get(i));
                }
            } else {
                progressBar.setVisibility(View.VISIBLE);
//                ArrayList<String> slotsDate = AppUtils.getInstance().getSlotsDates(ordersList.get(i).getSlotArr());
                for (String date : ordersList.get(i).getSelectedDateArr()) {
                    if (AppUtils.getInstance().formatDate(date, SERVICE_DATE_FORMAT, DATE_FORMAT).equalsIgnoreCase
                            (AppUtils.getInstance().formatDate(String.valueOf(selectedDay), SERVICE_DATE_FORMAT, DATE_FORMAT))
                            && !selectedOrdersList.contains(ordersList.get(i)))
                        selectedOrdersList.add(ordersList.get(i));
                }
            }
        }
        Collections.reverse(selectedOrdersList);
        progressBar.setVisibility(View.GONE);
        noDataAvailable();
        ordersAdapter.notifyDataSetChanged();
        calendarDateView.notifyData();

    }

    /**
     * method to set system ui
     * @param viewHolder
     * @param bean
     * @param eventDate
     */
    private void setEventUI(ViewHolder viewHolder, CalendarBean bean, String eventDate) {

        if (orderDayList != null && bean.mothFlag == 0) {
            // int frequency = Collections.frequency(orderDayList, eventDate);

            for (int i = 0; i < orderDayList.size(); i++) {
                if (orderDayList.get(i).equalsIgnoreCase(eventDate)) {
                    viewHolder.ivEvent1.setVisibility(View.VISIBLE);
                } else {
//                    viewHolder.ivEvent1.setVisibility(View.GONE);
                }
            }
        }


    }

    /**
     * View holder for calendar date view
     */
    public class ViewHolder {
        TextView dayView;
        ImageView ivEvent1;
        RelativeLayout rlDayView, rlEventMore;
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
                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_december) + " " + (bean.year - 1)));
                tvNextYear.setText(TextUtils.concat(getString(R.string.month_feburary) + " " + bean.year));
                break;

            case 2:
                selectedMonth = getString(R.string.month_feburary);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_feburary) + " " + bean.year));
                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_january) + " " + (bean.year)));
                tvNextYear.setText(TextUtils.concat(getString(R.string.month_march) + " " + bean.year));
                break;

            case 3:
                selectedMonth = getString(R.string.month_march);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_march) + " " + bean.year));
                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_feburary) + " " + (bean.year)));
                tvNextYear.setText(TextUtils.concat(getString(R.string.month_april) + " " + bean.year));
                break;

            case 4:
                selectedMonth = getString(R.string.month_april);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_april) + " " + bean.year));
                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_march) + " " + (bean.year)));
                tvNextYear.setText(TextUtils.concat(getString(R.string.month_may) + " " + bean.year));
                break;

            case 5:
                selectedMonth = getString(R.string.month_may);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_may) + " " + bean.year));
                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_april) + " " + (bean.year)));
                tvNextYear.setText(TextUtils.concat(getString(R.string.month_june) + " " + bean.year));
                break;

            case 6:
                selectedMonth = getString(R.string.month_june);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_june) + " " + bean.year));
                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_may) + " " + (bean.year)));
                tvNextYear.setText(TextUtils.concat(getString(R.string.month_july) + " " + bean.year));
                break;

            case 7:
                selectedMonth = getString(R.string.month_july);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_july) + " " + bean.year));
                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_june) + " " + (bean.year)));
                tvNextYear.setText(TextUtils.concat(getString(R.string.month_august) + " " + bean.year));
                break;

            case 8:
                selectedMonth = getString(R.string.month_august);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_august) + " " + bean.year));
                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_july) + " " + (bean.year)));
                tvNextYear.setText(TextUtils.concat(getString(R.string.month_september) + " " + bean.year));
                break;

            case 9:
                selectedMonth = getString(R.string.month_september);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_september) + " " + bean.year));
                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_august) + " " + (bean.year)));
                tvNextYear.setText(TextUtils.concat(getString(R.string.month_october) + " " + bean.year));
                break;

            case 10:
                selectedMonth = getString(R.string.month_october);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_october) + " " + bean.year));
                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_september) + " " + (bean.year)));
                tvNextYear.setText(TextUtils.concat(getString(R.string.month_november) + " " + bean.year));
                break;

            case 11:
                selectedMonth = getString(R.string.month_november);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_november) + " " + bean.year));
                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_october) + " " + (bean.year)));
                tvNextYear.setText(TextUtils.concat(getString(R.string.month_december) + " " + bean.year));
                break;

            case 12:
                selectedMonth = getString(R.string.month_december);
                tvCurrentYear.setText(TextUtils.concat(getString(R.string.month_december) + " " + bean.year));
                tvPreviousYear.setText(TextUtils.concat(getString(R.string.month_november) + " " + (bean.year)));
                tvNextYear.setText(TextUtils.concat(getString(R.string.month_january) + (bean.year + 1)));
                break;

        }
        tvShopperSelectedDate.setText(TextUtils.concat(selectedMonth + " " + bean.day + "," + " " + bean.year));
    }


    /**
     * method to initialize the variables
     */
    private void initVariable() {
        orderDayList = new ArrayList<>();
        ordersList = new ArrayList<>();
        selectedOrdersList = new ArrayList<>();
        lastClickTime = 0;
        mCalendar = Calendar.getInstance();
        currentDate = mCalendar.getTime();
        orderDate = AppUtils.getInstance().getDate(Calendar.getInstance().getTimeInMillis(), SERVICE_DATE_FORMAT);
        gifProgress.setImageResource(R.drawable.shopholic_loader);
        ordersAdapter = new OrdersAdapter(mActivity, selectedOrdersList, (position, view) -> {
            switch (view.getId()) {
                case R.id.rl_root_view:
                    Intent intent = new Intent(mActivity, OrderDetailsActivity.class);
                    intent.putExtra(Constants.IntentConstant.ORDER_DETAILS, selectedOrdersList.get(position));
                    intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.MY_ORDERS);
                    MyOrdersFragment.this.startActivityForResult(intent, Constants.IntentConstant.REQUEST_ORDER_CODE);
                    break;
            }
        });
    }

    /**
     * method to hit order list API
     * @param year
     * @param month
     */
    private void hitOrdersListApi(int year, int month) {
        chooseMonth = month;
        isLoading = true;
        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.NetworkConstant.USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_ORDER_MONTH, String.valueOf(month));
        params.put(Constants.NetworkConstant.PARAM_ORDER_YEAR, String.valueOf(year));
        params.put(Constants.NetworkConstant.PARAM_COUNT, String.valueOf(count));
        Call<ResponseBody> call = apiInterface.hitOrdersListApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_BUDDY);
    }


    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_BUDDY:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            OrderListDetailsRespose ordersResponse = new Gson().fromJson(response, OrderListDetailsRespose.class);
                            ordersList.clear();
                            ordersList.addAll(ordersResponse.getResult());
                            orderDayList.clear();
                            for (int i = 0; i < ordersList.size(); i++) {
                                if (ordersList.get(i).getSlotArr().size() == 0) {
                                    orderDayList.add(AppUtils.getInstance().formatDate(ordersList.get(i).getDileveryDate(), SERVICE_DATE_FORMAT, DATE_FORMAT));
                                } else {
//                                    ArrayList<String> slotsDate = AppUtils.getInstance().getSlotsDates(ordersList.get(i).getSlotArr());
                                    for (String date : ordersList.get(i).getSelectedDateArr()) {
                                        orderDayList.add(AppUtils.getInstance().formatDate(date, SERVICE_DATE_FORMAT, DATE_FORMAT));
                                    }
                                }
                            }
                            Calendar c = Calendar.getInstance();
                            int year = c.get(Calendar.YEAR);
                            int month = chooseMonth - 1;
                            int day = c.get(Calendar.MONTH) == chooseMonth - 1 ? c.get(Calendar.DAY_OF_MONTH) : 1;
                            CalendarBean calendarBean = new CalendarBean(year, month + 1, day);
                            filterCalenderList(calendarBean);
                            break;

                        case Constants.NetworkConstant.NO_DATA:
                            ordersList.clear();
                            selectedOrdersList.clear();
                            ordersAdapter.notifyDataSetChanged();
                            noDataAvailable();
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            noDataAvailable();
            AppUtils.getInstance().showToast(mActivity, response);
        }
    }

    @Override
    public void onFailure() {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            noDataAvailable();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * This method checks whether data is present in list or not and update UI accordingly
     */
    public void noDataAvailable() {
        if (selectedOrdersList.size() == 0) {
            layoutNoDataFound.setVisibility(View.VISIBLE);
        } else {
            layoutNoDataFound.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getExtras() != null) {
            if (requestCode == Constants.IntentConstant.REQUEST_ORDER_CODE && resultCode == Activity.RESULT_OK) {
                Result orderResponse = (Result) data.getExtras().getSerializable(Constants.IntentConstant.ORDER_DETAILS);
                if (orderResponse != null) {
                    for (int i = 0; i < ordersList.size(); i++) {
                        if (ordersList.get(i).getId().equals(orderResponse.getId())) {
                            ordersList.set(i, orderResponse);
                            CalendarBean calendarBean = new CalendarBean(year, month + 1, day);
                            filterCalenderList(calendarBean);
                        }
                    }
                }
            }
            if (requestCode == Constants.IntentConstant.REQUEST_PUSH_ORDER_CODE && resultCode == Activity.RESULT_OK) {
                if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                    progressBar.setVisibility(View.VISIBLE);
                    hitOrdersListApi(year, month + 1);
                    orderDayList.clear();
                    calendarDateView.notifyData();
                }
            }
        }
    }
}
