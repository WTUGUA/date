package com.planapps.countdown.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import com.planapps.countdown.R;

import java.util.List;

public class DialogItemAdapter extends BaseAdapter {
    private int defaultSelection = -1;
    private Context mContext;
    private List<String> list;
    private int text_selected_color;
    private ColorStateList colors;
    private ImageView imageView;

    public DialogItemAdapter(Context mContext, List<String> list) {
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
        this.list = list;
        Resources resources = mContext.getResources();
        text_selected_color = resources.getColor(R.color.bule);// 文字选中的颜色

        // 背景选中的颜色
        colors = mContext.getResources().getColorStateList(
                R.color.deepblack);// 文字未选中状态的selector
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
        DialogItemAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new DialogItemAdapter.ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.listitem3, parent, false);
            viewHolder.txt_item = (TextView) convertView
                    .findViewById(R.id.textview);
            viewHolder.imageView=convertView.findViewById(R.id.item_image_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (DialogItemAdapter.ViewHolder) convertView.getTag();
        }
        viewHolder.txt_item.setText(getItem(position).toString());

        if (position == defaultSelection) {// 选中时设置单纯颜色
            viewHolder.txt_item.setTextColor(text_selected_color);
            viewHolder.imageView.setImageResource(R.drawable.navbar_icon_check2);
        } else {// 未选中时设置selector
            viewHolder.txt_item.setTextColor(colors);
        }
        return convertView;
    }

    class ViewHolder {
        TextView txt_item;
        ImageView imageView;
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
}

