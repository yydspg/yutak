package com.yutak.vertx.kit;

import com.yutak.vertx.anno.Order;

public class OrderKit {
    public static int getOrder(Object obj ,int defaultOrder) {
        if(obj instanceof Order){
            Order order = (Order) obj;
            return order.order();
        }
        Order orderAnno = obj.getClass().getAnnotation(Order.class);
        return orderAnno == null ? defaultOrder:orderAnno.order();
    }
}
