package anda.travel.driver.data.order.remote;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.BuildConfig;
import anda.travel.driver.api.OrderApi;
import anda.travel.driver.baselibrary.network.RequestParams;
import anda.travel.driver.data.entity.CancelDesEntity;
import anda.travel.driver.data.entity.ComplainResultEntity;
import anda.travel.driver.data.entity.HeatMapEntity;
import anda.travel.driver.data.entity.LocationEntity;
import anda.travel.driver.data.entity.OrderCostEntity;
import anda.travel.driver.data.entity.OrderEntity;
import anda.travel.driver.data.entity.OrderHomeStatusEntity;
import anda.travel.driver.data.entity.OrderSummaryEntity;
import anda.travel.driver.data.entity.WxPayInfo;
import anda.travel.driver.data.order.OrderSource;
import anda.travel.driver.data.user.UserRepository;
import retrofit2.Response;
import rx.Observable;

/**
 * 功能描述：
 */
public class OrderRemoteSource implements OrderSource {

    private final OrderApi mOrderApi;
    private final UserRepository mUserRepository;

    @Inject
    public OrderRemoteSource(OrderApi orderApi, UserRepository userRepository) {
        mOrderApi = orderApi;
        mUserRepository = userRepository;
    }

    /**
     * 特别注意：
     * 需要和API地址中，用于区分"出租车"、"专车"的string 保持统一！
     * 在OrderApi中有使用到
     */
    private String getApiPath() {
        return "specialOrder";
        //return BuildConfig.FLAVOR_type.equals("taxi") ? "order" : "specialOrder";
    }

    @Override
    public Observable<OrderHomeStatusEntity> reqHomeStatus() {
        return mOrderApi.reqHomeStatus();
    }

    @Override
    public Observable<List<OrderSummaryEntity>> reqOrderList(HashMap<String, Integer> params) {
        return mOrderApi.reqOrderList(params);
    }

    @Override
    public Observable<OrderEntity> acceptOrder(HashMap<String, String> params) {
        return mOrderApi.acceptOrder(
                getParamsBuilder()
                        .putParam(params)
                        .build());
    }

    @Override
    public Observable<String> refuseOrder(HashMap<String, String> params) {
        return mOrderApi.refuseOrder(
                getParamsBuilder()
                        .putParam(params)
                        .build());
    }

    @Override
    public Observable<OrderEntity> acceptRedistributeOrder(HashMap<String, String> params) {
        return mOrderApi.acceptRedistributeOrder(
                getParamsBuilder()
                        .putParam(params)
                        .build());
    }

    @Override
    public Observable<OrderEntity> reqOrderDetail(String orderUuid) {
        return mOrderApi.reqOrderDetail(orderUuid, BuildConfig.VERSION_CODE);
    }

    @Override
    public Observable<OrderEntity> reqPickUpPas(String orderUuid) {
        return mOrderApi.reqPickUpPas(getParams(orderUuid));
    }

    @Override
    public Observable<OrderEntity> reqDepart(String orderUuid) {
        return mOrderApi.reqDepart(getParams(orderUuid));
    }

    @Override
    public Observable<OrderEntity> lateBilling(String orderUuid) {
        return mOrderApi.lateBilling(getParams(orderUuid));
    }

    @Override
    public Observable<OrderEntity> reqGeton(String orderUuid) {
        return mOrderApi.reqGeton(getParams(orderUuid));
    }

    @Override
    public Observable<OrderEntity> reqArrive(String orderUuid, double tripDistance, int mileType) {
        HashMap<String, String> params = getParams(orderUuid);
        params.put("tripDistance", String.valueOf(tripDistance)); //行驶里程(km)
        params.put("mileType", String.valueOf(mileType)); //计价类型
        return mOrderApi.reqArrive(params);
    }

    @Override
    public Observable<String> reqSpecialArrive(String orderUuid, double tripDistance) {
        return mOrderApi.reqSpecialArrive(getApiPath(), orderUuid, tripDistance);
    }

    @Override
    public Observable<OrderEntity> reqUpdateFare(HashMap<String, String> params) {
        return mOrderApi.reqUpdateFare(
                getParamsBuilder()
                        .putParam(params)
                        .build());
    }

    @Override
    public Observable<String> reqFinish(String orderUuid) {
        return mOrderApi.reqFinish(getApiPath(), orderUuid);
    }

    @Override
    public Observable<String> reqCancelOrder(String orderUuid, int status, String cancelMsg) {
        RequestParams.Builder builder = getParamsBuilder();
        builder.putParam("orderUuid", orderUuid);
        builder.putParam("status", status);
        builder.putParam("cancelMsg", cancelMsg);
        return mOrderApi.cancelOrder(builder.build());
    }

