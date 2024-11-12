package app.model.user_input.menu_collections;

import app.model.appointments.Appointment;
import app.model.appointments.Prescription;
import app.model.appointments.Prescription.PrescriptionStatus;
import app.model.inventory.Medication;
import app.model.user_input.InputMenu;
import app.model.user_input.Menu;
import app.model.user_input.MenuState;
import app.model.user_input.OptionMenu;
import app.model.user_input.menu_collections.MenuCollection.Control;
import app.model.user_input.option_collections.OptionGeneratorCollection;
import app.service.AppointmentService;
import app.service.MedicationService;
import java.util.List;
import java.util.stream.Collectors;

public class PharmacistMenuCollection {
    public static Menu getPharmacistMainMenu() {
        return new OptionMenu("Pharmacist Main Menu", null)
            .setOptionGenerator(OptionGeneratorCollection::generatePharmacistMenuOptions)
            .shouldAddLogoutOptions();
    }

    public static Menu getPharmacistViewOutcomeRecordsMenu() {
        OptionMenu menu = new OptionMenu("All Appointment Outcomes", null);
            menu.setOptionGenerator(() -> {
                final Boolean hideCompleted = menu.getFormData() != null && menu.getFormData().containsKey("hideCompleted") && (Boolean) menu.getFormData().get("hideCompleted");
                // Generate numbered options for appointment outcomes
                List<Appointment> appointments = AppointmentService.getAllAppointments().stream()
                    .filter(appointment -> appointment.getAppointmentOutcome() != null)
                    .filter(appointment -> {
                        if (hideCompleted) {
                            PrescriptionStatus status = appointment.getAppointmentOutcome().getPrescription().getStatus();
                            return !status.equals(PrescriptionStatus.DISPENSED);
                        } else {
                            return true;
                        }
                    })
                    .collect(Collectors.toList());
                if(appointments.isEmpty()) System.out.println("Empty Appointments");
                else System.out.println(appointments);
                return OptionGeneratorCollection.generatePharmacistViewOutcomeOptions(appointments, hideCompleted);
            }).shouldAddLogoutOptions()
            .shouldAddMainMenuOption();
        
        return menu;
    }

    public static Menu getPharmacistUpdateOutcomesMenu() {
        OptionMenu menu = new OptionMenu("Update Outcomes", "");
            
        menu.setOptionGenerator(() -> {
            List<Appointment> appointments = AppointmentService.getAllAppointments().stream()
                .filter(appointment -> appointment.getAppointmentOutcome() != null)
                .filter(appointment -> {
                    PrescriptionStatus status = appointment.getAppointmentOutcome().getPrescription().getStatus();
                    return !status.equals(PrescriptionStatus.DISPENSED);
                })
                .collect(Collectors.toList());
                return OptionGeneratorCollection.generatePharmacistOutcomesOptions(appointments, true);
            }).shouldAddLogoutOptions()
            .shouldAddMainMenuOption();
        
        return menu;
    }

    public static Menu getPharmacistUpdatePrescriptionsMenu() {
        return new OptionMenu("Update Prescriptions", "")
            .setOptionGenerator(() -> {
                List<Prescription> prescriptions = AppointmentService.getAllAppointments().stream()
                    .filter(appointment -> appointment.getAppointmentOutcome() != null)
                    .filter(appointment -> 
                        !appointment.getAppointmentOutcome().getPrescription().getStatus().equals(PrescriptionStatus.DISPENSED)
                    )
                    .map(appointment -> appointment.getAppointmentOutcome().getPrescription()) // Return the prescription
                    .collect(Collectors.toList());

                return OptionGeneratorCollection.generatePharmacistUpdatePrescriptionOptions(prescriptions);
            });
    }

    public static Menu getPharmacistHandlePrescriptionMenu() {
        OptionMenu menu = new OptionMenu("Prescription and Medication Order Details", "");
        menu
            .setDisplayGenerator(() -> {
                final Prescription prescription = (Prescription) menu.getFormData().get("prescription");
                if (prescription == null) throw new IllegalArgumentException("No prescription found");
    
                String prescriptionInfo = String.join(" | ",
                        "Prescription ID: " + prescription.getId(),
                        "Outcome ID: " + prescription.getOutcomeId(),
                        "Status: " + prescription.getStatus().toString()
                    );
    
                System.out.println("Prescription Details:\n");
                System.out.println(prescriptionInfo);
                System.out.println("\n\nMedications:\n");
            })
            .setOptionGenerator(() -> {
                final Prescription prescription = (Prescription) menu.getFormData().get("prescription");
                if (prescription == null) throw new IllegalArgumentException("No prescription found");
                return OptionGeneratorCollection.getPharmacistHandleStatusOptions(prescription);
            })
            .shouldAddLogoutOptions().shouldAddMainMenuOption();
    
        return menu;
    }

    public static Menu getPharmacistAddRequestMenu() {
    return new OptionMenu("Submit Replenish Request", "Please select the medication: ")
        .setOptionGenerator(() -> {
            return OptionGeneratorCollection.generateMedicationOptions(Control.ADD);
        }).shouldAddMainMenuOption();
    }

    public static Menu getPharmacistAddCountMenu() {
        InputMenu menu = new InputMenu("Medication Quantity", "Please enter the quantity of medication to add: ");
    
        menu.getInput()
            .setNextMenuState(MenuState.VIEW_INVENTORY)
            .setNextAction(formValues -> {
                Medication medication = (Medication) formValues.get("medication");
                int quantity = Integer.parseInt((String) formValues.get("input"));
                
                if (quantity <= 0) {
                    throw new IllegalArgumentException("Please enter a valid quantity greater than zero.");
                }
    
                MedicationService.submitReplenishRequest(medication.getId(), quantity);
                return null;
            }).setRequiresConfirmation(true)
            .setExitMenuState(MenuState.PHARMACIST_ADD_COUNT);
    
        return menu;
    }
}
