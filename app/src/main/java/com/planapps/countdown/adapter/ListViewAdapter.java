package com.planapps.countdown.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.planapps.countdown.R;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends BaseAdapter {

    private int defaultSelection = -1;
    private Context mContext;
    private List<String> list;
    private int text_selected_color;
    private ColorStateList colors;

    public ListViewAdapter(Context mContext, List<String> list) {
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
        this.list = list;
        Resources resources = mContext.getResources();
        text_selected_color = resources.getColor(R.color.bule);// 文字选中的颜色
        // 背景选中的颜色
        colors = mContext.getResources().getColorStateList(
                R.color.white);// 文字未选中状态的selector
        resources = null;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);

    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.listitem2, parent, false);
            viewHolder.txt_item = (TextView) convertView
                    .findViewById(R.id.item_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txt_item.setText(getItem(position).toString());

        if (position == defaultSelection) {// 选中时设置单纯颜色
            viewHolder.txt_item.setTextColor(text_selected_color);
        } else {// 未选中时设置selector
            viewHolder.txt_item.setTextColor(colors);
        }
        return convertView;
    }

    class ViewHolder {
        TextView txt_item;
    }

    /**
     * @param position 设置高亮状态的item
     */
    public void setSelectPosition(int position) {
        if (!(position < 0 || position > list.size())) {
            defaultSelection = position;
            notifyDataSetChanged();
        }
    }
    public void add(String data) {
        List<String> arrList = new ArrayList(list);
        arrList.add(data);
        list=arrList;
        notifyDataSetChanged();
    }
}
