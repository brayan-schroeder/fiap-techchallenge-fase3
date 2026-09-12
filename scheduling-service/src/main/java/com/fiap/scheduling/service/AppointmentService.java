package com.fiap.scheduling.service;

import com.fiap.scheduling.dto.AppointmentResponse;
import com.fiap.scheduling.dto.CreateAppointmentRequest;
import com.fiap.scheduling.dto.UpdateAppointmentRequest;
import com.fiap.scheduling.entity.Appointment;
import com.fiap.scheduling.entity.AppointmentStatus;
import com.fiap.scheduling.entity.Doctor;
import com.fiap.scheduling.entity.Nurse;
import com.fiap.scheduling.entity.Patient;
import com.fiap.scheduling.repository.AppointmentRepository;
import com.fiap.scheduling.repository.DoctorRepository;
import com.fiap.scheduling.repository.NurseRepository;
import com.fiap.scheduling.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fiap.scheduling.exception.BusinessException;
import com.fiap.scheduling.exception.ResourceNotFoundException;
import com.fiap.scheduling.messaging.AppointmentEventPublisher;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final NurseRepository nurseRepository;
    private final AppointmentEventPublisher eventPublisher;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            NurseRepository nurseRepository,
            AppointmentEventPublisher eventPublisher
    ) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.nurseRepository = nurseRepository;
        this.eventPublisher = eventPublisher;
    }

    public AppointmentResponse create(CreateAppointmentRequest request) {

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Doctor doctor = null;

        if (request.doctorId() != null) {
            doctor = doctorRepository.findById(request.doctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        }

        Nurse nurse = null;

        if (request.nurseId() != null) {
            nurse = nurseRepository.findById(request.nurseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Nurse not found"));
        }

        if (doctor == null && nurse == null) {
            throw new BusinessException(
                    "Appointment must have a doctor or nurse"
            );
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .nurse(nurse)
                .dateTime(request.dateTime())
                .description(request.description())
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        eventPublisher.publishCreated(saved);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse findById(Long id) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        return toResponse(appointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> findByPatient(Long patientId) {

        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found");
        }

        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> findFutureByPatient(Long patientId) {

        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found");
        }

        return appointmentRepository
                .findByPatientIdAndDateTimeAfter(
                        patientId,
                        LocalDateTime.now()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AppointmentResponse update(
            Long id,
            UpdateAppointmentRequest request
    ) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessException(
                    "Cancelled appointment cannot be updated"
            );
        }

        if (request.doctorId() != null) {
            Doctor doctor = doctorRepository.findById(request.doctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

            appointment.setDoctor(doctor);
        }

        if (request.nurseId() != null) {
            Nurse nurse = nurseRepository.findById(request.nurseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Nurse not found"));

            appointment.setNurse(nurse);
        }

        if (request.dateTime() != null) {
            appointment.setDateTime(request.dateTime());
        }

        if (request.description() != null) {
            appointment.setDescription(request.description());
        }

        Appointment saved = appointmentRepository.save(appointment);

        eventPublisher.publishUpdated(saved);

        return toResponse(saved);
    }

    public AppointmentResponse cancel(Long id) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Appointment not found")
                );

        appointment.setStatus(AppointmentStatus.CANCELLED);

        Appointment saved = appointmentRepository.save(appointment);

        eventPublisher.publishCancelled(saved);

        return toResponse(saved);
    }

    private AppointmentResponse toResponse(Appointment appointment) {

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getDoctor() != null
                        ? appointment.getDoctor().getId()
                        : null,
                appointment.getNurse() != null
                        ? appointment.getNurse().getId()
                        : null,
                appointment.getDateTime(),
                appointment.getStatus(),
                appointment.getDescription(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }
}