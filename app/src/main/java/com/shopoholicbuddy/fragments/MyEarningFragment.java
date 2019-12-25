package com.shopoholicbuddy.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.charting.charts.BarChart;
import com.charting.charts.Chart;
import com.charting.charts.LineChart;
import com.charting.components.Description;
import com.charting.components.Legend;
import com.charting.components.XAxis;
import com.charting.data.BarData;
import com.charting.data.BarDataSet;
import com.charting.data.BarEntry;
import com.charting.data.Entry;
import com.charting.data.LineData;
import com.charting.data.LineDataSet;
import com.charting.formatter.IndexAxisValueFormatter;
import com.google.gson.Gson;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.activities.EarningListActivity;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.dialogs.CustomDialogForError;
import com.shopoholicbuddy.interfaces.AddressCallback;
import com.shopoholicbuddy.interfaces.PaymentResponseCallback;
import com.shopoholicbuddy.models.buddyearningresponse.BuddyEarningResponse;
import com.shopoholicbuddy.models.buddyearningresponse.CategoryEarn;
import com.shopoholicbuddy.models.countrymodel.CountryBean;
import com.shopoholicbuddy.network.ApiCall;
import com.shopoholicbuddy.network.ApiInterface;
import com.shopoholicbuddy.network.NetworkListener;
import com.shopoholicbuddy.network.RestApi;
import com.shopoholicbuddy.utils.AppSharedPreference;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;
import com.shopoholicbuddy.utils.PaymentUtils;
import com.shopoholicbuddy.utils.ReverseGeocoding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by appinventiv-pc on 23/3/18.
 */

