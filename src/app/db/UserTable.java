package app.db;

import app.controller.AppController;
import app.model.users.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import app.service.CsvReaderService;

public class UserTable {

    private static CsvReaderService csvReaderService = AppController.getCsvReaderService();

    public static String filename = "src/resources/User_List.csv";

    public static String getFilename() {
        return filename;
    }


    public static void create(User user) {
        List<String> userStr = new ArrayList<>();

        userStr.add(String.valueOf(user.getUserId()));
        userStr.add(user.getUsername());
        userStr.add(user.getPassword());
        userStr.add(user.getName());
        userStr.add(user.getGender());

        // Convert to a List<List<String>> to represent the CSV rows
        List<List<String>> userData = new ArrayList<>();
        userData.add(userStr);

        try {
            csvReaderService.write(filename, userData); // Use your csvReaderService write method here
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void edit(User user) {
        try {
            List<List<String>> allUsers = csvReaderService.read(filename);
            List<List<String>> updatedUsers = new ArrayList<>();
    
            // Find the user to edit by matching their ID
            for (List<String> userData : allUsers) {
                if (userData.get(0).equals(String.valueOf(user.getUserId()))) {
                    userData.set(1, user.getUsername());
                    userData.set(2, user.getPassword());
                    userData.set(3, user.getName());
                    userData.set(4, user.getGender());
                }
                updatedUsers.add(userData);
            }
    
            csvReaderService.write(filename, updatedUsers);
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete(User user) {
        try {
            List<List<String>> allUsers = csvReaderService.read(filename);
            List<List<String>> updatedUsers = new ArrayList<>();
    
            for (List<String> userData : allUsers) {
                if (!userData.get(0).equals(String.valueOf(user.getUserId()))) {
                    updatedUsers.add(userData); // add all except user with user id
                }
            }
            csvReaderService.write(filename, updatedUsers); // Overwrite with the updated data
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}
