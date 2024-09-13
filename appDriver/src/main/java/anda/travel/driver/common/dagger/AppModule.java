package anda.travel.driver.common.dagger;

import android.content.Context;

import javax.inject.Singleton;

import anda.travel.driver.api.ApiConfig;
import anda.travel.driver.api.DriverApi;
import anda.travel.driver.api.OrderApi;
import anda.travel.driver.api.RootApi;
import anda.travel.driver.baselibrary.network.RetrofitUtil;
import anda.travel.driver.baselibrary.utils.SP;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final Context mContext;

    public AppModule(Context context) {
        mContext = context;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return mContext;
    }

    @Provides
    @Singleton
    public SP provideSP(Context context) {
        return new SP(context);
    }

    /**
     * 获取用户信息API
     *
     * @param sp
     * @return
     */
    @Provides
    @Singleton
    public DriverApi provideDriverApi(SP sp) {
        return RetrofitUtil.getService(DriverApi.class, ApiConfig.getDriverApi(), sp);
    }

    /**
     * 获取"出租车/专车"订单API
     *
     * @param sp
     * @return
     */
    @Provides
    @Singleton
    public OrderApi provideOrderApi(SP sp) {
        return RetrofitUtil.getService(OrderApi.class, ApiConfig.getOrderApi(), sp);
    }

    /**
     * 获取RootApi
     *
     * @param sp
     * @return
     */
    @Provides
    @Singleton
    public RootApi provideRootApi(SP sp) {
        return RetrofitUtil.getService(RootApi.class, ApiConfig.getRootApi(), sp);
    }

}
