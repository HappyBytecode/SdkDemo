package anda.travel.driver.client.message.body;

import anda.travel.driver.client.message.Body;

/**
 * body-心跳
 *
 * @author Zoro
 * @date 2017/3/28
 */

public class HeartBeat implements Body {

    private long time;

    public HeartBeat() {
        this.time = System.currentTimeMillis();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
