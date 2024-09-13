package anda.travel.driver.api;

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
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * 功能描述："出租车/专车"订单API
 * <p>
 * appid、noncestr和sign 这三个参数，将在RequestInterceptor中添加；调用接口时，无需传入这三个参数。
 * <p>
 * 但需要注意：如果除path外，还需要传其它参数，则要设置 @FormUrlEncoded
 */
public interface OrderApi {

    /**
     * 获取订单详情
     *
     * @param orderUuid
     * @return
     */
    @POST("order/detail")
    @FormUrlEncoded
    Observable<OrderEntity> reqOrderDetail(@Field("orderUuid") String orderUuid, @Field("versionCode") int versionCode);

    /**
     * 接受派单
     *
     * @param params
     * @return
     */
    @POST("order/confirm")
    @FormUrlEncoded
    Observable<OrderEntity> acceptOrder(@FieldMap HashMap<String, String> params);

    /**
     * 拒绝派单
     *
     * @param params
     * @return
     */
    @POST("order/asn/refuse")
    @FormUrlEncoded
    Observable<String> refuseOrder(@FieldMap HashMap<String, String> params);

    /**
     * 接受改派订单
     *
     * @param params
     * @return
     */
    @POST("order/asn")
    @FormUrlEncoded
    Observable<OrderEntity> acceptRedistributeOrder(@FieldMap HashMap<String, String> params);

    /**
     * 获取订单列表
     *
     * @param params
     * @return
     */
    @POST("order/center/list")
    @FormUrlEncoded
    Observable<List<OrderSummaryEntity>> reqOrderList(@FieldMap HashMap<String, Integer> params);

    /**
     * 出发去接乘客
     *
     * @param params
     * @return
     */
    @POST("order/pickup")
    @FormUrlEncoded
    Observable<OrderEntity> reqPickUpPas(@FieldMap HashMap<String, String> params);

    /**
     * 到达上车地点
     *
     * @param params
     * @return
     */
    @POST("order/wait")
    @FormUrlEncoded
    Observable<OrderEntity> reqDepart(@FieldMap HashMap<String, String> params);

    /**
     * 接到乘客
     *
     * @param params
     * @return
     */
    @POST("order/depart")
    @FormUrlEncoded
    Observable<OrderEntity> reqGeton(@FieldMap HashMap<String, String> params);

    /**
     * 到达目的地
     *
     * @param params
     * @return
     */
    @POST("order/arrive")
    @FormUrlEncoded
    Observable<OrderEntity> reqArrive(@FieldMap HashMap<String, String> params);

    /**
     * 保存订单金额
     *
     * @param params
     * @return
     */
    @POST("order/checkout")
    @FormUrlEncoded
    Observable<OrderEntity> reqUpdateFare(@FieldMap HashMap<String, String> params);

    /**
     * 获取投诉状态
     *
     * @param orderUuid
     * @return
     */
    @POST("order/complain/status")
    @FormUrlEncoded
    Observable<Response<ComplainResultEntity>> isComplain(@Field("orderUuid") String orderUuid);

    /**
     * 投诉
     *
     * @param orderUuid
     * @param commentTag
     * @param remark
     * @return
     */
    @POST("order/complain/add")
    @FormUrlEncoded
    Observable<Response<String>> complainOrder(@Field("orderUuid") String orderUuid,
                                               @Field("commentTag") String commentTag,
                                               @Field("remark") String remark);

    /**
     * 取消订单
     *
     * @return
     */
    @POST("order/cancel")
    @FormUrlEncoded
    Observable<String> cancelOrder(@FieldMap HashMap<String, String> params);

    /**
     * 收到现金支付
     *
     * @param params
     * @return
     */
    @POST("order/fare/received")
    @FormUrlEncoded
    Observable<String> completeOrder(@FieldMap HashMap<String, String> params);

    /**
     * 获取费用明细
     *
     * @param orderUuid
     * @return
     */
    @POST("order/fare/detail")
    @FormUrlEncoded
    Observable<OrderCostEntity> fareItems(@Field("orderUuid") String orderUuid);

    /**
     * 确认费用
     *
     * @param params
     * @return
     */
    @POST("order/confirmFare")
    @FormUrlEncoded
    Observable<OrderEntity> confirmFare(@FieldMap HashMap<String, String> params);

    /**
     * 催款接口
     *
     * @param params
     * @return
     */
    @POST("order/fare/urge")
    @FormUrlEncoded
    Observable<String> rushFare(@FieldMap HashMap<String, String> params);

    /**
     * 获取首页订单状态
     *
     * @return
     */
    @POST("order/homepage/status")
    Observable<OrderHomeStatusEntity> reqHomeStatus();

