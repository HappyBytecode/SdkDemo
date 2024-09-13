package anda.travel.driver.data.entity;

import anda.travel.driver.baselibrary.utils.TypeUtil;

/**
 * @Author moyuwan
 * @Date 18/3/19
 */
public class WithdrawRuleEntity {

    private String title;
    private String content;

    public String getTitle() {
        return TypeUtil.getValue(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return TypeUtil.getValue(content);
    }

    public void setContent(String content) {
        this.content = content;
    }
}
