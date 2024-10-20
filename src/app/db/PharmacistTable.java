package app.db;

import app.controller.AppController;
import app.model.users.staff.Pharmacist;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import app.service.CsvReaderService;

public class PharmacistTable {

    private static CsvReaderService csvReaderService = AppController.getCsvReaderService();

    private static final String filename = "src/resources/Pharmacist_List.csv";

    public static String getFilename() {
        return filename;
    }

    // Create a new Pharmacist record
    public static void create(Pharmacist pharmacist) {
        List<String> pharmacistStr = new ArrayList<>();

        pharmacistStr.add(String.valueOf(pharmacist.getPharmacistId()));
        pharmacistStr.add(String.valueOf(pharmacist.getStaffId()));

        // Convert to a List<List<String>> to represent the CSV rows
        List<List<String>> pharmacistData = new ArrayList<>();
        pharmacistData.add(pharmacistStr);

        try {
            csvReaderService.write(filename, pharmacistData); // Append new pharmacist data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // IMPT NO EDIT!

    // Delete a Pharmacist record by pharmacistId
    public static void delete(Pharmacist pharmacist) {
        try {
            List<List<String>> allPharmacists = csvReaderService.read(filename);
            List<List<String>> updatedPharmacists = new ArrayList<>();

            for (List<String> pharmacistData : allPharmacists) {
                if (!pharmacistData.get(0).equals(String.valueOf(pharmacist.getPharmacistId()))) {
                    updatedPharmacists.add(pharmacistData); // Add all except the one with pharmacistId
                }
            }

            csvReaderService.write(filename, updatedPharmacists); // Overwrite with updated data

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}

