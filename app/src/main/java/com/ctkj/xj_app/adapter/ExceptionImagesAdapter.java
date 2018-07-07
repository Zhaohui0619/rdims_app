package com.ctkj.xj_app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ctkj.xj_app.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mocking on 2018/6/29.
 */

public class ExceptionImagesAdapter extends BaseAdapter {

    private List<String> mImagePaths = new ArrayList<>(0);
    private Context mContext;

    public ExceptionImagesAdapter(List<String> imagePaths,Context context){
        mImagePaths = imagePaths;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mImagePaths.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageViewHolder mHolder;
        if (view == null){
            view = View.inflate(mContext,R.layout.item_exception_image,null);
            mHolder = new ImageViewHolder();
            mHolder.imageView = view.findViewById(R.id.iv_exception);
            view.setTag(mHolder);
        }else {
            mHolder = (ImageViewHolder) view.getTag();
        }
        String path = mImagePaths.get(i);
        Glide.with(mContext).load(new File(path)).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(mHolder.imageView);

        return view;
    }

    class ImageViewHolder{
        ImageView imageView;
    }
}
