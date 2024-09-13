package anda.travel.driver.module.order.ongoing.calculate;

import anda.travel.driver.data.entity.PointEntity;
import anda.travel.driver.module.amap.assist.CalculateUtils;

public class LineCalculateImpl implements ICalculate {

    private final static float MAX_SPEED = 50F; // 50 米每秒（也就是 180 千米每小时）

    private float mTotalDistance; //总里程，单位：米（前几段里程的总和）
    private PointEntity mPreviousPoint;
    private boolean isNotFirst;

    public LineCalculateImpl() {
    }

    @Override
    public void addPoint(PointEntity point) {
        if (isNotFirst) {
            ////////第二次进入计算逻辑
            long duration = point.getLoctime() - mPreviousPoint.getLoctime(); //时间(单位：毫秒)

            float distance = CalculateUtils.calculateLineDistance(mPreviousPoint.getLatLng(), point.getLatLng()); //距离(单位米)
            float avgSpeed = distance * 1000 / duration; //计算出来的平均速度(m/s)

            //////////平均速度小于最大速度的时候才计算里程
            if (avgSpeed < MAX_SPEED) {
                mTotalDistance += distance;
            }
            mPreviousPoint = point;
        } else {
            ////////第一次进入计算逻辑
            mPreviousPoint = point;
            isNotFirst = true;
        }
    }

    @Override
    public int queryTotalDistance() {
        return (int) mTotalDistance; //总里程(单位米)
    }

}
