package anda.travel.driver.module.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

import anda.travel.driver.data.entity.ProblemEntity;

public class FaqVO implements Serializable {

    public static FaqVO createFrom(ProblemEntity entity) {
        if (entity == null) return new FaqVO();
        String strJson = JSON.toJSONString(entity);
        return JSON.parseObject(strJson, FaqVO.class);
    }

    @JSONField(name = "title")
    public String title;//标题

    @JSONField(name = "linkUrl")
    public String link_url;//链接
}
