package com.shopoholicbuddy.calendar;


import com.shopoholicbuddy.utils.AppUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Akash Jain on 19/07/2017.
 */
public class CalendarBean {

    public int year;
    public int month;
    public int day;
    public int week;
    //-1,0,1
    public int mothFlag;

    public CalendarBean(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public String getDisplayWeek(){
        String s="";
         switch(week){
             case 1:
                 s="Sunday";
          break;
             case 2:
                 s="Monday";
          break;
             case 3:
                 s="Tuesday";
                 break;
             case 4:
                 s="Wednesday";
                 break;
             case 5:
                 s="Thursday";
                 break;
             case 6:
                 s="Friday";
                 break;
             case 7:
                 s="Saturday";
                 break;

         }
        return s ;
    }

    @Override
    public String toString() {
//        String s=year+"/"+moth+"/"+day+"\t"+getDisplayWeek()+"\t农历"+":"+chinaMonth+"/"+chinaDay;
        return year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
    }


    public Date getDate(){
        return AppUtils.getInstance().getDateFromString(year+"-"+month+"-"+day,new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH));
    }
}