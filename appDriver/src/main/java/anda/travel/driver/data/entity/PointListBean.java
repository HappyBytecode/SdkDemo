package anda.travel.driver.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class PointListBean implements Parcelable {
    /**
     * address : 五缘·金绿广场
     * lng : 118.188729
     * lat : 24.52857
     * mobile : 16866666666
     * status : 1
     * sort : 1
     */

    private String address;
    private double lng;
    private double lat;
    private String mobile;
    private int status;//1已完成;2未完成,3.进行中
    private int sort;//顺序排序,
    private int type;//1:起点;2:终点

    public PointListBean() {
    }

    protected PointListBean(Parcel in) {
        address = in.readString();
        lng = in.readDouble();
        lat = in.readDouble();
        mobile = in.readString();
        status = in.readInt();
        sort = in.readInt();
        type = in.readInt();
    }

    public static final Creator<PointListBean> CREATOR = new Creator<PointListBean>() {
        @Override
        public PointListBean createFromParcel(Parcel in) {
            return new PointListBean(in);
        }

        @Override
        public PointListBean[] newArray(int size) {
            return new PointListBean[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeDouble(lng);
        dest.writeDouble(lat);
        dest.writeString(mobile);
        dest.writeInt(status);
        dest.writeInt(sort);
        dest.writeInt(type);
    }

}
