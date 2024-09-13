package anda.travel.driver.module.order.ongoing.calculate;

import anda.travel.driver.data.entity.PointEntity;

public interface ICalculate {

    /**
     * 增加点
     *
     * @param point
     */
    void addPoint(PointEntity point);

    /**
     * 获取里程
     */
    int queryTotalDistance();

}
