package project.tdlogistics.orders.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import project.tdlogistics.orders.entities.Order;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    public Optional<Order> findByOrderId(String orderId);
    public Optional<Order> findByOrderIdAndAgencyId(String orderId, String agencyId);

}


