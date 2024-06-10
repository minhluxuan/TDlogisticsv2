package project.tdlogistics.orders.repositories;

import java.util.Map;

public interface OrderRepositoryInterface {

    int cancelOrderWithTimeConstraint(Map<String, Object> conditions);
    int cancelOrderWithoutTimeConstraint(Map<String, Object> conditions);
}
