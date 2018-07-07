package com.ctkj.xj_app.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ctkj.xj_app.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Collection;

import uk.co.senab.photoview.PhotoView;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

public class PhotoBrowserActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private static final String PHOTOS = "PHOTOS";
    private static final String POSITION = "POSITION";

    private final SparseArray<SoftReference<View>> cache = new SparseArray<>();
    private ViewPager viewPager;
    private String[] images;
    private int index;

    public static void browser(Context context, int startPosition, String... urls) {
        if (startPosition + 1 > urls.length) {
            startPosition = 0;
        }
        Intent intent = new Intent(context, PhotoBrowserActivity.class);
        intent.putExtra(POSITION, startPosition);
        intent.putExtra(PHOTOS, urls);
        context.startActivity(intent);
    }

    public static void browser(Context context, int startPosition, Collection<String> urls) {
        browser(context, startPosition, urls.toArray(new String[urls.size()]));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            images = getIntent().getStringArrayExtra(PHOTOS);
            index = getIntent().getIntExtra(POSITION, 0);
        } else {
            images = savedInstanceState.getStringArray(PHOTOS);
            index = savedInstanceState.getInt(POSITION, 0);
        }
        this.setTitle("预览");
        viewPager = new MyViewPager(this);
        viewPager.setBackgroundColor(Color.BLACK);
        this.setContentView(viewPager);

        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(new MyPhotoPagerAdapter());
        viewPager.setCurrentItem(index);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(PHOTOS, images);
        outState.putInt(POSITION, viewPager.getCurrentItem());
    }


    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }


    private static Uri getUri(@NonNull String uri) {
        if (uri.startsWith("http")) {
            return Uri.parse(uri);
        } else {
            return Uri.fromFile(new File(uri));
        }
    }


    private class MyPhotoPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return images != null ? images.length : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final Context context = container.getContext();

            View convertView = null;
            SoftReference<View> reference = cache.get(position);
            if (reference != null && reference.get() != null) {
                convertView = reference.get();
            }
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_view_photo, null);
                cache.put(position, new SoftReference<>(convertView));
            }
            PhotoView imageView = ViewHolder.get(convertView, R.id.image);
            final Uri uri = getUri(images[position]);
            Picasso.with(context).load(uri)
                    .memoryPolicy(NO_CACHE, NO_STORE)
                    .placeholder(R.drawable.photoview_placeholder).error(R.drawable.nophotos)
                    .config(Bitmap.Config.RGB_565)
                    .into(imageView);
            container.addView(convertView);
            return convertView;
        }

    }


    private static class MyViewPager extends ViewPager {

        public MyViewPager(Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            try {
                return super.onTouchEvent(ev);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            try {
                return super.onInterceptTouchEvent(ev);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
            return false;
        }

    }

}
