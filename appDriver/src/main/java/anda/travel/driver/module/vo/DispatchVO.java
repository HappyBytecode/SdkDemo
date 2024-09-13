package anda.travel.driver.module.vo;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.model.LatLng;

import java.io.Serializable;

import anda.travel.driver.data.entity.DispatchEntity;
import anda.travel.driver.baselibrary.utils.TypeUtil;

public class DispatchVO implements Serializable {

    public String content; ///返回的内容
    public String uuid; // 调度编号
    public Double endLat; //调度结束经度
    public Double endLng; //调度结束维度
    public String endAddress; //调度结束地址
    public Double actualTrip; //最后一次上传的调度里程
    public Integer endRange; //调度终点范围
    public String driverUuid;
    public String vehicleUuid;
    public long startTime;
    public String startCity;
    public String startAddress;
    public double startLat;
    public double startLng;
    public String startAccount;
    public String endTime;
    public String endCity;
    public String endReason;
    public String endAccount;
    public int mainStatus;
    public int subStatus;
    public String orderUuid;
    public double planTrip;
    public int planTime;
    public String actualTime;
    public String updater;
    public long updateTime;
    public long createTime;
    public String lastUpdTime;

    public LatLng getEndLatLng() {
        if (endLat == null || endLng == null
                || endLat == 0 || endLng == 0)
            return null;
        else
            return new LatLng(endLat, endLng);
    }

    public String getUuid() {
        return TypeUtil.getValue(uuid);
    }

    public static DispatchVO createFrom(DispatchEntity entity) {
        if (entity == null) return new DispatchVO();
        String strJson = JSON.toJSONString(entity);
        return JSON.parseObject(strJson, DispatchVO.class);
    }
}
