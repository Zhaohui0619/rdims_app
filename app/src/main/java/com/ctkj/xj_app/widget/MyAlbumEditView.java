package com.ctkj.xj_app.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.ctkj.xj_app.R;
import com.ctkj.xj_app.app.PhotoBrowserActivity;
import com.ctkj.xj_app.app.SelectorPicGridAdapter;
import com.ctkj.xj_app.bean.PhotoInfo;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.hotdog.hdlibrary.utils.PhotoPicker;


import java.util.List;

import javax.annotation.Nullable;

public class MyAlbumEditView extends FrameLayout implements OnItemClickListener, PhotoPicker.Callback {

	private TextView view_title_tv;
	private GridView gridview;
	private List<PhotoInfo> photos;
	private SelectorPicGridAdapter adapter;
	private int imgSum;
	private boolean isEditable;
	private int mType;
	public Activity mActivity;

	public MyAlbumEditView(Context context) {
		this(context, null);
	}

	public MyAlbumEditView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyAlbumEditView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context, attrs);
		isEditable = true;
	}

	private void initView(Context context, AttributeSet attrs) {
		View view = View.inflate(context, R.layout.my_album_editview, this);
		view_title_tv = (TextView) view.findViewById(R.id.view_title_tv);
		gridview = (GridView) view.findViewById(R.id.notepad_noScrollgridview);
		gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyAlbumEditView);
		CharSequence title = a.getText(R.styleable.MyAlbumEditView_albumTitle);  //标题内容
		float titleWidth = a.getDimension(R.styleable.MyAlbumEditView_albumTitlewidth, 0); //标题长度
		int numColumns = a.getInt(R.styleable.MyAlbumEditView_numColumns, 3); //gridview一行展示图片数
		boolean noDivider = a.getBoolean(R.styleable.MyAlbumEditView_noDivider, false);
		imgSum = a.getInt(R.styleable.MyAlbumEditView_imgSum, 9); //gridview总图片数
		mType = a.getInt(R.styleable.MyAlbumEditView_type, 0); //0 是actuivity  1 是FragmentActivity

		view_title_tv.setText(title);
		gridview.setNumColumns(numColumns);

		if (noDivider) {
//			view_divider.setVisibility(GONE);
		}

		if (TextUtils.isEmpty(title)) {
			view_title_tv.setVisibility(View.GONE);
		}

		if (titleWidth > 0) {
			view_title_tv.setWidth((int) titleWidth);
		}
		a.recycle();
		if (adapter == null) {
			adapter = new SelectorPicGridAdapter(imgSum);
			gridview.setAdapter(adapter);
			gridview.setOnItemClickListener(this);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (!isEditable) {
			return;
		}
		if (position == (adapter.getImages() != null ? adapter.getImages().size() : 0)) {
			if (position < imgSum) {
				showItemAlert(parent.getContext());
			}
		} else {
			String path = adapter.getImages().get(position);
			PhotoBrowserActivity.browser(parent.getContext(), position, path);
		}
	}

	@Override
	public void onTakePicturesResult(List<String> pathList) {
		if (callback != null) {
			callback.onTakePicturesResult(pathList);
		} else {
			addData(pathList, isEditable);
		}
	}

	private long createTime;

	public void initData(PhotoInfo info) {
		createTime = info.getCreateDate().getTime();
		initData(info.getPics(), true, photos);
	}


	public void initData(PhotoInfo info, List<PhotoInfo> list) {
		this.createTime = info.getCreateDate().getTime();
		this.photos = list;
		initData(info.getPics(), true, photos);
	}

	public void initData(List<String> paths, boolean edit) {
		adapter.setImages(paths);
		adapter.setData(createTime, this.hashCode());
		adapter.notifyDataSetChanged();
		isEditable = edit;
	}

	public void initData(List<String> paths, boolean edit, List<PhotoInfo> list) {
		adapter.setImages(paths);
		adapter.setListPhotos(list);
		adapter.setData(createTime, this.hashCode());
		adapter.notifyDataSetChanged();
		isEditable = edit;
	}

	public void addData(List<String> photos, boolean edit) {
		final List<String> pics = getAllSelectImage();
		pics.addAll(Collections2.filter(photos, new Predicate<String>() {
			@Override
			public boolean apply(@Nullable String input) {
				return !TextUtils.isEmpty(input) && !pics.contains(input);
			}
		}));
		adapter.setImages(pics);
		adapter.notifyDataSetChanged();
		isEditable = edit;
	}

	public List<String> getAllSelectImage() {
		return adapter.getImages();
	}

	public String getAllSelectImageString() {
		final List<String> pics = getAllSelectImage();
		return Joiner.on(",").join(Iterables.filter(pics, new Predicate<String>() {
			@Override
			public boolean apply(@Nullable String input) {
				return !TextUtils.isEmpty(input);
			}
		}));
	}

	private void showItemAlert(Context context) {
		showPickPictureDialog(imgSum - adapter.getImages().size(), new PhotoPicker.Callback() {
			@Override
			public void onTakePicturesResult(List<String> pathList) {
				adapter.getImages().addAll(pathList);
				adapter.notifyDataSetChanged();
			}
		});
	}

	private PhotoPicker.Callback callback;

	public void setCallback(PhotoPicker.Callback callback) {
		this.callback = callback;
	}

	/**
	 * 显示普通图片选择框
	 * @param num
	 * @param callback
	 */
	public final void showPickPictureDialog(final int num, @NonNull final PhotoPicker.Callback callback) {
		PhotoPicker.getInstance().showPickPictureDialog(mActivity, false, num, callback);
	}
}