public class MyEarningFragment extends Fragment implements NetworkListener, AddressCallback {
    @BindView(R.id.tv_total_earning)
    CustomTextView tvTotalEarning;
    @BindView(R.id.ll_last_week)
    LinearLayout llLastWeek;
    @BindView(R.id.ll_last_month)
    LinearLayout llLastMonth;
    @BindView(R.id.ll_last_year)
    LinearLayout llLastYear;
    @BindView(R.id.chart_earning)
    LineChart chartEarning;
    @BindView(R.id.chart_categories)
    BarChart chartCategories;
    Unbinder unbinder;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tv_week_earning)
    CustomTextView tvWeekEarning;
    @BindView(R.id.tv_month_earning)
    CustomTextView tvMonthEarning;
    @BindView(R.id.tv_year_earning)
    CustomTextView tvYearEarning;
    @BindView(R.id.ll_payment)
    LinearLayout llPayment;
    @BindView(R.id.tv_payment)
    CustomTextView tvPayment;
    @BindView(R.id.paymentProgressBar)
    ProgressBar paymentProgressBar;

    private View rootView;
    private AppCompatActivity mActivity;
    private int type = 0;
    private BuddyEarningResponse buddyEarningResponse;
    private boolean isLoading;
    private double pendingAmount = 0.00;
    private String currencyCode = "";
    private String currencySymbol = "";
    private PaymentUtils paymentUtils;
    private boolean isClicked = false;
    private int count = 0;
    private double totalEarning = 0.0;
    private double lastWeekEarning = 0.0;
    private double lastMonthEarning = 0.0;
    private double lastYearEarning = 0.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_earnings, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initVariables();
        if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
            type = Constants.ChartConstants.WEEKLY;
            hitBuddyEarningApi(type);
        }
        return rootView;
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        mActivity = (AppCompatActivity) getActivity();
        paymentUtils = PaymentUtils.getInstance();
        String latitude = AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.LATITUDE);
        String longitude = AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.LONGITUDE);
        if (!latitude.equals("") && !longitude.equals("")) {
            new ReverseGeocoding(mActivity, Double.parseDouble(latitude), Double.parseDouble(longitude), this).execute();
        }
    }

    /**
     * Method to hit the profile data api
     */
    private void hitBuddyEarningApi(int type) {
        progressBar.setVisibility(View.VISIBLE);
        isLoading = true;

        ApiInterface apiInterface = RestApi.createServiceAccessToken(mActivity, ApiInterface.class);//empty field is for the access token
        final HashMap<String, String> params = new HashMap<>();

        String startDate;
        String month = "";
        String year = "";
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.AppConstant.SERVICE_DATE_FORMAT);
        Calendar calendar = Calendar.getInstance();
        String endDate = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        String startDateWeek = dateFormat.format(calendar.getTime());
        startDate = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, +7);
        calendar.add(Calendar.MONTH, -1);
        String startDateMonth = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.MONTH, +1);
        calendar.add(Calendar.YEAR, -1);
        String startDateYear = dateFormat.format(calendar.getTime());

        switch (type) {
            case Constants.ChartConstants.WEEKLY:
                calendar.add(Calendar.YEAR, +1);
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                startDate = dateFormat.format(calendar.getTime());
                break;
            case Constants.ChartConstants.MONTHLY:
                calendar.add(Calendar.YEAR, +1);
                calendar.add(Calendar.MONTH, -1);
                startDate = dateFormat.format(calendar.getTime());
                month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH));
                year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                break;
            case Constants.ChartConstants.YEARLY:
                startDate = dateFormat.format(calendar.getTime());
                year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                break;
        }

        params.put(Constants.NetworkConstant.PARAM_USER_ID, AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID));
        params.put(Constants.NetworkConstant.PARAM_START_DATE, startDate);
        params.put(Constants.NetworkConstant.PARAM_START_WEEK, startDateWeek);
        params.put(Constants.NetworkConstant.PARAM_START_MONTH, startDateMonth);
        params.put(Constants.NetworkConstant.PARAM_START_YEAR, startDateYear);
        params.put(Constants.NetworkConstant.PARAM_END_DATE, endDate);
        params.put(Constants.NetworkConstant.PARAM_MONTH, month);
        params.put(Constants.NetworkConstant.PARAM_YEAR, year);
        Call<ResponseBody> call = apiInterface.hitBuddyEarningApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, this, Constants.NetworkConstant.REQUEST_EARNING);
        Call<ResponseBody> callEarning = apiInterface.hitBuddyLastEarningApi(AppUtils.getInstance().encryptData(params));
        ApiCall.getInstance().hitService(mActivity, callEarning, this, Constants.NetworkConstant.REQUEST_LAST_EARNING);
    }


    @Override
    public void onSuccess(int responseCode, String response, int requestCode) {
        if (isAdded()) {
            if (count > 0) {
                progressBar.setVisibility(View.INVISIBLE);
                isLoading = false;
                count = 0;
            } else {
                count++;
            }
            AppUtils.getInstance().printLogMessage(Constants.NetworkConstant.ALERT, response);
            switch (requestCode) {
                case Constants.NetworkConstant.REQUEST_EARNING:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            buddyEarningResponse = new Gson().fromJson(response, BuddyEarningResponse.class);

                            pendingAmount = Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", buddyEarningResponse.getResult().getPendingAmount()));
                            llPayment.setVisibility(View.GONE);
                            /*if (pendingAmount > 0) {
                                llPayment.setVisibility(View.VISIBLE);
                                tvPayment.setText(TextUtils.concat(getString(R.string.pending_amount) + " " + currencySymbol + pendingAmount));
                            }*/
                            setLineChartTheme(chartEarning, type);
                            setChartData(type, chartEarning);

                            setBarChartTheme(chartCategories);
                            setChartData(0, chartCategories);
                            break;
                        default:
                            try {
                                AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                    break;
                case Constants.NetworkConstant.REQUEST_LAST_EARNING:
                    switch (responseCode) {
                        case Constants.NetworkConstant.SUCCESS_CODE:
                            buddyEarningResponse = new Gson().fromJson(response, BuddyEarningResponse.class);
                            totalEarning = buddyEarningResponse.getResult().getTotalEarn();
                            lastWeekEarning = buddyEarningResponse.getResult().getLastweekEarn();
                            lastMonthEarning = buddyEarningResponse.getResult().getLastmonthEarn();
                            lastYearEarning = buddyEarningResponse.getResult().getLastyearEarn();
                            setValues();
                            break;
                        default:
                            try {
                                AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                    break;
                default:
                    try {
                        AppUtils.getInstance().showToast(mActivity, new JSONObject(response).optString(Constants.NetworkConstant.RESPONSE_MESSAGE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    /**
     * function to set values
     */
    private void setValues() {
        tvTotalEarning.setText(TextUtils.concat(currencySymbol + String.format(Locale.ENGLISH, "%.2f", totalEarning)));
        tvWeekEarning.setText(TextUtils.concat(currencySymbol + String.format(Locale.ENGLISH, "%.2f", lastWeekEarning)));
        tvMonthEarning.setText(TextUtils.concat(currencySymbol + String.format(Locale.ENGLISH, "%.2f", lastMonthEarning)));
        tvYearEarning.setText(TextUtils.concat(currencySymbol + String.format(Locale.ENGLISH, "%.2f", lastYearEarning)));
    }

    @Override
    public void onError(String response, int requestCode) {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.INVISIBLE);
            AppUtils.getInstance().showToast(mActivity, response);
            showErrorDialog();
        }
    }

    @Override
    public void onFailure() {
        if (isAdded()) {
            isLoading = false;
            progressBar.setVisibility(View.INVISIBLE);
            showErrorDialog();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * dialog to show error
     */
    private void showErrorDialog() {
        CustomDialogForError customDialogForError = new CustomDialogForError(mActivity, () -> {
            if (!isLoading && AppUtils.getInstance().isInternetAvailable(mActivity)) {
                hitBuddyEarningApi(type);
            }
        });
        customDialogForError.show();
    }

    @OnClick({R.id.ll_last_week, R.id.ll_last_month, R.id.ll_last_year, R.id.iv_earning_list, R.id.tv_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_last_week:
                if (!isLoading && AppUtils.getInstance().isInternetAvailable(mActivity)) {
                    type = Constants.ChartConstants.WEEKLY;
                    hitBuddyEarningApi(type);
                }
                break;
            case R.id.ll_last_month:
                if (!isLoading && AppUtils.getInstance().isInternetAvailable(mActivity)) {
                    type = Constants.ChartConstants.MONTHLY;
                    hitBuddyEarningApi(type);
                }
                break;
            case R.id.ll_last_year:
                if (!isLoading && AppUtils.getInstance().isInternetAvailable(mActivity)) {
                    type = Constants.ChartConstants.YEARLY;
                    hitBuddyEarningApi(type);
                }
                break;
            case R.id.iv_earning_list:
                startActivity(new Intent(mActivity, EarningListActivity.class));
                mActivity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            case R.id.tv_pay:
                if (!isClicked) {
                    if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                        isClicked = true;
                        paymentProgressBar.setVisibility(View.VISIBLE);
                        paymentUtils.payOnlineBill(mActivity, currencyCode, pendingAmount, new PaymentResponseCallback() {
                            @Override
                            public void onPaymentSuccess(int responseCode, String message) {
                                if (isAdded()) {
                                    isClicked = false;
                                    paymentProgressBar.setVisibility(View.INVISIBLE);
                                    if (responseCode != 201) {
                                        llPayment.setVisibility(View.GONE);
                                        AppUtils.getInstance().showToast(mActivity, message);
                                    }
                                }
                            }

                            @Override
                            public void onPaymentFailuer(int responseCode, String message) {
                                if (isAdded()) {
                                    isClicked = false;
                                    paymentProgressBar.setVisibility(View.INVISIBLE);
                                    AppUtils.getInstance().showToast(mActivity, message);
                                }
                            }
                        });
                    }
                }
                break;
        }
    }


    /**
     * set theme and value formats for all chart
     */
    private void setLineChartTheme(LineChart chart, int timePeriod) {
        chart.setBackground(new ColorDrawable(ContextCompat.getColor(mActivity, android.R.color.transparent)));
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setGranularityEnabled(true);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.setScaleX(1.0f);
        switch (timePeriod) {
            case Constants.ChartConstants.MONTHLY:
                chart.setScaleMinima(1.4f, 0.5f);
                break;
            case Constants.ChartConstants.WEEKLY:
                chart.setScaleMinima(1.4f, 0.5f);
                break;
            case Constants.ChartConstants.YEARLY:
                chart.setScaleMinima(1.2f, 0.5f);
                break;
        }
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setTextColor(Color.WHITE);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
        Description description = new Description();
        description.setText("");
        description.setEnabled(false);
        chart.setDescription(description);
        chart.animateX(800);
    }

    /**
     * set theme and value formaters for all chart
     */
    private void setBarChartTheme(BarChart chart) {
        chart.setBackground(new ColorDrawable(ContextCompat.getColor(mActivity, android.R.color.transparent)));
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setGranularityEnabled(true);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.setScaleX(1.0f);
        chart.setScaleMinima(1f, 0.5f);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setTextColor(Color.WHITE);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
        Description description = new Description();
        description.setText("");
        description.setEnabled(false);
        chart.setDescription(description);
        chart.animateY(500);
    }

    /**
     * method to set data on chart
     *
     * @param timePeriod .
     */
    private void setChartData(int timePeriod, final Chart chart) {
        chart.getXAxis().setValueFormatter(AppUtils.getInstance().getLabelFormatter(timePeriod, mActivity));
        switch (chart.getId()) {
            case R.id.chart_earning:
                chart.scrollTo(0, 0);
                LineDataSet lineDataSet = null;
                lineDataSet = new LineDataSet(setEarningData(timePeriod), "");
                lineDataSet.setFillColor(ContextCompat.getColor(mActivity, R.color.colorMessageTitle));
                lineDataSet.setColor(ContextCompat.getColor(mActivity, R.color.colorAccent));
                lineDataSet.setCircleColor(ContextCompat.getColor(mActivity, android.R.color.transparent));
                lineDataSet.setLineWidth(3f);
                lineDataSet.setCircleRadius(3f);
                lineDataSet.setDrawCircleHole(false);
                lineDataSet.setValueTextSize(9f);
                lineDataSet.setDrawValues(false);
                lineDataSet.setDrawFilled(true);
                LineData chartEarningData = new LineData();
                chartEarningData.addDataSet(lineDataSet);
                ((LineChart) chart).setData(chartEarningData);
                switch (timePeriod) {
                    case Constants.ChartConstants.MONTHLY:
                        ((LineChart) chart).setVisibleXRangeMinimum(3);
                        ((LineChart) chart).setVisibleXRangeMaximum(3);
                        ((LineChart) chart).moveViewToX(4);
                        break;
                    case Constants.ChartConstants.WEEKLY:
                        ((LineChart) chart).setVisibleXRangeMinimum(4);
                        ((LineChart) chart).setVisibleXRangeMaximum(4);
                        ((LineChart) chart).moveViewToX(5);
                        break;
                    case Constants.ChartConstants.YEARLY:
                        ((LineChart) chart).setVisibleXRangeMinimum(5);
                        ((LineChart) chart).setVisibleXRangeMaximum(5);
                        ((LineChart) chart).moveViewToX(7);
                        break;
                }
                ((LineChart) chart).getAxisLeft().resetAxisMinimum();
                ((LineChart) chart).getAxisLeft().setAxisMinimum(0);
                new Handler().post(((LineChart) chart)::invalidate);
                // get the legend (only possible after setting data)
                Legend legendBadges = ((LineChart) chart).getLegend();
                // modify the legend ...
                legendBadges.setForm(Legend.LegendForm.LINE);
                break;
            case R.id.chart_categories:
                List<String> categories = new ArrayList<>();
                for(int i=0; i<5; i++) categories.add("");
                List<Integer> colors = new ArrayList<>();
                List<CategoryEarn> categoryEarn1 = buddyEarningResponse.getResult().getCategoryEarn();
                for (int i = 0; i < categoryEarn1.size(); i++) {
                    categories.set(i, categoryEarn1.get(i).getCategory());
                    colors.add(AppUtils.getInstance().getBarColors(mActivity, i));
                }
                if (colors.size() == 0) colors.add(AppUtils.getInstance().getBarColors(mActivity, 0));
                chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(categories));
                BarDataSet barDataSet = null;
                barDataSet = new BarDataSet(setCategoryData(categories), "");
                barDataSet.setColors(colors);
                barDataSet.setFormLineWidth(2f);
                barDataSet.setDrawValues(false);
                BarData chartCategoryData = new BarData();
                chartCategoryData.addDataSet(barDataSet);
                ((BarChart) chart).setData(chartCategoryData);
                ((BarChart) chart).getAxisLeft().resetAxisMinimum();
                ((BarChart) chart).getAxisLeft().setAxisMinimum(0);
                new Handler().post(((BarChart) chart)::invalidate);
                // get the legend (only possible after setting data)
                Legend score = ((BarChart) chart).getLegend();
                // modify the legend ...
                score.setForm(Legend.LegendForm.LINE);
                break;
        }
    }

    /**
     * method to set category data
     * @param categories
     * @return
     */
    private List<BarEntry> setCategoryData(List<String> categories) {
        List<BarEntry> categoryData = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            categoryData.add(new BarEntry(i, 0));
            if (buddyEarningResponse.getResult().getCategoryEarn().size() > i)
                categoryData.set(i, new BarEntry(i, buddyEarningResponse.getResult().getCategoryEarn().get(i).getCategoryEarning()));
        }
        return categoryData;
    }

    /**
     * method to set badges data
     *
     * @return
     */
    private List<Entry> setEarningData(int timePeriod) {
        List<Entry> monthlyData = new ArrayList<>();
        List<Entry> weeklyData = new ArrayList<>();
        List<Entry> yearlyData = new ArrayList<>();
        switch (timePeriod) {
            case Constants.ChartConstants.MONTHLY:
                for (int i = 0; i < 5; i++) {
                    monthlyData.add(new Entry(i, 0));
                    for (int j = 0; j < buddyEarningResponse.getResult().getDateEarn().size(); j++) {
                        if (buddyEarningResponse.getResult().getDateEarn().get(j).getDate().equals(String.valueOf(i))) {
                            monthlyData.set(i, new Entry(i, buddyEarningResponse.getResult().getDateEarn().get(j).getDateEarning()));
                            break;
                        }
                    }
                }
                return monthlyData;
            case Constants.ChartConstants.WEEKLY:
                List<String> weeks = AppUtils.getInstance().getWeekDates();
                for (int i = 0; i < weeks.size(); i++) {
                    weeklyData.add(new Entry(i, 0));
                    for (int j = 0; j < buddyEarningResponse.getResult().getDateEarn().size(); j++) {
                        String date = AppUtils.getInstance().formatDate(buddyEarningResponse.getResult().getDateEarn().get(j).getDate(),
                                "yyyy-MM-dd", "dd MMM");
                        if (date.equals(weeks.get(i))) {
                            weeklyData.set(i, new Entry(i, buddyEarningResponse.getResult().getDateEarn().get(j).getDateEarning()));
                            break;
                        }
                    }
                }
                return weeklyData;
            case Constants.ChartConstants.YEARLY:
                String[] months = /*AppUtils.getInstance().getMonthList();*/getResources().getStringArray(R.array.arr_month);
                for (int i = 0; i < months.length; i++) {
                    yearlyData.add(new Entry(i, 0));
                    for (int j = 0; j < buddyEarningResponse.getResult().getDateEarn().size(); j++) {
                        if (buddyEarningResponse.getResult().getDateEarn().get(j).getDate().equals(months[i])) {
                            yearlyData.set(i, new Entry(i, buddyEarningResponse.getResult().getDateEarn().get(j).getDateEarning()));
                            break;
                        }
                    }
                }
                return yearlyData;
        }
        return new ArrayList<>();
    }

    @Override
    public void setAddress(Address address) {
        if (isAdded()) {
            String countryCode = address.getCountryCode();
            ArrayList<CountryBean> countries = AppUtils.getInstance().getAllCountries(mActivity);
            for (CountryBean country : countries) {
                if (country.getISOCode().equals(countryCode)) {
                    currencySymbol = country.getCurrency();
                    currencyCode = country.getCurrencyCode();
                    setValues();
                    break;
                }
            }
        }
    }

    /**
     * function to hit payment activity
     */
    public void hitPaymentApi() {
        if (isAdded()) {
            if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                isClicked = true;
                paymentProgressBar.setVisibility(View.VISIBLE);
                paymentUtils.hitPaymentApi();
            }
        }
    }
}
