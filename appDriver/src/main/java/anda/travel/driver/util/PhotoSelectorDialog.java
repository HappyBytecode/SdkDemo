package anda.travel.driver.util;

import static anda.travel.driver.baselibrary.utils.file.UriToPathUtil.getDataColumn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.Date;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.DisplayUtil;
import anda.travel.driver.baselibrary.utils.ToastUtil;
import anda.travel.driver.baselibrary.view.dialog.ExSweetAlertDialog;
import timber.log.Timber;

public class PhotoSelectorDialog extends ExSweetAlertDialog {

    public static final int REQUEST_PAI_ZHAO = 1;// startactivityforresult 记录照片

    public static final int REQUEST_XUAN_ZE = 2;// startactivityforresult

    private static final int CROP_IMAGE = 5003;
    public static final int PERMISSION_ALLOWED = 2000;

    private static String pathName;

    public PhotoSelectorDialog(Context context, PhotoSelectorCallback callback) {
        super(context, R.layout.hxyc_dialog_photo_selector);
        setCanceledOnTouchOutside1(false);
        setCancelable1(true);
        setAnimIn(R.anim.dialog_selecter_in);
        setAnimOut(R.anim.dialog_selecter_out);
        setWidth(DisplayUtil.getScreenW(getContext()));
        setHeight(DisplayUtil.getScreenH(getContext()) - DisplayUtil.getStatusHeight(getContext()));
        setListener(R.id.dialog_bg, ExSweetAlertDialog::dismiss);
        setListener(R.id.dialog_cancel_button, ExSweetAlertDialog::dismiss);
        setListener(R.id.dialog_from_album_button, dialog -> {
            dialog.dismiss();
            callback.selected(SelectPhotoType.FROM_ALBUM);
        });
        setListener(R.id.dialog_take_photo_button, dialog -> {
            dialog.dismiss();
            callback.selected(SelectPhotoType.TAKE_PHOTO);
        });
    }

    public interface PhotoSelectorCallback {
        void selected(SelectPhotoType type);
    }

    public enum SelectPhotoType {
        FROM_ALBUM,
        TAKE_PHOTO
    }

    /**
     * 获取保存图片的完整路径:例子(path\name.jpg)
     */
    public static String getImagePathName() {
        return pathName;
    }

    public static void singleAlbum(final Activity activity) {// 单选图片
        Intent intent;
        intent = new Intent(Intent.ACTION_GET_CONTENT);
//        if (Build.VERSION.SDK_INT <= 18) {
//            // ToastUtils.getInstance().toast("小");
//            intent = new Intent(Intent.ACTION_GET_CONTENT);
//        } else {
//            // ToastUtils.getInstance().toast("大");
//            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        }
        intent.setType("image/*");
        activity.startActivityForResult(intent, REQUEST_XUAN_ZE);
    }

    public static void photograph(final Activity activity) {// 拍照获得图片
        Intent intent;
        String picName = getImageName();
        File dir = new File(Environment.getExternalStorageDirectory()
                + "/ANDA/" + "photo");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, picName);
        pathName = file.getAbsolutePath();
        Timber.e("picName:" + picName);
        // 下面这句指定调用相机拍照后的照片存储的路径
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            uri = FileProvider.getUriForFile(activity, activity.getPackageName(), file);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, REQUEST_PAI_ZHAO);
    }

    private static String getImageName() {
        return "img" + new Date().getTime() + ".png";
    }

    // 取到绝对路径
    public static String getAbsoluteImagePath(Activity content, Uri uri) {
        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = content.managedQuery(uri, proj, // Which columns
                // to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * 拍照后，照片剪辑
     *
     * @param file
     */
    public static void doCropPhoto(final Activity activity, File file) {
        try {

            Uri uri;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                uri = FileProvider.getUriForFile(activity, activity.getPackageName(), file);
            } else {
                uri = Uri.fromFile(file);
            }
            // 启动gallery去剪辑这个照片
            final Intent intent = getCropImageIntent(uri);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            activity.startActivityForResult(intent, CROP_IMAGE);
        } catch (Exception e) {
            ToastUtil.getInstance().toast("失败");
        }
    }

    /**
     * Constructs an intent for image cropping. 调用图片剪辑程序 剪裁后的图片跳转到新的界面
     */
    private static Intent getCropImageIntent(Uri photoUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        return intent;
    }

    /**
     * 为解决4.4及以上，图片路径获取不到的问题
     */

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }
}
