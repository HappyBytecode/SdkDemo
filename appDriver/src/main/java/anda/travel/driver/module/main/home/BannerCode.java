package anda.travel.driver.module.main.home;

public enum BannerCode {
    DRIVER_SCORE("driver_score");

    public final String value;

    BannerCode(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
