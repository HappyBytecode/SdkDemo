package anda.travel.driver.data.entity;

import java.util.List;

public class UserCenterMenuEntity {

    private List<MenusBean> horizontalMenus;
    private List<MenusBean> verticalMenus;

    public List<MenusBean> getHorizontalMenus() {
        return horizontalMenus;
    }

    public void setHorizontalMenus(List<MenusBean> horizontalMenus) {
        this.horizontalMenus = horizontalMenus;
    }

    public List<MenusBean> getVerticalMenus() {
        return verticalMenus;
    }

    public void setVerticalMenus(List<MenusBean> verticalMenus) {
        this.verticalMenus = verticalMenus;
    }

    public static class MenusBean {
        /**
         * code : drvCode
         * name : 码上有钱
         * type : native
         * icon :
         * url :
         * sort : 1
         */

        private String code;
        private String name;
        private String type;
        private String icon;
        private String url;
        private int sort;
        private int banner;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getBanner() {
            return banner;
        }

        public void setBanner(int banner) {
            this.banner = banner;
        }
    }

}
