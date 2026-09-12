package com.fiap.scheduling.messaging;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentEvent(

        UUID eventId,

        String eventType,

        Long appointmentId,

        Long patientId,

        String patientName,

        String patientEmail,

        LocalDateTime appointmentDateTime
) {
}