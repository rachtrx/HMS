package app.service;

import app.constants.exceptions.InvalidTimeslotException;
import app.model.appointments.Appointment;
import app.model.appointments.Appointment.AppointmentStatus;
import app.model.appointments.AppointmentDisplay;
import app.model.appointments.AppointmentOutcomeRecord;
import app.model.appointments.DoctorEvent;
import app.model.appointments.Timeslot;
import app.model.user_credentials.MedicalRecord;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
* CRUD functionality for appointments.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class AppointmentService {
// public class AppointmentService extends Service {

    private static List<DoctorEvent> doctorEvents = new ArrayList<>();
    private static List<MedicalRecord> medicalRecords = new ArrayList<>();

    public static List<DoctorEvent> getAllEvents() {
        return doctorEvents;
    }

    public static void setAllEvents(List<DoctorEvent> newEvents) {
        AppointmentService.doctorEvents = newEvents;
    }

    public static void addEvent(DoctorEvent newEvent) {
        AppointmentService.doctorEvents.add(newEvent);
    }

    public static List<MedicalRecord> getAllMedicalRecords() {
        return medicalRecords;
    }

    public static void setAllMedicalRecords(List<MedicalRecord> medicalRecords) {
        AppointmentService.medicalRecords = medicalRecords;
    }

    public static void addMedicalRecord(MedicalRecord medicalRecord) {
        AppointmentService.medicalRecords.add(medicalRecord);
    }

    // private AppointmentParse appointmentParse;
    // public static ArrayList<Appointment> appointments;

    public static Appointment getAppointment(int appointmentId) {
        return getAllEvents()
            .stream()
            .filter(event -> event.isAppointment() && ((Appointment) event).getAppointmentId() == appointmentId)
            .map(Appointment.class::cast)
            .findFirst()
            .orElse(null);
    }
    
    public static List<Appointment> getAllAppointmentsForPatient(int patientId) {
        return getAllEvents()
            .stream()
            .filter(event -> event.isAppointment() && ((Appointment) event).getPatientId() == patientId)
            .map(Appointment.class::cast)
            .collect(Collectors.toList());
    }
    
    public static List<Appointment> getAllAppointmentsForDoctor(int doctorId) {
        return getAllEvents()
            .stream()
            .filter(event -> event.isAppointment() && ((Appointment) event).getDoctorId() == doctorId)
            .map(Appointment.class::cast)
            .collect(Collectors.toList());
    }
    
    public static MedicalRecord getMedicalRecord(int patientId) {
        return getAllMedicalRecords()
            .stream()
            .filter(record -> record.getPatientId() == patientId)
            .findFirst()
            .orElse(null);
    }
    
    public static AppointmentOutcomeRecord getAppointmentRecordById(int appointmentId) {
        Appointment appointment = getAppointment(appointmentId);
        if (appointment == null) return null;
    
        MedicalRecord medicalRecord = getMedicalRecord(appointment.getPatientId());
        if (medicalRecord == null) return null;
    
        return medicalRecord.getAppointmentOutcomes()
            .stream()
            .filter(outcome -> outcome.getAppointmentId() == appointmentId)
            .findFirst()
            .orElse(null);
    }
    
    public static List<AppointmentOutcomeRecord> getAppointmentRecordsByPatientId(int patientId) {
        MedicalRecord medicalRecord = getMedicalRecord(patientId);
        if (medicalRecord == null) return new ArrayList<>();
    
        return new ArrayList<>(medicalRecord.getAppointmentOutcomes());
    }
    
    public static Map<Patient, List<AppointmentOutcomeRecord>> getAppointmentRecordsByDoctorId(int doctorId) {
        List<Integer> patientIds = getAllAppointmentsForDoctor(doctorId)
            .stream()
            .map(Appointment::getPatientId)
            .distinct()
            .collect(Collectors.toList());
    
        Map<Patient, List<AppointmentOutcomeRecord>> outcomesMap = new HashMap<>();
    
        for (int patientId : patientIds) {
            Patient patient = UserService.findUserByIdAndType(patientId, Patient.class, true);
            MedicalRecord medicalRecord = getMedicalRecord(patientId);
            if (medicalRecord != null) {
                outcomesMap.put(patient, new ArrayList<>(medicalRecord.getAppointmentOutcomes()));
            }
        }
    
        return outcomesMap;
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
        if (LocalDate.now().equals(date.toLocalDate()) && dateTimeNow.getHour() == date.getHour()) {
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
                return getAllEvents()
                    .stream()
                    .filter(event -> event.getDoctorId() == doctor.getRoleId() && event.getTimeslot().isEqual(timeslotDateTime))
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

        Appointment appointment = new Appointment(
            doctorId, timeslot, patientId, AppointmentStatus.CONFIRMED
        );
        AppointmentService.addEvent(appointment);
        System.out.println("Appointment successfully scheduled");
    }

    public static void cancelAppointment(
        Appointment oldAppointment
    ) throws Exception {
        List<DoctorEvent> events = AppointmentService.getAllEvents();
        boolean removed = events.removeIf(event -> 
            event instanceof Appointment &&
            event.getId() == oldAppointment.getId()
        );
        if (!removed) {
            throw new Exception("Old appointment was not found");
        }
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
