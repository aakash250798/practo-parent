package com.practo.appointment.service;

import com.practo.appointment.entity.Patient;
import com.practo.appointment.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    @Autowired
    PatientRepository patientRepository;

    public void bulkSavePatients(List<Patient> patientList){
        for(Patient p : patientList){
            patientRepository.save(p);
        }

    }
}
