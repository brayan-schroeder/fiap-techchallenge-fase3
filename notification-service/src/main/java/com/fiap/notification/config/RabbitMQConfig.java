package com.fiap.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME =
            "appointment.exchange";

    public static final String QUEUE_NAME =
            "notification.queue";

    public static final String APPOINTMENT_CREATED_ROUTING_KEY =
            "appointment.created";

    public static final String APPOINTMENT_UPDATED_ROUTING_KEY =
            "appointment.updated";

    public static final String APPOINTMENT_CANCELLED_ROUTING_KEY =
            "appointment.cancelled";

    @Bean
    public DirectExchange appointmentExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder
                .durable(QUEUE_NAME)
                .build();
    }

    @Bean
    public Binding appointmentCreatedBinding(
            Queue notificationQueue,
            DirectExchange appointmentExchange
    ) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(appointmentExchange)
                .with(APPOINTMENT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding appointmentUpdatedBinding(
            Queue notificationQueue,
            DirectExchange appointmentExchange
    ) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(appointmentExchange)
                .with(APPOINTMENT_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding appointmentCancelledBinding(
            Queue notificationQueue,
            DirectExchange appointmentExchange
    ) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(appointmentExchange)
                .with(APPOINTMENT_CANCELLED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}