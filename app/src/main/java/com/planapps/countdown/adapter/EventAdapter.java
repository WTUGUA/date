package com.planapps.countdown.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.planapps.countdown.R;
import com.planapps.countdown.uitl.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class EventAdapter extends ArrayAdapter<Event> {
    private int resourceId;
    public EventAdapter(@NonNull Context context,  int textViewResourceId, @NonNull List<Event> objects) {
        super(context,  textViewResourceId, objects);
        resourceId=textViewResourceId;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Event event=getItem(position);
        View view;
        ViewHodler viewHodler;
        if(convertView==null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHodler=new ViewHodler();
            viewHodler.Fes=view.findViewById(R.id.event_date);
            viewHodler.date=view.findViewById(R.id.date);
            viewHodler.time=view.findViewById(R.id.event_time);
            viewHodler.timeago=view.findViewById(R.id.time_ago);
            viewHodler.progressBar=view.findViewById(R.id.progress_bar);
            view.setTag(viewHodler);
        }else{
            view=convertView;
            viewHodler=(ViewHodler) view.getTag();
        }
        viewHodler.Fes.setText(event.getEvent());
        viewHodler.date.setText(event.getEvent());
        String str=getWeekDay(event.getDate().toString());
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        viewHodler.date.setText(sdf.format(event.getDate())+"("+str+")");
        Date date1 = new Date(System.currentTimeMillis());
        Date date2 =event.getDate();
        int daysBetween=daysBetween(date1,date2);
        viewHodler.time.setText(String.valueOf(Math.abs(daysBetween)));
        if(daysBetween>0){
            viewHodler.timeago.setText("天后");
            viewHodler.progressBar.setProgress(Math.abs(daysBetween));
        }else{
            viewHodler.timeago.setText("天前");
            viewHodler.progressBar.setProgress(Math.abs(daysBetween));
            if(Math.abs(daysBetween)>365&&daysBetween<0){
                viewHodler.Fes.setTextColor(Color.argb(20,0,0,0));
                viewHodler.date.setTextColor(Color.argb(20,0,0,0));
                viewHodler.time.setTextColor(Color.argb(20,0,0,0));
                viewHodler.timeago.setTextColor(Color.argb(20,0,0,0));
                viewHodler.progressBar.setProgress(366);
            }
        }
        return view;
    }
    class ViewHodler {
        TextView Fes;
        TextView date;
        TextView time;
        TextView timeago;
        ProgressBar progressBar;
    }
    public static String getWeekDay(String seconds) {

        Date date = new Date(seconds);
        String Week = "";
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int wek = c.get(Calendar.DAY_OF_WEEK);

        if (wek == 1) {
            Week += "周日";
        }
        if (wek == 2) {
            Week += "周一";
        }
        if (wek == 3) {
            Week += "周二";
        }
        if (wek == 4) {
            Week += "周三";
        }
        if (wek == 5) {
            Week += "周四";
        }
        if (wek == 6) {
            Week += "周五";
        }
        if (wek == 7) {
            Week += "周六";
        }
        return Week;
    }
    public static int daysBetween(Date date1,Date date2)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        long time1 = cal.getTimeInMillis();
        cal.setTime(date2);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);
        return Integer.parseInt(String.valueOf(between_days));
    }
}
