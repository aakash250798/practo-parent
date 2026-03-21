package com.practo.appointment;

import com.practo.appointment.dto.AppointmentDTO;
import com.practo.appointment.entity.Hospital;
import com.practo.appointment.repository.HospitalRepository;
import com.practo.appointment.service.AppointmentService;
import com.practo.appointment.service.DoctorService;
import com.practo.appointment.service.PatientService;
import com.practo.appointment.staticData.PractoData;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.List;

@EnableRabbit
@SpringBootApplication
public class AppointmentApplication {

    @Autowired
    DoctorService doctorService;

    @Autowired
    PatientService patientService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private HospitalRepository hospitalRepository;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger("");

    public static void main(String[] args) {
        SpringApplication.run(AppointmentApplication.class, args);
    }

    @PostConstruct
    public void setSomeValues() throws IOException {

        List<Hospital> hospitals = PractoData.getHospitalValues();
        for(Hospital hospital:hospitals){
            hospitalRepository.save(hospital);
        }

        doctorService.bulkSaveDoctors(PractoData.getDoctorValues(hospitals));
        patientService.bulkSavePatients(PractoData.getPatientsValues());

//        for (AppointmentDTO a : PractoData.getAppointmentValues()) {
//            appointmentService.saveAppointment(a);
//        }

    }

}
