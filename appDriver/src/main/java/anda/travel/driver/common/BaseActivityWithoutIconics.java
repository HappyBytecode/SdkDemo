package anda.travel.driver.common;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;

import com.gyf.immersionbar.ImmersionBar;

import anda.travel.driver.R;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.baselibrary.base.LibBaseActivity;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.socket.SocketService;

/**
 * 功能描述：因"使用字体图片"会导致"高德地图的AMapNaviView"不能正常使用，增加该类
 */
public abstract class BaseActivityWithoutIconics extends LibBaseActivity {

    //管理屏幕常亮状态
    private PowerManager pm;
    private PowerManager.WakeLock wl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isNotLoginActivity()) {
            AppManager.getInstance().addActivity(this);
        }
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (Exception e) {

        }

        // 统一沉浸式状态栏
        ImmersionBar.with(this)
                .statusBarColor(R.color.hx_statusBar_background)
                .fitsSystemWindows(true)
                .init();

        //检查SocketService是否开启
        if (checkServiceOnCreate()) {
            //如果已被关闭，则重新启动
            SocketService.checkServiceWithStart(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isNotLoginActivity()) {
            AppManager.getInstance().removeActivity(this);
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 获取状态栏颜色
     *
     * @return
     */
    public int getBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            return R.color.white;
        } else {
            return R.color.main_black;
        }
    }

    /**
     * 屏幕常亮
     */
    protected void bright() {
        try {
            creatWL();
            wl.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消常亮
     */
    protected void cancelBright() {
        try {
            creatWL();
            wl.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void creatWL() {
        if (pm == null) {
            pm = (PowerManager) getSystemService(POWER_SERVICE);
        }
        if (wl == null) {
            wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    getResources().getString(R.string.app_name));
        }
    }

    public AppComponent getAppComponent() {
        return HxClientManager.getAppComponent();
    }

    protected boolean isNotLoginActivity() {
        return true;
    }

    /**
     * 是否检查SocketService
     *
     * @return
     */
    protected boolean checkServiceOnCreate() {
        return true;
    }

}
