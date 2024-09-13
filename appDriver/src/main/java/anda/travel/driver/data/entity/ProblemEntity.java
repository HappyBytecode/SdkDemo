package anda.travel.driver.data.entity;

import anda.travel.driver.baselibrary.utils.TypeUtil;

/**
 * 常见问题
 */
public class ProblemEntity {

    public String uuid; // "90006cbc31fbb41224452ebb2494bff0",
    public String title; // "司机百度",
    public String linkUrl; // "http://www.baidu.com",
    public String identity; // 2,
    public String tag; // null,
    public String createTime; // 1484878072000,
    public String updateTime; // null,
    public String updater; // null,
    public String appid; // "9dd58b6d5f64a22d00c3f6264f8ce001"

    public String getTitle() {
        return TypeUtil.getValue(title);
    }
}
