package project.tdlogistics.agency_company.configurations;

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
    public Queue agencyQueue() {
        return new Queue("rpc.agency", true);
    }

    @Bean
    Binding agencyBinding(Queue agencyQueue, DirectExchange exchange) {
        return BindingBuilder.bind(agencyQueue).to(exchange).with("rpc.agency");
    }

    // @Bean
    // public Queue commonQueue() {
    //     return new Queue("rpc.common", true);
    // }

    // @Bean
    // Binding commonBinding(Queue commonQueue, DirectExchange exchange) {
    //     return BindingBuilder.bind(commonQueue).to(exchange).with("rpc.#");
    // }
}
