package anda.travel.driver.module.web;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.luck.picture.lib.animators.AnimationType;
import com.luck.picture.lib.basic.PictureSelectionModel;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.baselibrary.utils.TypeUtil;
import anda.travel.driver.baselibrary.utils.file.FileUtil;
import anda.travel.driver.baselibrary.utils.security.Base64;
import anda.travel.driver.baselibrary.view.HeadView;
import anda.travel.driver.baselibrary.view.SweetAlert.SweetAlertDialog;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.pictureselect.GlideEngine;
import anda.travel.driver.pictureselect.ImageFileCompressEngine;
import anda.travel.driver.pictureselect.MeSandboxFileEngine;
import anda.travel.driver.util.PhotoSelectorDialog;
import rx.Observable;

/**
 * 用于加载网页的Activity(比如条款页)
 */
public class WebActivity extends BaseActivity {

    private static final String CROP_FILE_PATH = Environment.getExternalStorageDirectory() +
            File.separator + "ANDA" + File.separator + "crop" + File.separator;
    private SweetAlertDialog mLoadingDialog;

    @Inject
    UserRepository mUserRepository;

    /**
     * url为需要展示的网页链接
     * <p/>
     * title为头部名称
     */
    public static void actionStart(Context context, String url, String title) {
        actionStart(context, url, title, false);
    }

    public static void actionStart(Context context, String url, String title, boolean hideTitle) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("url", TypeUtil.getValue(url));
        intent.putExtra("title", TypeUtil.getValue(title));
        intent.putExtra("hideTitle", hideTitle);
        context.startActivity(intent);
    }

    public static Intent getIntent(Context context, String url, String title) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("url", TypeUtil.getValue(url));
        intent.putExtra("title", TypeUtil.getValue(title));
        return intent;
    }

    private String mUrl = ""; // 条款页链接
    private WebView webView;
    private ProgressBar pBar;
    private View mLayoutLoadFail;
    private View mTvReload;
    private boolean mLoadFail; //是否加载失败

    private HeadView mHeadView;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.hxyc_activity_web);
        findView();
        getExtraData();
        initClickListener();
        initWebView();
        setWebViewListener();
        webView.loadUrl(mUrl); // 加载网页
    }

    private void findView() {
        mHeadView = findViewById(R.id.head_view);
        pBar = findViewById(R.id.progressBar);
        FrameLayout mWebContainer = findViewById(R.id.web_container);
        webView = new WebView(HxClientManager.getInstance().application.getApplicationContext());
        mWebContainer.addView(webView);
        mLayoutLoadFail = findViewById(R.id.layout_load_fail);
        mTvReload = findViewById(R.id.tv_reload);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    private void getExtraData() {
        mUrl = getIntent().getStringExtra("url");
        boolean hideTitle = getIntent().getBooleanExtra("hideTitle", false);
        String title = getIntent().getStringExtra("title");
        if (TextUtils.isEmpty(title)) {
            title = "";
        }
        if (hideTitle) {
            mHeadView.setVisibility(View.GONE);
        }
        mHeadView.setTitle(title);
    }

    private void initClickListener() {
        mHeadView.getLeftView().setOnClickListener(view -> {
            back(); // 页面回退 或 关闭界面
        });
        mTvReload.setOnClickListener(view -> {
            if (webView != null) {
                webView.reload(); // 刷新页面
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

            //For Android  >= 41
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

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoadSuccess();
                pBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pBar.setVisibility(View.GONE);
                if (mLoadFail) {
                    showLoadError();
                } else {
                    showLoadSuccess();
                }
                mLoadFail = false;
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                mLoadFail = true;
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                mLoadFail = true;
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 监听网址变化
                if (url.equals("javascripts:void(0);")) {
                    return true;
                } else if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else if (url.contains("http://goback.html")) {
                    finish(); //关闭界面
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
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
        ////2019 0812 zhangyan 内存泄漏处理
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ViewGroup parent = (ViewGroup) webView.getParent();
            if (parent != null) {
                parent.removeView(webView);
            }
            webView.removeAllViews();
            webView.stopLoading();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
            webView = null;
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
    private final static int FILECHOOSER_RESULTCODE = 1;// 表单的结果回调</span>
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
        /////// 2019/8/12 zhangyan 内存泄漏
        NativeObject nativeObject = new NativeObject(HxClientManager.getInstance().application.getApplicationContext());
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
                .withAspectRatio(mCropAspectX, mCropAspectY)
                .withMaxResultSize(1000, 1000);
        if (mViewId != null && mViewId.equals("idcard_pic")) {
            UCrop.Options options = new UCrop.Options();
            options.setFreeStyleCropEnabled(true);
            uCrop.withOptions(options);
        }
        uCrop.start(this);
    }

    private void upload(Uri resultUri) {
        String path = resultUri.getPath();
        Observable.just(path)
                .compose(RxUtil.applySchedulers())
                .map(imgFile -> {
                    //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
                    InputStream in = null;
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
                    String suffix = imgFile.substring(index + 1, imgFile.length());
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
        super.onActivityResult(requestCode, resultCode, data);
    }

}
