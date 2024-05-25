package project.tdlogistics.orders.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import project.tdlogistics.orders.services.OrderService;

@RestController
@RequestMapping("/v2/orders")
public class OrderRestController {
    @Autowired
    OrderService orderService;

    // Implement relative methods here
}
