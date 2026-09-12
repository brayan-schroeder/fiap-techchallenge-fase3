package com.fiap.scheduling.dto;

import com.fiap.scheduling.entity.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponse(

        Long id,

        Long patientId,

        Long doctorId,

        Long nurseId,

        LocalDateTime dateTime,

        AppointmentStatus status,

        String description,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}