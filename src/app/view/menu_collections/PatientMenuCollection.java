package app.view.menu_collections;

import app.controller.AppointmentService;
import app.controller.UserService;
import app.model.appointments.Appointment;
import app.model.appointments.Appointment.AppointmentStatus;
import app.model.appointments.Timeslot;
import app.model.users.Patient;
import app.model.users.staff.Doctor;
import app.utils.DateTimeUtils;
import app.view.Menu;
import app.view.OptionMenu;
import app.view.menu_collections.MenuCollection.Control;
import app.view.option_collections.OptionGeneratorCollection;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PatientMenuCollection {

    public static Menu getPatientMainMenu() {
        return new OptionMenu("Patient Main Menu", null)
            .setOptionGenerator(OptionGeneratorCollection::generatePatientMenuOptions)
            .shouldAddLogoutOptions();
    }

    public static Menu getPatientViewMedicalRecordMenu() {
        OptionMenu menu = new OptionMenu("Your Medical Record", "Enter 'M' or 'MenuState' to return to the main menu.");

        Patient patient = (Patient) UserService.getCurrentUser();
        
        menu
        .setDisplayGenerator(() -> {
            Patient p = (Patient) UserService.getCurrentUser();
            System.out.println();
            System.out.println("Your Details: ");
            System.out.println(p);
        })
        .setOptionGenerator(() -> {
            List<Appointment> appointments = AppointmentService.getAllAppointmentsForPatient(patient.getRoleId());
            return OptionGeneratorCollection.generateAppointmentDisplayOptions(appointments, Control.NONE);
        })
        .shouldAddLogoutOptions()
        .shouldAddMainMenuOption();

        return menu;
    }

    public static Menu getPatientEditMedicalRecordMenu() {
        OptionMenu menu = new OptionMenu("Select Field to update", "");

        menu.setOptionGenerator(() -> {
            Patient patient = (Patient) UserService.getCurrentUser();
            return OptionGeneratorCollection.generateEditPatientDetailsOptions(patient);
        }).shouldAddMainMenuOption();
        return menu;
    }

    public static Menu getPatientViewAvailAppointmentsDoctorMenu() {
        OptionMenu menu = new OptionMenu("Available Appointments for Doctor", null);

        menu
            .setDisplayGenerator(() -> {
                Map<String, Object> formValues = menu.getFormData();
                if (formValues == null || !formValues.containsKey("doctor")) throw new IllegalArgumentException("Doctor not found");
                Doctor d = (Doctor) formValues.get("doctor");
                System.out.println("Available Appointments For Next Month for " + d.getName());
            })
            .setOptionGenerator(() -> {
                Map<String, Object> formValues = menu.getFormData();
                if (formValues == null || !formValues.containsKey("doctor")) throw new IllegalArgumentException("Doctor not found");
                Doctor d = (Doctor) formValues.get("doctor");
                List<Timeslot> timeslotsByDoctor = AppointmentService.getAvailableAppointmentSlotsForDoctorNextMonth(d);
    
                if (!timeslotsByDoctor.isEmpty()) {
                    return OptionGeneratorCollection.generateAvailableTimeslotOptionsByDate(d);
                } else {
                    System.out.println("No available timeslots for " + d.getName());
                    return new ArrayList<>();
                }     
            })
            .shouldAddMainMenuOption().shouldAddLogoutOptions();

        return menu;
    }

    public static Menu getPatientViewAvailAppointmentsMenu() {
        return new OptionMenu("Available Appointments", null)
            .setDisplayGenerator(() -> {
                LocalDateTime now = LocalDateTime.now();
                if (now.toLocalTime().isAfter(Timeslot.lastSlotStartTime)) {
                    System.out.println("Timeslots for " + 
                        DateTimeUtils.printLongDate(now.toLocalDate()));
                } else {
                    System.out.println("Timeslots for " + 
                        DateTimeUtils.printLongDate(now.plusDays(1).toLocalDate()));
                }
            })
            .setOptionGenerator(() -> {
                LocalDateTime now = LocalDateTime.now();
                Map<Doctor, List<Timeslot>> timeslotsByDoctor;
    
                if (now.toLocalTime().isAfter(Timeslot.lastSlotStartTime)) {
                    // If so, get timeslots for tomorrow
                    timeslotsByDoctor = AppointmentService.getAvailableAppointmentSlotsByDoctor(
                        LocalDateTime.of(now.plusDays(1).toLocalDate(), LocalTime.of(0, 0))
                    );
                } else {
                    timeslotsByDoctor = AppointmentService.getAvailableAppointmentSlotsByDoctor(now);
                }
    
                if (!timeslotsByDoctor.isEmpty()) {
                    return OptionGeneratorCollection.generateAvailableTimeslotOptions(timeslotsByDoctor);
                } else {
                    System.out.println("No available timeslots for today and tomorrow.");
                    return new ArrayList<>();
                }
            })
            .shouldAddMainMenuOption().shouldAddLogoutOptions();
    }

    public static Menu getPatientViewConfirmedAppointmentsMenu() {
        return new OptionMenu("Upcoming Scheduled Appointments", null)
        .setOptionGenerator(() -> {
            Patient patient = (Patient) UserService.getCurrentUser();
            List<Appointment> appointments = AppointmentService.getAllAppointmentsForPatient(patient.getRoleId())
                .stream()
                .filter(appointment -> AppointmentStatus.isIn(appointment.getAppointmentStatus(), 
                    AppointmentStatus.CONFIRMED,
                    AppointmentStatus.PENDING)
                )
                .collect(Collectors.toList());
            if (!appointments.isEmpty()) {
                return OptionGeneratorCollection.generateAppointmentDisplayOptions(appointments, Control.NONE);
            } else {
                System.out.println("No appointments scheduled. Start scheduling one today.\n");
                return new ArrayList<>();
            }
        })
        .shouldAddMainMenuOption()
        .shouldAddLogoutOptions();
    }

    public static Menu getDoctorSelectionMenu() {
        OptionMenu menu = new OptionMenu("Select Doctor", null);
        menu
            .setOptionGenerator(() -> {
                List<Doctor> allDoctors = UserService.getAllUserByType(Doctor.class)
                    .stream()
                    .map(user -> (Doctor) user)
                    .collect(Collectors.toList());
                if (allDoctors == null || allDoctors.isEmpty()) {
                    throw new IllegalArgumentException("No doctors available.");
                }
                return OptionGeneratorCollection.getInputDoctorOptionGenerator(allDoctors, null);
            })
            .shouldAddMainMenuOption();
        return menu;
    }

    public static Menu getAppointmentSelectDoctorMenu() {
        OptionMenu menu = new OptionMenu("Select Available Doctor", null);
        menu
            .setOptionGenerator(() -> {
                LocalDateTime selectedDateTime = (LocalDateTime) menu.getFormData().get("dateTime");
                Patient p = (Patient) UserService.getCurrentUser();
                if (AppointmentService.getAllAppointmentsForPatient(p.getRoleId())
                .stream()
                .anyMatch(appointment -> appointment.getTimeslot().isEqual(selectedDateTime) && 
                        !appointment.getAppointmentStatus().equals(AppointmentStatus.CANCELLED))) {
                    throw new IllegalArgumentException("Appointment already exists at this time.");
                }
                
                List<Doctor> availableDoctors = AppointmentService.getAvailableDoctorsAtTimeslot(selectedDateTime);
                if (availableDoctors == null || availableDoctors.isEmpty()) {
                    throw new IllegalArgumentException("No doctors available.");
                }
                return OptionGeneratorCollection.getInputDoctorOptionGenerator(availableDoctors, selectedDateTime);
            })
            .shouldAddMainMenuOption();
        return menu;
    }

    public static Menu getPatientRescheduleSelectionMenu() {
        return new OptionMenu("Reschedule Appointment", null)
            .setOptionGenerator(() -> {
                Patient patient = (Patient) UserService.getCurrentUser();
                List<Appointment> appointments = AppointmentService.getAllAppointmentsForPatient(patient.getRoleId())
                    .stream()
                    .filter(appointment -> AppointmentStatus.isIn(
                        appointment.getAppointmentStatus(),
                        AppointmentStatus.CONFIRMED,
                        AppointmentStatus.PENDING)
                    )
                    .collect(Collectors.toList());

                if (appointments.isEmpty()) {
                    throw new IllegalArgumentException("No appointments available.");
                }
                return OptionGeneratorCollection.generateUpdateAppointmentOptions(appointments, Control.EDIT);
            })
            .shouldAddMainMenuOption();
    }

    public static Menu getPatientCancelSelectionMenu() {
        return new OptionMenu("Cancel Appointment", null)
            .setOptionGenerator(() -> {
                
                Patient patient = (Patient) UserService.getCurrentUser();
                List<Appointment> appointments = AppointmentService.getAllAppointmentsForPatient(patient.getRoleId())
                    .stream()
                    .filter(appointment -> (appointment.getAppointmentStatus() == AppointmentStatus.CONFIRMED
                    || appointment.getAppointmentStatus() == AppointmentStatus.PENDING))
                    .collect(Collectors.toList());

                if (appointments.isEmpty()) {
                    throw new IllegalArgumentException("No appointments available.");
                }
                return OptionGeneratorCollection.generateUpdateAppointmentOptions(appointments, Control.DELETE);
            })
            .shouldAddMainMenuOption();
    }

    public static Menu getPatientViewOutcomesMenu() {
        return new OptionMenu("View Appointment Outcomes", null)
        .setOptionGenerator(() -> {
            Patient patient = (Patient) UserService.getCurrentUser();
            List<Appointment> appointments = AppointmentService
                .getAllAppointmentsForPatient(patient.getRoleId()).stream()
                .filter(appointment -> appointment.getAppointmentOutcome() != null)
                .collect(Collectors.toList());
            if (!appointments.isEmpty()) {
                return OptionGeneratorCollection.generateAppointmentDisplayOptions(appointments, Control.SELECT);
            } else {
                System.out.println("No appointment outcomes found. Start scheduling an appointment today.\n");
                return new ArrayList<>();
            }
        })
        .shouldAddMainMenuOption()
        .shouldAddLogoutOptions();
    }
}
