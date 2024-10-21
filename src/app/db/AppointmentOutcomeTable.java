package app.db;

import app.controller.AppController;
import app.model.appointments.AppointmentOutcomeRecord;
import app.service.CsvReaderService;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class AppointmentOutcomeTable {

    private static CsvReaderService csvReaderService = AppController.getCsvReaderService();

    private static final String filename = "src/resources/Appointment_Outcome_List.csv";

    public static String getFilename() {
        return filename;
    }

    // Create a new AppointmentOutcome record
    public static void create(AppointmentOutcomeRecord outcomeRecord) {
        List<String> outcomeStr = new ArrayList<>();

        outcomeStr.add(String.valueOf(outcomeRecord.getId()));
        outcomeStr.add(String.valueOf(outcomeRecord.getAppointment().getAppointmentId()));
        // outcomeStr.add(String.valueOf(outcomeRecord.getPrescription().getId()));
        outcomeStr.add(outcomeRecord.getServiceType().toString()); // Store serviceType as a string
        outcomeStr.add(outcomeRecord.getConsultationNotes()); // Store consultation notes

        // Convert to a List<List<String>> to represent the CSV rows
        List<List<String>> outcomeData = new ArrayList<>();
        outcomeData.add(outcomeStr);

        try {
            csvReaderService.write(filename, outcomeData); // Append new outcome data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Edit an existing AppointmentOutcome by ServiceType
    public static void edit(AppointmentOutcomeRecord outcomeRecord) {
        try {
            List<List<String>> allOutcomes = csvReaderService.read(filename);
            List<List<String>> updatedOutcomes = new ArrayList<>();

            // Find the outcome to edit by matching the old serviceType
            for (List<String> outcomeData : allOutcomes) {
                if (outcomeData.get(0).equals(String.valueOf(outcomeRecord.getId()))) {
                    outcomeData.set(1, String.valueOf(outcomeRecord.getAppointment().getAppointmentId()));
                    // outcomeData.set(2, String.valueOf(outcomeRecord.getPrescription().getId())); // Update service type
                    outcomeData.set(2, outcomeRecord.getServiceType().toString());  // Update consultation notes
                    outcomeData.set(3, outcomeRecord.getConsultationNotes());
                }
                updatedOutcomes.add(outcomeData);
            }

            csvReaderService.write(filename, updatedOutcomes); // Overwrite with updated data

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Delete an AppointmentOutcome by ServiceType
    public static void delete(AppointmentOutcomeRecord outcomeRecord) {
        try {
            List<List<String>> allOutcomes = csvReaderService.read(filename);
            List<List<String>> updatedOutcomes = new ArrayList<>();

            for (List<String> outcomeData : allOutcomes) {
                if (!outcomeData.get(0).equals(String.valueOf(outcomeRecord.getId()))) {
                    updatedOutcomes.add(outcomeData); // Add all except the one with serviceType
                }
            }

            csvReaderService.write(filename, updatedOutcomes); // Overwrite with updated data

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}