package app.service;

import app.model.users.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserService {
    
    private static User currentUser;
    
    private static final List<User> users = new ArrayList<>();
    // private List<String> roles = Arrays
    //     .asList(Patient.class, Doctor.class)
    //     .stream()
    //     .map(Class::getSimpleName)
    //     .collect(Collectors.toList());
    
    public static List<User> getAllUsers() {
        return users;
    }

    public static void addUsers(List<User> users) {
        UserService.users.addAll(users);
    }

    public static List<? extends User> getAllUserByType(Class<? extends User> userType) {
        return UserService.users
            .stream()
            .filter(user -> userType.equals(user.getClass()))
            .collect(Collectors.toList());
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void login(String username, String password) throws Exception {
        Optional<User> findUser = UserService.users.stream()
            .filter(user -> (
                user.getUsername().equals(username) &&
                user.getPassword().equals(password))
            )
            .findFirst();
        if (findUser.isEmpty()) {
            throw new Exception("Incorrect username or password. Please try again.");
        }
        UserService.currentUser = findUser.get();
    }

    public static void logout() {
        UserService.currentUser = null;
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
    
    public static <T extends User> T findUserByIdAndType(int userId, Class<T> userType) {
        Optional<T> result = UserService.getAllUserByType(userType)
            .stream()
            .filter(user -> userId == user.getUserId())
            .map(userType::cast) // Cast the user to the correct type
            .findFirst();
        return result.isPresent() ? result.get() : null;
    }
    
    // public void loadPatients(List<List<String>> patients) throws Exception {
    //     for (List<String> row : patients) {
    //         String username = row.get(0);
    //         String password = row.get(1);
    //         String patientId = row.get(2); // TODO
    //         String name = row.get(3);
    //         String gender = row.get(4);
    //         String mobileNumber = row.get(5);
    //         String homeNumber = row.get(6);
    //         String email = row.get(7);
    //         String dateOfBirth = row.get(8); // Assuming the DoB format is correct in the new data
    //         String bloodType = row.get(9);
            
    //         // Create a patient and add to users list
    //         users.add(new Patient(
    //             username, password, name, patientId, gender,
    //             mobileNumber, homeNumber, email, dateOfBirth, bloodType
    //         ));
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
