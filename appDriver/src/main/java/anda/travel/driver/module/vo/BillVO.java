package anda.travel.driver.module.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

import anda.travel.driver.data.entity.BillEntity;

public class BillVO {
    public static BillVO createFrom(BillEntity entity) {
        if (entity == null) return new BillVO();
        String strJson = JSON.toJSONString(entity);
        return JSON.parseObject(strJson, BillVO.class);
    }

    @JSONField(name = "totalMoney")
    public String total;//总余额

    @JSONField(name = "driverAccountBeanList")
    public List<BillIitemVO> bills;

}
