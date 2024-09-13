package anda.travel.driver.data.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 功能描述：
 */
public class OrderCostEntity implements Serializable {

    public OrderCostEntity() {
    }

    public OrderCostEntity(List<OrderCostItemEntity> costItemBean, Double totalFare) {
        this.costItemBean = costItemBean;
        this.totalFare = totalFare;
    }

    public List<OrderCostItemEntity> costItemBean;
    public Double totalFare;

}
