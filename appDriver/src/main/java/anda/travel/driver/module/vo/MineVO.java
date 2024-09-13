package anda.travel.driver.module.vo;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import anda.travel.driver.data.entity.DriverEntity;
import anda.travel.driver.baselibrary.utils.TypeUtil;

/**
 * 功能描述："我"中使用的数据模型
 */
public class MineVO {

    public static MineVO createFrom(DriverEntity entity) {
        if (entity == null) return new MineVO();
        String strJson = JSON.toJSONString(entity);
        return JSON.parseObject(strJson, MineVO.class);
    }

    @JSONField(name = "avatar")
    public String driverAvatar; //司机头像

    @JSONField(name = "name")
    public String driverName; //司机姓名

    public String actualName; //司机真实姓名

    @JSONField(name = "vehicleTypeName")
    public String carType; //车辆类型

    @JSONField(name = "vehicleNo")
    public String licencePlate; //汽车牌照

    @JSONField(name = "vehicleBrand")
    public String brandName; //汽车品牌

    @JSONField(name = "vehicleColor")
    public String carColor;//车辆外观颜色

    @JSONField(name = "vehicleModel")
    public String carModel; //汽车品牌

    public String labelName; //所属公司简称

    public String getBrandAndColor() {
        StringBuilder str = new StringBuilder();
        if (!TextUtils.isEmpty(carType)) {
            str.append(carType);
            str.append("  ");
        }
        if (!TextUtils.isEmpty(labelName)) {
            str.append(labelName);
            return str.toString();
        }
        if (!TextUtils.isEmpty(carColor)) {
            str.append(carColor);
            str.append("·");
        }
        if (!TextUtils.isEmpty(brandName)) {
            str.append(brandName);
        }
        if (!TextUtils.isEmpty(carModel)) {
            str.append(carModel);
        }
        return str.toString();
    }

    public String getName() {
        if (!TextUtils.isEmpty(driverName)) return driverName;
        return TypeUtil.getValue(actualName);
    }
}
