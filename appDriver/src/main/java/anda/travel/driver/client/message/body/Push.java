package anda.travel.driver.client.message.body;

import anda.travel.driver.client.message.Body;

/**
 * Push
 *
 * @author Zoro
 * @date 2017/3/27
 */

public class Push<D> implements Body {

    private int operateCode;//操作码
    private D data;

    public Push() {

    }

    public Push(int operateCode, D data) {
        this.operateCode = operateCode;
        this.data = data;
    }

    public int getOperateCode() {
        return operateCode;
    }

    public void setOperateCode(int operateCode) {
        this.operateCode = operateCode;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }
}
