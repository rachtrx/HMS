package app.service;

import app.constants.exceptions.InvalidTimeslotException;
import app.model.appointments.Appointment;
import app.model.appointments.Appointment.AppointmentStatus;
import app.model.appointments.AppointmentOutcomeRecord;
import app.model.appointments.Timeslot;
import app.model.users.MedicalRecord;
import app.model.users.Patient;
import app.model.users.staff.Doctor;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public static List<Appointment> getAllAppointments() {
        List<Appointment> appointments = UserService.getAllUsers()
            .stream()
            .filter(user -> user instanceof Patient)
            .flatMap(user -> ((Patient) user).getAppointments().stream())
            .collect(Collectors.toList());
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

    public static List<Timeslot> getAvailableAppointmentSlotsForDoctorNextMonth(Doctor doctor) {
        LocalDateTime startDate = LocalDateTime.now();
        if (LocalTime.now().isAfter(Timeslot.lastSlotStartTime)) {
            // Start from tomorrow if today's time has passed the last available slot time
            startDate = startDate.plusDays(1).with(LocalTime.MIN);
        }
        LocalDateTime endDate = startDate.plusDays(30);
        List<Timeslot> availableSlots = new ArrayList<>();
    
        LocalDateTime currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            Map<Doctor, List<Timeslot>> dailySlotsByDoctor = getAvailableAppointmentSlotsByDoctor(currentDate);
            List<Timeslot> doctorSlots = dailySlotsByDoctor.getOrDefault(doctor, Collections.emptyList());
    
            // Include today with empty slots if no slots are available and it's before the last timeslot time
            if (currentDate.toLocalDate().equals(startDate.toLocalDate()) &&
                LocalTime.now().isBefore(Timeslot.lastSlotStartTime) && doctorSlots.isEmpty()) {
                dailySlotsByDoctor.put(doctor, new ArrayList<>());
            }
    
            availableSlots.addAll(doctorSlots);
            currentDate = currentDate.plusDays(1);
        }
    
        return availableSlots;
    }

    // TODO: requires testing
    public static Map<Doctor, List<Timeslot>> getAvailableAppointmentSlotsByDoctor(LocalDateTime date) {
        LocalDateTime dateTimeNow = LocalDateTime.now();
        LocalTime startSlotTime;
    
        // Apply time restriction only for today's date
        if (dateTimeNow.toLocalDate().equals(date.toLocalDate())) {
            LocalTime currentSlotStartTime = LocalTime.of(dateTimeNow.getHour(), 0);
            startSlotTime = currentSlotStartTime.compareTo(Timeslot.firstSlotStartTime) < 0 ?
                    Timeslot.firstSlotStartTime : currentSlotStartTime;
        } else {
            startSlotTime = Timeslot.firstSlotStartTime;
        }
    
        Map<Doctor, List<Timeslot>> availableSlotsByDoctor = new HashMap<>();
    
        IntStream.range(0, (int) (startSlotTime.until(Timeslot.lastSlotStartTime, ChronoUnit.HOURS) /
                Timeslot.TIMESLOTLENGTHINHOURS) + 1) // Include last slot
            .forEach(timeslotOffset -> {
                LocalDateTime slotDateTime = LocalDateTime.of(date.toLocalDate(),
                        startSlotTime.plusHours(timeslotOffset * Timeslot.TIMESLOTLENGTHINHOURS));
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
    
        // Ensure consistency by adding empty lists for fully booked doctors
        UserService.getAllUserByType(Doctor.class).forEach(doctor ->
            availableSlotsByDoctor.putIfAbsent((Doctor) doctor, new ArrayList<>())
        );
    
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

        // Assumes appointment is allowed to be set for specified doctor and patient
        Optional<Appointment> existingAppointment = AppointmentService.getAllAppointments()
            .stream()
            .filter(appointment -> (
                appointment.getTimeslot().equals(timeslot) && (
                    appointment.getDoctorId() == doctorId ||
                    appointment.getPatientId() == patientId
                )
            )).findFirst();
        if (existingAppointment.isPresent()) {
            if (existingAppointment.get().getAppointmentStatus().equals(AppointmentStatus.CANCELLED)) {
                existingAppointment.get().setAppointmentStatus(AppointmentStatus.PENDING);
            } else {
                throw new IllegalArgumentException("Appointment already exists");
            }
        }

        Doctor doctor = (Doctor) UserService.findUserByIdAndType(doctorId, Doctor.class, true);
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor not found.");
        }
        Patient patient = (Patient) UserService.findUserByIdAndType(patientId, Patient.class, true);
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found.");
        }

        Appointment appointment = Appointment.create(doctorId, timeslot, patientId);
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

    // public static Prescription getPrescription() {
    //     return AppointmentService.currentPrescription;
    // }

    // public static void setPrescription(Prescription prescription) {
    //     AppointmentService.currentPrescription = prescription;
    // }

    // public static void resetPrescription() {
    //     AppointmentService.currentPrescription = null;
    // }
}
