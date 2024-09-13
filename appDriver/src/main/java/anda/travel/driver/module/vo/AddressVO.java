package anda.travel.driver.module.vo;

import com.amap.api.maps.model.LatLng;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

import anda.travel.driver.baselibrary.utils.TypeUtil;

/**
 * 功能描述：地址信息
 */
public class AddressVO extends DataSupport implements Serializable {

    private String address;
    private String addressDetail;
    private Double lat;
    private Double lng;
    private String adcode;

    public String getAddress() {
        return TypeUtil.getValue(address);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressDetail() {
        return TypeUtil.getValue(addressDetail);
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public LatLng getLatLng() {
        if (lat == null || lng == null) return null;
        return new LatLng(lat, lng);
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    @Override
    public String toString() {
        return "AddressVO{" +
                ", address='" + address + '\'' +
                ", addressDetail='" + addressDetail + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }

}
