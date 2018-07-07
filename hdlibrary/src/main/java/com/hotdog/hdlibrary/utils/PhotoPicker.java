package com.hotdog.hdlibrary.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.google.common.collect.Maps;
import com.hotdog.hdlibrary.encrypt.Digest;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class PhotoPicker {

    /**
     * 获取图片的回调
     */
    public interface Callback {

        void onTakePicturesResult(List<String> pathList);

    }

    private static PhotoPicker instance = new PhotoPicker();

    public static PhotoPicker getInstance() {
        return instance;
    }

    private static final int REQUEST_CODE_CAMERA = 9991;
    private static final int REQUEST_CODE_CAMERA_2_CROP = REQUEST_CODE_CAMERA + 1;
    private static final int REQUEST_CODE_GALLERY = REQUEST_CODE_CAMERA_2_CROP + 1;
    private static final int REQUEST_CODE_GALLERY_2_CROP = REQUEST_CODE_GALLERY + 1;

    private final Map<Activity, Pair<Callback, Uri>> ref = Maps.newHashMap();

    private PhotoPicker() {

    }

    @Nullable
    private Callback getCallback(Activity activity) {
        Pair<Callback, Uri> pair = ref.get(activity);
        if (pair == null) {
            return null;
        } else {
            return pair.first;
        }
    }

    @Nullable
    private Uri getUri(Activity activity) {
        Pair<Callback, Uri> pair = ref.get(activity);
        if (pair == null) {
            return null;
        } else {
            return pair.second;
        }
    }

    @SuppressWarnings("unchecked")
    public void onActivityResult(Activity context, int requestCode, int resultCode, Intent data) {
        final Callback callbackRef = getCallback(context);
        if (callbackRef == null) {
            return;
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(context, "裁剪图片出错,请重试", Toast.LENGTH_SHORT).show();
            return;
        }
        if (resultCode != RESULT_OK) {
            return;
        }

        List<String> result;
        Uri src, det;
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                src = getUri(context);
                callbackRef.onTakePicturesResult(Collections.singletonList(IOUtils.localUri2Path(context, src)));
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, src));
                break;
            case REQUEST_CODE_CAMERA_2_CROP:
                src = getUri(context);
                det = Uri.fromFile(generateRandomImageFile(context));
                UCrop.of(src, det).start(context);

                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, src));
                break;
            case REQUEST_CODE_GALLERY_2_CROP:
                result = (List<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);
                src = Uri.fromFile(new File(result.get(0)));
                det = Uri.fromFile(generateRandomImageFile(context));
                UCrop.of(src, det).start(context);
                break;
            case REQUEST_CODE_GALLERY:
                ref.remove(context);
                result = (List<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);
                callbackRef.onTakePicturesResult(result);
                break;
            case UCrop.REQUEST_CROP:
                ref.remove(context);
                det = UCrop.getOutput(data);
                callbackRef.onTakePicturesResult(Collections.singletonList(IOUtils.localUri2Path(context, det)));

                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, det));
                break;
        }
    }

    /**
     * 显示普通图片选择框
     *
     * @param num
     * @param callback
     */
    public void showPickPictureDialog(final Activity context, final boolean needCrop, final int num, @NonNull final Callback callback) {
        Uri photoUri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            photoUri = FileProvider.getUriForFile(context,
                    context.getPackageName() + ".provider",
                    generateRandomImageFile(context));
        } else {
            photoUri = Uri.fromFile(generateRandomImageFile(context));
        }
        this.ref.put(context, Pair.create(callback, photoUri));
        takePicturesFromCamera(context, needCrop);

//        final String[] items = {"本地相册", "相机拍照"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("选择图片:");
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (which == 0) {
//                    takePicturesFromGallery(context, needCrop, num);
//                } else {
//                    takePicturesFromCamera(context, needCrop);
//                }
//            }
//        }).create().show();
    }

    /**
     * 从相册中
     *
     * @param limit
     */
    public void takePicturesFromGallery(final Activity context, final boolean needCrop, final int limit, @NonNull final Callback callback) {
        this.ref.put(context, Pair.create(callback, Uri.fromFile(generateRandomImageFile(context))));
        takePicturesFromGallery(context, needCrop, limit);
    }

    /**
     * 从相册中
     *
     * @param limit
     */
    private void takePicturesFromGallery(final Activity context, final boolean needCrop, final int limit) {
        final String[] permission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (PermissionsManager.getInstance().hasAllPermissions(context, permission)) {
            boolean single = limit == 1;
            int code = needCrop ? REQUEST_CODE_GALLERY_2_CROP : REQUEST_CODE_GALLERY;
            String hint = String.format("最多选择%s张", limit);
            GalleryActivity.openActivity(context, code, new GalleryConfig.Build().singlePhoto(single).hintOfPick(hint).limitPickPhoto(limit).build());
        } else {
            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(context, permission, new PermissionsResultAction() {

                @Override
                public void onGranted() {
                    takePicturesFromGallery(context, needCrop, limit);
                }

                @Override
                public void onDenied(String permission1) {
                    Toast.makeText(context, "请允许获取权限信息，否则无法使用该功能", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }

    /**
     * 相机拍照
     */
    private void takePicturesFromCamera(final Activity context, final boolean needCrop) {
        final Uri photoTempUri = getUri(context);
        final String[] permission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (PermissionsManager.getInstance().hasAllPermissions(context, permission)) {
            int code = needCrop ? REQUEST_CODE_CAMERA_2_CROP : REQUEST_CODE_CAMERA;
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoTempUri);
            context.startActivityForResult(intent, code);
        } else {
            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(context, permission, new PermissionsResultAction() {

                @Override
                public void onGranted() {
                    takePicturesFromCamera(context, needCrop);
                }

                @Override
                public void onDenied(String permission1) {
                    Toast.makeText(context, "请允许获取权限信息，否则无法使用该功能", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }

    /**
     * 生成一个随机命名的图片文件(还没有创建)
     *
     * @return
     */
    private static File generateRandomImageFile(Context context) {
        String name = Digest.md5Hex(UUID.randomUUID().toString().getBytes()).toUpperCase();
        return new File(StorageUtils.getImageDirectory(context), name + ".jpg");
//        String name = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//                .format(new GregorianCalendar().getTime());
//        return new File(Environment.getExternalStorageDirectory().getPath()+"巡检/照片/",name+".jpg");
    }


}
