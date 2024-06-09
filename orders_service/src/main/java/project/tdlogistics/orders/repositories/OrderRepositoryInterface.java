package project.tdlogistics.orders.repositories;

import java.util.Map;

import project.tdlogistics.orders.entities.Order;

public interface OrderRepositoryInterface {
    public int cancelOrderWithTimeConstraint(Map<String, Object> conditions);
    public int cancelOrderWithoutTimeConstraint(Map<String, Object> conditions);
        public Order getOneOrder(String orderId, String postalCode);
}
