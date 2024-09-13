package anda.travel.driver.data.order;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import anda.travel.driver.data.entity.CancelDesEntity;
import anda.travel.driver.data.entity.ComplainResultEntity;
import anda.travel.driver.data.entity.HeatMapEntity;
import anda.travel.driver.data.entity.OrderCostEntity;
import anda.travel.driver.data.entity.OrderEntity;
import anda.travel.driver.data.entity.OrderHomeStatusEntity;
import anda.travel.driver.data.entity.OrderSummaryEntity;
import anda.travel.driver.data.entity.WxPayInfo;
import anda.travel.driver.data.order.remote.OrderRemoteSource;
import retrofit2.Response;
import rx.Observable;

/**
 * 功能描述：订单仓库
 */
@Singleton
public class OrderRepository implements OrderSource {

    private final OrderRemoteSource mRemoteSource;

    @Inject
    public OrderRepository(OrderRemoteSource remoteSource) {
        mRemoteSource = remoteSource;
    }

    @Override
    public Observable<OrderHomeStatusEntity> reqHomeStatus() {
        return mRemoteSource.reqHomeStatus();
    }

    @Override
    public Observable<List<OrderSummaryEntity>> reqOrderList(HashMap<String, Integer> params) {
        return mRemoteSource.reqOrderList(params);
    }

    @Override
    public Observable<OrderEntity> acceptOrder(HashMap<String, String> params) {
        return mRemoteSource.acceptOrder(params);
    }

    @Override
    public Observable<String> refuseOrder(HashMap<String, String> params) {
        return mRemoteSource.refuseOrder(params);
    }

    @Override
    public Observable<OrderEntity> acceptRedistributeOrder(HashMap<String, String> params) {
        return mRemoteSource.acceptRedistributeOrder(params);
    }

    @Override
    public Observable<OrderEntity> reqOrderDetail(String orderUuid) {
        return mRemoteSource.reqOrderDetail(orderUuid);
    }

    @Override
    public Observable<OrderEntity> reqPickUpPas(String orderUuid) {
        return mRemoteSource.reqPickUpPas(orderUuid);
    }

    @Override
    public Observable<OrderEntity> reqDepart(String orderUuid) {
        return mRemoteSource.reqDepart(orderUuid);
    }

    @Override
    public Observable<OrderEntity> lateBilling(String orderUuid) {
        return mRemoteSource.lateBilling(orderUuid);
    }

    @Override
    public Observable<OrderEntity> reqGeton(String orderUuid) {
        return mRemoteSource.reqGeton(orderUuid);
    }

    @Override
    public Observable<OrderEntity> reqArrive(String orderUuid, double tripDistance, int mileType) {
        return mRemoteSource.reqArrive(orderUuid, tripDistance, mileType);
    }

    @Override
    public Observable<String> reqSpecialArrive(String orderUuid, double tripDistance) {
        return mRemoteSource.reqSpecialArrive(orderUuid, tripDistance);
    }

    @Override
    public Observable<OrderEntity> reqUpdateFare(HashMap<String, String> params) {
        return mRemoteSource.reqUpdateFare(params);
    }

    @Override
    public Observable<String> reqFinish(String orderUuid) {
        return mRemoteSource.reqFinish(orderUuid);
    }

    @Override
    public Observable<String> reqCancelOrder(String orderUuid, int status, String cancelMsg) {
        return mRemoteSource.reqCancelOrder(orderUuid, status, cancelMsg);
    }

    @Override
    public Observable<Response<String>> reqComplainOrder(String orderUuid, String contents, String remark) {
        return mRemoteSource.reqComplainOrder(orderUuid, contents, remark);
    }

    @Override
    public Observable<String> rushFare(String orderUuid) {
        return mRemoteSource.rushFare(orderUuid);
    }

    @Override
    public Observable<Response<ComplainResultEntity>> isComplain(String orderUuid) {
        return mRemoteSource.isComplain(orderUuid);
    }

    @Override
    public Observable<CancelDesEntity> cancelDescription(String orderUuid) {
        return mRemoteSource.cancelDescription(orderUuid);
    }

    @Override
    public Observable<WxPayInfo> payByWechat(String orderUuid, String spbillCreateIp) {
        return mRemoteSource.payByWechat(orderUuid, spbillCreateIp);
    }

    @Override
    public Observable<String> payByAlipay(String orderUuid) {
        return mRemoteSource.payByAlipay(orderUuid);
    }

    @Override
    public Observable<String> payByBalance(String orderUuid) {
        return mRemoteSource.payByBalance(orderUuid);
    }

    @Override
    public Observable<OrderCostEntity> reqFareItems(String orderUuid) {
        return mRemoteSource.reqFareItems(orderUuid);
    }

    @Override
    public Observable<OrderCostEntity> getRealtimeFare(String orderUuid) {
        return mRemoteSource.getRealtimeFare(orderUuid);
    }

    @Override
    public Observable<OrderCostEntity> orderFare(String orderUuid) {
        return mRemoteSource.orderFare(orderUuid);
    }

    @Override
    public Observable<OrderEntity> confirmFare(HashMap<String, String> params) {
        return mRemoteSource.confirmFare(params);
    }

    @Override
    public Observable<String> completeOrder(String orderUuid) {
        return mRemoteSource.completeOrder(orderUuid);
    }

    @Override
    public Observable<OrderEntity> contToServer(String orderUuid) {
        return mRemoteSource.contToServer(orderUuid);
    }

    @Override
    public Observable<String> dispatchComplete(HashMap<String, String> params) {
        return mRemoteSource.dispatchComplete(params);
    }

    @Override
    public Observable<List<HeatMapEntity>> findOrderHeatMap(int timeType) {
        return mRemoteSource.findOrderHeatMap(timeType);
    }

    @Override
    public Observable<String> orderDistanceWarn(String orderUuid, double distance) {
        return mRemoteSource.orderDistanceWarn(orderUuid, distance);
    }

    @Override
    public Observable<Integer> authStatus(String orderUuid, String authType) {
        return mRemoteSource.authStatus(orderUuid, authType);
    }

    @Override
    public Observable<String> carpoolReCalculateRoute(String mOrderUuid) {
        return mRemoteSource.carpoolReCalculateRoute(mOrderUuid);
    }
}
