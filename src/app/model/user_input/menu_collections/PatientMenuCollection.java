package app.model.user_input.menu_collections;

import app.model.appointments.Appointment;
import app.model.appointments.Appointment.AppointmentStatus;
import app.model.appointments.AppointmentDisplay;
import app.model.appointments.AppointmentOutcomeRecord;
import app.model.appointments.Timeslot;
import app.model.user_input.InputMenu;
import app.model.user_input.MenuState;
import app.model.user_input.NewMenu;
import app.model.user_input.OptionMenu;
import app.model.user_input.option_collections.OptionGeneratorCollection;
import app.model.users.Patient;
import app.model.users.staff.Doctor;
import app.service.AppointmentService;
import app.service.UserService;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PatientMenuCollection {

    public static NewMenu getPatientMainMenu() {
        return new OptionMenu("Select Option", "Choose an option")
            .setOptionGenerator(OptionGeneratorCollection::generatePatientMenuOptions)
            .shouldAddLogoutOptions().shouldAddMainMenuOption();
    }

    public static NewMenu getPatientViewMedicalRecordMenu() {
        OptionMenu menu = new OptionMenu("Select Patient To Edit Medical Record", "Enter 'M' or 'MenuState' to return to the main menu.");
        
        menu.setDisplayGenerator(() -> {
                Patient patient = (Patient) getTargetPatientFromArgs(menu.getFormData());
                System.out.println("\nPatient Information");
                NewMenu.printLineBreak(10);
                System.out.println(patient.toString());
                System.out.println("\nAppointment History");
                NewMenu.printLineBreak(10);
                List<Appointment> appointments = AppointmentService.getAllAppointmentsForPatient(patient.getRoleId());
                AppointmentDisplay.printAppointmentDetails(appointments);
            })
        .shouldAddLogoutOptions()
        .shouldAddMainMenuOption();

        return menu;
    }

    public static NewMenu getPatientEditMedicalRecordMenu() {
        OptionMenu menu = new OptionMenu("Select Field to update", "");

        menu.setOptionGenerator(() -> {
            Patient patient = (Patient) getTargetPatientFromArgs(menu.getFormData());
            return OptionGeneratorCollection.generateEditPatientDetailsOptions(patient);
        }).shouldAddMainMenuOption();
        return menu;
    }

    public static NewMenu getPatientViewAvailAppointmentsMenu() {
        return new OptionMenu("Available Appointments Today", null).setDisplayGenerator(() -> {
            Map<Doctor, List<Timeslot>> timeslotsByDoctor = AppointmentService.getAvailableAppointmentSlotsByDoctor(LocalDateTime.now());
            // Get tomorrow's timeslots if hospital is closed today
            if (timeslotsByDoctor.isEmpty()) {
                timeslotsByDoctor = AppointmentService.getAvailableAppointmentSlotsByDoctor(
                    LocalDateTime.of(LocalDateTime.now().plusDays(1).toLocalDate(), LocalTime.of(0, 0))
                );
            }
            if (!timeslotsByDoctor.isEmpty()) {
                AppointmentDisplay.printAvailableTimeslots(timeslotsByDoctor);
            } else {
                System.out.println("No available timeslots for today and tomorrow.");
            }     
        }).shouldAddMainMenuOption().shouldAddLogoutOptions();
    }

    public static NewMenu getPatientViewConfirmedAppointmentsMenu() {
        return new OptionMenu("Upcoming Scheduled Appointments", null).setDisplayGenerator(() -> {
                Patient patient = (Patient) UserService.getCurrentUser();
                    List<Appointment> appointments = AppointmentService.getAllAppointmentsForPatient(patient.getRoleId())
                        .stream()
                        .filter(appointment -> AppointmentStatus.isIn(appointment.getAppointmentStatus(), 
                            AppointmentStatus.CONFIRMED,
                            AppointmentStatus.PENDING)
                        )
                        .collect(Collectors.toList());
                    if (!appointments.isEmpty()) {
                        AppointmentDisplay.printAppointmentDetails(appointments);
                    } else {
                        System.out.println("No appointments scheduled. Start scheduling one today.\n");
                    }
            }).shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
    }

    public static NewMenu getPatientAppointmentSelectionTypeMenu() {
        return new OptionMenu("Select a Date", null)
            .setOptionGenerator(() -> OptionGeneratorCollection.getAppointmentOptionGenerator())
            .shouldAddMainMenuOption();
    }

    public static NewMenu getInputAppointmentYearMenu() {
        return new OptionMenu("Choose Year", null)
            .setOptionGenerator(() -> OptionGeneratorCollection.getInputAppointmentYearOptionGenerator())
            .shouldAddMainMenuOption();
    }

    public static NewMenu getInputAppointmentMonthMenu() {
        OptionMenu menu = new OptionMenu("Choose a Month", null);

        menu.setOptionGenerator(() -> OptionGeneratorCollection.getInputAppointmentMonthOptionGenerator(menu.getFormData()))
            .shouldAddMainMenuOption();
        return menu;
    }

    public static NewMenu getInputAppointmentDayMenu() {
        InputMenu menu = new InputMenu("Enter a day from selected range", "");

        menu.getInput()
            .setNextMenuState(MenuState.INPUT_APPOINTMENT_HOUR)
            .setNextAction((formValues) -> {
                int startDay = Integer.parseInt(formValues.get("startDay").toString());
                int endDay = Integer.parseInt(formValues.get("endDay").toString());
                int day = Integer.parseInt(formValues.get("day").toString());
                
                if (day < startDay || day > endDay) {
                    throw new IllegalArgumentException(
                        String.format("Please enter a valid date between %d and %d", startDay, endDay)
                    );
                }
                
                formValues.put("day", day);
                return formValues;
            });
        return menu;
    }

    public static NewMenu getInputAppointmentHourMenu() {
        OptionMenu menu = new OptionMenu("Choose an Hour", null);
        return menu
            .setOptionGenerator(() -> OptionGeneratorCollection.getInputAppointmentHourOptionGenerator(menu.getFormData()))
            .shouldAddMainMenuOption();
    }

    public static NewMenu getInputAppointmentDoctorMenu() {
        OptionMenu menu = new OptionMenu("Select Available Doctor", null);
        menu
            .setOptionGenerator(() -> {
                LocalDateTime selectedDateTime = (LocalDateTime) menu.getFormData().get("dateTime");
                Patient p = getTargetPatientFromArgs(menu.getFormData());
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

    public static NewMenu getPatientRescheduleSelectionMenu() {
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
                return OptionGeneratorCollection.generateRescheduleAppointmentOptions(appointments);
            })
            .shouldAddMainMenuOption();
    }

    public static NewMenu getPatientCancelSelectionMenu() {
        return new OptionMenu("Cancel Appointment", null)
            .setOptionGenerator(() -> {
                
                Patient patient = (Patient) UserService.getCurrentUser();
                List<Appointment> appointments = AppointmentService.getAllAppointmentsForPatient(patient.getRoleId())
                    .stream()
                    .filter(appointment -> appointment.getAppointmentStatus() == AppointmentStatus.CONFIRMED)
                    .collect(Collectors.toList());

                if (appointments.isEmpty()) {
                    throw new IllegalArgumentException("No appointments available.");
                }
                return OptionGeneratorCollection.generateCancelAppointmentOptions(appointments);
            })
            .shouldAddMainMenuOption();
    }

    public static NewMenu getPatientViewOutcomesMenu() {
        return new OptionMenu("View Appointment Outcomes", null)
        .setDisplayGenerator(() -> {
            Patient patient = (Patient) UserService.getCurrentUser();
            List<AppointmentOutcomeRecord> outcomeRecords = AppointmentService.getAppointmentRecordsByPatientId(patient.getRoleId());
            if (!outcomeRecords.isEmpty()) {
                outcomeRecords.stream().forEach(outcomeRecord -> 
                    AppointmentDisplay.printAppointmentOutcomeDetails(outcomeRecord)
                );
            } else {
                System.out.println("No appointment outcomes found. Start scheduling an appointment today.\n");
            }
        })
        .shouldAddMainMenuOption()
        .shouldAddLogoutOptions();
    }
    
    
    // public static MenuState getPatientAppointmentSelectionTypeMenu(Map<String, Object> formData) {
    //     return new OptionMenu(

    //     )
    // }



    private static Patient getTargetPatientFromArgs(Map<String, Object> formValues) throws Exception {
        Patient patient = (Patient) (
            formValues != null && formValues.containsKey("patientId") ?
                UserService.findUserByIdAndType(
                    Integer.parseInt(
                        (String) formValues.get("patientId")
                    ),
                    Patient.class,
                    true
                ) : (
                    Patient.class.equals(UserService.getCurrentUser().getClass()) ?
                        UserService.getCurrentUser() : null
                )
            );
        if (patient == null) {
            throw new Exception("No patient found.");
        }
        return patient;
    }
}
