package anda.travel.driver.util;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.Arrays;
import java.util.List;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.PhoneUtil;
import anda.travel.driver.baselibrary.utils.SP;
import anda.travel.driver.data.entity.DrvCodeEntity;
import anda.travel.driver.data.entity.SysConfigEntity;
import anda.travel.driver.data.entity.WithdrawRuleEntity;

/**
 * @Author moyuwan
 * @Date 17/11/1
 * <p>
 * 配置信息 相关工具类
 */
public class SysConfigUtils {

    private final static String SYS_CONFIG = "SYS_CONFIG";

    private static SysConfigUtils mInstance;
    private static Context mContext;
    private static SP mSP;

    private SysConfigUtils() {

    }

    public static SysConfigUtils get() {
        if (mInstance == null) mInstance = new SysConfigUtils();
        return mInstance;
    }

    public static void init(Context context) {
        mContext = context;
        mSP = new SP(context);
    }

    // 配置信息
    private SysConfigEntity mSysConfig;

    /**
     * 从本地获取配置信息
     *
     * @return
     */
    public SysConfigEntity getSysConfig() {
        if (mSysConfig == null) mSysConfig = mSP.getObject(SYS_CONFIG, SysConfigEntity.class);
        return mSysConfig;
    }

    /**
     * 设置配置信息
     *
     * @param sysConfig
     */
    public void setSysConfig(SysConfigEntity sysConfig) {
        if (sysConfig != null) {
            mSysConfig = sysConfig;
            mSP.putObject(SYS_CONFIG, sysConfig);
        }
    }

    /**
     * 获取客服电话
     */
    public String getServerPhone() {
        return (mSysConfig == null || TextUtils.isEmpty(mSysConfig.getServerPhone()))
                ? mContext.getResources().getString(R.string.contact_phone)
                : mSysConfig.getServerPhone();
    }

    /**
     * 拨打客服电话
     */
    public void dialServerPhone(Context context) {
        PhoneUtil.skip(context, getServerPhone());
    }

    /**
     * 订单播报页面说明
     *
     * @return
     */
    public String getOrderReportExplain(Context context) {
        return (mSysConfig == null || TextUtils.isEmpty(mSysConfig.getOrderReportExplain()))
                ? context.getString(R.string.report_notice)
                : mSysConfig.getOrderReportExplain();
    }

    /**
     * 提现手续费
     *
     * @return 默认1元
     */
    public double getCashFee() {
        if (mSysConfig != null && mSysConfig.getCashFee() != null) {
            try {
                return Double.valueOf(mSysConfig.getCashFee());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 获取导航策略
     *
     * @return
     */
    public int getNaviStrategy() {
        SysConfigEntity sysConfig = getSysConfig();
        try {
            if (sysConfig != null && !TextUtils.isEmpty(sysConfig.getNaviStrategy())) {
                return Integer.parseInt(sysConfig.getNaviStrategy());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0; //默认的导航策略
    }

    public List<DrvCodeEntity> getQrData() {
        SysConfigEntity sysConfig = getSysConfig();
        if (sysConfig != null && !TextUtils.isEmpty(sysConfig.getDrvCode())) {
            DrvCodeEntity[] codes = JSON.parseObject(sysConfig.getDrvCode(), new TypeReference<DrvCodeEntity[]>() {
            });
            return Arrays.asList(codes);
        } else {
            return null;
        }
    }

    public List<WithdrawRuleEntity> getOwnRuleData() {
        SysConfigEntity sysConfig = getSysConfig();
        if (sysConfig != null && !TextUtils.isEmpty(sysConfig.getDriverOwnCashRule())) {
            WithdrawRuleEntity[] codes = JSON.parseObject(sysConfig.getDriverOwnCashRule(), new TypeReference<WithdrawRuleEntity[]>() {
            });
            return Arrays.asList(codes);
        } else {
            return null;
        }
    }
}
