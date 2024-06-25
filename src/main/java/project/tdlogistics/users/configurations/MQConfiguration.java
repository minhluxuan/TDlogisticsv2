package project.tdlogistics.users.configurations;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;

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
    public Queue partnerStaffQueue() {
        return new Queue("rpc.partner_staff", true);
    }

    @Bean
    Binding partnerStaffBinding(Queue partnerStaffQueue, DirectExchange exchange) {
        return BindingBuilder.bind(partnerStaffQueue).to(exchange).with("rpc.partner_staff");
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
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setReplyTimeout(5000);
        return rabbitTemplate;
    }
}
