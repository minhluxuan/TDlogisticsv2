package project.tdlogistics.orders.configurations;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Do not change
@Configuration
public class MQConfiguration {

    @Bean
    DirectExchange exchange() {
        return new DirectExchange("rpc-direct-exchange");
    }

    @Bean
    public Queue orderQueue() {
        return new Queue("rpc.orders", true);
    }

    @Bean
    Binding orderBinding(Queue orderQueue, DirectExchange exchange) {
        return BindingBuilder.bind(orderQueue).to(exchange).with("rpc.orders");
    }
}
