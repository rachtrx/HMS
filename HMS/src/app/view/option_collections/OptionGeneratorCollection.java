package app.view.option_collections;

import app.constants.BloodType;
import app.constants.Gender;
import app.constants.exceptions.ExitApplication;
import app.controller.AppointmentService;
import app.controller.MedicationService;
import app.controller.UserService;
import app.controller.UserService.SortFilter;
import app.model.appointments.Appointment;
import app.model.appointments.Appointment.AppointmentStatus;
import app.model.appointments.AppointmentOutcomeRecord;
import app.model.appointments.AppointmentOutcomeRecord.ServiceType;
import app.model.appointments.DoctorEvent;
import app.model.appointments.Prescription;
import app.model.appointments.Prescription.PrescriptionStatus;
import app.model.appointments.Timeslot;
import app.model.inventory.Medication;
import app.model.inventory.Request;
import app.model.users.Patient;
import app.model.users.User;
import app.model.users.staff.Admin;
import app.model.users.staff.Doctor;
import app.model.users.staff.Pharmacist;
import app.model.users.staff.Staff;
import app.utils.DateTimeUtils;
import app.view.MenuState;
import app.view.Option;
import app.view.FunctionalInterfaces.NextAction;
import app.view.Option.OptionType;
import app.view.menu_collections.DoctorMenuCollection.UpcomingEventControl;
import app.view.menu_collections.MenuCollection.Control;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OptionGeneratorCollection {

    /**
     * Generates a list of confirmation options, allowing users to confirm or cancel an action.
     * This method acts as a middleware option generator that accepts the specific actions 
     * and menu states to use for each confirmation choice, allowing for flexibility in various 
     * workflows.
     * <p>
     * The generated options include:
     * <ul>
     *   <li>Yes Option (confirm): Sets the <code>nextAction</code> and transitions to <code>nextState</code>.</li>
     *   <li>No Option (cancel): Returns to the <code>exitState</code> without modifying form data.</li>
     * </ul>
     * Each option is unnumbered, with "Y" and "N" as selectors for confirmation and cancellation, respectively.
     *
     * @param nextAction The action to execute when the "Yes" option is selected.
     * @param nextState  The menu state to navigate to after confirming.
     * @param exitState  The menu state to navigate to if canceled.
     * @return A list containing "Yes" and "No" options for confirmation and cancellation.
     */
    public static List<Option> generateConfirmOptions(NextAction<Exception> nextAction, MenuState nextState, MenuState exitState) {
        // Define options for confirmation
        Option yesOption = new Option(
            "yes|y|yes( )?\\(?y\\)?", 
            OptionType.UNNUMBERED,
            Map.of("Select", "Y", "Action", "Confirm"))
            .setNextMenuState(nextState)
            .setExitMenuState(exitState)
            .setNextAction(nextAction);
    
        Option noOption = new Option(
                "no|n|no( )?\\(?n\\)?",
                OptionType.UNNUMBERED,
                Map.of("Select", "N", "Action", "Cancel")
            )
            .setNextMenuState(exitState)
            .setNextAction((formData) -> formData);
    
        // Return a list containing both options
        return Arrays.asList(yesOption, noOption);
    }

    /**
     * Generates a list of options for logging out or exiting the application, often appended to existing option menus.
     * <p>
     * The generated options include:
     * <ul>
     *   <li>Logout Option ("LO"): Logs the user out and navigates back to the login screen.</li>
     *   <li>Exit Option ("E"): Exits the application, throwing an <code>ExitApplication</code> exception.</li>
     * </ul>
     * Each option is unnumbered and provides a concise selection pattern for easy access.
     *
     * @return A list containing logout and exit options.
     */
    public static List<Option> generateLogoutAndExitOptions() {
        List<Option> options = new ArrayList<>();

        // Logout option
        options.add(new Option(
            "^LO$|log( )?out(( )?\\(LO\\))?",
            OptionType.UNNUMBERED,
            Map.of("Select", "LO", "Action", "Logout")
        ).setNextAction((formData) -> {
            UserService.logout();
            return null;
        }).setNextMenuState(MenuState.LOGIN_USERNAME));

        // Exit option
        options.add(new Option(
            "^E$|exit(( )?((app)?plication)?)?(( )?\\(E\\))?",
            OptionType.UNNUMBERED,
            new LinkedHashMap<>() {{
                put("Select", "E");
                put("Action", "Exit Application");
            }}
        ).setNextAction((formData) -> {
            throw new ExitApplication();
        }));

        return options;
    }

    /**
     * Generates an option for returning to the main menu, often appended to existing option menus.
     * <p>
     * The generated option allows users to select "Main Menu" or "M" to navigate 
     * back to their respective main menu state.
     * <ul>
     *   <li>Main Menu Option ("M"): Navigates back to the main menu for the current user.</li>
     * </ul>
     * Additional options can be added to this list if needed.
     *
     * @return A list containing the main menu option.
     */
    public static List<Option> generateMainMenuOption() {
        List<Option> options = new ArrayList<>();

        // Main MenuState option
        options.add(new Option(
            "^M$|main|menu|(main|menu|main menu)(( )?\\(M\\))?",
            OptionType.UNNUMBERED,
            new LinkedHashMap<>() {{
                put("Select", "M");
                put("Action", "Return to main menu");
            }}
        ).setNextMenuState(MenuState.getUserMainMenuState()));

        // Additional options can be added to this list as needed

        return options;
    }

    /**
     * Generates an option for users to reset their password.
     * <p>
     * This option allows users to navigate to the password reset process, which leads
     * to the {@link MenuState#LOGIN_PASSWORD} state for further password-related actions.
     * The option is designed as a numbered choice, making it easily selectable in a menu.
     * 
     * @return An <code>Option</code> object configured for the password reset action, with
     *         a transition to the login password menu state.
     */
    public static Option generateResetPasswordOption() {
        return new Option(
            "reset( )?(password( )?)?", 
            OptionType.NUMBERED,
            new LinkedHashMap<>() {{
                put("Action", "Reset Password");
            }}
        ).setNextMenuState(MenuState.LOGIN_PASSWORD);
    }

    // SECTION PATIENT MAIN MENU
    /**
     * Generates a list of menu options for patients, allowing them to view and manage
     * their medical records and appointments.
     * <p>
     * The generated options cover various actions for patient interaction, including:
     * <ul>
     *   <li>Viewing and editing medical records</li>
     *   <li>Viewing available appointments and scheduling new ones</li>
     *   <li>Rescheduling, canceling, and viewing scheduled appointments</li>
     *   <li>Viewing historical appointment outcomes</li>
     * </ul>
     * Each option sets the appropriate {@link MenuState} to handle user navigation.
     *
     * @return A list of <code>Option</code> objects, each representing a different patient action
     *         with necessary configurations for menu navigation.
     */
    public static List<Option> generatePatientMenuOptions() {
        List<Option> options = new ArrayList<>(List.of(
            new Option(
                    "(view( )?)?medical(( )?record)?", 
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Action", "View Medical Record");
                    }}
                ).setNextMenuState(MenuState.PATIENT_VIEW_MEDICAL_RECORD), // PATIENT_VIEW_MEDICAL_RECORD
            new Option(
                    "(edit( )?)?contact(( )?info(rmation)?)?", 
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Action", "Edit Medical Record");
                    }}
                ).setNextMenuState(MenuState.PATIENT_EDIT_MEDICAL_RECORD),
            new Option(
                    "view( )?(available( )?)?appointment(s)?", 
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Action", "View Available Appointments");
                    }}
                ).setNextMenuState(MenuState.PATIENT_VIEW_AVAIL_APPOINTMENTS),
            new Option(
                    "^schedule( )?(a(n)?( )?)?appointment(s)?",
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Action", "Schedule an Appointment");
                    }}
                ).setNextMenuState(MenuState.TIMESLOT_SELECTION_TYPE)
                .setNextAction((formData) -> new HashMap<String, Object>() {{
                        put("yearValidator", DateTimeUtils.DateConditions.FUTURE_OR_PRESENT.toString());
                        put("monthValidator", DateTimeUtils.DateConditions.FUTURE_OR_PRESENT.toString());
                        put("dayValidator", DateTimeUtils.DateConditions.FUTURE_OR_PRESENT.toString());
                        put("hourValidator", DateTimeUtils.DateConditions.FUTURE.toString());
                    }}
                ),
            new Option(
                    "^Reschedule( )?(a(n)?( )?)?appointment(s)?",
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Action", "Reschedule an Appointment");
                    }}
                ).setNextMenuState(MenuState.PATIENT_RESCHEDULE_SELECTION)
                .setNextAction((formData) -> new HashMap<String, Object>() {{
                        put("yearValidator", DateTimeUtils.DateConditions.FUTURE_OR_PRESENT.toString());
                        put("monthValidator", DateTimeUtils.DateConditions.FUTURE_OR_PRESENT.toString());
                        put("dayValidator", DateTimeUtils.DateConditions.FUTURE_OR_PRESENT.toString());
                        put("hourValidator", DateTimeUtils.DateConditions.FUTURE.toString());
                    }}
                ),
            new Option(
                "^Cancel( )?(a(n)?( )?)?appointment(s)?",
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Action", "Cancel an Appointment");
                    }}
                ).setNextMenuState(MenuState.PATIENT_CANCEL_SELECTION)
                .setNextAction((formData) -> new HashMap<String, Object>()),
            new Option(
                    "view( )?(scheduled( )?)?appointment(s)?|(view( )?)?confirmed", 
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Action", "View Scheduled Appointments");
                    }}
                ).setNextMenuState(MenuState.PATIENT_VIEW_CONFIRMED_APPOINTMENTS),
            new Option(
                    "view( )?(appointment( )?)?outcomes(s)?|(view( )?)?history", 
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Action", "View Appointment Outcomes");
                    }}
                ).setNextMenuState(MenuState.PATIENT_VIEW_OUTCOMES)
        ));

        options.add(generateResetPasswordOption());

        return options;
    }

    /**
     * Generates an option for editing the blood type of a patient.
     * <p>
     * When selected, this option prompts the user to enter a new blood type 
     * for the specified patient, updating the patient’s record with the inputted value.
     * </p>
     *
     * @param p The patient whose blood type is being edited.
     * @return An option for editing the patient’s blood type, which redirects to the main menu state after updating.
     */
    public static Option getEditBloodTypeOption(Patient p) {
        return new Option(
            "blood|type|blood(( )?type)?|(blood( )?)?type",
            OptionType.NUMBERED,
            new LinkedHashMap<>() {{
                put("Field", "Blood Type");
                put("Current Value", p.getBloodType());
            }}
        ).setNextAction((formData) -> {
            p.setBloodType((String) formData.get("input"));
            return formData;
        }) // next menu set in generateEditPatientDetailsOptions
        .setEditRedirect(true);
    }
    
    /**
     * Generates an option for editing the mobile number of a patient.
     * <p>
     * This option allows the user to input a new mobile number, updating the patient’s record.
     * The default country code "+65" is prefixed to the current and new numbers.
     * </p>
     *
     * @param patient The patient whose mobile number is being edited.
     * @return An option for editing the patient’s mobile number, which exits to the main menu state after updating.
     */
    public static Option getEditMobileNumberOption(Patient patient) {
        return new Option(
            "mobile(( )?number)?",
            OptionType.NUMBERED,
            new LinkedHashMap<>() {{
                put("Field", "Mobile Number");
                put("Current Value", "+65" + patient.getMobileNumber());
            }}
        ).setNextAction((formData) -> {
            patient.setMobileNumber((String) formData.get("input"));
            return null;
        }) // next menu set in generateEditPatientDetailsOptions
        .setEditRedirect(true);
    }
    
    /**
     * Generates an option for editing the home number of a patient.
     * <p>
     * This option allows the user to input a new home number, with the default country code "+65"
     * prefixed to the displayed current and new values.
     * </p>
     *
     * @param patient The patient whose home number is being edited.
     * @return An option for editing the patient’s home number, with a redirect to the main menu state upon completion.
     */
    public static Option getEditHomeNumberOption(Patient patient) {
        return new Option(
            "home(( )?number)?",
            OptionType.NUMBERED,
            new LinkedHashMap<>() {{
                put("Field", "Home Number");
                put("Current Value", "+65" + patient.getHomeNumber());
            }}
        ).setNextAction((formData) -> {
            patient.setHomeNumber((String) formData.get("input"));
            return null;
        }) // next menu set in generateEditPatientDetailsOptions
        .setEditRedirect(true);
    }
    
    /**
     * Generates an option for editing the email address of a patient.
     * <p>
     * Upon selection, this option allows input of a new email address, which is saved to the patient’s record.
     * </p>
     *
     * @param patient The patient whose email is being edited.
     * @return An option for editing the patient’s email, redirecting to the medical record view upon completion.
     */
    public static Option getEditEmailOption(Patient patient) {
        return new Option(
            "email",
            OptionType.NUMBERED,
            new LinkedHashMap<>() {{
                put("Field", "Email");
                put("Current Value", patient.getEmail());
            }}
        ).setNextAction((formData) -> {
            patient.setEmail((String) formData.get("input"));
            return null;
        }) // next menu set in generateEditPatientDetailsOptions
        .setEditRedirect(true);
    }
    
    /**
     * Creates an option for doctors to edit appointments. Selecting this option
     * redirects the doctor to a menu where they can manage patient appointments.
     * 
     * @return An <code>Option</code> for editing appointments, accessible only to doctors.
     */
    public static Option getEditAppointmentOption() {
        return new Option(
            "edit( )?",
            OptionType.UNNUMBERED,
            new LinkedHashMap<>() {{
                put("Select", "edit");
                put("Action", "Edit Appointments");
            }}
        ).setNextMenuState(MenuState.DOCTOR_VIEW_PAST_APPOINTMENTS)
        .setNextAction((formData) -> formData);
    }

    /**
     * Generates a list of options for editing a patient's details. The available options
     * depend on the user type (doctor or patient):
     * <ul>
     *   <li><b>Doctors:</b> Can edit the patient's blood type and have the additional option
     *   to edit appointments.</li>
     *   <li><b>Patients:</b> Can only edit their personal contact details: mobile number,
     *   home number, and email.</li>
     * </ul>
     * 
     * Each option provides a redirection to the appropriate menu state based on the user type,
     * ensuring that:
     * <ul>
     *   <li>Doctors are directed to <code>MenuState.DOCTOR_EDIT_PAST_PATIENT</code> after
     *   each edit, allowing them to manage multiple details sequentially.</li>
     *   <li>Patients are redirected to <code>MenuState.PATIENT_VIEW_MEDICAL_RECORD</code>,
     *   allowing them to return to their personal medical record after editing contact details.</li>
     * </ul>
     * 
     * @param p The patient whose details are being edited.
     * @return A list of <code>Option</code> objects representing editable fields based on
     * the user's role.
     * @see #getEditBloodTypeOption(Patient)
     * @see #getEditMobileNumberOption(Patient)
     * @see #getEditHomeNumberOption(Patient)
     * @see #getEditEmailOption(Patient)
     */
    public static List<Option> generateEditPatientDetailsOptions(Patient p) {
        List<Option> options = new ArrayList<>();

        boolean isDoctor = UserService.getCurrentUser().getClass() == Doctor.class;
        MenuState nextMenuState = isDoctor ? 
            MenuState.DOCTOR_EDIT_PAST_PATIENT : 
            MenuState.PATIENT_EDIT_MEDICAL_RECORD;

        if (UserService.getCurrentUser().getClass() == Doctor.class) {
            options.add(OptionGeneratorCollection.getEditBloodTypeOption(p).setNextMenuState(nextMenuState));
            options.add(OptionGeneratorCollection.getEditAppointmentOption());
        }

        options.add(OptionGeneratorCollection.getEditMobileNumberOption(p).setNextMenuState(nextMenuState));
        options.add(OptionGeneratorCollection.getEditHomeNumberOption(p).setNextMenuState(nextMenuState));
        options.add(OptionGeneratorCollection.getEditEmailOption(p).setNextMenuState(nextMenuState));

        if (!isDoctor) {
            options.add(new Option(
                "view",
                OptionType.UNNUMBERED,
                Map.of("Select", "LO", "Action", "Logout")
            ).setNextAction((formData) -> {
                UserService.logout();
                return null;
            }).setNextMenuState(MenuState.LOGIN_USERNAME));
        }

        return options;
    }

    /**
     * Generates a list of options for displaying or editing appointments on the Patient Menu. This method provides a detailed view of each
     * appointment, including appointment timeslot, patient and doctor names, status, and (if available) additional outcome
     * details such as service type and consultation notes.
     * <p>
     * The generated options vary based on the provided <code>Control</code> parameter:
     * <ul>
     *   <li><b>Display Only:</b> When <code>ctl</code> is <code>Control.NONE</code>, the options provide a read-only 
     *   display of appointment details.</li>
     *   <li><b>Select Mode:</b> When <code>ctl</code> is <code>Control.SELECT</code>, the options are numbered, allowing 
     *   users to select an appointment for viewing. Selecting an option redirects to the detailed appointment record.</li>
     * </ul>
     * 
     * Additional actions:
     * <ul>
     *   <li>A "View Appointment Outcomes" option is added for patients if they are viewing without SELECT access. 
     *   This redirects to the <code>MenuState.PATIENT_VIEW_OUTCOMES</code> for viewing detailed outcome information.</li>
     * </ul>
     *
     * @param appointments The list of appointments to be displayed.
     * @param ctl Specifies whether options are displayed as read-only (<code>Control.NONE</code>) or allow editing 
     * (<code>Control.EDIT</code>).
     * @return A list of <code>Option</code> objects representing each appointment's details, with select or view functionality
     * depending on the control type.
     */
    public static List<Option> generateAppointmentDisplayOptions(List<Appointment> appointments, Control ctl) {
        boolean allOutcomesNull = appointments.stream()
            .allMatch(appointment -> appointment.getAppointmentOutcome() == null);
            
        List<Option> options = IntStream.range(0, appointments.size())
            .mapToObj(appointmentIndex -> {
                Appointment appointment = appointments.get(appointmentIndex);
                
                String timeslot = DateTimeUtils.printLongDateTime(appointment.getTimeslot());
                String patientName = UserService
                    .findUserByIdAndType(appointment.getPatientId(), Patient.class, true)
                    .getName();
                Doctor doctor = UserService.findUserByIdAndType(appointment.getDoctorId(), Doctor.class, true);
                String doctorName = (doctor == null) ? "No doctor assigned" : doctor.getName();
                String status = appointment.getAppointmentStatus().toString();
                
                LinkedHashMap<String, String> displayFields = new LinkedHashMap<>();
                displayFields.put("Appointment Timeslot", timeslot);
                displayFields.put("Patient Name", patientName);
                displayFields.put("Doctor Name", doctorName);
                displayFields.put("Appointment Status", status);

                if (!allOutcomesNull) {
                    boolean hasOutcome = appointment.getAppointmentOutcome() != null;
                    displayFields.put("Service Type", !hasOutcome ? "N/A" : appointment.getAppointmentOutcome().getServiceType());
                    displayFields.put("Consultation Notes", !hasOutcome ? "N/A" : appointment.getAppointmentOutcome().getConsultationNotes());
                    displayFields.put("Outcome Added", String.valueOf(hasOutcome));
                }
                

                Option option = new Option(
                    String.format("Appointment #%d", appointmentIndex + 1),
                    ctl != Control.SELECT ? OptionType.DISPLAY : OptionType.NUMBERED,
                    displayFields
                );

                if (ctl == Control.SELECT) {
                    option.setNextMenuState(MenuState.VIEW_RECORD)
                    .setNextAction(formValues -> {
                        formValues.put("appointment", appointment);
                        return formValues;
                    });
                }

                return option;
            })
            .collect(Collectors.toList());
        
        if (ctl != Control.SELECT) {
            options.add(new Option(
                "view( )?",
                OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "view");
                    put("Action", "View Appointment Outcomes");
                }}
            ).setNextAction((formValues) -> {
                return new HashMap<>();
            }).setNextMenuState(MenuState.PATIENT_VIEW_OUTCOMES));
            options.add(new Option(
                "edit( )?",
                OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "edit");
                    put("Action", "Edit Medical Record");
                }}
            ).setNextAction((formValues) -> {
                return new HashMap<>();
            }).setNextMenuState(MenuState.PATIENT_EDIT_MEDICAL_RECORD));
        }
        

        return options;
    }

    // TODO
    public static List<Option> generateAvailableTimeslotOptionsByDate(Doctor doctor) {
        // Get all available timeslots for the doctor over the next month
        List<Timeslot> availableSlots = AppointmentService.getAvailableAppointmentSlotsForDoctorNextMonth(doctor);
    
        // Collect all unique times across dates for column headers
        Set<LocalTime> uniqueTimes = availableSlots.stream()
            .map(timeslot -> timeslot.getTimeSlot().toLocalTime())
            .collect(Collectors.toCollection(TreeSet::new)); // Sorted time
    
        // Group timeslots by date with ticks and crosses
        Map<LocalDate, Map<LocalTime, String>> availabilityByDate = availableSlots.stream()
            .collect(Collectors.groupingBy(
                timeslot -> timeslot.getTimeSlot().toLocalDate(),
                TreeMap::new, // sort date
                Collectors.toMap(
                    timeslot -> timeslot.getTimeSlot().toLocalTime(),  // Map time to availability tick
                    t -> "✓",
                    (a, b) -> a,
                    () -> uniqueTimes.stream().collect(Collectors.toMap(time -> time, time -> "✗")) // Default "✗"
                )
            ));
    
        // Fill in the ticks for available timeslots
        availableSlots.forEach(timeslot -> {
            LocalDate date = timeslot.getTimeSlot().toLocalDate();
            LocalTime time = timeslot.getTimeSlot().toLocalTime();
            availabilityByDate.get(date).put(time, "✓");
        });
    
        // Create display options with dates as rows and timeslots as columns
        List<Option> options = availabilityByDate.entrySet().stream()
            .map(entry -> {
                LocalDate date = entry.getKey();
                Map<LocalTime, String> timeslotAvailability = entry.getValue();
    
                LinkedHashMap<String, String> displayFields = new LinkedHashMap<>();
                displayFields.put("Date", date.toString());
    
                // Add each timeslot in the row for this date with corresponding tick/cross
                uniqueTimes.forEach(time -> displayFields.put(time.toString(), timeslotAvailability.getOrDefault(time, "✗")));
    
                return new Option(
                    date.toString(),
                    Option.OptionType.DISPLAY,
                    displayFields
                );
            })
            .collect(Collectors.toList());

        options.add(new Option(
            "view( )?",
            OptionType.UNNUMBERED,
            new LinkedHashMap<>() {{
                put("Select", "view");
                put("Action", "View Another Doctor");
            }}
        ).setNextAction((formValues) -> {
            return new HashMap<>();
        }).setNextMenuState(MenuState.INPUT_DOCTOR));

        options.add(new Option(
            "add",
            OptionType.UNNUMBERED,
            new LinkedHashMap<>() {{
                put("Select", "add");
                put("Action", "Schedule an Appointment");
            }}
        ).setNextAction((formValues) -> {
            return new HashMap<>();
        }).setNextMenuState(MenuState.TIMESLOT_SELECTION_TYPE));

        return options;
    }

    /**
     * Generates a list of options displaying available timeslots for each doctor, along with their availability.
     * This list is intended to provide an overview of each doctor's schedule with quick availability indicators 
     * (ticks and crosses) for each timeslot.
     * <p>
     * This option generator performs the following:
     * <ul>
     *   <li>Collects all unique timeslots across the doctors' schedules to standardize the display.</li>
     *   <li>Creates a display option for each doctor, listing their name and marking each unique timeslot as 
     *   available ("✓") or unavailable ("✗").</li>
     * </ul>
     * Additionally, an unnumbered "Schedule an Appointment" option is included at the end of the list for users 
     * to initiate the scheduling process, redirecting them to <code>MenuState.TIMESLOT_SELECTION_TYPE</code>.
     *
     * @param timeslotsByDoctor A map of doctors to their list of available timeslots.
     * @return A list of <code>Option</code> objects, each representing a doctor's availability for each timeslot, 
     * with an added scheduling option at the end.
     */
    public static List<Option> generateAvailableTimeslotOptions(Map<Doctor, List<Timeslot>> timeslotsByDoctor) {
        // Collect all unique timeslots
        Set<LocalDateTime> uniqueTimeslots = timeslotsByDoctor.values().stream()
            .flatMap(List::stream)
            .map(Timeslot::getTimeSlot)
            .collect(Collectors.toCollection(TreeSet::new)); // Sorted by default for ordering

        // Generate options for each doctor
        List<Option> options = new ArrayList<>();

        timeslotsByDoctor.forEach((doctor, timeslots) -> {
            LinkedHashMap<String, String> displayFields = new LinkedHashMap<>();
            displayFields.put("Doctor Name", doctor.getName());

            // Populate availability for each timeslot with ticks and crosses
            uniqueTimeslots.forEach(timeslot -> {
                String availability = timeslots.stream().anyMatch(t -> t.getTimeSlot().equals(timeslot)) ? "✓" : "✗";
                displayFields.put(DateTimeUtils.printShortestDateTime(timeslot), availability);
            });

            // Create an Option for each doctor with their timeslot availability
            options.add(new Option(
                doctor.getName(),
                Option.OptionType.DISPLAY,
                displayFields
            ));
        });

        options.add(new Option(
            "view",
            OptionType.UNNUMBERED,
            new LinkedHashMap<>() {{
                put("Select", "view");
                put("Action", "Select Doctor");
            }}
        ).setNextAction((formValues) -> {
            return new HashMap<>();
        }).setNextMenuState(MenuState.INPUT_DOCTOR));

        options.add(new Option(
            "add",
            OptionType.UNNUMBERED,
            new LinkedHashMap<>() {{
                put("Select", "add");
                put("Action", "Schedule an Appointment");
            }}
        ).setNextAction((formValues) -> {
            return new HashMap<>();
        }).setNextMenuState(MenuState.TIMESLOT_SELECTION_TYPE));

        return options;
    }

    /**
     * Generates a list of options for selecting a date to schedule an appointment. 
     * The options include pre-defined choices for "Today" (if available) and "Tomorrow," 
     * as well as a "Custom" option to select any other date.
     * <p>
     * This option generator performs the following:
     * <ul>
     *   <li>If it is before the last available timeslot of the day, includes a "Today" option 
     *   that automatically sets the date to the current day.</li>
     *   <li>Includes a "Tomorrow" option that sets the date to the next day.</li>
     *   <li>Includes a "Custom" option allowing the user to manually select a date, 
     *   leading to <code>MenuState.INPUT_APPOINTMENT_YEAR</code>.</li>
     * </ul>
     * Each option sets the date fields (year, month, and day) in the <code>formData</code> 
     * map for use in subsequent scheduling steps.
     *
     * @return A list of <code>Option</code> objects, each representing a date selection choice.
     */
    public static List<Option> generateTimeSlotSelectOptions() {
        List<Option> options = new ArrayList<>(Arrays.asList(
            new Option(
                "tmr|tomorrow",
                OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Date", "Tomorrow");
                }}
            )
                .setNextAction((formData) -> {
                    LocalDate tmr = LocalDate.now().plusDays(1);
                    formData.put("year", String.valueOf(tmr.getYear()));
                    formData.put("month", String.valueOf(tmr.getMonthValue()));
                    formData.put("day", String.valueOf(tmr.getDayOfMonth()));
                    return formData;
                })
                .setNextMenuState(MenuState.INPUT_APPOINTMENT_HOUR),

            new Option("custom", OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Date", "Custom");
                }}
            )
            .setNextAction((formData) -> formData)
            .setNextMenuState(MenuState.INPUT_APPOINTMENT_YEAR)
        ));
        if (LocalTime.now().isBefore(Timeslot.lastSlotStartTime)) {
            options.add(0, new Option(
                    "today", 
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Date", "Today");
                    }}
                )
                .setNextAction((formData) -> {
                    LocalDate today = LocalDate.now();
                    formData.put("year", String.valueOf(today.getYear()));
                    formData.put("month", String.valueOf(today.getMonthValue()));
                    formData.put("day", String.valueOf(today.getDayOfMonth()));
                    return formData;
                })
                .setNextMenuState(MenuState.INPUT_APPOINTMENT_HOUR)
            );
        }
        return options;
    }

    /**
     * Generates a list of year options for scheduling appointments or specifying unavailability.
     * <p>
     * This option generator is shared between patients and doctors:
     * <ul>
     *   <li>Patients: Used to select the year when adding or rescheduling an appointment.</li>
     *   <li>Doctors: Used to select the year when adding or editing unavailability.</li>
     * </ul>
     *
     * @return A list of <code>Option</code> representing available years.
     */
    public static List<Option> getInputYearOptionGenerator() {
        LocalDateTime now = LocalDateTime.now();
        // Do not show this year if today is last day of the year and current time exceeds
        // last time slot
        int currentYear = now.getYear() + (
            now.equals(lastDayOfYear()) && now.toLocalTime().isAfter(Timeslot.lastSlotStartTime) ?
            1 : 0
        );
        return IntStream.range(currentYear, currentYear+2)
            .<Option>mapToObj(year -> new Option(
                    Integer.toString(year), OptionType.NUMBERED, new LinkedHashMap<>() {{
                        put("Year", Integer.toString(year));
                    }}
                ).setNextAction((formData) -> {
                    if (formData.isEmpty()) {
                        formData = new HashMap<>();
                    }
                    formData.put("year", Integer.toString(year));
                    return formData;
                }).setNextMenuState(MenuState.INPUT_APPOINTMENT_MONTH)
            ).collect(Collectors.toList());
    }

    /**
     * Generates a list of month options based on the selected year for scheduling appointments or specifying unavailability.
     * <p>
     * This option generator is shared between patients and doctors:
     * <ul>
     *   <li>Patients: Used to select the month when adding or rescheduling an appointment.</li>
     *   <li>Doctors: Used to select the month when adding or editing unavailability.</li>
     * </ul>
     *
     * @param formValues Contains previously selected year information to determine start month.
     * @return A list of <code>Option</code> representing available months within the selected year.
     */
    public static List<Option> getInputMonthOptionGenerator(Map<String, Object> formValues) {
        LocalDateTime now = LocalDateTime.now();
        int selectedYear = Integer.parseInt((String) formValues.get("year"));
        int startMonth = (selectedYear == now.getYear()) ? now.getMonthValue() : 1;

        return IntStream.rangeClosed(startMonth, 12)
            .<Option>mapToObj(month -> {
                Month currentMonth = Month.of(month);
                return new Option(
                        String.format(
                            "%s(%s)?",
                            currentMonth.toString().substring(0, 3),
                            currentMonth.toString().substring(3, currentMonth.toString().length())
                        ), 
                        OptionType.NUMBERED,
                        new LinkedHashMap<>() {{
                            put("Month", currentMonth.toString());
                        }}
                    ).setNextAction((formData) -> {
                        if (formData.isEmpty()) {
                            formData = new HashMap<String, Object>();
                        }
                        formData.put("month", Integer.toString(month));
                        formData.put(
                            "startDay",
                            Integer.toString(
                                selectedYear < now.getYear()+1 && month < now.getMonthValue()+1 ?
                                now.getDayOfMonth() : 1 + (
                                    now.toLocalTime().isAfter(Timeslot.lastSlotStartTime) ? 1 : 0
                        )));
                        formData.put("endDay", Integer.toString(now.with(lastDayOfMonth()).getDayOfMonth()));
                        return formData;
                    }).setNextMenuState(MenuState.INPUT_APPOINTMENT_DAY);
            }).collect(Collectors.toList());
    }

    /**
     * Generates a list of hour options for the selected date, allowing patients to set appointment times
     * or doctors to specify unavailability times.
     * <p>
     * This option generator is shared between patients and doctors:
     * <ul>
     *   <li>Patients: Used to select the hour when adding or rescheduling an appointment.</li>
     *   <li>Doctors: Used to select the hour when adding or editing unavailability.</li>
     * </ul>
     *
     * @param formValues Contains the selected year, month, and day to determine available hours.
     * @return A list of <code>Option</code> representing available hours within the selected date.
     */
    public static List<Option> getInputHourOptionGenerator(Map<String, Object> formValues) {
        // System.out.println("Start Getting hours");
        

        boolean isPatient = UserService.getCurrentUser() instanceof Patient;

        LocalDateTime now = LocalDateTime.now();
        int selectedYear = Integer.parseInt((String) formValues.get("year"));
        int selectedMonth = Integer.parseInt((String) formValues.get("month"));
        int selectedDay = Integer.parseInt((String) formValues.get("day"));
        
        LocalDate selectedDate = LocalDate.of(selectedYear, selectedMonth, selectedDay);
        boolean isToday = (selectedDate.isEqual(now.toLocalDate()) || selectedDate.isBefore(now.toLocalDate()));
        
        int startHour = isToday && now.toLocalTime().isAfter(Timeslot.firstSlotStartTime) ? 
        now.getHour() + 1 : Timeslot.firstSlotStartTime.getHour();

        // System.out.println("End Getting hours");

        return IntStream.range(startHour, Timeslot.lastSlotStartTime.getHour() + 1)
            .<Option>mapToObj(hour -> {
                Option option = new Option(
                    String.format("%02d:00", hour), 
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Hour", LocalTime.of(hour, 0).toString());
                    }}
                );
                if (isPatient) {
                    option
                    .setNextAction((newFormValues) -> {
                        newFormValues.put("dateTime", LocalDateTime.of(selectedDate, LocalTime.of(hour, 0)));
                        return newFormValues;
                    })
                    .setNextMenuState(MenuState.INPUT_APPOINTMENT_DOCTOR);
                } else {
                    Doctor d = (Doctor) UserService.getCurrentUser();
                    option
                    .setNextAction((newFormValues) -> {
                        LocalDateTime selectedBusyDateTime = LocalDateTime.of(selectedDate, LocalTime.of(hour, 0));
                        if (selectedBusyDateTime == null) {
                            throw new IllegalArgumentException("No timeslot selected.");
                        }
                        Optional<DoctorEvent> existingEvent = ((Doctor) UserService.getCurrentUser()).getDoctorEvents()
                            .stream()
                            .filter(event -> event.getTimeslot().equals(selectedBusyDateTime))
                            .findFirst();
                        
                        if (existingEvent.isPresent()) {
                            throw new IllegalArgumentException("An event already exists at this timeslot");
                        }

                        // Check if edit busy date
                        if (newFormValues != null && newFormValues.containsKey("originalDateTime")) {
                            LocalDateTime originalDateTime = DateTimeUtils.parseShortDateTime(
                                (String) newFormValues.get("originalDateTime")
                            );
                            d.deleteDoctorEvent(originalDateTime);
                        }
                        
                        d.addDoctorEvent(
                            DoctorEvent.create(d.getRoleId(), selectedBusyDateTime)
                        );
                        System.out.println(String.format(
                            "New event created at %s",
                            DateTimeUtils.printLongDateTime(selectedBusyDateTime)
                        ));

                        if (newFormValues != null) newFormValues.remove("originalDateTime");
                        
                        return newFormValues;
                    })
                    .setExitMenuState(MenuState.DOCTOR_VIEW_UPCOMING_UNAVAILABILITY)
                    .setNextMenuState(MenuState.DOCTOR_VIEW_UPCOMING_UNAVAILABILITY)
                    .setRequiresConfirmation(true);
                }
                return option;
            })
            .collect(Collectors.toList());
    }

    /** 
     * Generates a list of options for selecting an available doctor for a patient’s appointment, 
     * either to schedule a new appointment or reschedule an existing one.
     * <p>
     * This option generator is used by patients to view and select from a list of available doctors 
     * for the specified date and time.
     * <ul>
     *   <li>If rescheduling, it updates the existing appointment with the selected doctor.</li>
     *   <li>If scheduling a new appointment, it creates the appointment with the selected doctor.</li>
     * </ul>
     *
     * @param availableDoctors List of doctors available for the specified date and time.
     * @param selectedDateTime The chosen date and time for the appointment.
     * @return A list of <code>Option</code> representing available doctors, with actions for scheduling or rescheduling.
     */
    public static List<Option> getInputDoctorOptionGenerator(List<Doctor> availableDoctors, LocalDateTime selectedDateTime) {
        return availableDoctors.stream()
            .<Option>map(doctor -> {
                Option option = new Option( 
                    doctor.getName(), 
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Doctor", doctor.getName());
                    }}
                );
                
                if (selectedDateTime != null) {
                    option.setNextMenuState(MenuState.getUserMainMenuState())
                        .setNextAction((formData) -> {
                            if (formData.get("appointment") != null) {
                                Appointment appointment = (Appointment) formData.get("appointment");
                                appointment.cancel();
                                AppointmentService.scheduleAppointment(
                                    ((Patient) UserService.getCurrentUser()).getRoleId(),
                                    doctor.getRoleId(),
                                    selectedDateTime
                                );
                            } else {
                                AppointmentService.scheduleAppointment(
                                    ((Patient) UserService.getCurrentUser()).getRoleId(),
                                    doctor.getRoleId(),
                                    selectedDateTime
                                );
                            }
                            return formData;
                        })
                        .setExitMenuState(MenuState.INPUT_APPOINTMENT_DOCTOR)
                        .setRequiresConfirmation(true);
                } else {
                    option.setNextMenuState(MenuState.PATIENT_VIEW_AVAIL_APPOINTMENTS_DOCTOR)
                        .setNextAction(formData -> {
                            formData.put("doctor", doctor);
                            return formData;
                        });
                }
                return option;
            })
            .collect(Collectors.toList());
    }

    /**
     * Generates a list of options for patients to update their appointments by either rescheduling or canceling.
     * <p>
     * This option generator is exclusively for patients:
     * <ul>
     *   <li>If the control type is <code>Control.EDIT</code>, patients can reschedule their appointments by selecting a new timeslot.</li>
     *   <li>If the control type is <code>Control.DELETE</code>, patients can cancel their appointment, requiring confirmation.</li>
     * </ul>
     *
     * @param appointments List of appointments for the patient to manage.
     * @param ctl Specifies the control type, either <code>Control.EDIT</code> (reschedule) or <code>Control.DELETE</code> (cancel).
     * @return A list of <code>Option</code> representing each appointment, with actions for rescheduling or canceling.
     */
    public static List<Option> generateUpdateAppointmentOptions(List<Appointment> appointments, Control ctl) {
        return appointments.stream()
            .<Option>map(appointment -> {
                
                Option option = new Option(
                    DateTimeUtils.printShortDateTime(appointment.getTimeslot()), 
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("DateTime", DateTimeUtils.printLongDateTime(appointment.getTimeslot()));
                        put("Doctor", UserService.findUserByIdAndType(appointment.getDoctorId(), Doctor.class, true).getName());
                        put("Status", appointment.getAppointmentStatus().toString());
                    }}
                );

                if (ctl == Control.EDIT) {
                    option.setNextMenuState(MenuState.TIMESLOT_SELECTION_TYPE)
                    .setNextAction((formValues) -> {
                        formValues.put("appointment", appointment);
                        return formValues;
                    });
                } else { // IMPT Control.DELETE = cancel appt
                    option.setNextMenuState(MenuState.PATIENT_MAIN_MENU)
                    .setNextAction((formValues) -> {
                        appointment.cancel();
                        return formValues;
                    })
                    .setExitMenuState(MenuState.PATIENT_CANCEL_SELECTION)
                    .setRequiresConfirmation(true);
                }

                return option;
            })
            .collect(Collectors.toList());
    }

    // SECTION DOCTOR MAIN MENU
    /**
     * Generates a list of menu options for doctors, enabling them to manage patient records, 
     * view and update their schedule, and handle appointment requests.
     * <p>
     * This menu provides the following options:
     * <ul>
     *   <li><b>View Patient's Medical Record:</b> Navigate to a menu for viewing past patients' medical records.</li>
     *   <li><b>Update Patient's Medical Record:</b> Access a menu to update information in a patient’s medical record.</li>
     *   <li><b>View Personal Schedule:</b> View upcoming scheduled events and appointments.</li>
     *   <li><b>Manage Availability:</b> Adjust availability by setting or removing unavailability periods.</li>
     *   <li><b>Accept or Decline Appointment Requests:</b> Review and manage appointment requests from patients.</li>
     *   <li><b>View Upcoming Appointments:</b> View scheduled appointments that are yet to occur and cancel them if required.</li>
     *   <li><b>Record Appointment Outcome:</b> Enter outcomes of past appointments, updating the patient’s record with consultation details.</li>
     * </ul>
     * Each option transitions the doctor to the appropriate menu state for managing the selected task.
     *
     * @return A list of <code>Option</code> representing the various administrative and clinical tasks available to doctors.
     */
    public static List<Option> generateDoctorMenuOptions() {
        List<Option> options = new ArrayList<>(List.of(
            new Option(
                    "select( )?(patient(\\'s)?( )?)?(manage( )?)?", 
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Action", "Select a patient to manage");
                    }}
                ).setNextMenuState(MenuState.DOCTOR_VIEW_PAST_PATIENTS),
            new Option(
                    "(view( )?)?(personal( )?)?schedule", 
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Action", "View Personal Schedule");
                    }}
                ).setNextMenuState(MenuState.DOCTOR_VIEW_UPCOMING_EVENTS),
    
            new Option(
                    "(manage( )?)?Availability", 
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Action", "Manage Availability");
                    }}
                ).setNextMenuState(MenuState.DOCTOR_VIEW_UPCOMING_UNAVAILABILITY),
    
            new Option(
                    "accept|decline|(appointment)?( )?requests", 
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Action", "Accept or Decline Appointment Requests");
                    }}
                ).setNextMenuState(MenuState.DOCTOR_HANDLE_UPCOMING_APPOINTMENTS),
    
            new Option(
                    "view|upcoming", 
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Action", "View Upcoming Appointments");
                    }}
                ).setNextMenuState(MenuState.DOCTOR_VIEW_CONFIRMED_APPOINTMENTS),
    
            new Option(
                    "record|outcome", 
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Action", "Record Appointment Outcome");
                    }}
                ).setNextMenuState(MenuState.DOCTOR_ADD_RECORDS)
        ));

        options.add(generateResetPasswordOption());
        return options;
    }

    /**
     * Generates a list of options for doctors to view patients under their care, enabling further 
     * actions or navigation related to each patient.
     * <p>
     * This method retrieves all unique patients associated with the current doctor’s appointments, 
     * displaying basic details for each patient. Doctors can:
     * <ul>
     *   <li>See the patient’s name and ID.</li>
     *   <li>Select a patient to navigate to the past appointments view for that patient.</li>
     * </ul>
     * Each selected option adds the patient to the form data, allowing the doctor to view past appointments upon selection.
     *
     * @return A list of <code>Option</code> representing each patient under the doctor’s care, with actions to view their past appointments.
     */
    public static List<Option> generatePatientOptions() {
        Doctor d = (Doctor) UserService.getCurrentUser();
        Set<Integer> patientIds = AppointmentService.getAllAppointmentsForDoctor(d.getRoleId()).stream()
            .map(Appointment::getPatientId)
            .collect(Collectors.toSet());
        List<Option> userOptions = patientIds.stream()
            .map(patientId -> UserService.findUserByIdAndType(patientId, Patient.class, true))
            .filter(Objects::nonNull)
            .map(patient -> new Option(
                patient.getName(),
                OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Name", patient.getName());
                    put("Patient ID", "P" + patient.getRoleId());
                }}
            ).setNextMenuState(MenuState.DOCTOR_VIEW_PAST_APPOINTMENTS)
                .setNextAction(formValues -> new HashMap<String, Object>() {{
                    put("patient", patient);
                }}))
            .collect(Collectors.toList());
        return userOptions;
    }

    /**
     * Generates a list of options for doctors to manage upcoming events and appointments, with actions 
     * tailored to the specified control type. This menu supports viewing, adding, editing, and deleting 
     * busy timeslots, as well as managing appointment requests.
     * <p>
     * This method enables the following actions based on the <code>control</code> parameter:
     * <ul>
     *   <li><b>View Busy Timeslots:</b> Display times where the doctor is marked as unavailable.</li>
     *   <li><b>Manage Appointments:</b> Show upcoming appointments, with options to cancel or respond to requests.</li>
     *   <li><b>Edit Busy Timeslots:</b> Allows editing specific busy timeslots, useful for adjusting the doctor’s schedule.</li>
     *   <li><b>Delete Busy Timeslots:</b> Enables removing selected times where the doctor is marked as busy.</li>
     *   <li><b>Add Busy Timeslot:</b> Option to set a new busy timeslot for unavailability.</li>
     *   <li><b>Reset Filters:</b> Allows toggling views between busy timeslots and appointments, and displaying both.</li>
     * </ul>
     * Each option transitions the user to an appropriate menu state for handling further actions.
     *
     * @param control Specifies the type of control action to generate options for, such as viewing, editing, or deleting busy timeslots, or managing appointments.
     * @return A list of <code>Option</code> for managing upcoming events based on the specified control type, including actions for viewing, modifying, and responding to events or appointments.
     */
    public static List<Option> generateUpcomingEventControlOptions(UpcomingEventControl control) {
        EnumSet<UpcomingEventControl> busyControls = EnumSet.of(
            UpcomingEventControl.VIEW_BUSY,
            UpcomingEventControl.EDIT_BUSY,
            UpcomingEventControl.DEL_BUSY
        );

        EnumSet<UpcomingEventControl> apptControls = EnumSet.of(
            UpcomingEventControl.VIEW_APPT,
            UpcomingEventControl.VIEW_CFM_APPT,
            UpcomingEventControl.CANCEL_APPT,
            UpcomingEventControl.RESPOND_APPT
        );

        EnumSet<UpcomingEventControl> viewControls = EnumSet.of(
            UpcomingEventControl.VIEW_BUSY,
            UpcomingEventControl.VIEW_APPT,
            UpcomingEventControl.VIEW_CFM_APPT,
            UpcomingEventControl.VIEW
        );

        Doctor doctor = (Doctor) UserService.getCurrentUser();

        List<Option> options = doctor.getDoctorEvents()
            .stream()
            .filter(event -> event.getTimeslot().isAfter(LocalDateTime.now()))
            .filter(event -> busyControls.contains(control) ? !event.isAppointment() : true)
            .filter(event -> apptControls.contains(control) ? event.isAppointment() : true)
            .filter(event -> event.isAppointment() ? ((Appointment) event).getAppointmentStatus() != AppointmentStatus.CANCELLED : true)
            .filter(event -> control == UpcomingEventControl.CANCEL_APPT || control == UpcomingEventControl.VIEW_CFM_APPT ? 
                ((Appointment) event).getAppointmentStatus() == AppointmentStatus.CONFIRMED : true)
            .filter(event -> control == UpcomingEventControl.RESPOND_APPT ? ((Appointment) event).getAppointmentStatus() == AppointmentStatus.PENDING : true)
            .map(event -> {
                String eventTime = DateTimeUtils.printLongDateTime(event.getTimeslot());
    
                LinkedHashMap<String, String> displayFields = new LinkedHashMap<>();
                displayFields.put("Event Type", event.isAppointment() ? "Appointment" : "Event");
                displayFields.put("Timeslot", eventTime);
                displayFields.put("Status", event.isAppointment() ? ((Appointment) event).getAppointmentStatus().toString() : "Confirmed");
                
                if (!busyControls.contains(control)) {
                    displayFields.put("Patient", event.isAppointment() ? 
                    UserService.findUserByIdAndType(((Appointment) event).getPatientId(), Patient.class, true).getName() : 
                    "N/A");
                }
                
                Option option = new Option(
                    eventTime,
                    viewControls.contains(control) ? OptionType.DISPLAY : OptionType.NUMBERED,
                    displayFields
                );

                if (control == UpcomingEventControl.EDIT_BUSY) {
                    option.setNextAction(formValues -> {
                        if (formValues == null) {
                            formValues = new HashMap<>();
                        }
                        formValues.put(
                            "originalDateTime",
                            DateTimeUtils.printShortDateTime(event.getTimeslot())
                        );
                        return formValues;
                    }).setNextMenuState(MenuState.TIMESLOT_SELECTION_TYPE);
                }

                if (control == UpcomingEventControl.DEL_BUSY) {
                    option.setNextAction(formValues -> {
                        doctor.deleteDoctorEvent(event);
                        return null;
                    }).setRequiresConfirmation(true)
                    .setNextMenuState(MenuState.DOCTOR_VIEW_UPCOMING_UNAVAILABILITY);
                }

                if (control == UpcomingEventControl.CANCEL_APPT) {
                    option.setNextAction(formValues -> {
                        ((Appointment) event).cancel();
                        return null;
                    }).setRequiresConfirmation(true)
                    .setExitMenuState(MenuState.DOCTOR_CANCEL_UPCOMING_APPOINTMENTS);
                }

                if (control == UpcomingEventControl.RESPOND_APPT) {
                    option.setNextAction(formValues -> {
                        formValues.put("appointment", event);
                        return formValues;
                    }).setNextMenuState(MenuState.DOCTOR_HANDLE_UPCOMING_APPOINTMENT);
                }
                return option;
            }).collect(Collectors.toList());

        if (busyControls.contains(control)) {
            options.add(new Option(
                "add( )?",
                Option.OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "add");
                    put("Action", "Add Busy Timeslot");
                }}
            ).setNextAction((formValues) -> {
                return null;
            }).setNextMenuState(MenuState.TIMESLOT_SELECTION_TYPE));
        }

        if (control != UpcomingEventControl.VIEW) {
            options.add(new Option(
                "reset( )?",
                Option.OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "reset");
                    put("Action", "Reset filters");
                }}
            ).setNextAction((formValues) -> {
                return null;
            }).setNextMenuState(MenuState.DOCTOR_VIEW_UPCOMING_EVENTS));
        } else {
            options.add(new Option(
                "busy( )?",
                Option.OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "busy");
                    put("Action", "Show Busy Dates Only");
                }}
            ).setNextAction((formValues) -> {
                return null;
            }).setNextMenuState(MenuState.DOCTOR_VIEW_UPCOMING_UNAVAILABILITY));
            options.add(new Option(
                "appt( )?",
                Option.OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "appt");
                    put("Action", "Show Appointments Only");
                }}
            ).setNextAction((formValues) -> {
                return null;
            }).setNextMenuState(MenuState.DOCTOR_VIEW_UPCOMING_APPOINTMENTS));
        }

        if (null != control) switch (control) {
            case VIEW_BUSY -> {
                options.add(new Option(
                        "edit( )?",
                        Option.OptionType.UNNUMBERED,
                        new LinkedHashMap<>() {{
                            put("Select", "edit");
                            put("Action", "Edit Busy Timeslot");
                        }}
                ).setNextAction((formValues) -> {
                    return formValues;
                }).setNextMenuState(MenuState.DOCTOR_EDIT_UPCOMING_UNAVAILABILITY));
                options.add(new Option(
                        "del( )?",
                        Option.OptionType.UNNUMBERED,
                        new LinkedHashMap<>() {{
                            put("Select", "del");
                            put("Action", "Delete Busy Timeslot");
                        }}
                ).setNextAction((formValues) -> {
                    return formValues;
                }).setNextMenuState(MenuState.DOCTOR_DELETE_UPCOMING_UNAVAILABILITY));
            }
            case VIEW_APPT -> {
                options.add(new Option(
                        "cfm( )?",
                        Option.OptionType.UNNUMBERED,
                        new LinkedHashMap<>() {{
                            put("Select", "cfm");
                            put("Action", "View Confirmed Appointments");
                        }}
                ).setNextAction((formValues) -> {
                    return formValues;
                }).setNextMenuState(MenuState.DOCTOR_VIEW_CONFIRMED_APPOINTMENTS));
                options.add(new Option(
                        "rsvp( )?",
                        Option.OptionType.UNNUMBERED,
                        new LinkedHashMap<>() {{
                            put("Select", "rsvp");
                            put("Action", "Respond to appointment request");
                        }}
                ).setNextAction((formValues) -> {
                    return formValues;
                }).setNextMenuState(MenuState.DOCTOR_HANDLE_UPCOMING_APPOINTMENTS));
            }
            case VIEW_CFM_APPT -> options.add(new Option(
                        "cancel( )?",
                        Option.OptionType.UNNUMBERED,
                        new LinkedHashMap<>() {{
                            put("Select", "cancel");
                            put("Action", "Cancel Appointment");
                        }}
                ).setNextAction((formValues) -> {
                    return formValues;
                }).setNextMenuState(MenuState.DOCTOR_CANCEL_UPCOMING_APPOINTMENTS));
            default -> {
            }
        }

        return options;
    }

    /**
     * Generates options for doctors to respond to appointment requests, allowing them to either 
     * accept or reject each request.
     * <p>
     * This method provides the following options:
     * <ul>
     *   <li><b>Accept Appointment:</b> Confirms the selected appointment, setting its status to confirmed.</li>
     *   <li><b>Reject Appointment:</b> Cancels the selected appointment, marking it as rejected.</li>
     * </ul>
     * Each option:
     * <ul>
     *   <li>Validates the presence of an appointment object in <code>formValues</code>.</li>
     *   <li>Transitions to the upcoming appointments view after completion.</li>
     *   <li>Requires confirmation before proceeding with the action.</li>
     * </ul>
     *
     * @return A list of <code>Option</code> representing actions to accept or reject an appointment request.
     * @throws IllegalArgumentException If the appointment is not found in <code>formValues</code>.
     */
    public static List<Option> generateAcceptRejectOptions() {
        return List.of(
            new Option(
                "Accept Appointment",
                OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Action", "Accept");
                }}
            ).setNextAction(formValues -> {
                Appointment appointment = null;
                if (formValues != null && formValues.containsKey("appointment")) {
                    appointment = (Appointment) formValues.get("appointment");
                } else throw new IllegalArgumentException("Appointment not found");
                appointment.confirm();
                return null;
            }).setNextMenuState(MenuState.DOCTOR_VIEW_UPCOMING_APPOINTMENTS)
            .setExitMenuState(MenuState.DOCTOR_HANDLE_UPCOMING_APPOINTMENTS)
            .setRequiresConfirmation(true),
    
            new Option(
                "Reject Appointment",
                Option.OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Action", "Reject");
                }}
            ).setNextAction(formValues -> {
                Appointment appointment = null;
                if (formValues != null && formValues.containsKey("appointment")) {
                    appointment = (Appointment) formValues.get("appointment");
                } else throw new IllegalArgumentException("Appointment not found");
                appointment.cancel();
                return null;
            }).setNextMenuState(MenuState.DOCTOR_VIEW_UPCOMING_APPOINTMENTS)
            .setExitMenuState(MenuState.DOCTOR_HANDLE_UPCOMING_APPOINTMENTS)
            .setRequiresConfirmation(true)
        );
    }
    
    /**
     * Generates a list of options for doctors to view, add, and manage past appointment outcomes, 
     * allowing toggling between completed and pending outcomes.
     * <p>
     * This method enables doctors to:
     * <ul>
     *   <li><b>View Appointments:</b> View past confirmed or completed appointments for a specific patient or all patients under their care.</li>
     *   <li><b>Toggle Outcomes:</b> Switch between viewing appointments with existing outcomes and those without, allowing the doctor to manage outcomes as needed.</li>
     *   <li><b>Add Outcomes:</b> Navigate to add a service type and consultation notes for appointments without existing outcomes.</li>
     *   <li><b>View Completed Outcomes:</b> Access appointments that have recorded outcomes for review and editing.</li>
     *   <li><b>Edit Patient Details:</b> Access options to edit the patient’s personal information.</li>
     *   <li><b>Reset Filters:</b> Reset view filters to show all past appointments regardless of outcome status.</li>
     * </ul>
     * 
     * Depending on the flags <code>showNullOutcomes</code> and <code>showNonNullOutcomes</code>, the doctor can:
     * <ul>
     *   <li><b>Show Pending Outcomes:</b> View appointments without outcomes, navigating to add details if needed.</li>
     *   <li><b>Show Completed Outcomes:</b> Access appointments with recorded outcomes, allowing for review and potential updates.</li>
     * </ul>
     *
     * @param p The patient to filter appointments by, or <code>null</code> to include all patients.
     * @param showNullOutcomes Flag to display appointments without outcomes (pending).
     * @param showNonNullOutcomes Flag to display appointments with outcomes (completed).
     * @return A list of <code>Option</code> representing the past appointments, with actions to view, add, or toggle outcomes.
     */
    public static List<Option> generateSelectDoctorPastAppointmentOptions(
            Patient p, 
            boolean showNullOutcomes,
            boolean showNonNullOutcomes
        ) {
        if (!showNullOutcomes && !showNonNullOutcomes) System.err.println("Warning: Not showing any outcomes");
        List<Option> options = AppointmentService
            .getAllAppointments()
            .stream()
            .filter(appointment ->
                appointment.getDoctorId() == UserService.getCurrentUser().getRoleId() &&
                (appointment.getAppointmentStatus() == AppointmentStatus.CONFIRMED ||
                appointment.getAppointmentStatus() == AppointmentStatus.COMPLETED)
            )
            .filter(appointment -> p != null ? appointment.getPatientId() == p.getRoleId() : true)
            .filter(appointment -> !showNonNullOutcomes ? appointment.getAppointmentOutcome() == null : true)
            .filter(appointment -> !showNullOutcomes ? 
                appointment.getAppointmentOutcome() != null &&
                appointment.getTimeslot().isAfter(LocalDateTime.now()) : true)
            .map(appointment -> {
                Patient patient = UserService.findUserByIdAndType(
                    appointment.getPatientId(),
                    Patient.class,
                    true
                );
    
                String timeslot = DateTimeUtils.printLongDateTime(appointment.getTimeslot());
                LinkedHashMap<String, String> displayFields = new LinkedHashMap<>();
    
                displayFields.put("Timeslot", timeslot);
                if (patient != null) {
                    displayFields.put("Patient", patient.getName());
                } else {
                    displayFields.put("Patient", "Error not found");
                }
                displayFields.put("Outcome Added", String.valueOf(appointment.getAppointmentOutcome() != null));
    
                Option option = new Option(
                    timeslot,
                    showNullOutcomes && showNonNullOutcomes ? OptionType.DISPLAY : OptionType.NUMBERED,
                    displayFields
                ).setNextAction(formValues -> {
                    return new HashMap<>() {{
                        put("appointment", Integer.toString(appointment.getAppointmentId()));
                    }};
                });

                if(showNullOutcomes && !showNonNullOutcomes) {
                    option.setNextAction(formValues -> {
                        formValues.put("patient", patient);
                        formValues.put("appointment", appointment);
                        return formValues;
                    }).setNextMenuState(MenuState.DOCTOR_ADD_SERVICE_TYPE);
                } else if (!showNullOutcomes && showNonNullOutcomes) {
                    option.setNextAction(formValues -> {
                        formValues.put("patient", patient);
                        formValues.put("appointment", appointment);
                        return formValues;
                    }).setNextMenuState(MenuState.VIEW_RECORD);
                }
                return option;
            })
            .collect(Collectors.toList());

        if (p != null) {
            options.add(new Option(
                "edit( )?",
                OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "edit");
                    put("Action", "Edit User Details");
                }}
            ).setNextMenuState(MenuState.DOCTOR_EDIT_PAST_PATIENT)
            .setNextAction(formValues -> {
                return formValues;
            }));
        }
    
        if (p != null || !showNullOutcomes || !showNonNullOutcomes) {
            options.add(new Option(
                "reset( )?",
                OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "reset");
                    put("Action", "Reset Filters");
                }}
            ).setNextMenuState(MenuState.DOCTOR_VIEW_PAST_APPOINTMENTS)
            .setNextAction(formValues -> {
                formValues.remove("patient");
                return formValues;
            }));
        }

        if (showNullOutcomes) {
            options.add(new Option(
                "view( )?",
                OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "view");
                    put("Action", "View completed outcomes");
                }}
            )
            .setNextMenuState(MenuState.DOCTOR_VIEW_RECORDS));
            
        }

        if (showNonNullOutcomes) {
            options.add(new Option(
                "add( )?",
                OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "add");
                    put("Action", "Manage pending outcomes");
                }}
            ).setNextMenuState(MenuState.DOCTOR_ADD_RECORDS));
        }
        return options;
    }

    /**
     * Generates options to display and manage medication orders within an existing outcome record.
     * <p>
     * This method serves both doctors and patients, enabling:
     * <ul>
     *   <li><b>Viewing Medication Orders:</b> Displays each medication order associated with the prescription, showing fields such as order ID, medication ID, prescription ID, and quantity.</li>
     *   <li><b>Doctor Actions:</b> Adds an option for doctors to add a new medication order to the existing prescription of the selected outcome record.</li>
     *   <li><b>Patient Navigation:</b> Provides patients with an option to view other outcome records, facilitating a comprehensive review.</li>
     * </ul>
     * Depending on the role of the user, different options and next menu states are presented:
     * <ul>
     *   <li>If the user is a doctor, an "Add Medication" option is included, navigating to the add-medication workflow.</li>
     *   <li>If the user is a patient, a "View Another Outcome" option is included, allowing the patient to continue viewing related outcomes.</li>
     * </ul>
     *
     * @param prescription The prescription object containing medication orders linked to an outcome record.
     * @return A list of <code>Option</code>, each representing a medication order, along with role-specific actions for adding or viewing.
     */
    public static List<Option> generateAddMedicationOptions(Prescription prescription) {
        List<Option> options = prescription.getMedicationOrders().stream()
            .map(order -> {
                LinkedHashMap<String, String> displayFields = new LinkedHashMap<>();
                displayFields.put("Order ID", String.valueOf(order.getId()));
                displayFields.put("Medication ID", MedicationService.getMedication(order.getMedicationId()).getName());
                displayFields.put("Prescription ID", String.valueOf(order.getPrescriptionId()));
                displayFields.put("Quantity", String.valueOf(order.getQuantity()));

                return new Option(
                    "Order " + order.getId(),
                    OptionType.DISPLAY,
                    displayFields
                );
            })
            .collect(Collectors.toList());
        
        if (UserService.getCurrentUser().getClass() == Doctor.class) {
            options.add(
                new Option(
                    "add( )?",
                    OptionType.UNNUMBERED,
                    new LinkedHashMap<>() {{
                        put("Select", "add");
                        put("Action", "Add Medication");
                    }}
                ).setNextAction(formValues -> {
                    return formValues;
                }).
                setNextMenuState(MenuState.DOCTOR_ADD_MEDICATION)
            );
        } else { // PATIENT
            options.add(
                new Option(
                    "view( )?",
                    OptionType.UNNUMBERED,
                    new LinkedHashMap<>() {{
                        put("Select", "view");
                        put("Action", "View Another Outcome");
                    }}
                ).setNextAction(formValues -> {
                    return formValues;
                }).
                setNextMenuState(MenuState.PATIENT_VIEW_OUTCOMES)
            );
        }
        
        return options;
    }

    /**
     * Generates a list of options for selecting a service type when starting to add a new outcome record 
     * to an appointment. This option generator is used exclusively by doctors to define the service type 
     * for a specific appointment outcome.
     * <p>
     * The options allow:
     * <ul>
     *   <li>Displaying available service types as numbered options for selection.</li>
     *   <li>Saving the selected service type in form data with the key <code>"serviceType"</code>.</li>
     *   <li>Navigating to the <code>DOCTOR_ADD_MEDICATION</code> menu state after selecting a service type, 
     *       where additional details can be specified.</li>
     * </ul>
     * 
     * @return A list of <code>Option</code> representing each available service type for adding to an outcome record.
     */
    public static List<Option> generateServiceTypeOptions() {
        return Stream.of(ServiceType.values())
            .map(serviceType -> new Option(
                    serviceType.toString(),
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Service Type", serviceType.toString());
                    }}
                ).setNextAction(formValues -> {
                    formValues.put("serviceType", serviceType.toString());
                    return formValues;
                }).setNextMenuState(MenuState.DOCTOR_ADD_MEDICATION)
            ).collect(Collectors.toList());
        }

    /**
     * Generates a list of menu options for pharmacists, providing quick access to their primary functions.
     * <p>
     * This menu includes options for:
     * <ul>
     *   <li>Viewing appointment outcomes, showing pending items by default.</li>
     *   <li>Updating prescriptions based on appointment outcomes.</li>
     *   <li>Submitting requests to replenish inventory stock.</li>
     *   <li>Viewing the current inventory levels and details.</li>
     * </ul>
     *
     * @return A list of <code>Option</code> representing the pharmacist's main menu actions, each linked to its appropriate menu state.
     */
    public static List<Option> generatePharmacistMenuOptions() {
        List<Option> options = new ArrayList<>(List.of(
            new Option(
                    "(view( )?)?outcomes(s)?", 
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Action", "View Appointment Outcomes");
                    }}
                ).setNextAction((formValues) -> new HashMap<String, Object>() {{
                    put("hideCompleted", true);
                }}).setNextMenuState(MenuState.PHARMACIST_VIEW_OUTCOME_RECORDS),
            new Option(
                    "update( )?prescription(s)?", 
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Action", "Update Prescriptions");
                    }}
                ).setNextMenuState(MenuState.PHARMACIST_UPDATE_OUTCOMES),
            new Option(
                    "submit( )?request(s)?", 
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Action", "Submit Replenish Request");
                    }}
                ).setNextMenuState(MenuState.PHARMACIST_ADD_REQUEST),
            new Option(
                    "(view( )?)?inventory", 
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Action", "View Inventory");
                    }}
                ).setNextMenuState(MenuState.VIEW_INVENTORY)
        ));

        options.add(generateResetPasswordOption());
        return options;
    }

    /**
    * Generates a list of options for pharmacists to view or update appointment outcomes.
    * <p>
    * This option generator is used to display appointment outcomes and related prescription details:
    * <ul>
    *   <li>If <code>isUpdate</code> is <code>true</code>, pharmacists can select an option to update the prescription.</li>
    *   <li>If <code>isUpdate</code> is <code>false</code>, options are displayed without further action.</li>
    * </ul>
    * Each option displays details such as patient ID, doctor ID, the number of medications, prescription status, and consultation notes.
    *
    * @param appointments List of appointments for which outcomes are displayed or updated.
    * @param isUpdate Specifies whether the options are for updating (<code>true</code>) or just displaying (<code>false</code>) outcomes.
    * @return A list of <code>Option</code> representing appointment outcomes, with actions for updating or displaying based on <code>isUpdate</code>.
    */
    public static List<Option> generatePharmacistOutcomesOptions(List<Appointment> appointments, boolean isUpdate) {
        return appointments.stream()
        .map(appointment -> {
            AppointmentOutcomeRecord outcome = appointment.getAppointmentOutcome();
            Option option = new Option(
                String.valueOf(appointment.getAppointmentId()), 
                isUpdate ? OptionType.NUMBERED : OptionType.DISPLAY,
                new LinkedHashMap<>() {{ 
                    put("Patient", UserService.findUserByIdAndType(appointment.getPatientId(), Patient.class, true).getName());
                    put("Doctor", UserService.findUserByIdAndType(appointment.getPatientId(), Patient.class, true).getName());
                    put("Medications", String.valueOf(outcome.getPrescription().getMedicationOrders().size()));
                    put("Prescription Status", outcome.getPrescription().getStatus().toString());
                    put("Notes", outcome.getConsultationNotes());
                }}
            );
            if (isUpdate) option.setNextAction((formValues) -> new HashMap<>() {{
                    put("prescription", outcome.getPrescription());
                }}).setNextMenuState(MenuState.PHARMACIST_HANDLE_PRESCRIPTION);
            return option;
        })
        .collect(Collectors.toList());
    }

    /**
     * Generates a list of options for pharmacists to view and manage appointment outcomes, including 
     * actions to update prescriptions and toggle the visibility of completed (DISPENSED) outcomes.
     * <p>
     * This method combines two types of options:
     * <ul>
     *   <li>Outcome options: Lists appointment outcomes for viewing, generated by <code>generatePharmacistOutcomesOptions</code>.</li>
     *   <li>Action options:
     *     <ul>
     *       <li>Update Prescription: Allows pharmacists to navigate to a menu for updating prescriptions.</li>
     *       <li>Show/Hide Completed: Toggles visibility of outcomes based on whether their prescriptions have been set to <code>DISPENSED</code>.</li>
     *     </ul>
     *   </li>
     * </ul>
     * The <code>hideCompleted</code> flag determines if outcomes with a prescription status of <code>DISPENSED</code> are shown or hidden.
     *
     * @param appointments List of appointments for which outcomes are displayed.
     * @param hideCompleted If <code>true</code>, hides outcomes with prescriptions marked as <code>DISPENSED</code>; if <code>false</code>, shows them.
     * @return A combined list of outcome and action <code>Option</code> for viewing and managing appointment outcomes.
     */
    public static List<Option> generatePharmacistViewOutcomeOptions(List<Appointment> appointments, boolean hideCompleted) {
        List<Option> outcomeOptions = generatePharmacistOutcomesOptions(appointments, false);
        List<Option> actionOptions = new ArrayList<>();

        actionOptions.add(new Option(
            "U( )?",
            OptionType.UNNUMBERED,
            new LinkedHashMap<>() {{
                put("Select", "U");
                put("Action", "Update Prescription");
            }}
        ).setNextMenuState(MenuState.PHARMACIST_UPDATE_OUTCOMES));

        actionOptions.add(
            hideCompleted
                ? new Option(
                    "S( )?",
                    OptionType.UNNUMBERED,
                    new LinkedHashMap<>() {{
                        put("Select", "S");
                        put("Action", "Show Completed");
                    }}
                ).setNextAction((formData) -> new HashMap<String, Object>() {{
                    put("hideCompleted", false);
                }}).setNextMenuState(MenuState.PHARMACIST_VIEW_OUTCOME_RECORDS)
                : new Option(
                    "H( )?",
                    OptionType.UNNUMBERED,
                    new LinkedHashMap<>() {{
                        put("Select", "H");
                        put("Action", "Hide Completed");
                    }}
                ).setNextAction((formData) -> new HashMap<String, Object>() {{
                    put("hideCompleted", true);
                }}).setNextMenuState(MenuState.PHARMACIST_VIEW_OUTCOME_RECORDS)
                
        );
        List<Option> combinedOptions = new ArrayList<>();
        combinedOptions.addAll(outcomeOptions);
        combinedOptions.addAll(actionOptions);
        return combinedOptions;
    }

    /**
     * Generates a list of options for pharmacists to update pending prescriptions, displaying key details
     * such as outcome ID, medication count, and prescription status.
     * <p>
     * This option generator is used by pharmacists to:
     * <ul>
     *   <li>View and select prescriptions that are pending updates.</li>
     *   <li>Navigate to a detailed view to handle the selected prescription's updates.</li>
     * </ul>
     * Each option includes details about the prescription, such as the outcome ID, number of medications, 
     * and the current status.
     *
     * @param prescriptions List of prescriptions available for updating.
     * @return A list of <code>Option</code> representing each prescription that can be updated, with actions to navigate to the update menu.
     */
    public static List<Option> generatePharmacistUpdatePrescriptionOptions(List<Prescription> prescriptions) {
        List<Option> options = prescriptions.stream()
            .map(prescription -> new Option(
                "Update Prescription " + prescription.getOutcomeId(),
                OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Outcome ID", String.valueOf(prescription.getOutcomeId()));
                    put("Medications", String.valueOf(prescription.getMedicationOrders().size()));
                    put("Prescription Status", prescription.getStatus().toString());
                }}
            ).setNextAction((formValues) -> {
                    Map<String, Object> actionValues = new HashMap<>();
                    actionValues.put("prescription", prescription);
                    return actionValues;
                }).setNextMenuState(MenuState.PHARMACIST_HANDLE_PRESCRIPTION)
            )
            .collect(Collectors.toList());
        return options;
    }

    /**
     * Generates a list of options for pharmacists to handle a prescription, including viewing 
     * medication orders and updating the prescription status.
     * <p>
     * This option generator creates:
     * <ul>
     *   <li>Medication Order Options: Displays details for each medication order in the prescription, including order ID, medication name, and quantity.</li>
     *   <li>Status Options: Provides selectable status options that allow the pharmacist to progress the prescription status beyond its current state.</li>
     * </ul>
     * The generated options allow pharmacists to view each medication order and update the prescription status.
     *
     * @param p The prescription for which medication orders and status options are generated.
     * @return A combined list of <code>Option</code>, starting with medication order details, followed by status update options.
     */
    public static List<Option> getPharmacistHandleStatusOptions(Prescription p) {
        // Generate options for each medication order
        List<Option> medicationOrderOptions = p.getMedicationOrders().stream()
            .<Option>map(order -> new Option(
                "Medication Order Details",  // Option title
                OptionType.DISPLAY,  // Mark as a display option
                new LinkedHashMap<>() {{
                    put("Order ID", String.valueOf(order.getId()));
                    put("Prescription ID", String.valueOf(order.getPrescriptionId()));
                    put("Medication", MedicationService.getMedication(order.getMedicationId()).getName());
                    put("Quantity", String.valueOf(order.getQuantity()));
                }}
            ).setNextMenuState(MenuState.PHARMACIST_VIEW_OUTCOME_RECORDS))
            .collect(Collectors.toList());
    
        // Generate options for each prescription status after the current status
        List<Option> statusOptions = Arrays.stream(PrescriptionStatus.values())
            .filter(status -> status.ordinal() > p.getStatus().ordinal()) // Only statuses after the current one
            .map(status -> new Option(
                    status.toString(),
                    OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Status", status.toString());
                    }}
                ).setNextAction((formValues) -> {
                    p.setStatus(status);
                    return null;
                }).setNextMenuState(MenuState.PHARMACIST_VIEW_OUTCOME_RECORDS)
            ).collect(Collectors.toList());
    
        // Combine both lists
        List<Option> combinedOptions = new ArrayList<>();
        combinedOptions.addAll(medicationOrderOptions);  // Add medication order options first
        combinedOptions.addAll(statusOptions);           // Add status options after
    
        return combinedOptions;
    }

    /**
     * Generates a list of options for selecting medications, tailored to the specific actions allowed 
     * for Admins, Pharmacists, and Doctors.
     * <p>
     * This option generator provides the following role-based functionalities:
     * <ul>
     *   <li><b>Pharmacists:</b> Can select a medication to submit a replenishment request by choosing a medication and specifying the desired quantity. This functionality is accessed with <code>Control.ADD</code>.</li>
     *   <li><b>Admins:</b> Can view and manage the medication inventory. Admins can either select a medication to edit details (using <code>Control.EDIT</code>) or view a list with unnumbered options for adding or editing medications (with <code>Control.NONE</code>).</li>
     *   <li><b>Doctors:</b> Can select medications to add to a patient’s prescription, using <code>Control.ADD</code>.</li>
     * </ul>
     * 
     * The generated options allow:
     * <ul>
     *   <li>Medication information display for all roles, showing ID, name, stock level, and low alert level.</li>
     *   <li>Conditional actions based on the user role and control type.</li>
     * </ul>
     *
     * @param ctl Specifies the control type, determining if options are for display, replenishment request, or prescription addition.
     * @return A list of medication selection <code>Option</code>, with actions available based on the user's role and control type.
     */
    public static List<Option> generateMedicationOptions(Control ctl) {
        User user = UserService.getCurrentUser();

        List<Option> options = MedicationService.getAllMedications().stream()
            .map(medication -> {
                Option option = new Option(
                    medication.getName(),
                    ctl == Control.NONE ? OptionType.DISPLAY : OptionType.NUMBERED,
                    new LinkedHashMap<>() {{
                        put("Medication ID", String.valueOf(medication.getId()));
                        put("Name", medication.getName());
                        put("Stock", String.valueOf(medication.getStock()));
                        put("Low Alert Level", String.valueOf(medication.getLowAlertLevel()));
                    }}
                );

                if (user.getClass() == Admin.class && ctl == Control.EDIT) {
                    // Select medication to update fields
                    option.setNextAction(formData -> {
                        formData.put("medication", medication);
                        return formData;
                    })
                    .setNextMenuState(MenuState.ADMIN_EDIT_MEDICATION);
                } else if (user.getClass() == Pharmacist.class && ctl == Control.ADD) {
                    // Replenish Request
                    option.setNextAction(formValues -> {
                        Map<String, Object> newFormValues = new HashMap<>();
                        newFormValues.put("medication", medication);
                        return newFormValues;
                    }).setNextMenuState(MenuState.PHARMACIST_ADD_COUNT);
                } else if (user.getClass() == Doctor.class && ctl == Control.ADD) {
                    // Add medication to prescription
                    option.setNextAction((formValues) -> {
                        formValues.put("medication", medication);
                        return formValues;
                    })
                    .setNextMenuState(MenuState.DOCTOR_ADD_QUANTITY);
                }

                return option;
            })
            .collect(Collectors.toList());

        if (user.getClass() == Admin.class && ctl == Control.NONE) {
            options.add(new Option(
                "ADD( )?",
                OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "ADD");
                    put("Action", "Add a new medication");
                }}
            ).setNextMenuState(MenuState.ADMIN_ADD_MEDICATION));
            options.add(new Option(
                "EDIT( )?",
                OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "EDIT");
                    put("Action", "Edit a medication");
                }}
            ).setNextMenuState(MenuState.ADMIN_EDIT_INVENTORY));
        }
        return options;
    }

    /**
     * Generates a list of main menu options for administrators, providing access to user, appointment, 
     * and inventory management, as well as viewing requests.
     * <p>
     * This option generator allows administrators to:
     * <ul>
     *   <li>View and manage users, including filtering by role.</li>
     *   <li>View and manage appointments.</li>
     *   <li>Add new users to the system.</li>
     *   <li>View the medication inventory.</li>
     *   <li>View submitted requests, such as replenishment requests from pharmacists.</li>
     * </ul>
     * Each option is linked to a specific menu state to handle the selected action.
     *
     * @return A list of <code>Option</code> representing the main menu actions available to administrators.
     */
    public static List<Option> generateAdminMainMenuOptions() {
        List<Option> options = new ArrayList<>(List.of(
            new Option(
                "(view( )?)?user(s)?",
                OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Action", "View Staff");
                }}
            ).setNextAction((formValues) -> new HashMap<>(){{
                put("filter", SortFilter.ROLE);
                put("asc", true);
            }}
            ).setNextMenuState(MenuState.ADMIN_VIEW_USERS),

            new Option(
                "view( )?appointment(s)?",
                OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Action", "View Appointments");
                }}
            ).setNextMenuState(MenuState.ADMIN_VIEW_APPOINTMENTS),

            new Option(
                "add( )?user(s)?",
                OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Action", "Add User");
                }}
            ).setNextMenuState(MenuState.ADMIN_ADD_USER_TYPE),

            new Option(
                "(view( )?)?inventory",
                OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Action", "View Inventory");
                }}
            ).setNextMenuState(MenuState.VIEW_INVENTORY),

            new Option(
                "(view( )?)?requests(s)?",
                OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Action", "View Requests");
                }}
            ).setNextMenuState(MenuState.ADMIN_VIEW_REQUEST)
        ));

        options.add(generateResetPasswordOption());
        return options;
    }

    /**
     * Generates a list of options for administrators to view appointment details, including 
     * appointment ID, timeslot, patient and doctor information, and the appointment status.
     * <p>
     * This option generator provides a detailed view of each appointment, allowing administrators to:
     * <ul>
     *   <li>See the appointment ID and scheduled timeslot.</li>
     *   <li>View the patient’s name associated with each appointment.</li>
     *   <li>See the assigned doctor’s name or indicate if no doctor is assigned.</li>
     *   <li>Check the current status of the appointment (e.g., pending, completed).</li>
     * </ul>
     * Each option is displayed in a read-only format.
     *
     * @param appointments List of appointments to be displayed.
     * @return A list of display <code>Option</code> representing each appointment's details.
     */
    public static List<Option> generateAdminAppointmentsView(List<Appointment> appointments) {
        return appointments.stream()
        .map(appointment -> new Option(
            String.valueOf(appointment.getAppointmentId()),
            OptionType.DISPLAY,
            new LinkedHashMap<>() {{
                put("Appointment ID", String.valueOf(appointment.getAppointmentId()));
                put("Timeslot", DateTimeUtils.printLongDateTime(appointment.getTimeslot()));
                String patientName = UserService.findUserByIdAndType(appointment.getPatientId(), Patient.class, true).getName();
                put("Patient Name", patientName);
                Doctor doctor = UserService.findUserByIdAndType(appointment.getDoctorId(), Doctor.class, true);
                put("Doctor Name", doctor == null ? "No doctor assigned" : doctor.getName());
                put("Status", appointment.getAppointmentStatus().toString());
            }}
        ))
        .collect(Collectors.toList());
    }

    /**
     * Generates a list of options for viewing, sorting, and managing the staff list, tailored for administrators.
     * The generated options allow sorting by various fields (role, gender, age) and control user management actions.
     * <p>
     * This method provides the following options:
     * <ul>
     *   <li><b>View Staff List:</b> Displays a list of staff members with details including role, ID, name, gender, and age.</li>
     *   <li><b>Sorting Options:</b> Allows sorting the staff list by role, gender, or age in ascending or descending order, based on the specified filter and sorting direction.</li>
     *   <li><b>Management Controls:</b> Based on the control type:
     *       <ul>
     *         <li><code>Control.EDIT</code>: Allows editing a selected staff member's details.</li>
     *         <li><code>Control.DELETE</code>: Allows deleting a selected staff member, with a confirmation step.</li>
     *       </ul>
     *   </li>
     *   <li><b>Additional Admin Actions:</b> If no specific control is specified, admins are provided with additional options to:
     *       <ul>
     *         <li><code>ADD</code>: Add a new user to the system.</li>
     *         <li><code>EDIT</code>: Select a user for editing.</li>
     *         <li><code>DEL</code>: Select a user for deletion.</li>
     *       </ul>
     *   </li>
     * </ul>
     *
     * @param sortedUsers List of sorted staff members to be displayed, filtered based on the specified criteria.
     * @param filter Specifies the field to sort by (role, gender, or age).
     * @param isAsc Defines the sorting order; <code>true</code> for ascending, <code>false</code> for descending.
     * @param ctl Determines the control type for user management actions, either <code>Control.EDIT</code> or <code>Control.DELETE</code>, or none for view-only.
     * @return A list of <code>Option</code> representing the staff list view, including sorting and management actions.
     */
    public static List<Option> generateStaffListView(
        List<Staff> sortedUsers,
        SortFilter filter,
        boolean isAsc, 
        Control ctl
    ) {
        List<Option> options = sortedUsers.stream()
            .map(user -> {
            Staff staff = (Staff) user;
            Option option = new Option(
                staff.getName(),
                ctl == Control.EDIT || ctl == Control.DELETE ? OptionType.NUMBERED : OptionType.DISPLAY,
                new LinkedHashMap<>() {{
                    put("Role", staff.getClass().getSimpleName());
                    put("Staff ID", String.valueOf(staff.getStaffId()));
                    put("Role ID", String.valueOf(staff.getRoleId()));
                    put("Name", staff.getName());
                    put("Gender", staff.getGender());
                    put("Age", String.valueOf(Period.between(staff.getDateOfBirth(), LocalDate.now()).getYears()));
                }}
            );

            if (ctl != Control.EDIT && ctl != Control.DELETE) return option;

            if (ctl == Control.DELETE) {
                option.setNextAction((formValues) -> {
                    UserService.deleteStaff(staff);
                    return formValues;
                }).setRequiresConfirmation(true)
                .setExitMenuState(MenuState.ADMIN_SELECT_USER_DELETE)
                .setNextMenuState(MenuState.ADMIN_VIEW_USERS);
            } else {
                option.setNextAction((formValues) -> {
                    formValues.put("user", staff);
                    return formValues;
                })
                .setNextMenuState(MenuState.ADMIN_EDIT_USER);
            }
            return option;
        })
        .collect(Collectors.toList());

        if (filter != SortFilter.ROLE || isAsc != true) {  // Replace with your actual condition
            options.add(new Option(
                "/1( )?", 
                OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "/1");
                    put("Action", "Sort Role Asc");
                }}
            ).setNextMenuState(MenuState.ADMIN_VIEW_USERS)
            .setNextAction(formValues -> {
                formValues.put("filter", SortFilter.ROLE);
                formValues.put("asc", true);
                return formValues;
            }));
        }
        if (filter != SortFilter.ROLE || isAsc != false) {
            options.add(new Option(
                "/2( )?", 
                OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "/2");
                    put("Action", "Sort Role Desc");
                }}
            ).setNextMenuState(MenuState.ADMIN_VIEW_USERS)
            .setNextAction(formValues -> {
                formValues.put("filter", SortFilter.ROLE);
                formValues.put("asc", false);
                return formValues;
            }));
        }
        
        if (filter != SortFilter.GENDER || isAsc != true) { 
            options.add(new Option(
                "/3( )?", 
                OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "/3");
                    put("Action", "Sort Gender Asc");
                }}
            ).setNextMenuState(MenuState.ADMIN_VIEW_USERS)
            .setNextAction(formValues -> {
                formValues.put("filter", SortFilter.GENDER);
                formValues.put("asc", true);
                return formValues;
            }));
        }
        if (filter != SortFilter.GENDER || isAsc != false) {
            options.add(new Option(
                "/4( )?", 
                OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "/4");
                    put("Action", "Sort Gender Desc");
                }}
            ).setNextMenuState(MenuState.ADMIN_VIEW_USERS)
            .setNextAction(formValues -> {
                formValues.put("filter", SortFilter.GENDER);
                formValues.put("asc", false);
                return formValues;
            }));
        }

        if (filter != SortFilter.AGE || isAsc != true) {  // Replace with your actual condition
            options.add(new Option(
                "/5( )?", 
                OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "/5");
                    put("Action", "Sort Age Asc");
                }}
            ).setNextMenuState(MenuState.ADMIN_VIEW_USERS)
            .setNextAction(formValues -> {
                formValues.put("filter", SortFilter.AGE);
                formValues.put("asc", true);
                return formValues;
            }));
        }
        if (filter != SortFilter.AGE || isAsc != false) { 
            options.add(new Option(
                "/6( )?", 
                OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "/6");
                    put("Action", "Sort Age Desc");
                }}
            ).setNextMenuState(MenuState.ADMIN_VIEW_USERS)
            .setNextAction(formValues -> {
                formValues.put("filter", SortFilter.AGE);
                formValues.put("asc", false);
                return formValues;
            }));
        }

        if (ctl == Control.EDIT || ctl == Control.DELETE) return options;

        options.addAll(List.of(
            new Option(
                "ADD( )?",
                OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "ADD");
                    put("Action", "Add User");
                }}
            ).setNextMenuState(MenuState.ADMIN_ADD_USER_TYPE),

            new Option(
                "EDIT( )?",
                OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "EDIT");
                    put("Action", "Edit a user");
                }}
            )
            .setNextMenuState(MenuState.ADMIN_SELECT_USER_EDIT),

            new Option(
                "DEL( )?",
                OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "DEL");
                    put("Action", "Delete a user");
                }}
            ).setNextMenuState(MenuState.ADMIN_SELECT_USER_DELETE)
        ));

        return options;
    }

    /**
     * Generates a list of options for editing fields related to a staff member's account, including 
     * username, password, name, and gender. Each option allows updating the selected field.
     * <p>
     * This option generator is specific to editing <code>Staff</code> objects and cannot be used 
     * for editing <code>Patient</code> fields.
     * <ul>
     *   <li><b>Username:</b> Displays the current username and allows modification.</li>
     *   <li><b>Password:</b> Displays the masked current password and allows modification.</li>
     *   <li><b>Name:</b> Displays the current name and allows modification.</li>
     *   <li><b>Gender:</b> Displays the current gender and allows modification.</li>
     * </ul>
     * Each option saves the updated field back to the <code>Staff</code> object and redirects to the 
     * main user menu upon completion.
     *
     * @param staff The staff member whose fields are being edited.
     * @return A list of <code>Option</code> representing editable fields for the specified staff member.
     */
    public static List<Option> generateUserFieldsEditOptions(Staff staff) {
        return List.of(
            new Option(
                "username",
                OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Field", "Username");
                    put("Current Value", staff.getUsername());
                }}
            ).setNextAction((formData) -> {
                staff.setUsername((String) formData.get("input"));
                return formData;
            }).setNextMenuState(MenuState.ADMIN_EDIT_USER)
             .setExitMenuState(MenuState.getUserMainMenuState())
             .setEditRedirect(true),
    
            new Option(
                "password",
                OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Field", "Password");
                    put("Current Value", "*".repeat(staff.getPassword().length()));
                }}
            ).setNextAction((formData) -> {
                staff.setPassword((String) formData.get("input"));
                return formData;
            }).setNextMenuState(MenuState.ADMIN_EDIT_USER)
             .setExitMenuState(MenuState.getUserMainMenuState())
             .setEditRedirect(true),
    
            new Option(
                "name",
                OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Field", "Name");
                    put("Current Value", staff.getName());
                }}
            ).setNextAction((formData) -> {
                staff.setName((String) formData.get("input"));
                return formData;
            }).setNextMenuState(MenuState.ADMIN_EDIT_USER)
             .setExitMenuState(MenuState.getUserMainMenuState())
             .setEditRedirect(true),
    
            new Option(
                "gender",
                OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Field", "Gender");
                    put("Current Value", staff.getGender());
                }}
            ).setNextAction((formData) -> {
                staff.setGender((String) formData.get("input"));
                return formData;
            }).setNextMenuState(MenuState.ADMIN_EDIT_USER)
             .setExitMenuState(MenuState.getUserMainMenuState())
             .setEditRedirect(true),

            new Option(
                "view",
                OptionType.UNNUMBERED,
                new LinkedHashMap<>() {{
                    put("Select", "view");
                    put("Action", "View all Users");
                }}
            ).setNextAction((formData) -> formData).setNextMenuState(MenuState.ADMIN_VIEW_USERS)
        );
    }

    /**
     * Generates a list of options for selecting a user role, allowing administrators to specify the 
     * type of user to add (e.g., Patient, Doctor, Pharmacist, or Admin).
     * <p>
     * Each option represents a role type:
     * <ul>
     *   <li><b>Patient:</b> Creates a patient user.</li>
     *   <li><b>Doctor:</b> Creates a doctor user.</li>
     *   <li><b>Pharmacist:</b> Creates a pharmacist user.</li>
     *   <li><b>Admin:</b> Creates an admin user.</li>
     * </ul>
     * Each option sets the selected role in the form data and navigates to the menu for adding the user's name.
     *
     * @return A list of <code>Option</code> representing each available user role.
     */
    public static List<Option> getRoleOptions() {
        return new ArrayList<>(List.of(
            createRoleOption(Patient.class),
            createRoleOption(Doctor.class),
            createRoleOption(Pharmacist.class),
            createRoleOption(Admin.class)
        ));
    }

    /**
     * Helper method to create an option for selecting a specific user role.
     * <p>
     * This method generates an option based on the provided <code>roleClass</code>:
     * <ul>
     *   <li>Displays the role's class name as the option.</li>
     *   <li>Sets the role type in form data upon selection for further processing.</li>
     * </ul>
     * Each option is numbered and configured to transition to the username entry menu state after selection.
     *
     * @param roleClass The class representing the user role (e.g., <code>Patient.class</code>, <code>Doctor.class</code>).
     * @return An <code>Option</code> representing the specified role, with an action to set the role in form data.
     */
    private static Option createRoleOption(Class<?> roleClass) {
        String className = roleClass.getSimpleName();
        
        return new Option(
            className,
            OptionType.NUMBERED,
            new LinkedHashMap<>() {{
                put("role", className);
            }}
        ).setNextMenuState(MenuState.ADMIN_ADD_USER_NAME)
        .setNextAction((formData) -> {
            formData.put("role", className);
            return formData;
        });
    }

    /**
     * Generates a list of options for selecting a user’s gender, allowing administrators to specify 
     * the gender of a new user during the registration process.
     * <p>
     * Each option corresponds to a gender value from the <code>Gender</code> enum:
     * <ul>
     *   <li>Displays the gender name as the option title.</li>
     *   <li>Sets the selected gender in the form data for further processing.</li>
     * </ul>
     * Upon selecting a gender, the menu transitions to the date-of-birth entry stage.
     *
     * @return A list of <code>Option</code> representing each gender, with actions to save the selection in form data.
     */
    public static List<Option> getGenderOptions() {
        return Arrays.stream(Gender.values())
            .map(gender -> new Option(
                gender.name(),
                OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Blood Type", gender.toString());
                }}
            ).setNextAction((formValues) -> {
                formValues.put("gender", gender.toString());
                return formValues;
            }).setNextMenuState(MenuState.ADMIN_ADD_DOB))
            .collect(Collectors.toList());
    }

    /**
     * Generates a list of options for selecting a patient’s blood type, enabling administrators 
     * to finalize the registration of a new patient with all required attributes.
     * <p>
     * Each option corresponds to a blood type from the <code>BloodType</code> enum:
     * <ul>
     *   <li>Displays the blood type as the option title.</li>
     *   <li>Sets the selected blood type in the form data and completes the creation of a new <code>Patient</code> object.</li>
     * </ul>
     * Selecting a blood type triggers patient creation using previously gathered details and redirects 
     * the user to the main user view.
     *
     * @return A list of <code>Option</code> representing each blood type, with actions to finalize patient creation.
     */
    public static List<Option> getBloodTypeOptions() {
        return Arrays.stream(BloodType.values())
            .map(bloodType -> new Option(
                bloodType.name(),
                Option.OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Blood Type", bloodType.toString());
                }}
            ).setNextAction((formValues) -> {
                String input = bloodType.toString();
                Patient.create(
                    (String) formValues.get("username"),
                    (String) formValues.get("name"),
                    (String) formValues.get("gender"),
                    (String) formValues.get("dob"),
                    (String) formValues.get("mobile"),
                    (String) formValues.get("home"),
                    (String) formValues.get("email"),
                    input // Set selected blood type here
                );
                return null;
            }).setNextMenuState(MenuState.ADMIN_VIEW_USERS)
            .setRequiresConfirmation(true))
            .collect(Collectors.toList());
    }

    /**
     * Generates a list of options for administrators to edit medication properties, specifically 
     * stock level and low alert level, within the inventory.
     * <p>
     * This option generator allows administrators to:
     * <ul>
     *   <li><b>Update Stock:</b> Displays the current stock level and allows modification.</li>
     *   <li><b>Update Low Alert Level:</b> Displays the current low alert threshold and allows modification.</li>
     * </ul>
     * Each option captures the updated value from user input and saves it to the <code>Medication</code> object, 
     * then redirects to the inventory view.
     *
     * @param medication The medication item to be edited.
     * @return A list of <code>Option</code> for editing the specified medication's stock and low alert level.
     */
    public static List<Option> generateMedicationEditOptions(Medication medication) {
        return new ArrayList<>(List.of(
            new Option(
                "update stock",
                Option.OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Field", "Stock");
                    put("Current Value", String.valueOf(medication.getStock()));
                }}
            ).setNextAction((formValues) -> {
                medication.setStock(Integer.parseInt((String) formValues.get("input")));
                return null;
            }).setNextMenuState(MenuState.VIEW_INVENTORY)
             .setEditRedirect(true),
    
            new Option(
                "update level",
                Option.OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Field", "Low Level Alert");
                    put("Current Value", String.valueOf(medication.getLowAlertLevel()));
                }}
            ).setNextAction((formValues) -> {
                medication.setLowAlertLevel(Integer.parseInt((String) formValues.get("input")));
                return null;
            }).setNextMenuState(MenuState.VIEW_INVENTORY)
             .setEditRedirect(true)
        ));
    }

    /**
     * Generates a list of options for managing medication replenishment requests from Pharmacists, supporting display, 
     * approval, and rejection actions.
     * <p>
     * This method enables administrators to:
     * <ul>
     *   <li><b>Display Requests:</b> View pending replenishment requests for medications, showing details such as request ID, medication name, quantity, and status.</li>
     *   <li><b>Approve Requests:</b> Set a selected request’s status to approved, marking it for replenishment.</li>
     *   <li><b>Reject Requests:</b> Set a selected request’s status to rejected.</li>
     * </ul>
     * If <code>Control.NONE</code> is passed, options are displayed only; otherwise, action options for approving or rejecting requests are included.
     *
     * @param ctl The control type specifying the allowed actions, such as <code>Control.APPROVE</code> or <code>Control.REJECT</code>.
     * @return A list of <code>Option</code> representing the request display and management options, with actions determined by the control type.
     */
    public static List<Option> getRequestOptions(Control ctl) {
        List<Option> options = MedicationService.getAllMedications().stream()
            .flatMap(medication -> medication.getRequestList().stream()
                .filter(request -> request.getStatus() == Request.Status.PENDING)
                .map(request -> {
                    Option option = new Option(
                        String.valueOf(medication.getName()),
                        ctl == Control.NONE ? OptionType.DISPLAY : OptionType.NUMBERED,
                        new LinkedHashMap<>() {{
                            put("Request ID", String.valueOf(request.getId()));
                            put("Medication Name", medication.getName());
                            put("Quantity", String.valueOf(request.getCount()));
                            put("Status", request.getStatus().toString());
                        }}
                    );
    
                    // Set action based on control type
                    if (ctl == Control.APPROVE) {
                        option.setNextAction((formValues) -> {
                            MedicationService.approveReplenishRequest(request);
                            return null;
                        }).setNextMenuState(MenuState.VIEW_INVENTORY);
                    } else if (ctl == Control.REJECT) {
                        option.setNextAction((formValues) -> {
                            request.setStatus(Request.Status.REJECTED);
                            return null;
                        }).setNextMenuState(MenuState.VIEW_INVENTORY);
                    }
    
                    return option;
                })
            )
            .collect(Collectors.toList());

        if (ctl != Control.NONE) return options;
    
        // Add approve option
        options.add(new Option(
            "A",
            Option.OptionType.UNNUMBERED,
            new LinkedHashMap<>() {{
                put("Select", "A");
                put("Action", "Approve a request");
            }}
        ).setNextAction((formValues) -> {
            formValues.put("ctl", Control.APPROVE);
            return null;
        }).setNextMenuState(MenuState.APPROVE_REPLENISH_REQUEST));
    
        // Add reject option
        options.add(new Option(
            "R",
            Option.OptionType.UNNUMBERED,
            new LinkedHashMap<>() {{
                put("Select", "R");
                put("Action", "Reject a request");
            }}
        ).setNextAction((formValues) -> {
            formValues.put("ctl", Control.REJECT);
            return null;
        }).setNextMenuState(MenuState.REJECT_REPLENISH_REQUEST));
    
        return options;
    }
}