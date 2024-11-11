package app.service;

import app.db.DatabaseManager;
import app.model.user_input.OptionTable;
import app.model.users.User;
import app.model.users.staff.Staff;
import app.utils.LoggerUtils;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
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
        String userNames = users.stream()
                                .map(User::getName)
                                .collect(Collectors.joining(", "));
        LoggerUtils.info("users added: " + userNames);
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
            throw new IllegalArgumentException("Incorrect username or password. Please try again.");
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

    public static Staff findStaffById(int staffId) {
        return UserService.getAllUsers()
            .stream()
            .filter(user -> user instanceof Staff && ((Staff) user).getStaffId() == staffId)
            .map(user -> (Staff) user)
            .findFirst()
            .orElse(null);
    }
    
    public static <T extends User> T findUserByIdAndType(int userId, Class<T> userType, boolean byRole) {
        Optional<T> result = UserService.getAllUserByType(userType)
            .stream()
            .filter(user -> byRole ? userId == user.getRoleId() : userId == user.getUserId())
            .map(userType::cast) // Cast the user to the correct type
            .findFirst();
        return result.isPresent() ? result.get() : null;
    }

    public static enum SortFilter {
        ROLE {
            @Override
            public String toString() {
                return "Role";
            }
        },
        GENDER {
            @Override
            public String toString() {
                return "Gender";
            }
        },
        AGE {
            @Override
            public String toString() {
                return "Age";
            }
        },
    }

    public static List<Staff> getSortedUsers(SortFilter sortBy, boolean ascending) {
        if (users.isEmpty()) {
            System.out.println("No users found");
            return null;
        }

        // Sort users based on the specified field
        Comparator<User> comparator;
        switch (sortBy) {
            case ROLE:
                comparator = Comparator.comparing(user -> user.getClass().getSimpleName());
                break;
            case GENDER:
                comparator = Comparator.comparing(User::getGender);
                break;
            case AGE:
                comparator = Comparator.comparing(User::getDateOfBirth).reversed();
                break;
            default:
                System.out.println("Invalid sort field. Sorting by default role.");
                comparator = Comparator.comparing(user -> user.getClass().getSimpleName());
        }

        // Print each user in a row
        return users.stream()
            .sorted(ascending ? comparator : comparator.reversed())
            .filter(user -> user instanceof Staff)
            .map(user -> (Staff) user)
            .collect(Collectors.toList());
    }

    public static void deleteStaff(Staff staff) {
        DatabaseManager.delete(staff);
        users.removeIf(user -> {
            if (user instanceof Staff) {
                return staff.getStaffId() == ((Staff)user).getStaffId();
            }
            return false;
        });
    }
}

