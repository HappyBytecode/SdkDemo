package anda.travel.driver.data.entity;

import com.contrarywind.interfaces.IPickerViewData;

public class ApplyCityEntity implements IPickerViewData {
    public String adcode; //行政编码
    public String name; //城市名
    public int isDefault;//是否默认地址 (1.是 2.否)
    public String uuid;

    @Override
    public String getPickerViewText() {
        return name;
    }
}
