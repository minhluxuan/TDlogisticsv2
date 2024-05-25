package project.tdlogistics.orders.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.tdlogistics.orders.entities.Order;

public interface OrderRepository extends JpaRepository<Order, String> {
    // Add or implement more neccessary methods here
}
