package project.tdlogistics.users.configurations;

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
    public Queue usersQueue() {
        return new Queue("rpc.users", true);
    }

    @Bean
    Binding usersBinding(Queue usersQueue, DirectExchange exchange) {
        return BindingBuilder.bind(usersQueue).to(exchange).with("rpc.users");
    }

    @Bean
    public Queue emailsQueue() {
        return new Queue("rpc.emails", true);
    }

    @Bean
    Binding emailsBinding(Queue emailsQueue, DirectExchange exchange) {
        return BindingBuilder.bind(emailsQueue).to(exchange).with("rpc.emails");
    }
}
