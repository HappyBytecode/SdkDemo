package anda.travel.driver.module.main.mine.help.feedback;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.animators.AnimationType;
import com.luck.picture.lib.basic.PictureSelectionModel;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.engine.UriToFileTransformEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.style.BottomNavBarStyle;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.style.TitleBarStyle;
import com.luck.picture.lib.utils.DateUtils;
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.utils.SandboxTransformUtils;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.adapter.OnClickListener;
import anda.travel.driver.baselibrary.utils.ToastUtil;
import anda.travel.driver.common.AppConfig;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.module.feedback.list.FeedBackListActivity;
import anda.travel.driver.module.main.mine.help.feedback.adapter.FeedbackImgAdapter;
import anda.travel.driver.module.main.mine.help.feedback.dagger.DaggerFeedbackComponent;
import anda.travel.driver.module.main.mine.help.feedback.dagger.FeedbackModule;
import anda.travel.driver.module.vo.FeedbackImgVo;
import anda.travel.driver.pictureselect.GlideEngine;
import anda.travel.driver.pictureselect.ImageFileCompressEngine;
import anda.travel.driver.pictureselect.MeSandboxFileEngine;
import anda.travel.driver.widget.BaseTipsDialog;
import anda.travel.driver.widget.layout.BaseLinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import top.zibin.luban.Luban;
import top.zibin.luban.OnNewCompressListener;
import top.zibin.luban.OnRenameListener;

import static org.litepal.LitePalApplication.getContext;

/**
 * 建议反馈
 */
public class FeedbackActivity extends BaseActivity implements FeedbackContract.View, TextWatcher, OnClickListener<FeedbackImgVo>, FeedbackImgAdapter.OnDeleteClickListener {

