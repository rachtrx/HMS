package app.db;

import app.model.users.staff.Pharmacist;
import app.service.CsvReaderService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PharmacistTable {

    private static final String filename = "src/resources/Pharmacist_List.csv";

    public static String getFilename() {
        return filename;
    }

    // Create a new Pharmacist record
    public static void create(Pharmacist pharmacist) {
        List<String> pharmacistStr = new ArrayList<>();

        pharmacistStr.add(String.valueOf(pharmacist.getRoleId()));
        pharmacistStr.add(String.valueOf(pharmacist.getStaffId()));

        // Convert to a List<List<String>> to represent the CSV rows
        List<List<String>> pharmacistData = new ArrayList<>();
        pharmacistData.add(pharmacistStr);

        try {
            CsvReaderService.write(filename, pharmacistData); // Append new pharmacist data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // IMPT NO EDIT!

    // Delete a Pharmacist record by pharmacistId
    public static void delete(Pharmacist pharmacist) {
        try {
            List<List<String>> allPharmacists = CsvReaderService.read(filename);
            List<List<String>> updatedPharmacists = new ArrayList<>();

            for (List<String> pharmacistData : allPharmacists) {
                if (!pharmacistData.get(0).equals(String.valueOf(pharmacist.getRoleId()))) {
                    updatedPharmacists.add(pharmacistData); // Add all except the one with pharmacistId
                }
            }

            CsvReaderService.write(filename, updatedPharmacists); // Overwrite with updated data

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}

