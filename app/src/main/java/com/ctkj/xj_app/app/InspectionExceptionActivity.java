package com.ctkj.xj_app.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.ctkj.xj_app.R;
import com.ctkj.xj_app.app.base.BaseActivity;
import com.ctkj.xj_app.bean.InspectException;
import com.ctkj.xj_app.bean.InspectItem;
import com.ctkj.xj_app.util.CoordTransformUtils;
import com.ctkj.xj_app.widget.MyAlbumEditView;
import com.hotdog.hdlibrary.core.HDToast;
import com.hotdog.hdlibrary.encrypt.Digest;
import com.hotdog.hdlibrary.utils.PhotoPicker;
import com.hotdog.hdlibrary.utils.StorageUtils;

import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.media.ThumbnailUtils.OPTIONS_RECYCLE_INPUT;
import static android.media.ThumbnailUtils.extractThumbnail;

public class InspectionExceptionActivity extends BaseActivity {

    public final static int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 111;

    @BindView(R.id.maev_image)
    MyAlbumEditView mMyAlbumEditView;
    @BindView(R.id.iv_video_preview)
    ImageView mVideoPreviewImage;
    @BindView(R.id.iv_take_video)
    ImageView mTakeVideoImage;
    @BindView(R.id.layout_video_preview)
    RelativeLayout mVideoPreviewLayout;
    @BindView(R.id.et_exception)
    EditText exception_et;

    private String mVideoPath;

    private String mItemName;
    private Integer mItemId;
    private Integer mFacilityId;
    private LatLng mPosition = null;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private InspectItem mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_exception);
        ButterKnife.bind(this);
        mMyAlbumEditView.mActivity = this;
//        mItemName = getIntent().getStringExtra("itemName");
        mItemId = getIntent().getIntExtra("itemId", 0);
        //数据库读取
        mItem = LitePal.where("itemId = ?",mItemId.toString()).find(InspectItem.class).get(0);
        mItemName = mItem.getName();
        mFacilityId = getIntent().getIntExtra("leafFId", 0);
        setTitle(mItemName);

        startLocate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mTakeVideoImage.setVisibility(View.GONE);
                mVideoPreviewLayout.setVisibility(View.VISIBLE);
                Bitmap previewBitmap = createVideoThumbnail(mVideoPath, MediaStore.Images.Thumbnails.MICRO_KIND);
                mVideoPreviewImage.setImageBitmap(previewBitmap);
            } else {
                HDToast.show("视频拍摄失败", this);
            }
        }
        PhotoPicker.getInstance().onActivityResult(this, requestCode, resultCode, data);
    }

    @OnClick(R.id.iv_take_video)
    public void onTakeVideo(View view) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File videoFile = generateRandomVideoFile(this);
        Uri videoUri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            videoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", videoFile);
        } else {
            videoUri = Uri.fromFile(videoFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
    }

    @OnClick(R.id.iv_video_preview)
    public void onVideoPreviewClick(View view) {
        Uri uri = Uri.parse(mVideoPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "video/mp4");
        startActivity(intent);
    }

    @OnClick(R.id.iv_delete_video)
    public void onDeleteVideoClick(View view) {
        mTakeVideoImage.setVisibility(View.VISIBLE);
        mVideoPreviewLayout.setVisibility(View.GONE);
        mVideoPath = "";
    }

    @OnClick(R.id.btn_save)
    public void onSaveBtnClick(View view) {
        Intent intent = new Intent();
        List<String> images = mMyAlbumEditView.getAllSelectImage();
        String mImagePath = "";
        for(String str : images){
            mImagePath += str+",";
        }
//        格式化时间
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        InspectException exception =
                new InspectException(mFacilityId, mItemId, mItemName, exception_et.getText().toString(), mPosition.longitude+","+mPosition.latitude, mImagePath + mVideoPath);
        setResult(RESULT_OK, intent);
        //将异常添加到数据库中
//        exception.save();
        exception.saveOrUpdate("itemId = ?",exception.getItemId().toString());
        HDToast.show("保存成功", this);
        finish();
    }

    private File generateRandomVideoFile(Context context) {
        String name = Digest.md5Hex(UUID.randomUUID().toString().getBytes()).toUpperCase();
        File videoFile = new File(StorageUtils.getVideoDirectory(context), name + ".mp4");
        //视频拍摄存储文件地址
//        String name = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//                .format(new GregorianCalendar().getTime());
//        File videoFile = new File(Environment.getExternalStorageDirectory().getPath()+"巡检/视频/"+mItemName, name+ ".mp4");
        mVideoPath = videoFile.getAbsolutePath();
        return videoFile;
    }

    public static Bitmap createVideoThumbnail(String filePath, int kind) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(-1);
        } catch (IllegalArgumentException ex) {
        } catch (RuntimeException ex) {
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
            }
        }

        if (bitmap == null) return null;

        if (kind == MediaStore.Images.Thumbnails.MINI_KIND) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int max = Math.max(width, height);
            if (max > 512) {
                float scale = 512f / max;
                int w = Math.round(scale * width);
                int h = Math.round(scale * height);
                bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
            }
        } else if (kind == MediaStore.Images.Thumbnails.MICRO_KIND) {
            bitmap = extractThumbnail(bitmap,
                    50,
                    50,
                    OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

    private void startLocate() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    public class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Double latitude = bdLocation.getLatitude();
            Double longitude = bdLocation.getLongitude();
            mPosition = CoordTransformUtils.bd09_to_wgs84(new LatLng(latitude,longitude));
        }
    }
}
