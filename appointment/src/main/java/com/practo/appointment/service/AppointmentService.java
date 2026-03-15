package com.practo.appointment.service;

import com.practo.appointment.configuration.RabbitMQConfig;
import com.practo.appointment.dto.AppointmentDTO;
import com.practo.appointment.dto.BookingDTO;
import com.practo.appointment.dto.TimeSlot;
import com.practo.appointment.entity.Appointment;
import com.practo.appointment.entity.Doctor;
import com.practo.appointment.entity.Patient;
import com.practo.appointment.mapping.AppointmentMapper;
import com.practo.appointment.repository.AppointmentRepository;
import com.practo.appointment.repository.DoctorRepository;
import com.practo.appointment.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final RabbitTemplate rabbitTemplate;
    private final AppointmentMapper appointmentMapper;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            RabbitTemplate rabbitTemplate, AppointmentMapper appointmentMapper) {

        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.appointmentMapper = appointmentMapper;
    }

    public AppointmentDTO findAppointmentById(String id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found"));
        return appointmentMapper.mapToDTO(appointment);
    }

    // response format shud be better
    @Transactional
    public BookingDTO saveAppointment(AppointmentDTO appointmentDTO) {
        //make sure appointment is correct
        Doctor doctor = doctorRepository.findById(appointmentDTO.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Patient patient = patientRepository.findById(appointmentDTO.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        LocalDateTime timing = appointmentDTO.getTiming();
        TimeSlot timeSlot = doctor.getTimeSlot();
        if (!timeSlot.bookTimeSlot(timing))
            throw new RuntimeException("Timing not available");

        Appointment appointment = bookAppointment(doctor, patient, timing);
        publishAppointmentEvent(appointmentMapper.mapToDTO(appointment));
        return new BookingDTO("Appointment Booked Successfully", appointment.getId(), doctor.getName(),
                doctor.getSpecialization(), patient.getName(), doctor.getHospital(), timing, doctor.getFees());
    }

    private Appointment bookAppointment(Doctor doctor, Patient patient, LocalDateTime timing) {
        Appointment appointment = new Appointment();
        appointment.setId(String.valueOf(UUID.randomUUID()));
        doctorRepository.save(doctor);
        appointment.setTimeSlot(timing);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        return appointmentRepository.save(appointment);
    }

    private void publishAppointmentEvent(AppointmentDTO dto) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "Appointment booked", dto);
    }
}