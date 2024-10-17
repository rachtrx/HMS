package app.service;

import app.model.users.Patient;
import app.model.users.User;
import app.model.users.staff.Admin;
import app.model.users.staff.Doctor;
import app.model.users.staff.Pharmacist;
import app.utils.DateTimeUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserService {

    private List<User> users = new ArrayList<>();
    private List<String> roles = Arrays
        .asList(Patient.class, Doctor.class)
        .stream()
        .map(Class::getSimpleName)
        .collect(Collectors.toList());

    public User findUser(String id, String password) {
		return users.get(0); // TODO
	}

    public void loadPatients(List<List<String>> patients) {
        for (List<String> row : patients) {
            String hospitalId = row.get(0).substring(1);
            String name = row.get(1);
            String doB = DateTimeUtil.formatCsvDate(row.get(2));
            char gender = row.get(3).charAt(0);
            String bloodType = row.get(4);
            String email = row.get(5);

            // Create a patient and add to users list
            User patient = createPatient(hospitalId, name, doB, gender, bloodType, email);
            users.add(patient);
        }
    }

    public void loadStaff(List<List<String>> staff) {
        for (List<String> row : staff) {
            String hospitalId = row.get(0).substring(1);
            String name = row.get(1);
            char role = row.get(2).charAt(0);
            char gender = row.get(3).charAt(0);
            String age = row.get(4);

            // Create staff and add to users list
            User staffMember = createStaff(hospitalId, name, role, gender, age);
            users.add(staffMember);
        }
    }

    public User createPatient(String hospitalId, String name, String doB, char gender, String bloodType, String email) {
        Patient patient = new Patient(hospitalId, name, doB, gender, bloodType, email);
        return patient;
    }

    public User createStaff(String hospitalId, String name, String role, char gender, String age) {

        User user;

        switch (role) {
            case 'D': // TODO convert to Enum? same for class
                user = new Doctor(hospitalId, name, gender, age);
                break;
            case 'P':
                user = new Pharmacist(hospitalId, name, gender, age);
                break;
            case 'A':
                user = new Admin(hospitalId, name, gender, age);
                break;
            default:
                throw new IllegalArgumentException("Invalid role");
        }
        return user;
    }    
}
