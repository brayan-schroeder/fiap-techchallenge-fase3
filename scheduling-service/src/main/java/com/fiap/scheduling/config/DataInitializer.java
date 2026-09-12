package com.fiap.scheduling.config;

import com.fiap.scheduling.entity.Doctor;
import com.fiap.scheduling.entity.Nurse;
import com.fiap.scheduling.entity.Patient;
import com.fiap.scheduling.entity.Role;
import com.fiap.scheduling.entity.User;
import com.fiap.scheduling.repository.DoctorRepository;
import com.fiap.scheduling.repository.NurseRepository;
import com.fiap.scheduling.repository.PatientRepository;
import com.fiap.scheduling.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UserRepository userRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            NurseRepository nurseRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            if (userRepository.count() > 0) {
                return;
            }

            User patientUser = userRepository.save(
                    User.builder()
                            .name("João da Silva")
                            .email("joao@email.com")
                            .password(passwordEncoder.encode("123456"))
                            .role(Role.PATIENT)
                            .build()
            );

            User secondPatientUser = userRepository.save(
                    User.builder()
                            .name("Ana Souza")
                            .email("ana@email.com")
                            .password(passwordEncoder.encode("123456"))
                            .role(Role.PATIENT)
                            .build()
            );

            User doctorUser = userRepository.save(
                    User.builder()
                            .name("Dr. Carlos")
                            .email("carlos@email.com")
                            .password(passwordEncoder.encode("123456"))
                            .role(Role.DOCTOR)
                            .build()
            );

            User nurseUser = userRepository.save(
                    User.builder()
                            .name("Enfermeira Maria")
                            .email("maria@email.com")
                            .password(passwordEncoder.encode("123456"))
                            .role(Role.NURSE)
                            .build()
            );

            patientRepository.save(
                    Patient.builder()
                            .user(patientUser)
                            .birthDate(LocalDate.of(1995, 5, 10))
                            .build()
            );

            patientRepository.save(
                    Patient.builder()
                            .user(secondPatientUser)
                            .birthDate(LocalDate.of(1998, 8, 20))
                            .build()
            );

            doctorRepository.save(
                    Doctor.builder()
                            .user(doctorUser)
                            .crm("123456")
                            .specialty("Cardiology")
                            .build()
            );

            nurseRepository.save(
                    Nurse.builder()
                            .user(nurseUser)
                            .coren("987654")
                            .build()
            );
        };
    }
}