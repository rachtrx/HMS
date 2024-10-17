package app.model.user_input.menus;

import java.util.ArrayList;

import app.model.user_input.options.AppointmentOption;
import app.model.user_input.options.BaseOption;

public class SelectAppointmentMenu extends BaseMenu {

    public SelectAppointmentMenu(ArrayList<AppointmentOption> appointmentOptions, String action) {
        super(
            appointments, "Select an appointment to" + action);
    }

    // Ways to display for Patient: Viewing Available slots, Viewing scheduled appointments
    // ways to display for Doctor: Viewing Upcoming appointments, 
    
    // Next action for Patient: Schedule / Reschedule / Cancel
    // Next action for Doctor: Accept or decline, indicate busy?
}
