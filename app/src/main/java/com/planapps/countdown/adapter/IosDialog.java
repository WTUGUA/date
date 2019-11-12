package com.planapps.countdown.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.planapps.countdown.R;

public class IosDialog extends Dialog {

    /**取消按钮*/
    private Button button_cancel;

    /**确认按钮*/
    private Button button_exit;
    /**标题文字*/
    private TextView tv;
    private TextView tv2;
    private EditText dialog_edittext;



    public IosDialog(Context context){
        super(context, R.style.mdialog);
        //通过LayoutInflater获取布局
        View view = LayoutInflater.from(getContext()).
                inflate(R.layout.dialog_layout, null);
        tv =  view.findViewById(R.id.title);
        tv2= view.findViewById(R.id.title2);
        dialog_edittext=view.findViewById(R.id.dialog_text);
        button_cancel = view.findViewById(R.id.btn_cancel);
        button_exit =  view.findViewById(R.id.btn_exit);
        //设置显示的视图
        setContentView(view);
    }
    /**
     * 设置显示的标题文字
     */
    public void setTv(String content) {
        tv.setText(content);
    }
    public void setTv2(String content) {
        tv2.setText(content);
    }

    public EditText getDialog_edittext() {
        return dialog_edittext;
    }
    public String getText(){
        return dialog_edittext.getText().toString();
    }

    public void setDialog_edittext(EditText dialog_edittext) {
        this.dialog_edittext = dialog_edittext;
    }

    /**
     * 取消按钮监听
     * */
    public void setOnCancelListener(View.OnClickListener listener){
        button_cancel.setOnClickListener(listener);
    }

    /**
     * 退出按钮监听
     * */
    public void setOnExitListener(View.OnClickListener listener){
        button_exit.setOnClickListener(listener);
    }

}
