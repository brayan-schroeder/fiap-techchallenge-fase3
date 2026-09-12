package com.fiap.scheduling.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME =
            "appointment.exchange";

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
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}