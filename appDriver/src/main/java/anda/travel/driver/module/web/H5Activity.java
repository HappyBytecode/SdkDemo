package anda.travel.driver.module.web;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.luck.picture.lib.animators.AnimationType;
import com.luck.picture.lib.basic.PictureSelectionModel;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.network.RetrofitRequestTool;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.baselibrary.utils.SP;
import anda.travel.driver.baselibrary.utils.TypeUtil;
import anda.travel.driver.baselibrary.utils.file.FileUtil;
import anda.travel.driver.baselibrary.utils.security.Base64;
import anda.travel.driver.baselibrary.view.SweetAlert.SweetAlertDialog;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.data.entity.JSInterfaceEntity;
import anda.travel.driver.module.vo.FeedbackImgVo;
import anda.travel.driver.pictureselect.GlideEngine;
import anda.travel.driver.pictureselect.ImageFileCompressEngine;
import anda.travel.driver.pictureselect.MeSandboxFileEngine;
import anda.travel.driver.util.PhotoSelectorDialog;
import anda.travel.driver.util.SysConfigUtils;
import anda.travel.driver.util.zip.ZipManager;
import anda.travel.driver.widget.LollipopFixedWebView;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * 用于加载网页的Activity(比如条款页)
 */
public class H5Activity extends BaseActivity {

    private static final String CROP_FILE_PATH = Environment.getExternalStorageDirectory() +
            File.separator + "ANDA" + File.separator + "crop" + File.separator;
    private static final String KEY_TOKEN = "RetrofitRequestTool#KEY_TOKEN";
    private SweetAlertDialog mLoadingDialog;
    @SuppressLint("StaticFieldLeak")
    public static Context context;

    /**
     * url为需要展示的网页链接
     * <p/>
     * title为头部名称
     */
    public static void actionStart(Context context, String url, String title) {
        actionStart(context, url, title, false, 0, "");
    }

    public static void actionStart(Context context, String url, String title, int type) {
        actionStart(context, url, title, false, type, "");
    }

    public static void actionStart(Context context, String url, String title, String uuid) {
        actionStart(context, url, title, false, 0, uuid);
    }

    private static void actionStart(Context context, String url, String title, boolean hideTitle, int type, String uuid) {
        Intent intent = new Intent(context, H5Activity.class);
        intent.putExtra("url", TypeUtil.getValue(url));
        intent.putExtra("title", TypeUtil.getValue(title));
        intent.putExtra("hideTitle", hideTitle);
        intent.putExtra("type", type);
        intent.putExtra("uuid", uuid);
        context.startActivity(intent);
    }

    public static Intent getIntent(Context context, String url, String title) {
        Intent intent = new Intent(context, H5Activity.class);
        intent.putExtra("url", TypeUtil.getValue(url));
        intent.putExtra("title", TypeUtil.getValue(title));
        return intent;
    }

