package com.ctkj.xj_app.app;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ctkj.xj_app.R;
import com.ctkj.xj_app.bean.PhotoInfo;
import com.google.common.collect.Lists;
import com.hotdog.hdlibrary.utils.BitmapUtils;
import com.hotdog.hdlibrary.utils.ImageUrlUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SelectorPicGridAdapter extends BaseAdapter {

    private final PhotoInfo placeHolder = new PhotoInfo();

    private final int maxCount;
    private List<String> imageFiles = Lists.newArrayList();

    public SelectorPicGridAdapter(int maxCount) {
        this.maxCount = maxCount;
    }

    public void setImages(List<String> addImageFiles) {//直接使用引用，不然删除的时候，引用的数据源没有删掉
        imageFiles = addImageFiles;
    }

    public List<String> getImages() {
        return imageFiles;
    }


    private boolean isPhotos = false;

    public void setListPhotos(List<PhotoInfo> list) {//直接使用引用，不然删除的时候，引用的数据源没有删掉
        photos = list;
        isPhotos = true;
    }

    private long createTime;
    private int hashCode;
    private List<PhotoInfo> photos;

    public void setData(long createTime, int hashCode) {
        this.createTime = createTime;
        this.hashCode = hashCode;
    }

    @Override
    public int getCount() {
        return imageFiles.size() + 1;
    }

    @Override
    public String getItem(int position) {
        int realPosition = position - 1;
        return realPosition >= 0 ? imageFiles.get(realPosition) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.my_imgscan_seled_adapter, null);
        }
        ImageView view_image_view = ViewHolder.get(convertView, R.id.view_imageview);
        ImageView item_delete_btn = ViewHolder.get(convertView, R.id.item_delete_btn);

        if (position == getCount() - 1) {
            view_image_view.setImageResource(R.drawable.transparent);
            item_delete_btn.setVisibility(View.GONE);
            if (position == maxCount) {
                view_image_view.setVisibility(View.INVISIBLE);
                view_image_view.setSelected(false);
            }
        } else {
            String item = imageFiles.get(position);
            item_delete_btn.setVisibility(View.VISIBLE);
            if (ImageUrlUtil.isLocalPath(item)) {
                view_image_view.setImageBitmap(BitmapUtils.compressBitmap(item, 200));
            } else {
                Picasso.with(context)
                        .load("")
                        .into(view_image_view);
            }
        }

        item_delete_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageFiles != null && position >= 0 && position < imageFiles.size()) {
                    final String path = imageFiles.get(position);
                    imageFiles.remove(position);
                    notifyDataSetChanged();
                    return;

                }
            }
        });
        return convertView;
    }

}
