package app.model.user_input.menu_collections;

import app.model.appointments.Appointment;
import app.model.appointments.Appointment.AppointmentStatus;
import app.model.appointments.Timeslot;
import app.model.user_input.InputMenu;
import app.model.user_input.Menu;
import app.model.user_input.MenuState;
import app.model.user_input.OptionMenu;
import app.model.user_input.menu_collections.MenuCollection.Control;
import app.model.user_input.option_collections.OptionGeneratorCollection;
import app.model.users.Patient;
import app.model.users.staff.Doctor;
import app.service.AppointmentService;
import app.service.UserService;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PatientMenuCollection {

    public static Menu getPatientMainMenu() {
        return new OptionMenu("Select Option", "Choose an option")
            .setOptionGenerator(OptionGeneratorCollection::generatePatientMenuOptions)
            .shouldAddLogoutOptions().shouldAddMainMenuOption();
    }

    public static Menu getPatientViewMedicalRecordMenu() {
        OptionMenu menu = new OptionMenu("Select Patient To Edit Medical Record", "Enter 'M' or 'MenuState' to return to the main menu.");

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

    public static Menu getPatientViewAvailAppointmentsMenu() {
        return new OptionMenu("Available Appointments Today", null)
            .setOptionGenerator(() -> {
                Map<Doctor, List<Timeslot>> timeslotsByDoctor = AppointmentService.getAvailableAppointmentSlotsByDoctor(LocalDateTime.now());
    
                // Get tomorrow's timeslots if hospital is closed today
                if (timeslotsByDoctor.isEmpty()) {
                    timeslotsByDoctor = AppointmentService.getAvailableAppointmentSlotsByDoctor(
                        LocalDateTime.of(LocalDateTime.now().plusDays(1).toLocalDate(), 
                        LocalTime.of(0, 0))
                    );
                }
    
                if (!timeslotsByDoctor.isEmpty()) {
                    return OptionGeneratorCollection.generateAvailableTimeslotOptions(timeslotsByDoctor);
                } else {
                    System.out.println("No available timeslots for today and tomorrow.");
                    return new ArrayList<>();  // Return an empty list as required
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

    public static Menu getInputAppointmentDoctorMenu() {
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
                return OptionGeneratorCollection.getInputDoctorOptionGenerator(availableDoctors, p, selectedDateTime);
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
                return OptionGeneratorCollection.generateAppointmentDisplayOptions(appointments, Control.EDIT);
            } else {
                System.out.println("No appointment outcomes found. Start scheduling an appointment today.\n");
                return new ArrayList<>();
            }
        })
        .shouldAddMainMenuOption()
        .shouldAddLogoutOptions();
    }
}
