package com.fiap.scheduling.dto;

import jakarta.validation.constraints.Future;

import java.time.LocalDateTime;

public record UpdateAppointmentRequest(

        Long doctorId,

        Long nurseId,

        @Future
        LocalDateTime dateTime,

        String description
) {
}