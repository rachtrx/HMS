package app.model.user_input.menus;

import app.model.user_input.options.BaseOption;

import java.util.ArrayList;

// Simpler implementation?
public class BaseOption {
    private String title;
    private Runnable action;

    public BaseOption(String title, Runnable action) {
        this.title = title;
        this.action = action;
    }

    public void select() {
        action.run();
    }

    @Override
    public String toString() {
        return title;
    }
}

public class PatientMenu {
    private final ArrayList<BaseOption> options;

    public PatientMenu() {
        options = new ArrayList<>();
        setupOptions();
    }

    private void setupOptions() {
        options.add(new BaseOption("View Medical Record", this::viewMedicalRecord));
        options.add(new BaseOption("Update Personal Information", this::updatePersonalInformation));
        options.add(new BaseOption("View Available Appointment Slots", this::viewAvailableAppointments));
        options.add(new BaseOption("Schedule an Appointment", this::scheduleAppointment));
        options.add(new BaseOption("Reschedule an Appointment", this::rescheduleAppointment));
        options.add(new BaseOption("Cancel an Appointment", this::cancelAppointment));
        options.add(new BaseOption("View Scheduled Appointments", this::viewScheduledAppointments));
        options.add(new BaseOption("View Past Appointment Outcome Records", this::viewPastAppointmentRecords));
        options.add(new BaseOption("Logout", this::logout));
    }

    public void display() {
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
    }

    // methods for menu actions. Set next menu / perform some action?
    private void viewMedicalRecord() {  }
    private void updatePersonalInformation() {  }
    private void viewAvailableAppointments() {  }
    private void scheduleAppointment() {  }
    private void rescheduleAppointment() {  }
    private void cancelAppointment() {  }
    private void viewScheduledAppointments() {  }
    private void viewPastAppointmentRecords() {  }
    private void logout() {  }

    // Additional methods as necessary
}

