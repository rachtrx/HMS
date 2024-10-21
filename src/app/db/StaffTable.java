package app.db;

import app.controller.AppController;
import app.model.users.staff.Staff;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import app.service.CsvReaderService;

public class StaffTable {

    private static CsvReaderService csvReaderService = AppController.getCsvReaderService();

    private static final String filename = "src/resources/Staff_List.csv";

    public static String getFilename() {
        return filename;
    }

    // Create a new Staff record
    public static void create(Staff staff) {
        List<String> staffStr = new ArrayList<>();

        staffStr.add(String.valueOf(staff.getStaffId()));
        staffStr.add(String.valueOf(staff.getUserId()));
        staffStr.add(String.valueOf(staff.getAge()));

        // Convert to a List<List<String>> to represent the CSV rows
        List<List<String>> staffData = new ArrayList<>();
        staffData.add(staffStr);

        try {
            csvReaderService.write(filename, staffData); // Append new staff data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Edit an existing Staff record
    public static void edit(Staff staff) {
        try {
            List<List<String>> allStaff = csvReaderService.read(filename);
            List<List<String>> updatedStaff = new ArrayList<>();

            // Find the staff to edit by matching the staffId
            for (List<String> staffData : allStaff) {
                if (staffData.get(0).equals(String.valueOf(staff.getStaffId()))) {
                    staffData.set(1, String.valueOf(staff.getUserId())); // Update the userId
                    staffData.set(2, String.valueOf(staff.getAge()));    // Update age
                }
                updatedStaff.add(staffData);
            }

            csvReaderService.write(filename, updatedStaff); // Overwrite with updated data

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Delete a Staff record by staffId
    public static void delete(Staff staff) {
        try {
            List<List<String>> allStaff = csvReaderService.read(filename);
            List<List<String>> updatedStaff = new ArrayList<>();

            for (List<String> staffData : allStaff) {
                if (!staffData.get(0).equals(String.valueOf(staff.getStaffId()))) {
                    updatedStaff.add(staffData); // Add all except the one with staffId
                }
            }

            csvReaderService.write(filename, updatedStaff); // Overwrite with updated data

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}