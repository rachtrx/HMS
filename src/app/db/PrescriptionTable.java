package app.db;

import java.io.IOException;

import app.controller.AppController;
import app.model.appointments.Prescription;
import app.service.CsvReaderService;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionTable {

    private static CsvReaderService csvReaderService = AppController.getCsvReaderService();

    private static final String filename = "src/resources/Prescription_List.csv";

    public static String getFilename() {
        return filename;
    }

    // Create a new Prescription record
    public static void create(Prescription prescription) {
        List<String> prescriptionStr = new ArrayList<>();

        prescriptionStr.add(String.valueOf(prescription.getId()));
        prescriptionStr.add(prescription.getStatus().toString());
        prescriptionStr.add(String.valueOf(prescription.getOutcomeId()));

        // Convert to a List<List<String>> to represent the CSV rows
        List<List<String>> prescriptionData = new ArrayList<>();
        prescriptionData.add(prescriptionStr);

        try {
            csvReaderService.write(filename, prescriptionData); // Append new prescription data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Edit an existing Prescription record
    public static void edit(Prescription prescription) {
        try {
            List<List<String>> allPrescriptions = csvReaderService.read(filename);
            List<List<String>> updatedPrescriptions = new ArrayList<>();

            // Find the prescription to edit by matching the id
            for (List<String> prescriptionData : allPrescriptions) {
                if (prescriptionData.get(0).equals(String.valueOf(prescription.getId()))) {
                    prescriptionData.set(1, prescription.getStatus().toString()); // Update the status
                    prescriptionData.set(2, String.valueOf(prescription.getOutcomeId()));
                }
                updatedPrescriptions.add(prescriptionData);
            }

            csvReaderService.write(filename, updatedPrescriptions); // Overwrite with updated data

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Delete a Prescription record by id
    public static void delete(Prescription prescription) {
        try {
            List<List<String>> allPrescriptions = csvReaderService.read(filename);
            List<List<String>> updatedPrescriptions = new ArrayList<>();

            for (List<String> prescriptionData : allPrescriptions) {
                if (!prescriptionData.get(0).equals(String.valueOf(prescription.getId()))) {
                    updatedPrescriptions.add(prescriptionData); // Add all except the one with id
                }
            }

            csvReaderService.write(filename, updatedPrescriptions); // Overwrite with updated data

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}