    @Override
    public Observable<Response<String>> reqComplainOrder(String orderUuid, String contents, String remark) {
        return mOrderApi.complainOrder(orderUuid, contents, remark);
    }

    @Override
    public Observable<String> rushFare(String orderUuid) {
        RequestParams.Builder builder = getParamsBuilder();
        builder.putParam("orderUuid", orderUuid);
        return mOrderApi.rushFare(builder.build());
    }

    @Override
    public Observable<Response<ComplainResultEntity>> isComplain(String orderUuid) {
        return mOrderApi.isComplain(orderUuid);
    }

    @Override
    public Observable<CancelDesEntity> cancelDescription(String orderUuid) {
        return mOrderApi.cancelDescription(orderUuid);
    }

    @Override
    public Observable<WxPayInfo> payByWechat(String orderUuid, String spbillCreateIp) {
        RequestParams.Builder builder = getParamsBuilder();
        builder.putParam("orderUuid", orderUuid)
                .putParam("spbillCreateIp", spbillCreateIp);
        return mOrderApi.payByWechat(builder.build());
    }

    @Override
    public Observable<String> payByAlipay(String orderUuid) {
        RequestParams.Builder builder = getParamsBuilder();
        builder.putParam("orderUuid", orderUuid);
        return mOrderApi.payByAlipay(builder.build());
    }

    @Override
    public Observable<String> payByBalance(String orderUuid) {
        RequestParams.Builder builder = getParamsBuilder();
        builder.putParam("orderUuid", orderUuid);
        return mOrderApi.payByBalance(builder.build());
    }

    @Override
    public Observable<OrderCostEntity> reqFareItems(String orderUuid) {
        return mOrderApi.fareItems(orderUuid);
    }

    @Override
    public Observable<OrderCostEntity> getRealtimeFare(String orderUuid) {
        return mOrderApi.getRealtimeFare(orderUuid);
    }

    @Override
    public Observable<OrderCostEntity> orderFare(String orderUuid) {
        return mOrderApi.orderFare(orderUuid);
    }

    @Override
    public Observable<OrderEntity> confirmFare(HashMap<String, String> params) {
        return mOrderApi.confirmFare(
                getParamsBuilder()
                        .putParam(params)
                        .build());
    }

    @Override
    public Observable<String> completeOrder(String orderUuid) {
        RequestParams.Builder builder = getParamsBuilder();
        builder.putParam("orderUuid", orderUuid);
        return mOrderApi.completeOrder(builder.build());
    }

    @Override
    public Observable<OrderEntity> contToServer(String orderUuid) {
        RequestParams.Builder builder = getParamsBuilder();
        builder.putParam("orderUuid", orderUuid);
        return mOrderApi.contToServer(builder.build());
    }

    @Override
    public Observable<String> dispatchComplete(HashMap<String, String> params) {
        return mOrderApi.dispatchComplete(
                getParamsBuilder()
                        .putParam(params)
                        .build());
    }

    @Override
    public Observable<List<HeatMapEntity>> findOrderHeatMap(int timeType) {
        return mOrderApi.findOrderHeatMap(timeType);
    }

    private HashMap<String, String> getParams(String orderUuid) {
        HashMap<String, String> params = new HashMap<>();
        params.put("orderUuid", orderUuid);
        LocationEntity location = mUserRepository.getCurrentLocation();
        if (location != null) {
            params.put("adcode", location.adcode);
            params.put("lng", String.valueOf(location.lng));
            params.put("lat", String.valueOf(location.lat));
        }
        return params;
    }

    private RequestParams.Builder getParamsBuilder() {
        RequestParams.Builder builder = new RequestParams.Builder();
        LocationEntity location = mUserRepository.getCurrentLocation();
        if (location == null) return builder;
        builder.putParam("adcode", location.adcode);
        builder.putParam("lng", location.lng);
        builder.putParam("lat", location.lat);
        return builder;
    }

    @Override
    public Observable<String> orderDistanceWarn(String orderUuid, double distance) {
        return mOrderApi.orderDistanceWarn(orderUuid, distance);
    }

    @Override
    public Observable<Integer> authStatus(String orderUuid, String authType) {
        return mOrderApi.authStatus(orderUuid, authType);
    }

    @Override
    public Observable<String> carpoolReCalculateRoute(String mOrderUuid) {
        if (mUserRepository.getLatLng() != null) {
            return mOrderApi.carpoolReCalculateRoute(mOrderUuid, mUserRepository.getLatLng().longitude, mUserRepository.getLatLng().latitude);
        } else {
            return mOrderApi.carpoolReCalculateRoute2(mOrderUuid);
        }
    }
}
