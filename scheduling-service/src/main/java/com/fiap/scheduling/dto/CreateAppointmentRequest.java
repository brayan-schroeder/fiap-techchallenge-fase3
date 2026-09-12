package com.fiap.scheduling.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateAppointmentRequest(

        @NotNull
        Long patientId,

        Long doctorId,

        Long nurseId,

        @NotNull
        @Future
        LocalDateTime dateTime,

        String description
) {
}