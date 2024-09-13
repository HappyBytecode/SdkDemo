package anda.travel.driver.socket.utils;

import android.content.Context;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;

import anda.travel.driver.config.IConstants;
import anda.travel.driver.data.entity.LocationEntity;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.socket.SocketService;
import anda.travel.driver.baselibrary.utils.SP;

/**
 * @Author moyuwan
 * @Date 17/12/6
 */
public class LocUtils implements AMapLocationListener {

    private static LocUtils mInstance;
    private boolean isSurvival = true; // 判断是否被销毁,如果定位服务已死，调用了LocUtils.reset方法后，清空数据后，哪怕走到当前的回调也不执行，防止空指针

    private LocUtils() {

    }

    public static LocUtils get() {
        if (mInstance == null) {
            synchronized (LocUtils.class) {
                if (mInstance == null) mInstance = new LocUtils();
            }
        }
        return mInstance;
    }

    private UserRepository mUserRepository; //用户仓库
    private SP mSP;

    public void init(SP sp, UserRepository userRepository) {
        mUserRepository = userRepository;
        mSP = sp;
        isSurvival = true;
    }

    public void reset() {
        mSP = null;
        mLocationOption = null;
        mLocationClient = null;
        mAdcode = null;
        mCurrentPoint = null;
        mAngle = 0;
        isSurvival = false;
    }

    public LatLng getCurrentPoint() {
        if (mCurrentPoint == null) {
            if (mUserRepository != null) {
                mCurrentPoint = mUserRepository.getLatLng();
            }
        }
        return mCurrentPoint;
    }

    public double getAngle() {
        return mAngle;
    }

    public String getAdcode() {
        return mAdcode;
    }

    /************************************
     * 定位相关
     ************************************/

    private AMapLocationClientOption mLocationOption;
    private AMapLocationClient mLocationClient;

    private String mAdcode;
    private LatLng mCurrentPoint;
    private double mAngle;

    /**
     * 开始定位
     */
    public void startLocation(Context context) {
        try {
            mLocationClient = new AMapLocationClient(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mLocationOption = new AMapLocationClientOption();
        mLocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(SocketService.getInterval()); //10秒定位一次
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    public void stopLocation() {
        if (mLocationClient != null) mLocationClient.stopLocation();
        mLocationClient = null;
        mLocationOption = null;
    }

    /**
     * 监听定位到的地点
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (isSurvival) {
            if (aMapLocation == null
                    || aMapLocation.getLatitude() == 0
                    || aMapLocation.getLongitude() == 0)
                return;

            mCurrentPoint = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            //保存坐标
            if (mCurrentPoint.latitude != 0 && mCurrentPoint.longitude != 0) {
                mUserRepository.saveLatLng(mCurrentPoint);
            }
            //保存"省"、"市"
            mUserRepository.saveProvince(aMapLocation.getProvince());
            String city = aMapLocation.getCity();
            if (city != null && city.endsWith("市")) city = city.substring(0, city.length() - 1);
            mUserRepository.saveCity(city);
            //获取角度
            mAngle = aMapLocation.getBearing();
            //获取行政区域编码
            String adcode = aMapLocation.getAdCode();
            if (TextUtils.isEmpty(adcode)) {
                mAdcode = mSP.getString(IConstants.ADCODE, IConstants.DefaultAdcode); //从本地获取adcode
            } else {
                mAdcode = adcode;
                mSP.putString(IConstants.ADCODE, adcode); //保存adcode到本地
            }

            //保存当前位置
            LocationEntity location = new LocationEntity();
            location.lat = aMapLocation.getLatitude();
            location.lng = aMapLocation.getLongitude();
            location.adcode = mAdcode;
            location.timeStmap = System.currentTimeMillis();
            location.angle = aMapLocation.getBearing();
            location.speed = aMapLocation.getSpeed();
            location.address = TextUtils.isEmpty(aMapLocation.getPoiName())
                    ? aMapLocation.getAddress()
                    : aMapLocation.getPoiName();
            location.accuracy = aMapLocation.getAccuracy();
            mUserRepository.setCurrentLocation(location);
        }
    }
}
