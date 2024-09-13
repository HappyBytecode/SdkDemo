package com.hxyc.myapplication;

import org.litepal.LitePalApplication;

import anda.travel.driver.auth.HxClientManager;

public class TestApplication extends LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        HxClientManager.getInstance().init(this);
    }
}
