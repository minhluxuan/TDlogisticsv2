package project.tdlogistics.administrative.configurations;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfiguration {

    @Bean
    DirectExchange exchange() {
        return new DirectExchange("rpc-direct-exchange");
    }

    @Bean
    public Queue administrativeQueue() {
        return new Queue("rpc.administrative", true);
    }

    @Bean
    Binding administrativeBinding(Queue administrativeQueue, DirectExchange exchange) {
        return BindingBuilder.bind(administrativeQueue).to(exchange).with("rpc.administrative");
    }
}
