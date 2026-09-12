package com.fiap.scheduling.controller;

import com.fiap.scheduling.dto.AppointmentResponse;
import com.fiap.scheduling.dto.CreateAppointmentRequest;
import com.fiap.scheduling.dto.UpdateAppointmentRequest;
import com.fiap.scheduling.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(
            AppointmentService appointmentService
    ) {
        this.appointmentService = appointmentService;
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse create(
            @Valid @RequestBody CreateAppointmentRequest request
    ) {
        return appointmentService.create(request);
    }

    @PreAuthorize("""
        hasAnyRole('DOCTOR', 'NURSE')
        or @securityService.canAccessAppointment(#id, authentication)
        """)
    @GetMapping("/{id}")
    public AppointmentResponse findById(
            @PathVariable Long id
    ) {
        return appointmentService.findById(id);
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE') or @securityService.canAccessPatient(#patientId, authentication)")
    @GetMapping("/patient/{patientId}")
    public List<AppointmentResponse> findByPatient(
            @PathVariable Long patientId
    ) {
        return appointmentService.findByPatient(patientId);
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE') or @securityService.canAccessPatient(#patientId, authentication)")
    @GetMapping("/patient/{patientId}/future")
    public List<AppointmentResponse> findFutureByPatient(
            @PathVariable Long patientId
    ) {
        return appointmentService.findFutureByPatient(patientId);
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE')")
    @PutMapping("/{id}")
    public AppointmentResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentRequest request
    ) {
        return appointmentService.update(id, request);
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE')")
    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancel(
            @PathVariable Long id
    ) {
        return appointmentService.cancel(id);
    }
}