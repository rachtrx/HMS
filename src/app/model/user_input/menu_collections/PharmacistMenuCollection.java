package app.model.user_input.menu_collections;

import app.model.appointments.Appointment;
import app.model.appointments.AppointmentOutcomeRecord;
import app.model.appointments.Prescription;
import app.model.appointments.Prescription.PrescriptionStatus;
import app.model.inventory.Medication;
import app.model.user_input.FunctionalInterfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.text.html.Option;

import app.model.user_input.FunctionalInterfaces.NextAction;
import app.model.user_input.InputMenu;
import app.model.user_input.MenuState;
import app.model.user_input.NewMenu;
import app.model.user_input.OptionMenu;
import app.model.user_input.option_collections.OptionGeneratorCollection;
import app.service.AppointmentService;
import app.service.MedicationService;

public class PharmacistMenuCollection {
    public static NewMenu getPharmacistMainMenu() {
        return new OptionMenu("Select Option", "Choose an option")
            .setOptionGenerator(OptionGeneratorCollection::generatePharmacistMenuOptions)
            .shouldAddLogoutOptions().shouldAddMainMenuOption();
    }

    public static NewMenu getPharmacistViewOutcomeRecordsMenu() {
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

    public static NewMenu getPharmacistUpdateOutcomesMenu() {
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

    public static NewMenu getPharmacistUpdatePrescriptionsMenu() {
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

    public static NewMenu getPharmacistHandlePrescriptionMenu() {
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

    public static NewMenu getPharmacistAddRequestMenu() {
    return new OptionMenu("Submit Replenish Request", "Please select the medication: ")
        .setOptionGenerator(() -> {
            List<Medication> medications = MedicationService.getAllMedications();
            return OptionGeneratorCollection.getPharmacistMedicationOptions(medications);
        }).shouldAddMainMenuOption();
    }

    public static NewMenu getPharmacistAddCountMenu() {
        InputMenu menu = new InputMenu("Medication Quantity", "Please enter the quantity of medication to add: ");
    
        menu.getInput()
            .setNextMenuState(MenuState.VIEW_INVENTORY)
            .setNextAction(formValues -> {
                Integer medicationId = (Integer) formValues.get("medicationId");
                int quantity = Integer.parseInt((String) formValues.get("input"));
                
                if (quantity <= 0) {
                    throw new IllegalArgumentException("Please enter a valid quantity greater than zero.");
                }
    
                MedicationService.submitReplenishRequest(medicationId, quantity);
                return null;
            });
    
        return menu;
    }
}
