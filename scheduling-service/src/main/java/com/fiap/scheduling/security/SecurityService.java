package com.fiap.scheduling.security;

import com.fiap.scheduling.entity.Patient;
import com.fiap.scheduling.entity.User;
import com.fiap.scheduling.repository.PatientRepository;
import com.fiap.scheduling.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.fiap.scheduling.entity.Appointment;
import com.fiap.scheduling.repository.AppointmentRepository;

@Service("securityService")
public class SecurityService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    public SecurityService(
            UserRepository userRepository,
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository
    ) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public boolean canAccessPatient(
            Long patientId,
            Authentication authentication
    ) {

        if (authentication == null) {
            return false;
        }

        boolean isProfessional = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_DOCTOR")
                                || authority.getAuthority().equals("ROLE_NURSE")
                );

        if (isProfessional) {
            return true;
        }

        User user = userRepository.findByEmail(
                authentication.getName()
        ).orElse(null);

        if (user == null) {
            return false;
        }

        Patient patient = patientRepository
                .findByUserId(user.getId())
                .orElse(null);

        if (patient == null) {
            return false;
        }

        return patient.getId().equals(patientId);
    }

    public boolean canAccessAppointment(
            Long appointmentId,
            Authentication authentication
    ) {

        if (authentication == null) {
            return false;
        }

        boolean isProfessional = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_DOCTOR")
                                || authority.getAuthority().equals("ROLE_NURSE")
                );

        if (isProfessional) {
            return true;
        }

        User user = userRepository.findByEmail(
                authentication.getName()
        ).orElse(null);

        if (user == null) {
            return false;
        }

        Patient patient = patientRepository
                .findByUserId(user.getId())
                .orElse(null);

        if (patient == null) {
            return false;
        }

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElse(null);

        if (appointment == null) {
            return false;
        }

        return appointment.getPatient().getId().equals(patient.getId());
    }
}