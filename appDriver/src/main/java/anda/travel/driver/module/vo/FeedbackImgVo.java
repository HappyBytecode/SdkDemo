package anda.travel.driver.module.vo;

public class FeedbackImgVo {
    /**
     * 是否显示添加图片图标
     */
    private boolean showAddImg;
    /**
     * 图片路径
     */
    private String path;

    public boolean isShowAddImg() {
        return showAddImg;
    }

    public void setShowAddImg(boolean showAddImg) {
        this.showAddImg = showAddImg;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public FeedbackImgVo(boolean showAddImg, String path) {
        this.showAddImg = showAddImg;
        this.path = path;
    }
}
