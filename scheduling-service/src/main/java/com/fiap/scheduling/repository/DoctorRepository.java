package com.fiap.scheduling.repository;

import com.fiap.scheduling.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}