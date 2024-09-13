package anda.travel.driver.data.entity;

import java.io.Serializable;

public class LogoffEntity implements Serializable {
    private boolean logoff;

    public boolean isLogoff() {
        return logoff;
    }

    public void setLogoff(boolean logoff) {
        this.logoff = logoff;
    }
}
