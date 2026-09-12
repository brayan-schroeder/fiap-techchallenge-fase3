package com.fiap.notification.service;

import com.fiap.notification.dto.AppointmentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger =
            LoggerFactory.getLogger(NotificationService.class);

    public void processAppointmentEvent(
            AppointmentEvent event
    ) {

        logger.info(
                "Processing appointment notification. " +
                        "Event ID: {}, Event type: {}, Appointment ID: {}, " +
                        "Patient: {}, Email: {}, Appointment date: {}",
                event.eventId(),
                event.eventType(),
                event.appointmentId(),
                event.patientName(),
                event.patientEmail(),
                event.appointmentDateTime()
        );

        switch (event.eventType()) {

            case "APPOINTMENT_CREATED" ->
                    sendReminder(event);

            case "APPOINTMENT_UPDATED" ->
                    sendUpdatedAppointmentReminder(event);

            case "APPOINTMENT_CANCELLED" ->
                    sendCancelledAppointmentNotification(event);

            default ->
                    logger.warn(
                            "Unknown appointment event type: {}",
                            event.eventType()
                    );
        }
    }

    private void sendReminder(
            AppointmentEvent event
    ) {

        logger.info(
                "Reminder sent to patient {} at {} " +
                        "for appointment {} (eventId={})",
                event.patientEmail(),
                event.appointmentDateTime(),
                event.appointmentId(),
                event.eventId()
        );
    }

    private void sendUpdatedAppointmentReminder(
            AppointmentEvent event
    ) {

        logger.info(
                "Updated appointment reminder sent to patient {} " +
                        "for appointment {} at {} (eventId={})",
                event.patientEmail(),
                event.appointmentId(),
                event.appointmentDateTime(),
                event.eventId()
        );
    }

    private void sendCancelledAppointmentNotification(
            AppointmentEvent event
    ) {

        logger.info(
                "Appointment cancellation notification sent to patient {} " +
                        "for appointment {} (eventId={})",
                event.patientEmail(),
                event.appointmentId(),
                event.eventId()
        );
    }
}