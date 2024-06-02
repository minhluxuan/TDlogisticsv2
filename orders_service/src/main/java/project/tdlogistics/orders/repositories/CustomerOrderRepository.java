package project.tdlogistics.orders.repositories;

import java.util.Map;

public interface CustomerOrderRepository {
    public int cancelOrderWithTimeConstraint(Map<String, Object> conditions);
    public int cancelOrderWithoutTimeConstraint(Map<String, Object> conditions);
}
