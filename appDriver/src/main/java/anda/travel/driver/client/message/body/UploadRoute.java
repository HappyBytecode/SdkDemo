package anda.travel.driver.client.message.body;

/**
 * Created by liuwenwu on 2021/10/18.
 * Des : 上传路线
 */
public class UploadRoute {
    public static final int FROM_PASSENGER = 1;
    public static final int FROM_DRIVER = 2;

    private int from = 2;
    private String orderUuid;
    private int subStatus;
    private long routeUid;
    private long uploadDate;
    private String routePoints;
    private int routeIndex;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public int getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(int subStatus) {
        this.subStatus = subStatus;
    }

    public long getRouteUid() {
        return routeUid;
    }

    public void setRouteUid(long routeUid) {
        this.routeUid = routeUid;
    }

    public long getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(long uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getRoutePoints() {
        return routePoints;
    }

    public void setRoutePoints(String routePoints) {
        this.routePoints = routePoints;
    }

    public int getRouteIndex() {
        return routeIndex;
    }

    public void setRouteIndex(int routeIndex) {
        this.routeIndex = routeIndex;
    }

    public UploadRoute(int from, String orderUuid, int subStatus, long routeUid, long uploadDate, String routePoints, int routeIndex) {
        this.from = from;
        this.orderUuid = orderUuid;
        this.subStatus = subStatus;
        this.routeUid = routeUid;
        this.uploadDate = uploadDate;
        this.routePoints = routePoints;
        this.routeIndex = routeIndex;
    }
}
