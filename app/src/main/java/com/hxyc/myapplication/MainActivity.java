package com.hxyc.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.auth.LoginAuthListener;
import anda.travel.driver.auth.OrderStatusListener;
import anda.travel.driver.auth.RegisterListener;

public class MainActivity extends AppCompatActivity {
    private TextView tv_order_status;
    private boolean isOrderGoing = false;
    private String mOrderUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_order_status = findViewById(R.id.tv_order_status);
        HxClientManager.getInstance().reqOrderStatus(new OrderStatusListener() {
            @Override
            public void orderStatusObtain(boolean isOrdering, String orderUuid) {
                if (isOrdering) {
                    tv_order_status.setText("有进行中订单");
                    mOrderUuid = orderUuid;
                } else {
                    tv_order_status.setText("无订单");
                }
                isOrderGoing = isOrdering;
            }
        });
    }

    public void onClick(View view) {
        if (view.getId() == R.id.tv_login) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("mobile", "18956248181");
            HxClientManager.getInstance().loginAuth(hashMap, new LoginAuthListener() {
                @Override
                public void loginAuthSuccess() {

                }

                @Override
                public void loginAuthFail(int failCode, String errorMsg) {

                }
            });
        } else if (view.getId() == R.id.tv) {
            HxClientManager.getInstance().startHxDriver();
        } else if (view.getId() == R.id.tv_order_status) {
            if (isOrderGoing) {
                HxClientManager.getInstance().openOrderByStatus(mOrderUuid);
            }
        } else if (view.getId() == R.id.tv_register) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("vehicleNo", "皖A95951");
            hashMap.put("avatar", "http://oss-test.hexingyueche.com/www/images/9f946eb4ca7b4b128cca20b234d1cf31/apply/full/20210607fa57c463-b8b7-4950-b3e6-48ddaaa56c1a.jpg");
            hashMap.put("idcard", "342622198903212157");
            hashMap.put("actualName", "潘德");
            hashMap.put("mobile", "18956248182");
            hashMap.put("sex", "1");
            hashMap.put("vehicleColor", "红色");
            hashMap.put("vehicleBrand", "江淮");
            hashMap.put("vehicleModel", "A50");
            hashMap.put("idcardFrontPic", "http://oss-test.hexingyueche.com/www/images/9f946eb4ca7b4b128cca20b234d1cf31/apply/full/20210607fa57c463-b8b7-4950-b3e6-48ddaaa56c1a.jpg");
            hashMap.put("idcardReversePic", "http://oss-test.hexingyueche.com/www/images/9f946eb4ca7b4b128cca20b234d1cf31/apply/full/20210607fa57c463-b8b7-4950-b3e6-48ddaaa56c1a.jpg");
            hashMap.put("country", "中国");
            hashMap.put("censusRegister", "中国安徽合肥包河区");
            hashMap.put("address", "中国安徽合肥包河区街道小区1号");
            hashMap.put("birthday", "1989-12-25");
            hashMap.put("licence", "3343423434");
            hashMap.put("licencePic", "http://oss-test.hexingyueche.com/www/images/9f946eb4ca7b4b128cca20b234d1cf31/apply/full/20210607fa57c463-b8b7-4950-b3e6-48ddaaa56c1a.jpg");
            hashMap.put("firstGettime", "2000-12-25");
            hashMap.put("driverNation", "汉族");
            hashMap.put("driverLicenseOn", "2000-12-25");
            hashMap.put("driverLicenseOff", "2010-12-25");
            hashMap.put("vehicleLicensePic", "http://oss-test.hexingyueche.com/www/images/9f946eb4ca7b4b128cca20b234d1cf31/apply/full/20210607fa57c463-b8b7-4950-b3e6-48ddaaa56c1a.jpg");
            hashMap.put("qualityCertificatePic", "http://oss-test.hexingyueche.com/www/images/9f946eb4ca7b4b128cca20b234d1cf31/apply/full/20210607fa57c463-b8b7-4950-b3e6-48ddaaa56c1a.jpg");
            hashMap.put("certificateA", "3232323232322323");
            hashMap.put("networkCarIssueOrganization", "网络预约出租汽车驾驶员证发证机构");
            hashMap.put("networkCarIssueDate", "2022-12-25");
            hashMap.put("getNetworkCarProofDate", "2022-12-25");
            hashMap.put("networkCarProofOn", "2010-12-25");
            hashMap.put("networkCarProofOff", "2022-12-25");
            hashMap.put("emergencyContactPhone", "189565656565");
            hashMap.put("emergencyContact", "张三");
            hashMap.put("emergencyContactAddress", "紧急情况联系人通讯地址");
            hashMap.put("transportLicensePic", "3232323232322323");
            hashMap.put("adcode", "340104");
            HxClientManager.getInstance().register(hashMap, new RegisterListener() {
                @Override
                public void registerSuccess() {

                }

                @Override
                public void registerFail(int failCode, String errorMsg) {

                }

            });
        }
    }
}