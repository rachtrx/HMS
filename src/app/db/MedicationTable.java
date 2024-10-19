package app.db;

import app.db.utils.CsvReaderService;
import app.model.inventory.Medication;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MedicationTable {

    private static final String filename = "src/resources/Medication_List.csv";

    public static String getFilename() {
        return filename;
    }

    

    // Create a new Medicine record
    public static void create(Medication medication) {
        List<String> medicineStr = new ArrayList<>();

        medicineStr.add(String.valueOf(medication.getId()));
        medicineStr.add(medication.getName());
        medicineStr.add(String.valueOf(medication.getStock()));
        medicineStr.add(String.valueOf(medication.getLowAlertLevel()));

        // Convert to a List<List<String>> to represent the CSV rows
        List<List<String>> medicineData = new ArrayList<>();
        medicineData.add(medicineStr);

        try {
            CsvReaderService.write(filename, medicineData); // Append new medicine data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Edit an existing Medicine record
    public static void edit(Medication medication) {
        try {
            List<List<String>> allMedicines = CsvReaderService.read(filename);
            List<List<String>> updatedMedicines = new ArrayList<>();

            // Find the medicine to edit by matching the id
            for (List<String> medicineData : allMedicines) {
                if (medicineData.get(0).equals(String.valueOf(medication.getId()))) {
                    medicineData.set(1, medication.getName()); // Update the name
                    medicineData.set(2, String.valueOf(medication.getStock())); // Update stock
                    medicineData.set(3, String.valueOf(medication.getLowAlertLevel())); // Update lowAlertLevel
                }
                updatedMedicines.add(medicineData);
            }

            CsvReaderService.write(filename, updatedMedicines); // Overwrite with updated data

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Delete a Medicine record by id
    public static void delete(Medication medication) {
        try {
            List<List<String>> allMedicines = CsvReaderService.read(filename);
            List<List<String>> updatedMedicines = new ArrayList<>();

            for (List<String> medicineData : allMedicines) {
                if (!medicineData.get(0).equals(String.valueOf(medication.getId()))) {
                    updatedMedicines.add(medicineData); // Add all except the one with id
                }
            }

            CsvReaderService.write(filename, updatedMedicines); // Overwrite with updated data

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// public static Medication serialize(List<String> row) {
    //     return new Medication(row.get(0), row.get(1), row.get(2), row.get(3));
    // }

    // public List<String> deserialize(Medication medication) {
    //     List<String> row = new ArrayList<>();
    //     row.add(String.valueOf(medication.getId()));
    //     row.add(medication.getName());
    //     row.add(String.valueOf(medication.getStock()));
    //     row.add(String.valueOf(medication.getLowAlertLevel()));
    //     return row;
    // }