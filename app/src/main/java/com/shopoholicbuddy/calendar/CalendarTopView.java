package com.shopoholicbuddy.calendar;

/**
 * Created by Akash Jain on 19/07/2017.
 */

public interface CalendarTopView {

    int[] getCurrentSelectPositon();

    int getItemHeight();

    void setCaledarTopViewChangeListener(CalendarTopViewChangeListener listener);

}