    private String mUrl = ""; // 条款页链接
    private String mUuid = "";
    private LollipopFixedWebView webView;
    private ProgressBar pBar;
    private View mLayoutTitle, mImgHeadBack, mLayoutLoadFail;
    private View mTvReload;
    private TextView mTvHeadTitle;
    private TextView mTvRight;
    private CompositeSubscription mDisposable;
    private int type;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.hxyc_activity_h5);
        context = getApplicationContext();
        FileUtil.makeDirs(ZipManager.ZIP_UNPACK_PATH);
        findView();
        getExtraData();
        initClickListener();
        initWebView();
        setWebViewListener();
        WebViewCacheInterceptorInst.getInstance().loadUrl(webView, mUrl);
        mDisposable = new CompositeSubscription();
    }

    private void findView() {
        mLayoutTitle = findViewById(R.id.layout_title);
        mTvHeadTitle = findViewById(R.id.tv_head_title);
        mImgHeadBack = findViewById(R.id.img_head_back);
        mTvRight = findViewById(R.id.tv_right);
        pBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.webView);
        mLayoutLoadFail = findViewById(R.id.layout_load_fail);
        mTvReload = findViewById(R.id.tv_reload);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    private void getExtraData() {
        mUrl = getIntent().getStringExtra("url");
        mUuid = getIntent().getStringExtra("uuid");
        boolean hideTitle = getIntent().getBooleanExtra("hideTitle", false);
        if (hideTitle) {
            mLayoutTitle.setVisibility(View.GONE);
        }
        type = getIntent().getIntExtra("type", 0);
        if (type == IConstants.RANK_LIST) {
            mTvRight.setVisibility(View.VISIBLE);
        } else if (type == IConstants.WEB_CUSTOMER) {
            mTvRight.setCompoundDrawables(null, null, null, null);
            mTvRight.setTextColor(Color.parseColor("#000000"));
            mTvRight.setTextSize(16);
            mTvRight.setVisibility(View.VISIBLE);
            mTvRight.setText("客服");
        }
        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            setWebViewTitle(title);
        }
    }

    private void initClickListener() {
        mImgHeadBack.setOnClickListener(view -> {
            back(); // 页面回退 或 关闭界面
        });
        mTvReload.setOnClickListener(view -> {
            if (webView != null) {
                webView.reload(); // 刷新页面
            }
        });
        mTvRight.setOnClickListener(view -> {
            switch (type) {
                case IConstants.RANK_LIST:
                    if (webView != null) {
                        webView.loadUrl("javascript:screenShot()");
                    }
                    break;
                case IConstants.WEB_CUSTOMER:
                    SysConfigUtils.get().dialServerPhone(this);
                    break;
            }
        });
    }

    private void initWebView() {
        WebSettings settings = webView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不加载缓存
        // 支持视频播放
        settings.setPluginState(WebSettings.PluginState.ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // 设置支持JS
        settings.setJavaScriptEnabled(true);
        //增加与js的交互
        initNativeObject();
        // // 网页自动适配屏幕大小
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        // // 设置可放大缩小
        settings.setSupportZoom(false);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
    }

    private void setWebViewListener() {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // 监听网页加载进度
                pBar.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                take();
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                take();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                take();
            }

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                mUploadCallbackAboveL = filePathCallback;
                take();
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                Timber.d("shouldOverrideUrlLoading: url->" + url);
                WebViewCacheInterceptorInst.getInstance().loadUrl(webView, request.getUrl().toString());
                return true;
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return WebViewCacheInterceptorInst.getInstance().interceptRequest(request);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return WebViewCacheInterceptorInst.getInstance().interceptRequest(url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoadSuccess();
                pBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
                view.loadUrl("javascript:setWebViewFlag()");
                String appid = RetrofitRequestTool.getAppid();
                String token = SP.getInstance(getApplicationContext()).getString(KEY_TOKEN);
                JSInterfaceEntity entity = new JSInterfaceEntity();
                entity.setAppid(appid);
                entity.setToken(token);
                if (!TextUtils.isEmpty(mUuid)) {
                    entity.setUuid(mUuid);
                }
                String jsonString = JSON.toJSONString(entity);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript("javascript:getParamsByJson('" + jsonString + "')", null);
                } else {
                    webView.loadUrl("javascript:getParamsByJson('" + jsonString + "')");
                }
                String title = view.getTitle();
                if (!TextUtils.isEmpty(title)) {
                    setWebViewTitle(title);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                WebViewCacheInterceptorInst.getInstance().loadUrl(webView, mUrl);
                return true;
            }
        });
        webView.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                back(); // 页面回退 或 关闭界面
                return true;
            } else {
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        webView.onResume();
        super.onResume();
        onReceiveValueNull(); //为避免阻塞添加
    }

    @Override
    protected void onPause() {
        webView.onPause();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //重置监听，避免内存泄漏
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        if (mDisposable != null) {
            mDisposable.clear();
            mDisposable = null;
        }
    }

    /* ***** 20171130 新增 ***** */

    //页面回退 或 关闭界面
    private void back() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    //显示加载失败视图
    private void showLoadError() {
        mLayoutLoadFail.setVisibility(View.VISIBLE);
        webView.setEnabled(false);
    }

    //显示加载失败视图
    private void showLoadSuccess() {
        mLayoutLoadFail.setVisibility(View.GONE);
        webView.setEnabled(true);
    }

    /* ***** 20171130 调整：处理图片选择和裁剪 ***** */

    private String mViewId;
    private int mCropAspectX;
    private int mCropAspectY;

    private ValueCallback<Uri> mUploadMessage;// 表单的数据信息
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private Uri imageUri;

    private void onReceiveValueNull() {
        if (mUploadCallbackAboveL != null) {
            mUploadCallbackAboveL.onReceiveValue(null);
            mUploadCallbackAboveL = null;
        } else if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
            mUploadMessage = null;
        }
    }

    private void initNativeObject() {
        /* 监听事件，控制Loading的显示和隐藏 */
        NativeObject nativeObject = new NativeObject(this);
        webView.addJavascriptInterface(nativeObject, "NativeObject");
        nativeObject.setLisenter((isLoading, notice) -> {
            if (isLoading) { //显示
                if (mLoadingDialog == null) {
                    mLoadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                    mLoadingDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
                    mLoadingDialog.setTitleText(notice);
                }
                mLoadingDialog.setCancelable(true);
                mLoadingDialog.show();
            } else { //隐藏
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
        /* 增加监听 */
        nativeObject.setImageListener((viewId, scale) -> {
            mViewId = viewId; //记录viewId
            mCropAspectX = 1;
            mCropAspectY = 1;
            try {
                if (!TextUtils.isEmpty(scale)) {
                    String[] split = scale.split(":");
                    if (split.length >= 2) { //设置裁剪比例
                        mCropAspectX = Integer.parseInt(split[0]);
                        mCropAspectY = Integer.parseInt(split[1]);
                        Timber.e("cropAspectX = " + mCropAspectX + " | cropAspectY = " + mCropAspectY);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            new PhotoSelectorDialog(this, type -> {
                if (type == PhotoSelectorDialog.SelectPhotoType.FROM_ALBUM) {
                    PhotoSelectorDialog.singleAlbum(this);
                } else if (type == PhotoSelectorDialog.SelectPhotoType.TAKE_PHOTO) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, PhotoSelectorDialog.PERMISSION_ALLOWED);
                        } else {
                            PhotoSelectorDialog.photograph(this);
                        }
                    } else {
                        PhotoSelectorDialog.photograph(this);
                    }
                }
            }).show();
        });
    }

    /**
     * 选择图片或拍照，并编辑
     */
    private void take() {
        PictureSelectionModel selectionModel = PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setSelectorUIStyle(new PictureSelectorStyle())
                .setCompressEngine(new ImageFileCompressEngine())
                .setSandboxFileEngine(new MeSandboxFileEngine())
                .isDisplayTimeAxis(true)
                .isDisplayCamera(true)
                .setSelectionMode(SelectModeConfig.SINGLE)
                .setMaxSelectNum(1)
                .setRecyclerAnimationMode(AnimationType.DEFAULT_ANIMATION);
        selectionModel.forResult(new OnResultCallbackListener<LocalMedia>() {
            @Override
            public void onResult(ArrayList<LocalMedia> result) {
                if (!result.isEmpty()) {
                    imageUri = null; //重置
                    result.size();
                    LocalMedia info = result.get(0);
                    String photoPath;
                    if (info.isCompressed()) {
                        photoPath = info.getCompressPath();
                    } else {
                        photoPath = info.getPath();
                    }
                    if (!TextUtils.isEmpty(photoPath) && PictureMimeType.isContent(photoPath)) {
                        imageUri = Uri.fromFile(new File(photoPath));
                    }
                    if (mUploadCallbackAboveL != null) {
                        Uri[] uris = imageUri == null ? null : new Uri[]{imageUri};
                        mUploadCallbackAboveL.onReceiveValue(uris);
                        mUploadCallbackAboveL = null;
                    } else if (mUploadMessage != null) {
                        mUploadMessage.onReceiveValue(imageUri);
                        mUploadMessage = null;
                    }
                }
            }

            @Override
            public void onCancel() {
            }
        });
    }

    private void cropPhoto(String pathName) {
        if (TextUtils.isEmpty(pathName)) {
            return;
        }
        File file = new File(pathName);
        if (!file.exists()) {
            return;
        }
        Uri uri = Uri.fromFile(file);

        String targetPath = CROP_FILE_PATH + System.currentTimeMillis() + ".png";
        FileUtil.makeFolders(targetPath);
        UCrop uCrop = UCrop.of(uri, Uri.parse(targetPath))
                .useSourceImageAspectRatio()
//                .withAspectRatio(mCropAspectX, mCropAspectY)
                .withMaxResultSize(1000, 1000);
//        if (mViewId != null && mViewId.equals("idcard_pic")) {
        UCrop.Options options = new UCrop.Options();
        options.setFreeStyleCropEnabled(true);
        options.setAllowedGestures(UCropActivity.ALL, UCropActivity.ALL, UCropActivity.ALL);
        options.setToolbarCancelDrawable(R.drawable.icon_cancel_text);
        options.setToolbarCropDrawable(R.drawable.icon_sure_text);
        options.setToolbarWidgetColor(Color.WHITE);
        uCrop.withOptions(options);
//        }
        uCrop.start(this);
    }

    private void upload(Uri resultUri) {
        String path = resultUri.getPath();
        Timber.e(MessageFormat.format("path = {0}", path));
        Observable.just(path)
                .compose(RxUtil.applySchedulers())
                .map(imgFile -> {
                    Timber.e(MessageFormat.format("photoPath = {0}", imgFile));
                    Timber.e(MessageFormat.format("fileSize = {0}", FileUtil.getFileSize(imgFile)));
                    //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
                    InputStream in;
                    byte[] data = null;
                    //读取图片字节数组
                    try {
                        in = new FileInputStream(imgFile);
                        data = new byte[in.available()];
                        in.read(data);
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // 获取文件后缀
                    int index = imgFile.lastIndexOf(".");
                    String suffix = imgFile.substring(index + 1);
                    // 对字节数组Base64编码
                    return "data:image/" + suffix + ";base64," + Base64.encode(data);
                })
                .subscribe(str -> {
                    webView.loadUrl("javascript:backViewImage('" + mViewId + "','" + str + "')");
                }, ex -> {
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PhotoSelectorDialog.PERMISSION_ALLOWED) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    PhotoSelectorDialog.photograph(this);
                    return;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String pathName;
        switch (requestCode) {
            case PhotoSelectorDialog.REQUEST_PAI_ZHAO:
                try {
                    pathName = PhotoSelectorDialog.getImagePathName();
                    cropPhoto(pathName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PhotoSelectorDialog.REQUEST_XUAN_ZE:
                if (data != null) {
                    try {
                        Uri uri = data.getData();
                        if (Build.VERSION.SDK_INT <= 18) {
                            pathName = PhotoSelectorDialog.getAbsoluteImagePath(this,
                                    data.getData());
                        } else {
                            // 针对4.4版本以上的解决
                            pathName = PhotoSelectorDialog.getPath(this, uri);
                        }
                        cropPhoto(pathName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = UCrop.getOutput(data);
                    upload(resultUri);
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                    toast(cropError.getMessage());
                }
                break;
        }
    }

    public void setWebViewTitle(String title) {
        String DataTitle = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(DataTitle)) {
            title = DataTitle;
        }
        if (TextUtils.isEmpty(title)) {
            title = "";
        }
        mTvHeadTitle.setText(title);
    }
}
