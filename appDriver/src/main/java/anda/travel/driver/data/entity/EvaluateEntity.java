package anda.travel.driver.data.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 乘客评价
 */
public class EvaluateEntity implements Serializable {

    /**
     * count 总数
     */
    private int count;
    /**
     * badCount 差评数
     */
    private int badCount;
    /**
     * commonCount 一般评价数
     */
    private int commonCount;
    /**
     * niceCount   好评数
     */
    private int niceCount;
    /**
     * tagList 司机评价标签列表
     */
    private List<TagEntity> tagList;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getBadCount() {
        return badCount;
    }

    public void setBadCount(int badCount) {
        this.badCount = badCount;
    }

    public int getCommonCount() {
        return commonCount;
    }

    public void setCommonCount(int commonCount) {
        this.commonCount = commonCount;
    }

    public int getNiceCount() {
        return niceCount;
    }

    public void setNiceCount(int niceCount) {
        this.niceCount = niceCount;
    }

    public List<TagEntity> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagEntity> tagList) {
        this.tagList = tagList;
    }
}
