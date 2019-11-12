package com.planapps.countdown.uitl;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.planapps.countdown.R;


public class maintoolbar extends Toolbar {

    private ImageView setting;
    private ImageView add;
    private TextView title;
    private View mChildView;
    private Drawable left_button_icon;
    private Drawable right_button_icon;
    private OnLeftButtonClickListener onLeftButtonClickListener;
    private OnRightButtonClickListener onRightButtonClickListener;
    private OnTouchListener ontitleClickListener;


    public interface OnLeftButtonClickListener {
        void onClick();
    }

    public interface OnRightButtonClickListener {
        void onClick();
    }

    public void setOnLeftButtonClickListener(OnLeftButtonClickListener onLeftButtonClickListener) {
        this.onLeftButtonClickListener = onLeftButtonClickListener;
    }

    public void setOnRightButtonClickListener(OnRightButtonClickListener onRightButtonClickListener) {
        this.onRightButtonClickListener = onRightButtonClickListener;
    }

    public maintoolbar(Context context) {
       this(context,null,0);
    }

    public maintoolbar(Context context, @Nullable AttributeSet attrs) {
          this(context,attrs,0);
    }

    public maintoolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.maintoolbar, defStyleAttr, 0);
        left_button_icon = a.getDrawable(R.styleable.maintoolbar_leftButtonIcon);
        right_button_icon = a.getDrawable(R.styleable.maintoolbar_rightButtonIcon);
        a.recycle();
        initView();
        initListener();
    }
    private void initListener(){
        setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onLeftButtonClickListener.onClick();
            }
        });
        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onRightButtonClickListener.onClick();
            }
        });

    }
    private void initView(){
        if(mChildView==null){
            mChildView=View.inflate(getContext(),R.layout.maintoolbar,null);
            setting=mChildView.findViewById(R.id.setting);
            add=mChildView.findViewById(R.id.add);
            addView(mChildView);
            if(left_button_icon !=null){
                setting.setImageDrawable(left_button_icon);
            }
            if(right_button_icon !=null){
                add.setImageDrawable(right_button_icon);
            }
        }
    }
    public void setLeftButtonIconDrawable(Drawable d){
        setting.setImageDrawable(d);
    }
    public void setRightButtonIconDrawable(Drawable d){
        add.setImageDrawable(d);
    }
}
