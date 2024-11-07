package app.service;

import app.constants.exceptions.InvalidTimeslotException;
import app.model.appointments.Appointment;
import app.model.appointments.Appointment.AppointmentStatus;
import app.model.appointments.AppointmentDisplay;
import app.model.appointments.AppointmentOutcomeRecord;
import app.model.appointments.Timeslot;
import app.model.users.MedicalRecord;
import app.model.users.Patient;
import app.model.users.staff.Doctor;
import app.utils.DateTimeUtil;
import java.awt.Event;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
* CRUD functionality for appointments.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class AppointmentService {
// public class AppointmentService extends Service {

    // private AppointmentParse appointmentParse;
    // public static ArrayList<Appointment> appointments;

    public static List<Appointment> getAllAppointments() {
        List<Appointment> appointments = UserService.getAllUsers()
            .stream()
            .filter(user -> user instanceof Patient || user instanceof Doctor)
            // transform each user into their list of appointments
            .flatMap(user -> {
                if (user instanceof Patient patient) {
                    return patient.getAppointments().stream();
                } else if (user instanceof Doctor doctor) {
                    return doctor.getAppointments().stream().filter(event -> !event.isAppointment());
                }
                return Stream.empty(); // return an empty stream if not a Patient or Doctor, 
            }).collect(Collectors.toList());
        return appointments;
    }

    public static Appointment getAppointment(int appointmentId) {
        Optional<Appointment> appointment = AppointmentService.getAllAppointments()
            .stream()
            .filter(app -> app.getAppointmentId() == appointmentId)
            .findFirst(); // Get the first found appointment

        return appointment.orElse(null);
    }
    
    public static List<Appointment> getAllAppointmentsForPatient(int patientId) {
        Patient patient = UserService.findUserByIdAndType(patientId, Patient.class, true);
        return patient != null ? patient.getAppointments() : new ArrayList<>();
    }
    
    public static List<Appointment> getAllAppointmentsForDoctor(int doctorId) {
        Doctor doctor = UserService.findUserByIdAndType(doctorId, Doctor.class, true);
        return doctor != null ? doctor.getAppointments() : new ArrayList<>();
    }
    
    public static MedicalRecord getMedicalRecord(int patientId) {
        Patient patient = UserService.findUserByIdAndType(patientId, Patient.class, true);
        return patient != null ? patient.getMedicalRecord() : null;
    }
    
    public static AppointmentOutcomeRecord getAppointmentRecordById(int appointmentId) {
        Appointment appointment = getAppointment(appointmentId);
        if (appointment == null) return null;
        return appointment.getAppointmentOutcome();
    }

    public static List<AppointmentOutcomeRecord> getAppointmentOutcomes(List<Appointment> appointments) {
        return appointments.stream()
            .map(Appointment::getAppointmentOutcome)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    
    public static List<AppointmentOutcomeRecord> getAppointmentRecordsByPatientId(int patientId) {  
        List<Appointment> appointments = getAllAppointmentsForPatient(patientId);
        return !appointments.isEmpty() ? getAppointmentOutcomes(appointments) : new ArrayList<>();
    }
    
    public static Map<Patient, List<AppointmentOutcomeRecord>> getAppointmentRecordsByDoctorId(int doctorId) {
        List<Appointment> appointments = getAllAppointmentsForDoctor(doctorId);
    
        Map<Patient, List<AppointmentOutcomeRecord>> outcomesMap = new HashMap<>();
    
        for (Appointment appointment : appointments) {
            int patientId = appointment.getPatientId();
            Patient patient = UserService.findUserByIdAndType(patientId, Patient.class, true);
            
            if (patient != null) {
                AppointmentOutcomeRecord outcomeRecord = appointment.getAppointmentOutcome();
                outcomesMap.putIfAbsent(patient, new ArrayList<>()); // TODO maybe skip patient since this means appointment pending, no record yet
                outcomesMap.get(patient).add(outcomeRecord);
            }
        }
        return outcomesMap;
    }

    public static List<Appointment> getAppointmentByStatus(AppointmentStatus appointmentStatus) {
        return AppointmentService.getAllAppointments()
            .stream()
            .filter(appointment ->
                appointment.isAppointment() &&
                appointment.getAppointmentStatus().equals(appointmentStatus)
            ).collect(Collectors.toList());
    }

    private boolean userIdMatches(Patient patient, Appointment appointment) {
        return appointment.getPatientId() == UserService.getCurrentUser().getUserId();
    }

    // private Appointment (int appointmentId) throws ItemNotFoundException, UserNotFound {
    //     Optional<Appointment> result = this.getAppointments()
    //         .stream()
    //         .filter(appointment -> appointment.getId() == appointmentId)
    //         .findFirst();
    //     if (result.isPresent()) {
    //         return result.get();
    //     }
    //     throw new ItemNotFoundException(
    //         String.format("No appointment with ID %d exists", appointmentId)
    //     );
    // }

    // TODO: requires testing
    public static Map<Doctor, List<Timeslot>> getAvailableAppointmentSlotsByDoctor(LocalDateTime date) {
        LocalDateTime dateTimeNow = LocalDateTime.now();
        LocalTime currentSlotStartTime = LocalTime.of(dateTimeNow.getHour(), 0);
    
        // Determine earliest slot time based on current time and provided date
        if (dateTimeNow.toLocalDate().equals(date.toLocalDate()) && dateTimeNow.getHour() == date.getHour()) {
            LocalDateTime dateTimeOffset = date.plusHours(1);
            currentSlotStartTime = LocalTime.of(dateTimeOffset.getHour(), 0);
        }
        LocalTime earliestSlotTime = currentSlotStartTime.compareTo(Timeslot.firstSlotStartTime) < 0 ?
                Timeslot.firstSlotStartTime : currentSlotStartTime;
    
        Map<Doctor, List<Timeslot>> availableSlotsByDoctor = new HashMap<>();
    
        IntStream.range(0, (int) (earliestSlotTime.until(Timeslot.lastSlotStartTime, ChronoUnit.HOURS) /
                Timeslot.timeslotLengthInHours))
            .forEach(timeslotOffset -> {
                LocalDateTime slotDateTime = LocalDateTime.of(date.toLocalDate(),
                        earliestSlotTime.plusHours(timeslotOffset * Timeslot.timeslotLengthInHours));
                try {
                    Timeslot timeslot = new Timeslot(slotDateTime);
                    List<Doctor> availableDoctors = AppointmentService.getAvailableDoctorsAtTimeslot(slotDateTime);
    
                    if (availableDoctors != null) {
                        availableDoctors.forEach(doctor -> {
                            availableSlotsByDoctor
                                .computeIfAbsent(doctor, k -> new ArrayList<>())
                                .add(timeslot);
                        });
                    }
                } catch (InvalidTimeslotException ex) {
                    // Ignore invalid timeslot
                }
            });
    
        return availableSlotsByDoctor;
    }

    // TODO: requires testing
    public static List<Doctor> getAvailableDoctorsAtTimeslot(LocalDateTime timeslotDateTime) {
        return UserService.getAllUserByType(Doctor.class)
            .stream()
            .filter(user -> {
                Doctor doctor = (Doctor) user;
                return doctor.getDoctorEvents()
                    .stream()
                    .filter(event -> event.getTimeslot().isEqual(timeslotDateTime))
                    .findFirst()
                    .isEmpty();
            }).map(Doctor.class::cast)
            .collect(Collectors.toList());
    }

    public static void scheduleAppointment(
        int patientId, int doctorId, LocalDateTime timeslot
    ) throws Exception {

        Doctor doctor = (Doctor) UserService.findUserByIdAndType(doctorId, Doctor.class, true);
        if (doctor == null) {
            throw new Exception("Doctor not found.");
        }
        Patient patient = (Patient) UserService.findUserByIdAndType(patientId, Patient.class, true);
        if (patient == null) {
            throw new Exception("Patient not found.");
        }

        Appointment appointment = new Appointment(doctorId, timeslot, patientId);
        doctor.addAppointment(appointment);
        patient.addAppointment(appointment);
        System.out.println("Appointment successfully scheduled");
    }

    public static void cancelAppointment(
        Appointment oldAppointment
    ) throws Exception {
        oldAppointment.cancel();
        System.out.println("Old Appointment successfully cancelled");
    }

    public static void rescheduleAppointment(
        int patientId, int doctorId, LocalDateTime timeslot, Appointment oldAppointment
    ) throws Exception {
        cancelAppointment(oldAppointment);
        scheduleAppointment(patientId, doctorId, timeslot);
    }

    // public void confirmAppointment(int appointmentId) throws ItemNotFoundException, UserNotFound {
    //     Appointment appointment = this.findAppointmentById(appointmentId);
    //     appointment.confirm();
    // }

    // public void completeAppointment(int appointmentId) throws ItemNotFoundException, UserNotFound {
    //     Appointment appointment = this.findAppointmentById(appointmentId);
    //     appointment.completed();
    // }
}
