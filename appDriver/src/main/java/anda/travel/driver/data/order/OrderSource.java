package anda.travel.driver.data.order;

import java.util.HashMap;
import java.util.List;

import anda.travel.driver.data.entity.CancelDesEntity;
import anda.travel.driver.data.entity.ComplainResultEntity;
import anda.travel.driver.data.entity.HeatMapEntity;
import anda.travel.driver.data.entity.OrderCostEntity;
import anda.travel.driver.data.entity.OrderEntity;
import anda.travel.driver.data.entity.OrderHomeStatusEntity;
import anda.travel.driver.data.entity.OrderSummaryEntity;
import anda.travel.driver.data.entity.WxPayInfo;
import retrofit2.Response;
import rx.Observable;

/**
 * 功能描述："订单仓库"需实现的接口
 */
public interface OrderSource {

    Observable<OrderHomeStatusEntity> reqHomeStatus(); // 获取首页订单状态

    Observable<List<OrderSummaryEntity>> reqOrderList(HashMap<String, Integer> params); // 获取订单列表

    Observable<OrderEntity> acceptOrder(HashMap<String, String> params); // 接受派单

    Observable<String> refuseOrder(HashMap<String, String> params); //拒绝派单 orderUuid

    Observable<OrderEntity> acceptRedistributeOrder(HashMap<String, String> params); // 接受改派订单

    Observable<OrderEntity> reqOrderDetail(String orderUuid);

    Observable<OrderEntity> reqPickUpPas(String orderUuid); //出发去接乘客

    Observable<OrderEntity> reqDepart(String orderUuid); // 到达上车地点

    Observable<OrderEntity> lateBilling(String orderUuid); //迟到计费

    Observable<OrderEntity> reqGeton(String orderUuid); // 接到乘客

    Observable<OrderEntity> reqArrive(String orderUuid, double tripDistance, int mileType); // 到达目的地

    Observable<String> reqSpecialArrive(String orderUuid, double tripDistance); // 到达目的地（专车）

    Observable<OrderEntity> reqUpdateFare(HashMap<String, String> params); // 保存订单金额

    Observable<String> reqFinish(String orderUuid); // 完成订单？ 什么时候调用？

    Observable<String> reqCancelOrder(String orderUuid, int status, String cancelMsg); // 取消订单

    Observable<Response<String>> reqComplainOrder(String orderUuid, String contents, String remark); // 投诉

    Observable<String> rushFare(String orderUuid); // 催款

    Observable<Response<ComplainResultEntity>> isComplain(String orderUuid); //获取投诉状态

    Observable<CancelDesEntity> cancelDescription(String orderUuid); //获取取消描述

    Observable<WxPayInfo> payByWechat(String orderUuid, String spbillCreateIp); //微信支付

    Observable<String> payByAlipay(String orderUuid); //支付宝支付

    Observable<String> payByBalance(String orderUuid); //余额支付

    Observable<OrderCostEntity> reqFareItems(String orderUuid); //获取费用明细

    Observable<OrderCostEntity> getRealtimeFare(String orderUuid); //获取实时费用明细(专车)

    Observable<OrderCostEntity> orderFare(String orderUuid); //获取费用明细(专车)

    Observable<OrderEntity> confirmFare(HashMap<String, String> params); //确认费用

    Observable<String> completeOrder(String orderUuid); //收到现金支付，完成订单

    Observable<OrderEntity> contToServer(String orderUuid); //司机到达目的地后，返回继续计费

    Observable<String> dispatchComplete(HashMap<String, String> params); //结束调度

    Observable<List<HeatMapEntity>> findOrderHeatMap(int timeType); // 获取订单列表

    Observable<String> orderDistanceWarn(String orderUuid, double distance);

    Observable<Integer> authStatus(String orderUuid, String authType);

    Observable<String> carpoolReCalculateRoute(String mOrderUuid);
}
