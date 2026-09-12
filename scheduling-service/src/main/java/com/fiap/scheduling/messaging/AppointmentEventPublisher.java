package com.fiap.scheduling.messaging;

import com.fiap.scheduling.config.RabbitMQConfig;
import com.fiap.scheduling.entity.Appointment;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AppointmentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public AppointmentEventPublisher(
            RabbitTemplate rabbitTemplate
    ) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCreated(Appointment appointment) {

        AppointmentEvent event = buildEvent(
                appointment,
                "APPOINTMENT_CREATED"
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.APPOINTMENT_CREATED_ROUTING_KEY,
                event
        );
    }

    public void publishUpdated(Appointment appointment) {

        AppointmentEvent event = buildEvent(
                appointment,
                "APPOINTMENT_UPDATED"
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.APPOINTMENT_UPDATED_ROUTING_KEY,
                event
        );
    }

    public void publishCancelled(Appointment appointment) {

        AppointmentEvent event = buildEvent(
                appointment,
                "APPOINTMENT_CANCELLED"
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.APPOINTMENT_CANCELLED_ROUTING_KEY,
                event
        );
    }

    private AppointmentEvent buildEvent(
            Appointment appointment,
            String eventType
    ) {

        return new AppointmentEvent(
                UUID.randomUUID(),
                eventType,
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getUser().getName(),
                appointment.getPatient().getUser().getEmail(),
                appointment.getDateTime()
        );
    }
}