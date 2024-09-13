package anda.travel.driver.module.web;

import android.content.ClipboardManager;
import android.content.Context;
import android.webkit.JavascriptInterface;

import anda.travel.driver.baselibrary.utils.ToastUtil;
import timber.log.Timber;

class NativeObject {

    private LoadingListener mLisenter;
    private ImageListener mImageListener;
    private final Context mContext;

    public NativeObject(Context context) {
        this.mContext = context;
    }

    public void setLisenter(LoadingListener lisenter) {
        mLisenter = lisenter;
    }

    public void setImageListener(ImageListener imageListener) {
        mImageListener = imageListener;
    }

    @JavascriptInterface
    public void loading(String... args) {
        String type = args[0]; // 0 关闭加载效果  1 显示加载效果
        String notice = args[1]; // 提示语
        Timber.e("type = " + type + "  | notice = " + notice);
        if (mLisenter != null) mLisenter.onLoad("1".equals(type), notice);
    }

    @JavascriptInterface
    public void UploadImage(String... args) {
        Timber.e("触发UploadImage～");
        String viewId = args[0];
        String scale = args[1];
        Timber.e("viewId = " + viewId + " | scale = " + scale);
        if (mImageListener != null) mImageListener.onImageClick(viewId, scale);
    }

    @JavascriptInterface
    public void cpoyUrl(String... args) {
        //20181214复制功能
        String content = args[0];
        ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content);
        ToastUtil.getInstance().toast("复制链接成功");
    }

    public interface LoadingListener {
        void onLoad(boolean isLoading, String notice); //true－显示loading，false－隐藏loading
    }

    public interface ImageListener {
        void onImageClick(String viewId, String scale); //viewId－控件id，scale－图片裁剪比例
    }

}
