package app.model.user_input.menu_collections;

import app.model.appointments.Appointment;
import app.model.appointments.Appointment.AppointmentStatus;
import app.model.user_input.FunctionalInterfaces.NextAction;
import app.model.appointments.AppointmentOutcomeRecord;
import app.model.appointments.Prescription;
import app.model.inventory.Medication;
import app.model.inventory.MedicationOrder;
import app.model.user_input.InputMenu;
import app.model.user_input.MenuState;
import app.model.user_input.NewMenu;
import app.model.user_input.OptionMenu;
import app.model.user_input.option_collections.OptionGeneratorCollection;
import app.model.users.Patient;
import app.model.users.user_credentials.Email;
import app.service.AppointmentService;
import app.service.MedicationService;
import app.service.UserService;
import app.utils.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DoctorMenuCollection {
    public static NewMenu getDoctorMainMenu() {
        return new OptionMenu("Doctor Main Menu", null)
        .setOptionGenerator(OptionGeneratorCollection::generateDoctorMenuOptions)
        .shouldAddLogoutOptions()
        .shouldAddMainMenuOption();
    }

    public enum UpcomingEventControl {
        VIEW, VIEW_BUSY, EDIT_BUSY, DEL_BUSY, VIEW_APPT, CANCEL_APPT, RESPOND_APPT
    }

    public static NewMenu getDoctorViewEventsMenu() {
        return new OptionMenu("Doctor Schedule", null)
            .setOptionGenerator(() -> OptionGeneratorCollection.generateUpcomingEventControlOptions(UpcomingEventControl.VIEW))
            .shouldAddLogoutOptions()
            .shouldAddMainMenuOption();
    }
    public static NewMenu getDoctorViewUnAvailMenu() {
        return new OptionMenu("Doctor Unavailability", null)
            .setOptionGenerator(() -> OptionGeneratorCollection.generateUpcomingEventControlOptions(UpcomingEventControl.VIEW_BUSY))
            .shouldAddLogoutOptions()
            .shouldAddMainMenuOption();
    }
    public static NewMenu getDoctorEditUnAvailMenu() {
        return new OptionMenu("Edit Unavailability", null)
            .setOptionGenerator(() -> OptionGeneratorCollection.generateUpcomingEventControlOptions(UpcomingEventControl.EDIT_BUSY))
            .shouldAddLogoutOptions()
            .shouldAddMainMenuOption();
    }
    public static NewMenu getDoctorDelUnAvailMenu() {
        return new OptionMenu("Delete Unavailability", null)
            .setOptionGenerator(() -> OptionGeneratorCollection.generateUpcomingEventControlOptions(UpcomingEventControl.DEL_BUSY))
            .shouldAddLogoutOptions()
            .shouldAddMainMenuOption();
    }
    public static NewMenu getDoctorViewApptMenu() {
        return new OptionMenu("Upcoming Appointments", null)
            .setOptionGenerator(() -> OptionGeneratorCollection.generateUpcomingEventControlOptions(UpcomingEventControl.VIEW_APPT))
            .shouldAddLogoutOptions()
            .shouldAddMainMenuOption();
    }
    public static NewMenu getDoctorCancelApptMenu() {
        return new OptionMenu("Cancel Appointment", "Select an appointment to cancel: ")
            .setOptionGenerator(() -> OptionGeneratorCollection.generateUpcomingEventControlOptions(UpcomingEventControl.CANCEL_APPT))
            .shouldAddLogoutOptions()
            .shouldAddMainMenuOption();
    }
    public static NewMenu getDoctorHandleApptsMenu() {
        return new OptionMenu("Appointment Requests", "Select an request to respond to: ")
            .setOptionGenerator(() -> OptionGeneratorCollection.generateUpcomingEventControlOptions(UpcomingEventControl.RESPOND_APPT))
            .shouldAddLogoutOptions()
            .shouldAddMainMenuOption();
    }
    public static NewMenu getDoctorHandleApptMenu() {
        OptionMenu menu = new OptionMenu("Manage Appointment", "Accept or Reject Appointment: ");
            menu
            .setDisplayGenerator(() -> {
                Map<String, Object> formValues = menu.getFormData();
                Appointment appointment = null;
                if (formValues != null && formValues.containsKey("appointment")) {
                    appointment = (Appointment) formValues.get("appointment");
                } else throw new IllegalArgumentException("Appointment not found");
                int patientId = appointment.getPatientId();
                Patient p = UserService.getAllUserByType(Patient.class).stream()
                    .map(user -> (Patient) user) // Cast to Patient
                    .filter(patient -> patient.getRoleId() == patientId)
                    .findFirst()
                    .orElse(null);
                if(p == null) throw new IllegalArgumentException("Patient not found");
                System.out.println(p);
                System.out.println(DateTimeUtil.printLongDateTime(appointment.getTimeslot()));
                System.out.println(appointment.getAppointmentStatus());
                System.out.println(appointment.getAppointmentOutcome());
            })
            .setOptionGenerator(OptionGeneratorCollection::generateAcceptRejectOptions);

        return menu;
    }

    public static NewMenu getSelectPatientViewMenu() {
        return new OptionMenu("All patients under you", null)
        .setOptionGenerator(OptionGeneratorCollection::generatePatientOptions);
    }
    public static NewMenu getPatientEditMenu() {
        OptionMenu menu = new OptionMenu("Edit Patient Details", "Select a field to edit");

        menu.setOptionGenerator(() -> {
            Map<String, Object> formValues = menu.getFormData();
            Patient p = null;
            if (formValues != null && formValues.containsKey("patient")) {
                p = (Patient) formValues.get("patient");
            } else throw new IllegalArgumentException("Patient not found");
            return OptionGeneratorCollection.generateEditPatientDetailsOptions(p);
        }).shouldAddMainMenuOption();

        return menu;
    }
    
    public static NewMenu getDoctorPastApptViewMenu() {
        OptionMenu menu = new OptionMenu("All Past Appointments", null)
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();

        setOptionGeneratorForPatient(menu, true, true);
        setDisplayGeneratorForPatient(menu);
        return menu;
    }
    public static NewMenu getDoctorOutcomesViewMenu() {
        OptionMenu menu = new OptionMenu("All Appointment Outcomes", null)
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();

        setOptionGeneratorForPatient(menu, false, true);
        setDisplayGeneratorForPatient(menu);
        return menu;
    }

    public static NewMenu getDoctorOutcomesAddMenu() {
        OptionMenu menu = new OptionMenu("Add Appointment Outcome", "Select an appointment to add outcome: ")
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();

        setOptionGeneratorForPatient(menu, true, false);
        setDisplayGeneratorForPatient(menu);
        return menu;
    }

    public static NewMenu getDoctorServiceAddMenu() {
        return new OptionMenu("Appointment Service Type", "Select Service Type: ")
            .setOptionGenerator(OptionGeneratorCollection::generateServiceTypeOptions);
    }
    public static NewMenu getDoctorNotesAddMenu() {
        InputMenu menu = new InputMenu("Consultation Notes", "Enter consultation notes: ");

        menu.getInput()
            .setNextAction(formValues -> {
                String input = (String) menu.getFormData().get("input");
                formValues.put("notes", input);
                return formValues;
            }).setNextMenuState(MenuState.DOCTOR_ADD_MEDICATION);
        return menu;
    }

    public static NewMenu getDoctorMedicationAddMenu() {
        OptionMenu menu = new OptionMenu("Select Medication", null);

        menu.
            setOptionGenerator(() -> {
                MedicationService.getAllMedications()
                    .stream()
                    .filter(medication -> medication.getStock() > 0)
                    .collect(Collectors.toList());
                    return OptionGeneratorCollection.generateMedicationOptions();
            });
        return menu;
    }

    public static NewMenu getDoctorQuantityAddMenu() {
        InputMenu menu = new InputMenu("Medication Quantity", "Enter medication quantity: ");
        
        menu.getInput()
            .setNextAction((formValues) -> {
                Medication medication = (Medication) formValues.get("medication");
                int quantity = (int) formValues.get("input");
                if (quantity > medication.getStock()) {
                    throw new IllegalArgumentException("Quantity exceeds maximum quantity of " + medication.getStock());
                } else if (quantity < 1) {
                    throw new IllegalArgumentException("Quantity does not meet minimum quantity of 1");
                }
                
                if(!formValues.containsKey("appointment")) throw new IllegalArgumentException("Appointment not found!");

                Appointment appointment = (Appointment) formValues.get("appointment");

                if (appointment.getAppointmentOutcome() != null) {
                    AppointmentOutcomeRecord outcome = appointment.getAppointmentOutcome();
                    outcome.getPrescription().addMedicationOrder(medication.getId(), quantity, outcome.getPrescription().getId());
                    return formValues;
                }

                String serviceType = (String) formValues.get("serviceType");
                String notes = (String) formValues.get("notes");
                MedicationOrder order = MedicationOrder.create(medication.getId(), quantity);
                Prescription prescription = Prescription.create(order);
                AppointmentOutcomeRecord outcome = AppointmentOutcomeRecord.create(
                    appointment.getAppointmentId(), 
                    serviceType, 
                    prescription, 
                    notes
                );
                prescription.setOutcomeId(outcome.getId());
                order.setPrescriptionId(prescription.getId());
                appointment.setAppointmentStatus(AppointmentStatus.COMPLETED);
                return formValues;
            }).setNextMenuState(MenuState.DOCTOR_VIEW_RECORD).setExitMenuState(MenuState.DOCTOR_ADD_QUANTITY);

        return menu;
    }

    public static NewMenu getDoctorOutcomeViewMenu() {
        OptionMenu menu = new OptionMenu("Appointment Outcome", null);
        menu.
            setDisplayGenerator(() -> {
                Map<String, Object> formValues = menu.getFormData();
                Patient p = null;
                Appointment appointment = null;
                if (formValues != null && formValues.containsKey("patient")) {
                    p = (Patient) formValues.get("patient");
                } else throw new IllegalArgumentException("Patient not found");
                if (formValues.containsKey("appointment")) {
                    appointment = (Appointment) formValues.get("appointment");
                } else throw new IllegalArgumentException("Appointment not found");
                if (appointment.getPatientId() != p.getRoleId()) throw new IllegalArgumentException("Appointment and patient not matching");
                System.out.println(p);
                System.out.println(appointment.getTimeslot());
                System.out.println(appointment.getAppointmentStatus());
                System.out.println(appointment.getAppointmentOutcome());
            })
            .setOptionGenerator(() -> {
                Map<String, Object> formValues = menu.getFormData();
                Patient p = null;
                Appointment appointment = null;
                if (formValues != null && formValues.containsKey("appointment")) {
                    appointment = (Appointment) formValues.get("appointment");
                } else throw new IllegalArgumentException("Appointment not found");
                return OptionGeneratorCollection.generateAddMedicationOptions(appointment.getAppointmentOutcome().getPrescription());
            })
            .shouldAddMainMenuOption();
        return menu;
    }

    private static NewMenu setOptionGeneratorForPatient(OptionMenu menu, boolean includeAdd, boolean includeEdit) {
        menu.
            setOptionGenerator(() -> {
                Map<String, Object> formValues = menu.getFormData();
                Patient p = null;
                if (formValues != null && formValues.containsKey("patient")) {
                    p = (Patient) formValues.get("patient");
                }
                return OptionGeneratorCollection.generateSelectDoctorPastAppointmentOptions(
                    p, includeAdd, includeEdit
                );
            });
        return menu;
    }

    private static NewMenu setDisplayGeneratorForPatient(OptionMenu menu) {
        menu.
            setDisplayGenerator(() -> {
                Map<String, Object> formValues = menu.getFormData();
                Patient p = null;
                if (formValues != null && formValues.containsKey("patient")) {
                    p = (Patient) formValues.get("patient");
                }
                if (p!=null) {
                    System.out.println(p);
                }
            });
        return menu;
    }
}
