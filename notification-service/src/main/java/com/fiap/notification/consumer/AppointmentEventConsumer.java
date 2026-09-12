package com.fiap.notification.consumer;

import com.fiap.notification.dto.AppointmentEvent;
import com.fiap.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEventConsumer {

    private final NotificationService notificationService;

    public AppointmentEventConsumer(
            NotificationService notificationService
    ) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "notification.queue")
    public void consume(AppointmentEvent event) {

        notificationService.processAppointmentEvent(event);
    }
}