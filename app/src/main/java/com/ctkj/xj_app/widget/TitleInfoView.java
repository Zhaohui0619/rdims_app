package com.ctkj.xj_app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctkj.xj_app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 昭辉 on 2018/5/9.
 */

public class TitleInfoView extends RelativeLayout {

    @BindView(R.id.tv_title)
    TextView mTitleView;
    @BindView(R.id.tv_info)
    TextView mInfoView;

    private String mTitle = "标题";
    private String mInfo = "信息";

    public TitleInfoView(Context context) {
        super(context);
    }

    public TitleInfoView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.TitleInfoView);
        mTitle = typedArray.getString(R.styleable.TitleInfoView_title);
        mInfo = typedArray.getString(R.styleable.TitleInfoView_info);
        typedArray.recycle();
        initView(context);
    }

    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_titleinfo,this,true);
        ButterKnife.bind(this);
        mTitleView.setText(mTitle);
        mInfoView.setText(mInfo);
    }

    public void setTitle(String title){
        mTitleView.setText(title);
    }

    public void setInfo(String info){
        mInfoView.setText(info);
    }
}
