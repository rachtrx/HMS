package app.model.appointments;

import app.model.users.Patient;
import app.model.users.staff.Doctor;
import app.service.AppointmentService;
import app.service.UserService;
import app.utils.DateTimeUtil;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class AppointmentDisplay {
    
    public static void print(Appointment appointment) {

    }

    public static void printAppointmentOutcomeDetails(AppointmentOutcomeRecord outcomeRecord) {
        Appointment appointment = AppointmentService.getAppointment(outcomeRecord.getAppointmentId());
        System.out.println("Appointment Date: " + DateTimeUtil.printShortDateTime(appointment.getTimeslot()));
        System.out.println("Service Type: " + outcomeRecord.getServiceType());
        System.out.println("Consultation Notes: " + outcomeRecord.getConsultationNotes());
    }

    public static void printAppointmentDetails(List<Appointment> appointments) {
        IntStream.range(0, appointments.size()) 
            .forEach(appointmentIndex -> {
                Appointment appointment = appointments.get(appointmentIndex);
                System.out.println(String.format("%d.", appointmentIndex+1));
                System.out.println(String.format(
                    "Appointment Timeslot: %s",
                    DateTimeUtil.printLongDateTime(appointment.getTimeslot())
                ));
                System.out.println(String.format(
                    "Patient Name: %s",
                    UserService
                        .findUserByIdAndType(appointment.getPatientId(), Patient.class, true) // TODO why is this needed though
                        .getName()
                ));
                Doctor doctor = UserService
                        .findUserByIdAndType(appointment.getDoctorId(), Doctor.class, true);
                System.out.println(
                    doctor == null ?
                        "No doctor assigned." :
                        String.format("Doctor Name: %s", doctor.getName())
                );
                System.out.println(String.format(
                    "Appointment Status: %s",
                    appointment.getAppointmentStatus().toString()
                ));
                // TODO: display prescription
            });
        }

    public static void printAvailableTimeslots(Map<Doctor, List<Timeslot>> timeslotsByDoctor) {
        timeslotsByDoctor.forEach((doctor, timeslots) -> {
            // Print the doctor's name and a line break
            System.out.println(String.format("Doctor Name: %s", doctor.getName()));
            System.out.println("----------------------------------");
    
            // Print each timeslot for the current doctor
            IntStream.range(0, timeslots.size()).forEach(timeslotIdx -> {
                Timeslot timeslot = timeslots.get(timeslotIdx);
                System.out.println(String.format(" %d. Appointment Timeslot: %s", 
                        timeslotIdx + 1, 
                        DateTimeUtil.printLongDateTime(timeslot.getTimeSlot())
                ));
            });
    
            // Add an extra line break after each doctor’s timeslots for clarity
            System.out.println("\n");
        });
    }
}
