package anda.travel.driver.common;

import android.app.Activity;
import android.text.TextUtils;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import java.text.MessageFormat;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.network.RequestError;
import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.util.Navigate;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * 功能描述：基础Presenter
 */
public abstract class BasePresenter implements IBasePresenter {

    protected CompositeSubscription mDisposable = new CompositeSubscription();
    protected boolean mFirstSubscribe = true;

    @Override
    public void subscribe() {
    }

    @Override
    public void unsubscribe() {
        mFirstSubscribe = false;
        mDisposable.clear();
    }

    protected void showNetworkError(Throwable e, @StringRes int defErrorMsg, IBaseView view, UserRepository userRepository) {
        view.hideLoadingView(); //关闭loading指示器

        Throwable throwable = e.getCause() != null ? e.getCause() : e;
        ///调试的时候可以把这个打开，能见到详细错误信息
        e.printStackTrace();
        if (throwable instanceof RequestError) {
            RequestError requestError = (RequestError) throwable;
            String errorMsg = requestError.getMsg(); //错误提示
            Timber.e(MessageFormat.format("BasePresenter#showNetworkError(): (from RequestBean) {0}", requestError.getMsg()));

            if (TextUtils.isEmpty(errorMsg)) {
                view.toast(defErrorMsg);
            } else {
                view.toast(errorMsg);
            }

            checkTokenInvalid(requestError, view, userRepository); //检查token是否失效，并做相应处理
            return;
        }

        if ("timeout".equals(e.getMessage())) { //网络连接超时
            view.toast(R.string.network_timeout);
            Timber.e("BasePresenter#showNetworkError(): timeout ");
            return;
        }

        Timber.e(MessageFormat.format("BasePresenter#showNetworkError(): errorMsg = {0}", e.getMessage()));
        view.toast(defErrorMsg); //弹出默认的错误提示
    }

    /**
     * 检查token是否失效
     *
     * @param requestError
     * @param view
     * @param userRepository
     */
    private void checkTokenInvalid(RequestError requestError, IBaseView view, UserRepository userRepository) {
        if (userRepository == null || requestError.getReturnCode() != 91002) return;

        Activity activity = null;
        if (view instanceof Activity) {
            activity = (Activity) view;
        } else if (view instanceof Fragment) {
            activity = ((Fragment) view).getActivity();
        }
        if (activity == null) return;

        userRepository.logout(); //退出登录
//        Navigate.openLogin(activity); //跳转到登录页
        AppManager.getInstance().finishAllActivity(); //关闭所有界面（除登录页）
        //activity.finish(); //关闭当前界面
    }

}
