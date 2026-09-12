package com.fiap.scheduling.repository;

import com.fiap.scheduling.entity.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NurseRepository extends JpaRepository<Nurse, Long> {
}