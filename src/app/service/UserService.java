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
    
    public static <T extends User> T findUserByIdAndType(int userId, Class<T> userType, boolean byRole) {
        Optional<T> result = UserService.getAllUserByType(userType)
            .stream()
            .filter(user -> byRole ? userId == user.getRoleId() : userId == user.getUserId())
            .map(userType::cast) // Cast the user to the correct type
            .findFirst();
        return result.isPresent() ? result.get() : null;
    }
}
