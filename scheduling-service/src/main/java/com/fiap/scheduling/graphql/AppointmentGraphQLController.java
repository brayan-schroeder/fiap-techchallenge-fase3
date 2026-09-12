package com.fiap.scheduling.graphql;

import com.fiap.scheduling.dto.AppointmentResponse;
import com.fiap.scheduling.service.AppointmentService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AppointmentGraphQLController {

    private final AppointmentService appointmentService;

    public AppointmentGraphQLController(
            AppointmentService appointmentService
    ) {
        this.appointmentService = appointmentService;
    }

    @QueryMapping
    @PreAuthorize("""
            hasAnyRole('DOCTOR', 'NURSE')
            or @securityService.canAccessPatient(#patientId, authentication)
            """)
    public List<AppointmentResponse> patientHistory(
            @Argument Long patientId
    ) {
        return appointmentService.findByPatient(patientId);
    }

    @QueryMapping
    @PreAuthorize("""
            hasAnyRole('DOCTOR', 'NURSE')
            or @securityService.canAccessPatient(#patientId, authentication)
            """)
    public List<AppointmentResponse> futureAppointments(
            @Argument Long patientId
    ) {
        return appointmentService.findFutureByPatient(patientId);
    }

    @QueryMapping
    @PreAuthorize("""
        hasAnyRole('DOCTOR', 'NURSE')
        or @securityService.canAccessAppointment(#id, authentication)
        """)
    public AppointmentResponse appointment(
            @Argument Long id
    ) {
        return appointmentService.findById(id);
    }
}