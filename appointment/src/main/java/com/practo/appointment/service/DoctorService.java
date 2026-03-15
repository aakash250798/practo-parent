package com.practo.appointment.service;

import com.practo.appointment.dto.DoctorDTO;
import com.practo.appointment.dto.RequestDTO;
import com.practo.appointment.entity.Appointment;
import com.practo.appointment.entity.Doctor;
import com.practo.appointment.mapping.DoctorMapper;
import com.practo.appointment.repository.DoctorRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DoctorService {

    // field injection to be removed
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    public DoctorService(DoctorRepository doctorRepository,
                         DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
    }

    public List<Doctor> findAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<DoctorDTO> findAllDoctorsBySpecialization(String specialization) {
        List<Doctor> doctors = doctorRepository.findAll();
        List<Doctor> specializedDoctors = new ArrayList<>();
        for (Doctor doctor : doctors) {
            if (doctor.getSpecialization().name().equals(specialization))
                specializedDoctors.add(doctor);
        }
        List<DoctorDTO> doctorDTOList = new ArrayList<>();
        for(Doctor doctor : specializedDoctors){
            doctorDTOList.add(doctorMapper.mapToDTO(doctor));
        }
        return doctorDTOList;
    }

    public List<Appointment> findAppointmentByDoctor(String id) {
        //make sure appointment is correct
        Doctor doctor = doctorRepository.findById(id).get();
        return doctor.getAppointments();
    }

    public void bulkSaveDoctors(List<Doctor> doctors) {
        for (Doctor doctor : doctors) {
            doctorRepository.save(doctor);
        }
    }

    public List<DoctorDTO> findAll(RequestDTO requestDTO) {

        Pageable pageable = PageRequest.of(requestDTO.getPage(),
                requestDTO.getSize(), Sort.by(requestDTO.getSort()));
        List<Doctor> doctors = new ArrayList<>();
        if (requestDTO.getQuery() == null || requestDTO.getQuery().trim().isEmpty()) {
            doctors = doctorRepository.findAll(pageable).toList();
        } else {
            doctors = doctorRepository.searchDoctors(requestDTO.getQuery(),
                    PageRequest.of(requestDTO.getPage(), requestDTO.getSize(),
                            Sort.by(requestDTO.getSort()))).toList();
        }
        List<DoctorDTO> doctorDTOList = new ArrayList<>();
        for(Doctor doctor : doctors){
            doctorDTOList.add(doctorMapper.mapToDTO(doctor));
        }

        return doctorDTOList;
    }

    public DoctorDTO findById(String id) {
        Optional<Doctor> doctors =  doctorRepository.findById(id);
        return doctors.map(doctorMapper::mapToDTO).orElse(null);
    }
}