    @Inject
    FeedbackPresenter mPresenter;
    @BindView(R2.id.lay_feedback_head)
    BaseLinearLayout mLayFeedBackHead;
    @BindView(R2.id.lay_advice)
    BaseLinearLayout mLayAdvice;
    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R2.id.et_advice)
    EditText mEtAdvice;
    private FeedbackImgAdapter mFeedbackImgAdapter;
    private ArrayList<FeedbackImgVo> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_feedback);
        ButterKnife.bind(this);
        initView();

        DaggerFeedbackComponent.builder()
                .appComponent(getAppComponent())
                .feedbackModule(new FeedbackModule(this))
                .build().inject(this);
        AppConfig.setFloatView(false);
    }

    private void initView() {
        mLayFeedBackHead.setRadius(10);
        mLayFeedBackHead.setShadowColor(R.color.item_gray_primary);
        mLayFeedBackHead.setShadowElevation(16);
        mLayAdvice.setRadius(10);
        mLayAdvice.setShadowColor(R.color.item_gray_primary);
        mLayAdvice.setShadowElevation(16);
        recyclerView.setLayoutManager(new LinearLayoutManager(this
                , RecyclerView.HORIZONTAL, false));
        mEtAdvice.addTextChangedListener(this);

        mFeedbackImgAdapter = new FeedbackImgAdapter(this);

      /*  mList.add(new FeedbackImgVo(
                TextUtils.isEmpty(getArguments().getString(Constants.SCREEN_SHOT_FEEDBACK_PATH)),
                getArguments().getString(Constants.SCREEN_SHOT_FEEDBACK_PATH)));*/

        addAddPhoneImg();

        mFeedbackImgAdapter.setAll(mList);

        recyclerView.setAdapter(mFeedbackImgAdapter);
        mFeedbackImgAdapter.setOnItemClickListener(this);
        mFeedbackImgAdapter.setOnDeleteClickListener(this);
    }

    private PictureSelectorStyle getPictureSelectorStyle() {
        // 主体风格
        SelectMainStyle numberSelectMainStyle = new SelectMainStyle();
        numberSelectMainStyle.setSelectNumberStyle(true);
        numberSelectMainStyle.setPreviewSelectNumberStyle(false);
        numberSelectMainStyle.setPreviewDisplaySelectGallery(true);
        numberSelectMainStyle.setSelectBackground(R.drawable.ps_default_num_selector);
        numberSelectMainStyle.setPreviewSelectBackground(R.drawable.ps_preview_checkbox_selector);
        numberSelectMainStyle.setSelectNormalBackgroundResources(R.drawable.ps_select_complete_normal_bg);
        numberSelectMainStyle.setSelectNormalTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_53575e));
        numberSelectMainStyle.setSelectNormalText(getString(R.string.ps_send));
        numberSelectMainStyle.setAdapterPreviewGalleryBackgroundResource(R.drawable.ps_preview_gallery_bg);
        numberSelectMainStyle.setAdapterPreviewGalleryItemSize(DensityUtil.dip2px(getContext(), 52));
        numberSelectMainStyle.setPreviewSelectText(getString(R.string.ps_select));
        numberSelectMainStyle.setPreviewSelectTextSize(14);
        numberSelectMainStyle.setPreviewSelectTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
        numberSelectMainStyle.setPreviewSelectMarginRight(DensityUtil.dip2px(getContext(), 6));
        numberSelectMainStyle.setSelectBackgroundResources(R.drawable.ps_select_complete_bg);
        numberSelectMainStyle.setSelectText(getString(R.string.ps_send_num));
        numberSelectMainStyle.setSelectTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
        numberSelectMainStyle.setMainListBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_black));
        numberSelectMainStyle.setCompleteSelectRelativeTop(true);
        numberSelectMainStyle.setPreviewSelectRelativeBottom(true);
        numberSelectMainStyle.setAdapterItemIncludeEdge(false);

        // 头部TitleBar 风格
        TitleBarStyle numberTitleBarStyle = new TitleBarStyle();
        numberTitleBarStyle.setHideCancelButton(true);
        numberTitleBarStyle.setAlbumTitleRelativeLeft(true);
        numberTitleBarStyle.setTitleAlbumBackgroundResource(R.drawable.ps_album_bg);
        numberTitleBarStyle.setTitleDrawableRightResource(R.drawable.ps_ic_grey_arrow);
        numberTitleBarStyle.setPreviewTitleLeftBackResource(R.drawable.ps_ic_normal_back);

        // 底部NavBar 风格
        BottomNavBarStyle numberBottomNavBarStyle = new BottomNavBarStyle();
        numberBottomNavBarStyle.setBottomPreviewNarBarBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_half_grey));
        numberBottomNavBarStyle.setBottomPreviewNormalText(getString(R.string.ps_preview));
        numberBottomNavBarStyle.setBottomPreviewNormalTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_9b));
        numberBottomNavBarStyle.setBottomPreviewNormalTextSize(16);
        numberBottomNavBarStyle.setCompleteCountTips(false);
        numberBottomNavBarStyle.setBottomPreviewSelectText(getString(R.string.ps_preview_num));
        numberBottomNavBarStyle.setBottomPreviewSelectTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));

        PictureSelectorStyle selectorStyle = new PictureSelectorStyle();
        selectorStyle.setTitleBarStyle(numberTitleBarStyle);
        selectorStyle.setBottomBarStyle(numberBottomNavBarStyle);
        selectorStyle.setSelectMainStyle(numberSelectMainStyle);
        return selectorStyle;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @OnClick({R2.id.tv_submit, R2.id.tv_records})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_submit) {
            String advice = mEtAdvice.getText().toString().trim();
            if (!TextUtils.isEmpty(advice) && advice.length() > 100) {
                ToastUtil.getInstance().toast(R.string.feedback_max_length);
                return;
            }
            if (getImgCount() == 0 && TextUtils.isEmpty(advice)) {
                ToastUtil.getInstance().toast("反馈内容不能为空");
                return;
            }
            if (isBtnBuffering()) {
                return;
            }
            mPresenter.addFeedBack(advice, mList);
        } else if (id == R.id.tv_records) {
            Intent records = new Intent(this, FeedBackListActivity.class);
            startActivity(records);
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

        int len = editable.length();

        if (len > 100) {
            int selEndIndex = Selection.getSelectionEnd(editable);

            String str = editable.toString();
            //截取新字符串
            String newStr = str.substring(0, 100);
            mEtAdvice.setText(newStr);

            editable = mEtAdvice.getText();
            //新字符串的长度
            int newLen = editable.length();
            //旧光标位置超过字符串长度
            if (selEndIndex > newLen) {
                selEndIndex = editable.length();
            }
            //设置新光标所在的位置
            Selection.setSelection(editable, selEndIndex);
            ToastUtil.getInstance().toast(R.string.feedback_max_length);
        }
    }

    private void addAddPhoneImg() {
        boolean hasAddPhoneImg = false;
        for (FeedbackImgVo feedbackImgVo : mList) {
            if (feedbackImgVo.isShowAddImg()) {
                hasAddPhoneImg = true;
            }
        }
        if (!hasAddPhoneImg && mList.size() < 4) {
            mList.add(new FeedbackImgVo(true, null));
        }
    }

    @Override
    public void onClick(int position, View view, FeedbackImgVo item) {
        if (item.isShowAddImg()) {
            PictureSelectionModel selectionModel = PictureSelector.create(this)
                    .openGallery(SelectMimeType.ofImage())
                    .setImageEngine(GlideEngine.createGlideEngine())
                    .setSelectorUIStyle(getPictureSelectorStyle())
                    .setCompressEngine(new ImageFileCompressEngine())
                    .setSandboxFileEngine(new MeSandboxFileEngine())
                    .isDisplayTimeAxis(true)
                    .isDisplayCamera(false)
                    .setMaxSelectNum(4 - getImgCount())
                    .setRecyclerAnimationMode(AnimationType.DEFAULT_ANIMATION);
            selectionModel.forResult(new OnResultCallbackListener<LocalMedia>() {
                @Override
                public void onResult(ArrayList<LocalMedia> result) {
                    if (!result.isEmpty()) {
                        mList.remove(position);////移除之前的
                        for (LocalMedia info : result) {
                            if (info.isCompressed()) {
                                mList.add(new FeedbackImgVo(false, info.getCompressPath()));
                            } else {
                                mList.add(new FeedbackImgVo(false, info.getPath()));
                            }
                        }
                        addAddPhoneImg();
                        mFeedbackImgAdapter.setAll(mList);
                    }
                }

                @Override
                public void onCancel() {
                }
            });
        }
    }

    @Override
    public void onDeleteClick(int position) {
        mList.remove(position);
        addAddPhoneImg();
        mFeedbackImgAdapter.setAll(mList);
    }

    /////获得现在集合中有多少图片
    private int getImgCount() {
        if (mList.isEmpty()) {
            return 0;
        } else {
            int count = 0;
            for (FeedbackImgVo vo : mList) {
                if (!vo.isShowAddImg()) {
                    count++;
                }
            }
            return count;
        }
    }

    @Override
    public void feedBackSuccess() {
        BaseTipsDialog mBaseTipsDialog = new BaseTipsDialog(this);
        mBaseTipsDialog.setTipsTitle("提交成功！");
        mBaseTipsDialog.setTipsContent("谢谢您的建议，我们将持续为您改进");
        mBaseTipsDialog.setConfirmText("确定");
        mBaseTipsDialog.show();
        mBaseTipsDialog.setConfirmListener(this::finish);
    }
}
