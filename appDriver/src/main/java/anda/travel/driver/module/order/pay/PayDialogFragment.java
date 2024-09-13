package anda.travel.driver.module.order.pay;


import android.app.Dialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.base.LibBaseActivity;
import anda.travel.driver.baselibrary.utils.NumberUtil;
import anda.travel.driver.baselibrary.utils.ToastUtil;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.data.entity.PayTypeEntity;
import anda.travel.driver.data.entity.WxPayInfo;
import anda.travel.driver.module.order.pay.dagger.DaggerOrderPayComponent;
import anda.travel.driver.module.order.pay.dagger.OrderPayModule;

public class PayDialogFragment extends DialogFragment implements OrderPayContract.View {

    RecyclerView mRecList;
    TextView mTvPrice;
    TextView mTvNotice;

    @Inject
    OrderPayPresenter mPresenter;
    OrderPayAdapter mAdapter;
    boolean mShowPayPumping;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.PayBottomDialog);
        // 必须在setContentView之前调用。否则运行时报错。
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.hxyc_fragment_order_pay, null);
        initView(view);
        // 底部弹出的DialogFragment装载的View
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        // 设置底部弹出显示的DialogFragment窗口属性。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.windowAnimations = R.style.PayBottomDialogAnimation;
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT; // 底部弹出的DialogFragment的高度，如果是MATCH_PARENT则铺满整个窗口
        window.setAttributes(params);

        DaggerOrderPayComponent.builder().appComponent(HxClientManager.getAppComponent())
                .orderPayModule(new OrderPayModule(this)).build().inject(this);
        if (getArguments() != null) {
            mShowPayPumping = getArguments().getBoolean(IConstants.NOTICE_TYPE, false); //提示语类型
        }
        mTvNotice.setText(mShowPayPumping ? R.string.order_pay_notice_pump : R.string.order_pay_notice);

        mAdapter = new OrderPayAdapter(getActivity());
        mAdapter.setOnClickListener(R.id.layout_item, (position, v, item) -> {
            if (isBtnBuffering()) return;
            String payType = item.getPayType();
            if (payType.contains("余额")) { //余额支付
                mPresenter.reqPayByBalance();
            } else if (payType.contains("微信")) { //微信支付
                mPresenter.reqPayByWeixin(getIp());
            } else { //支付宝支付
                mPresenter.reqPayByAlipay();
            }
        });
        mRecList.setAdapter(mAdapter);

//        //如果是自营司机，则隐藏"余额支付"
//        if (mPresenter.getIsDependDriver()) mLayoutBalance.setVisibility(View.GONE);

        mPresenter.onCreate();
        if (getArguments() != null) {
            mPresenter.setOrderUuid(getArguments().getString(IConstants.ORDER_UUID));
        }
        showPriceInfo(getArguments().getDouble(IConstants.PRICE, 0));

        return dialog;
    }

    private void initView(View view) {
        mRecList = view.findViewById(R.id.rec_list);
        mTvPrice = view.findViewById(R.id.tv_price);
        mTvNotice = view.findViewById(R.id.tv_notice);
        view.findViewById(R.id.iv_close).setOnClickListener(view1 -> dismiss());
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void showLoadingView(boolean cancalable) {
        showLoadingView(cancalable, 0);
    }

    @Override
    public void showLoadingView(boolean cancalable, long delay) {
        if (!isAdded()) return;
        if (getActivity() instanceof LibBaseActivity) {
            ((LibBaseActivity) getActivity()).showLoadingView(cancalable, delay);
        }
    }

    @Override
    public void showLoadingViewWithDelay(boolean cancalable) {
        showLoadingView(cancalable, 400); //默认延迟 400ms显示loading指示
    }

    @Override
    public void hideLoadingView() {
        if (getActivity() instanceof LibBaseActivity) {
            ((LibBaseActivity) getActivity()).hideLoadingView();
        }
    }

    @Override
    public void toast(String msg) {
        ToastUtil.getInstance().toast(msg);
    }

    @Override
    public void toast(int msgId) {
        ToastUtil.getInstance().toast(msgId);
    }

    @Override
    public void showPriceInfo(double price) {
        String strPrice = NumberUtil.getFormatValue(price) + "元";
        mTvPrice.setText(strPrice);
    }

    @Override
    public void paySuccess() {
        toast("支付成功");
        dismiss(); //关闭界面
    }

    @Override
    public void payFail() {
        toast("支付失败");
    }

    @Override
    public void startAlipay(String tradeUrl) {

    }

    @Override
    public void startWxpay(WxPayInfo info) {
//        /* ***** 20170809修改，从配置文件中获取最新的wxAppid ***** */
//        if (ParseUtils.getInstance().getMyConfig() != null
//                && (!TextUtils.isEmpty(ParseUtils.getInstance().getMyConfig().getWxAppid()))) {
//            AppConfig.WX_APPID = ParseUtils.getInstance().getMyConfig().getWxAppid();
//        }
    }

    @Override
    public void showPayTypeList(List<PayTypeEntity> list) {
        if (mShowPayPumping && list != null) {
            for (PayTypeEntity entity : list) {
                String payType = entity.getPayType();
                if (payType != null & payType.contains("余额")) {
                    list.remove(entity);
                    break;
                }
            }
        }
        mAdapter.setAll(list);
    }

    /**
     * 获取本地ip地址
     */
    private String getIp() {
        WifiManager wifiManager = (WifiManager) requireActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        if (ipAddress == 0) return "127.0.0.1";
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }

    private long mBtnClickStamp; //记录按键点击的时间戳

    public boolean isBtnBuffering() {
        long duration = System.currentTimeMillis() - mBtnClickStamp;
        mBtnClickStamp = System.currentTimeMillis(); //记录点击时间
        return Math.abs(duration) <= 400;
    }
}
