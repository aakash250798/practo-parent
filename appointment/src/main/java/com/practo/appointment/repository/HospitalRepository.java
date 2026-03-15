package com.practo.appointment.repository;

import com.practo.appointment.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalRepository extends JpaRepository<Hospital,String> {
}
