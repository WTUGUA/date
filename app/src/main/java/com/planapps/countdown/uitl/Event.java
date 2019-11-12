package com.planapps.countdown.uitl;

import org.litepal.crud.LitePalSupport;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Event extends LitePalSupport {
    private int id;
    private String event;
    private Date date;
    private String classify;
    private boolean top;
    private String repeat;

    @Override
    public String toString() {
        return "事件='" + event + '\'' +
                ", 日期=" + datetoString(date) +
                ", 分类='" + classify + '\''
                ;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public boolean isTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public Event(int id) {
        this.id = id;
    }

    public Event() {
    }
    public String datetoString(Date date){
        DateToString(date);
        return DateToString(date);
    }
    private static String DateToString(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String str = sdf.format(time);
        return str;
    }
}
