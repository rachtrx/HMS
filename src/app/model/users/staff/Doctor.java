package app.model.users.staff;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Doctor extends Staff {

    private Map<LocalDate, List<LocalDateTime>> availability;

    public Doctor(String doctorId, String name, char gender, String age) {
        super(doctorId, name, gender, age, doctorId);
        this.staffRole = 'D';
    }

    public void printAvailability() {
        // TODO
    }

    public void displayUserMenu() {
        // TODO
    }
}