    /**
     * 余额支付
     *
     * @param params
     * @return
     */
    @POST("pay/balance/tradeUrl")
    @FormUrlEncoded
    Observable<String> payByBalance(@FieldMap HashMap<String, String> params);

    /**
     * 获取"微信支付信息"接口
     *
     * @param params
     * @return
     */
    @POST("pay/wx/tradeUrl")
    @FormUrlEncoded
    Observable<WxPayInfo> payByWechat(@FieldMap HashMap<String, String> params);

    /**
     * 获取"支付宝支付信息"接口
     *
     * @param params
     * @return
     */
    @POST("pay/alipay/tradeUrl")
    @FormUrlEncoded
    Observable<String> payByAlipay(@FieldMap HashMap<String, String> params);

    /**
     * 开始迟到计费
     *
     * @param params
     * @return
     */
    @POST("order/lateBilling")
    @FormUrlEncoded
    Observable<OrderEntity> lateBilling(@FieldMap HashMap<String, String> params);

    /**
     * 20170801追加:
     * 司机到达目的地后，返回继续计费
     *
     * @param params
     * @return
     */
    @POST("order/contToServer")
    @FormUrlEncoded
    Observable<OrderEntity> contToServer(@FieldMap HashMap<String, String> params);

    /**
     * 20170809追加：
     * 结束调度
     *
     * @param params 包含：dispatchUuid－调度编号、mileage－行驶的调度里程、orderUuid－订单编号(非必须)
     * @return
     */
    @POST("dispatch/complete")
    @FormUrlEncoded
    Observable<String> dispatchComplete(@FieldMap HashMap<String, String> params);

    /* ********** 以下为待调整的接口 ********** */

    //到达目的地（专车）
    @POST("{apiPath}/arrive.yueyue")
    @FormUrlEncoded
    Observable<String> reqSpecialArrive(@Path("apiPath") String apiPath,
                                        @Field("orderUuid") String orderUuid,
                                        @Field("tripDistance") double tripDistance); //单位：公里

    //完成订单
    @POST("{apiPath}/finish.yueyue")
    @FormUrlEncoded
    Observable<String> reqFinish(@Path("apiPath") String apiPath,
                                 @Field("orderUuid") String orderUuid);

    /**
     * 司机取消说明接口
     *
     * @param orderUuid
     * @return
     */
    @POST("order/cancelDescription.yueyue")
    @FormUrlEncoded
    Observable<CancelDesEntity> cancelDescription(@Field("orderUuid") String orderUuid);

    /**
     * 获取实时费用明细（专车）
     *
     * @param orderUuid
     * @return
     */
    @POST("specialOrder/getRealtimeFare.yueyue")
    @FormUrlEncoded
    Observable<OrderCostEntity> getRealtimeFare(@Field("orderUuid") String orderUuid);

    /**
     * 获取费用明细（专车）
     *
     * @param orderUuid
     * @return
     */
    @POST("specialOrder/orderFare.yueyue")
    @FormUrlEncoded
    Observable<OrderCostEntity> orderFare(@Field("orderUuid") String orderUuid);

    ///////////获取热力图数据接口
    @POST("order/map/findOrder4HeatMap")
    @FormUrlEncoded
    Observable<List<HeatMapEntity>> findOrderHeatMap(@Field("timeType") int timeType);

    /**
     * 设置派单模式
     *
     * @param mode
     * @return
     */
    @POST("user/vie/set")
    @FormUrlEncoded
    Observable<String> setDispatchOrderMode(@Field("status") int mode);

    /**
     * 行程结束后
     */
    @POST("orderDistanceWarn")
    @FormUrlEncoded
    Observable<String> orderDistanceWarn(@Field("orderUuid") String orderUuid, @Field("distance") double distance);

    /**
     * 是否授权录音
     *
     * @param orderUuid
     * @param authType
     * @return
     */
    @POST("order/authStatus")
    @FormUrlEncoded
    Observable<Integer> authStatus(@Field("orderUuid") String orderUuid, @Field("authType") String authType);

    /**
     * 偏航
     *
     * @param mOrderUuid
     * @param lng
     * @param lat
     * @return
     */
    @POST("share/isOfftrack")
    @FormUrlEncoded
    Observable<String> carpoolReCalculateRoute(@Field("orderUuid") String mOrderUuid, @Field("lng") double lng, @Field("lat") double lat);

    @POST("share/isOfftrack")
    @FormUrlEncoded
    Observable<String> carpoolReCalculateRoute2(@Field("orderUuid") String mOrderUuid);

}
