package app.db;

import app.model.users.staff.Admin;
import app.service.CsvReaderService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminTable {

    private static final String filename = "src/resources/Admin_List.csv";

    public static String getFilename() {
        return filename;
    }

    // Create a new Admin record
    public static void create(Admin admin) {
        List<String> adminStr = new ArrayList<>();

        adminStr.add(String.valueOf(admin.getRoleId()));
        adminStr.add(String.valueOf(admin.getStaffId()));

        // Convert to a List<List<String>> to represent the CSV rows
        List<List<String>> adminData = new ArrayList<>();
        adminData.add(adminStr);

        try {
            CsvReaderService.write(filename, adminData); // Append new admin data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // IMPT NO EDIT

    // Delete an Admin record by adminId
    public static void delete(Admin admin) {
        try {
            List<List<String>> allAdmins = CsvReaderService.read(filename);
            List<List<String>> updatedAdmins = new ArrayList<>();

            for (List<String> adminData : allAdmins) {
                if (!adminData.get(0).equals(String.valueOf(admin.getRoleId()))) {
                    updatedAdmins.add(adminData); // Add all except the one with adminId
                }
            }

            CsvReaderService.write(filename, updatedAdmins); // Overwrite with updated data

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}
