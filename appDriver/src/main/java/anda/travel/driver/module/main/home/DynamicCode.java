package anda.travel.driver.module.main.home;

public enum DynamicCode {
    HEATMAP("heatmap"),
    ORDER("order"),
    RANKLIST("ranklist"),
    MESSAGE("message");

    public final String value;

    DynamicCode(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
