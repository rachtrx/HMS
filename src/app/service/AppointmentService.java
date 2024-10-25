package app.service;

import app.constants.exceptions.InvalidTimeslotException;
import app.model.appointments.Appointment;
import app.model.appointments.Appointment.AppointmentStatus;
import app.model.appointments.Timeslot;
import app.model.users.Patient;
import app.model.users.staff.Doctor;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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

    // private AppointmentParse appointmentParse;
    // private AppointmentDisplay appointmentDisplay;
    // public static ArrayList<Appointment> appointments;

    public static List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        UserService.getAllUsers()
            .stream()
            .forEach(user -> {
                if (Patient.class.isInstance(user)) {
                    appointments.addAll(((Patient) user).getAppointments());
                } else if (Doctor.class.isInstance(user)) {
                    appointments.addAll(((Doctor) user).getDoctorEvents()
                        .stream()
                        .filter(event -> event.isAppointment(event))
                        .map(Appointment.class::cast)
                        .collect(Collectors.toList())
                    );
                }
            });
        return appointments;
    }

    // public static ArrayList<Appointment> getAppointments() throws UserNotFound {
    //     if (UserService.isLoggedIn()) {
    //         return (ArrayList<Appointment>) this.appointments
    //             .stream()
    //             .filter(appointment -> {
    //                 this.userIdMatches(appointment)
    //                 throw new AssertionError();
    //             }).collect(Collectors.toList());
    //     }
    //     throw new UserNotFound();
    // }

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
    public static List<Timeslot> getAvailableAppointmentSlotsToday() {
        LocalDateTime dateTimeNow = LocalDateTime.now();
        LocalDateTime dateTimeOffset = dateTimeNow.plusHours(1);
        LocalTime currentSlotStartTime = LocalTime.of(dateTimeOffset.getHour(), 0);
        LocalTime earliestSlotTime = currentSlotStartTime.compareTo(Timeslot.firstSlotStartTime) < 0 ?
            Timeslot.firstSlotStartTime : currentSlotStartTime;
        return IntStream.range(
            0,
            (int) (earliestSlotTime.until(Timeslot.lastSlotStartTime, ChronoUnit.HOURS) /
                Timeslot.timeslotLengthInHours + Timeslot.timeslotLengthInHours)
        ).mapToObj(timeslotOffset -> {
            try {
                Timeslot checkTimeslot = new Timeslot(LocalDateTime.of(
                    dateTimeOffset.toLocalDate(), earliestSlotTime)
                );
                List<Doctor> availableDoctors = AppointmentService
                    .getAvailableDoctorsAtTimeslot(checkTimeslot.getTimeSlot());
                if (
                    availableDoctors != null &&
                    availableDoctors.stream().findFirst().isPresent()
                ) {
                    try {
                        return new Timeslot(
                            LocalDateTime.of(dateTimeNow.toLocalDate(),
                                    LocalTime.of(earliestSlotTime.getHour()+timeslotOffset, 0)
                            ));
                    } catch (InvalidTimeslotException ex) {}
                }
            } catch (InvalidTimeslotException e) {
                return null;
            }
            
            return null;
        }).collect(Collectors.toList());
        // LocalTime offsetTime = LocalTime.of(dateTimeOffset.getHour(), dateTimeOffset.getMinute());
        // Timeslot nextAvailableTimeslot = new Timeslot(LocalDateTime.of(
        //     (
        //         offsetTime.isAfter(Timeslot.lastSlotStartTime) ?
        //         DateTimeUtil.addWorkingDays(dateTimeNow, 1) :
        //         dateTimeOffset
        //     ).toLocalDate(),
        //     (
        //         dateTimeNow.toLocalTime().isBefore(Timeslot.firstSlotStartTime) ||
        //         dateTimeNow.toLocalTime().isAfter(Timeslot.lastSlotStartTime)
        //     ) ? Timeslot.firstSlotStartTime : dateTimeNow.toLocalTime()
        // ));
        // Timeslot lastAvailableTimeslot = new Timeslot(LocalDateTime.of(
        //     DateTimeUtil.addWorkingDays(dateTimeNow, 7).toLocalDate(),
        //     Timeslot.lastSlotStartTime
        // ));
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
        Appointment appointment = new Appointment(
            doctorId, timeslot, patientId, AppointmentStatus.CONFIRMED
        );

        Doctor doctor = (Doctor) UserService.findUserByIdAndType(doctorId, Doctor.class);
        if (doctor == null) {
            throw new Exception("Doctor not found.");
        }
        doctor.addDoctorEvent(appointment);

        Patient patient = (Patient) UserService.findUserByIdAndType(patientId, Patient.class);
        if (patient == null) {
            throw new Exception("Patient not found.");
        }
        patient.addAppointment(appointment);
    }

    // public void rescheduleAppointment (int appointmentId) throws ItemNotFoundException, UserNotFound {
    //     Appointment appointment = this.(appointmentId);
        
    // }

    // public void cancelAppointment(int appointmentId) throws ItemNotFoundException, UserNotFound {
    //     Appointment appointment = this.findAppointmentById(appointmentId);
    //     appointment.cancel();
    // }

    // public void confirmAppointment(int appointmentId) throws ItemNotFoundException, UserNotFound {
    //     Appointment appointment = this.findAppointmentById(appointmentId);
    //     appointment.confirm();
    // }

    // public void completeAppointment(int appointmentId) throws ItemNotFoundException, UserNotFound {
    //     Appointment appointment = this.findAppointmentById(appointmentId);
    //     appointment.completed();
    // }
}
