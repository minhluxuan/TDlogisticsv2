package project.tdlogistics.orders.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.tdlogistics.orders.entities.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Add or implement more neccessary methods here
    // public Long countByOrderId(String orderId);
    public Optional<Order> findByOrderId(String orderId);
    public Optional<Order> findByOrderIdAndAgencyId(String orderId, String agencyId);
}
