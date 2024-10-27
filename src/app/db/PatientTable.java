package app.db;

import app.model.users.Patient;
import app.service.CsvReaderService;
import app.utils.DateTimeUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PatientTable {

    public static String filename = "src/resources/Patient_List.csv";

    public static String getFilename() {
        return filename;
    }

    public static void create(Patient patient) {
        List<String> patientStr = new ArrayList<>();

        patientStr.add(String.valueOf(patient.getRoleId()));
        patientStr.add(patient.getMobileNumber().toString());
        patientStr.add(patient.getHomeNumber().toString());
        patientStr.add(patient.getEmail());
        patientStr.add(DateTimeUtil.printShortDate(patient.getDateOfBirth()));
        patientStr.add(patient.getBloodType());

        // Convert to a List<List<String>> to represent the CSV rows
        List<List<String>> patientData = new ArrayList<>();
        patientData.add(patientStr);

        try {
            CsvReaderService.write(filename, patientData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void edit(Patient patient) {
        try {
            List<List<String>> allPatients = CsvReaderService.read(filename);
            List<List<String>> updatedPatients = new ArrayList<>();

            // Find the patient to edit by matching their ID
            for (List<String> patientData : allPatients) {
                if (patientData.get(0).equals(String.valueOf(patient.getRoleId()))) {
                    patientData.set(1, patient.getMobileNumber().toString());
                    patientData.set(2, patient.getMobileNumber().toString());
                    patientData.set(3, patient.getHomeNumber().toString());
                    patientData.set(4, patient.getEmail());
                    patientData.set(5, DateTimeUtil.printShortDate(patient.getDateOfBirth()));
                    patientData.set(6, patient.getBloodType());
                }
                updatedPatients.add(patientData);
            }

            CsvReaderService.write(filename, updatedPatients);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete(Patient patient) {
        try {
            List<List<String>> allPatients = CsvReaderService.read(filename);
            List<List<String>> updatedPatients = new ArrayList<>();

            for (List<String> patientData : allPatients) {
                if (!patientData.get(0).equals(String.valueOf(patient.getRoleId()))) {
                    updatedPatients.add(patientData);
                }
            }

            // Overwrite the CSV with the updated patient list
            CsvReaderService.write(filename, updatedPatients);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}
