package project.tdlogistics.orders.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import project.tdlogistics.orders.entities.Order;
import java.util.Map;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    // Add or implement more neccessary methods here
    // public Long countByOrderId(String orderId);
    public Optional<Order> findByOrderId(String orderId);
    public Optional<Order> findByOrderIdAndAgencyId(String orderId, String agencyId);

}


