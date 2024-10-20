package app.db;

import app.controller.AppController;
import app.model.users.Patient;
import app.utils.DateTimeUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import app.service.CsvReaderService;

public class PatientTable {

    private static CsvReaderService csvReaderService = AppController.getCsvReaderService();

    public static String filename = "src/resources/Patient_List.csv";

    public static String getFilename() {
        return filename;
    }

    public static void create(Patient patient) {
        List<String> patientStr = new ArrayList<>();

        patientStr.add(String.valueOf(patient.getPatientId()));
        patientStr.add(patient.getMobileNumber().toString());
        patientStr.add(patient.getHomeNumber().toString());
        patientStr.add(patient.getEmail());
        patientStr.add(DateTimeUtil.printShortDate(patient.getDateOfBirth()));
        patientStr.add(patient.getBloodType());

        // Convert to a List<List<String>> to represent the CSV rows
        List<List<String>> patientData = new ArrayList<>();
        patientData.add(patientStr);

        try {
            csvReaderService.write(filename, patientData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void edit(Patient patient) {
        try {
            List<List<String>> allPatients = csvReaderService.read(filename);
            List<List<String>> updatedPatients = new ArrayList<>();

            // Find the patient to edit by matching their ID
            for (List<String> patientData : allPatients) {
                if (patientData.get(0).equals(String.valueOf(patient.getPatientId()))) {
                    patientData.set(1, patient.getMobileNumber().toString());
                    patientData.set(2, patient.getMobileNumber().toString());
                    patientData.set(3, patient.getHomeNumber().toString());
                    patientData.set(4, patient.getEmail());
                    patientData.set(5, DateTimeUtil.printShortDate(patient.getDateOfBirth()));
                    patientData.set(6, patient.getBloodType());
                }
                updatedPatients.add(patientData);
            }

            csvReaderService.write(filename, updatedPatients);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete(Patient patient) {
        try {
            List<List<String>> allPatients = csvReaderService.read(filename);
            List<List<String>> updatedPatients = new ArrayList<>();

            for (List<String> patientData : allPatients) {
                if (!patientData.get(0).equals(String.valueOf(patient.getPatientId()))) {
                    updatedPatients.add(patientData);
                }
            }

            // Overwrite the CSV with the updated patient list
            csvReaderService.write(filename, updatedPatients);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}
