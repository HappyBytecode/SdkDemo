package anda.travel.driver.data.entity;

import java.util.List;

/**
 * 动态获取首页的Icon配置
 */
public class DynamicIconEntity {

    private int type;
    private int status;
    private List<IconBean> icon;
    private List<IconBean> screen;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<IconBean> getIcon() {
        return icon;
    }

    public void setIcon(List<IconBean> icon) {
        this.icon = icon;
    }

    public List<IconBean> getScreen() {
        return screen;
    }

    public void setScreen(List<IconBean> screen) {
        this.screen = screen;
    }

    public static class IconBean {
        /**
         * uuid : wuhihduahhfakfhakdfhakssd
         * name : 大图标
         * imgUrl : http://192.168.100.89/images//advertise/7acc9dbfa1ce4aefa517a12c49881e82/full/20200903e841697d-2c97-4ecb-ac8f-5167328ee712.jpg
         * imgIndex : 1
         * backdropColour : null
         * type : 2
         * version : v3.1.1
         * status : 1
         * href : http://www.baidu.com
         * jumpType : 1
         * openTime : 1619155379000
         * closeTime : 1619760183000
         * createTime : 1621299956000
         * updateTime : null
         * updater : null
         * code : cool
         */

        private String uuid;
        private String name;
        private String imgUrl;
        private int imgIndex;
        private String backdropColour;
        private String type;
        private String version;
        private int status;
        private String href;
        private int jumpType;
        private long openTime;
        private long closeTime;
        private long createTime;
        private String updateTime;
        private String updater;
        private String code;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public int getImgIndex() {
            return imgIndex;
        }

        public void setImgIndex(int imgIndex) {
            this.imgIndex = imgIndex;
        }

        public String getBackdropColour() {
            return backdropColour;
        }

        public void setBackdropColour(String backdropColour) {
            this.backdropColour = backdropColour;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public int getJumpType() {
            return jumpType;
        }

        public void setJumpType(int jumpType) {
            this.jumpType = jumpType;
        }

        public long getOpenTime() {
            return openTime;
        }

        public void setOpenTime(long openTime) {
            this.openTime = openTime;
        }

        public long getCloseTime() {
            return closeTime;
        }

        public void setCloseTime(long closeTime) {
            this.closeTime = closeTime;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getUpdater() {
            return updater;
        }

        public void setUpdater(String updater) {
            this.updater = updater;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

}
