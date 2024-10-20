package app.service;

import app.model.users.Patient;
import app.model.users.User;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    
    private static User currentUser;
    
    private static final List<User> users = new ArrayList<>();
    // private List<String> roles = Arrays
    //     .asList(Patient.class, Doctor.class)
    //     .stream()
    //     .map(Class::getSimpleName)
    //     .collect(Collectors.toList());
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static void setCurrentUser(User currentUser) {
        UserService.currentUser = currentUser;
    }

    public static User findUser(String username, String password) {
        for (User user : users) {
            System.out.println(user.getPassword());
            System.out.println(password);
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
    
    // public void loadPatients(List<List<String>> patients) throws Exception {
    //     for (List<String> row : patients) {
    //         String username = row.get(0);
    //         String password = row.get(1);
    //         String patientId = row.get(2); // TODO
    //         String name = row.get(3);
    //         String gender = row.get(4);
    //         String mobile = row.get(5);
    //         String home = row.get(6);
    //         String email = row.get(7);
    //         String dob = row.get(8); // Assuming the DoB format is correct in the new data
    //         String bloodType = row.get(9);
            
    //         // Create a patient and add to users list
    //         User patient = createPatient(username, password, patientId, name, gender, mobile, home, email, dob, bloodType);
    //         users.add(patient);
    //     }
    // }
    
    // public void loadStaff(List<List<String>> staff) {
    //     for (List<String> row : staff) {
    //         String hospitalId = row.get(0).substring(1);
    //         String name = row.get(1);
    //         char role = row.get(2).charAt(0);
    //         char gender = row.get(3).charAt(0);
    //         String age = row.get(4);
    
    //         // Create staff and add to users list
    //         User staffMember = createStaff(hospitalId, name, role, gender, age);
    //         users.add(staffMember);
    //     }
    // }
    
    // public User createPatient(String username, String password, String patientId, String name, String gender, String mobileNumber, String homeNumber, String email, String dateOfBirth, String bloodType) throws Exception {
    //     Patient patient = new Patient(username, password, patientId, name, gender, mobileNumber, homeNumber, email, dateOfBirth, bloodType, new ArrayList<>());
    //     return patient;
    // }
    
    // public User createStaff(String hospitalId, String name, String role, char gender, String age) {
    
    //     User user;
    
    //     switch (role) {
    //         case 'D': // TODO convert to Enum? same for class
    //             user = new Doctor(hospitalId, name, gender, age);
    //             break;
    //         case 'P':
    //             user = new Pharmacist(hospitalId, name, gender, age);
    //             break;
    //         case 'A':
    //             user = new Admin(hospitalId, name, gender, age);
    //             break;
    //         default:
    //             throw new IllegalArgumentException("Invalid role");
    //     }
    //     return user;
    // }    
}
