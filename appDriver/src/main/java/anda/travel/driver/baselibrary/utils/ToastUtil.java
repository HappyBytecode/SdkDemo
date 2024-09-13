package anda.travel.driver.baselibrary.utils;

import android.app.Activity;

import com.hjq.toast.ToastUtils;
import com.hjq.xtoast.XToast;

import anda.travel.driver.R;

/**
 * 自定义Toast
 */
public class ToastUtil {

    private ToastUtil() {

    }

    private static ToastUtil instance;
    private boolean isShowXToast = true;

    public static ToastUtil getInstance() {
        if (instance == null) {
            synchronized (ToastUtil.class) {
                if (instance == null) {
                    instance = new ToastUtil();
                }
            }
        }
        return instance;
    }

    public void toast(String content) {
        ToastUtils.show(content);
    }

    public void toast(int resId) {
        ToastUtils.show(resId);
    }

    public void showXToast(Activity context, String content, int duration) {
        if (isShowXToast) {
            new XToast(context)
                    .setDuration(duration)
                    .setView(ToastUtils.getToast().getView())
                    .setAnimStyle(android.R.style.Animation_Translucent)
                    .setText(android.R.id.message, content)
                    .setOnToastListener(new XToast.OnToastListener() {
                        @Override
                        public void onShow(XToast<?> toast) {
                            isShowXToast = false;
                        }

                        @Override
                        public void onDismiss(XToast<?> toast) {
                            isShowXToast = true;
                        }
                    }).show();
        }
    }

    public void showTwoLinesToast(Activity context, String content, String content2, int duration) {
        if (isShowXToast) {
            new XToast<>(context)
                    .setView(R.layout.hxyc_layout_toast_lines)
                    // 设置成可拖拽的
                    //.setDraggable()
                    // 设置显示时长
                    .setDuration(duration)
                    // 设置动画样式
                    .setAnimStyle(android.R.style.Animation_Translucent)
                    // 设置外层是否能被触摸
                    //.setOutsideTouchable(false)
                    // 设置窗口背景阴影强度
                    //.setBackgroundDimAmount(0.5f)
                    .setText(android.R.id.message, content)
                    .setText(R.id.tv_help, content2)
                    .setOnToastListener(new XToast.OnToastListener() {
                        @Override
                        public void onShow(XToast<?> toast) {
                            isShowXToast = false;
                        }

                        @Override
                        public void onDismiss(XToast<?> toast) {
                            isShowXToast = true;
                        }
                    })
                    .show();
        }
    }
}