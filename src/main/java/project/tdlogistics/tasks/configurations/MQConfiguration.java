package project.tdlogistics.tasks.configurations;

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

    @Bean
    public Queue staffsQueue() {
        return new Queue("rpc.staffs", true);
    }

    @Bean
    Binding staffsBinding(Queue staffsQueue, DirectExchange exchange) {
        return BindingBuilder.bind(staffsQueue).to(exchange).with("rpc.staffs");
    }

    @Bean
    public Queue administrativeQueue() {
        return new Queue("rpc.administrative", true);
    }

    @Bean
    Binding administrativeBinding(Queue administrativeQueue, DirectExchange exchange) {
        return BindingBuilder.bind(administrativeQueue).to(exchange).with("rpc.administrative");
    }

    @Bean
    public Queue agencyQueue() {
        return new Queue("rpc.agency", true);
    }

    @Bean
    Binding agencyBinding(Queue agencyQueue, DirectExchange exchange) {
        return BindingBuilder.bind(agencyQueue).to(exchange).with("rpc.agency");
    }

    @Bean
    public Queue fileQueue() {
        return new Queue("rpc.file", true);
    }

    @Bean
    Binding fileBinding(Queue fileQueue, DirectExchange exchange) {
        return BindingBuilder.bind(fileQueue).to(exchange).with("rpc.file");
    }

    @Bean
    public Queue tasksQueue() {
        return new Queue("rpc.tasks", true);
    }

    @Bean
    Binding tasksBinding(Queue tasksQueue, DirectExchange exchange) {
        return BindingBuilder.bind(tasksQueue).to(exchange).with("rpc.tasks");
    }

}
