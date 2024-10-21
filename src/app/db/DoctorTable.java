package app.db;

import app.controller.AppController;
import app.model.users.staff.Doctor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import app.service.CsvReaderService;

public class DoctorTable {

    private static CsvReaderService csvReaderService = AppController.getCsvReaderService();

    private static final String filename = "src/resources/Doctor_List.csv";

    public static String getFilename() {
        return filename;
    }

    // Create a new Doctor record
    public static void create(Doctor doctor) {
        List<String> doctorStr = new ArrayList<>();

        doctorStr.add(String.valueOf(doctor.getDoctorId()));
        doctorStr.add(String.valueOf(doctor.getStaffId()));

        // Convert to a List<List<String>> to represent the CSV rows
        List<List<String>> doctorData = new ArrayList<>();
        doctorData.add(doctorStr);

        try {
            csvReaderService.write(filename, doctorData); // Append new doctor data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // IMPT NO EDIT!

    // Delete a Doctor record by doctorId
    public static void delete(Doctor doctor) {
        try {
            List<List<String>> allDoctors = csvReaderService.read(filename);
            List<List<String>> updatedDoctors = new ArrayList<>();

            for (List<String> doctorData : allDoctors) {
                if (!doctorData.get(0).equals(String.valueOf(doctor.getDoctorId()))) {
                    updatedDoctors.add(doctorData); // Add all except the one with doctorId
                }
            }

            csvReaderService.write(filename, updatedDoctors); // Overwrite with updated data

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}