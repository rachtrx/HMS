package app.model.user_input;

import app.constants.AppMetadata;
import app.constants.exceptions.ExitApplication;
import app.model.appointments.Appointment;
import app.model.appointments.Appointment.AppointmentStatus;
import app.model.appointments.AppointmentDisplay;
import app.model.appointments.AppointmentOutcomeRecord;
import app.model.appointments.DoctorEvent;
import app.model.appointments.Timeslot;
import app.model.users.Patient;
import app.model.users.staff.Doctor;
import app.service.AppointmentService;
import app.service.MenuService;
import app.service.UserService;
import app.utils.DateTimeUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
/**
* Controls which menus to show (Equivalent to machine in FSM)
*
* @author Luke Eng (@LEPK02)
* @author Rachmiel Teo (@rachtrx)
* @version 1.0
* @since 2024-10-24
*/


public enum Menu {

    // Init START
    EDIT(new MenuBuilder(
        MenuType.INPUT,
        null,
        "Enter a new value: ",
        true
    )),
    CONFIRM(new MenuBuilder(
        MenuType.SELECT,
        "Confirm Action? ",
        null
    )),
    LANDING(new MenuBuilder(
        MenuType.DISPLAY,
        null,
        String.join(
            "\n",
            "| |  | |  \\/  |/ ____|",
            "| |__| | \\  / | (___  ",
            "|  __  | |\\/| |\\___ \\ ",
            "| |  | | |  | |____) |",
            "|_|  |_|_|  |_|_____/ ",
            String.format(
                "\nWelcome to the %s (%s)!",
                AppMetadata.APP_FULL_NAME.toString(),
                AppMetadata.APP_SHORT_NAME.toString()
            ), "\nPress 'Enter' to continue..."
        )
    )),
    LOGIN_USERNAME(new MenuBuilder(
        MenuType.INPUT,
        "Login",
        "Please enter your username: "
    )),
    LOGIN_PASSWORD(new MenuBuilder(
        MenuType.INPUT,
        "Login",
        "Please enter your password: "
    )),
    PATIENT_MAIN_MENU(new MenuBuilder(
        MenuType.SELECT,
        "Patient Main Menu",
        null
    )),
    DOCTOR_MAIN_MENU(new MenuBuilder(
        MenuType.SELECT,
        "Doctor Main Menu",
        null
    )),
    SELECT_PATIENT_VIEW_MEDICAL_RECORD(new MenuBuilder(
        MenuType.SELECT,
        "Select Patient To View Medical Record",
        "Enter 'M' or 'Menu' to return to the main menu."
    )),
    SELECT_PATIENT_EDIT_MEDICAL_RECORD(new MenuBuilder(
        MenuType.SELECT,
        "Select Patient To Edit Medical Record",
        "Enter 'M' or 'Menu' to return to the main menu."
    )),
    PATIENT_VIEW_MEDICAL_RECORD(new MenuBuilder(
        MenuType.SELECT,
        "Patient Medical Record",
        "Enter 'M' or 'Menu' to return to the main menu."
    )),
    PATIENT_EDIT_MEDICAL_RECORD(new MenuBuilder( // TODO implement treatments?
        MenuType.SELECT,
        "Patient Medical Record",
        "Please select a field to edit:"
    )),
    PATIENT_APPOINTMENT_SELECTION_TYPE(new MenuBuilder(
        MenuType.SELECT,
        "Choose Date",
        null
    )),
    PATIENT_VIEW_AVAIL_APPOINTMENTS(new MenuBuilder(
        MenuType.SELECT,
        "Available Appointments Today",
        null
    )),
    INPUT_APPOINTMENT_YEAR(new MenuBuilder(
        MenuType.SELECT,
        "Choose Appointment Date",
        "Please select the year:"
    )),
    INPUT_APPOINTMENT_MONTH(new MenuBuilder(
        MenuType.SELECT,
        "Choose Appointment Date",
        "Please select the month:"
    )),
    INPUT_APPOINTMENT_DAY(new MenuBuilder(
        MenuType.INPUT,
        "Choose Appointment Date",
        "Please enter the day:"
    )),
    INPUT_APPOINTMENT_HOUR(new MenuBuilder(
        MenuType.SELECT,
        "Choose Appointment Date",
        "Please select an appointment time:"
    )),
    INPUT_APPOINTMENT_DOCTOR(new MenuBuilder(
        MenuType.SELECT,
        "Choose Appointment Doctor",
        null,
        true
    )),
    PATIENT_RESCHEDULE_SELECTION(new MenuBuilder(
        MenuType.SELECT,
        "Choose Appointment to reschedule",
        null
    )),
    PATIENT_CANCEL_SELECTION(new MenuBuilder(
        MenuType.SELECT,
        "Choose Appointment to cancel",
        null,
        true
    )),
    PATIENT_VIEW_CONFIRMED_APPOINTMENTS(new MenuBuilder(
        MenuType.SELECT,
        "Upcoming Scheduled Appointments",
        null
    )),
    PATIENT_VIEW_OUTCOMES(new MenuBuilder(
        MenuType.SELECT,
        "Past Appointment Outcome Records",
        null
    )),
    // TODO: doctor menu
    // DOCTOR_UPDATE_RECORDS(new MenuBuilder(
    //     MenuType.SELECT,
    //     "Past Appointment Outcome Records",
    //     null
    // )),
    DOCTOR_VIEW_SCHEDULE(new MenuBuilder(
        MenuType.SELECT,
        "Doctor Schedule",
        "Select 'M' to return to the main menu."
    )),
    DOCTOR_SET_AVAILABILITY(new MenuBuilder( // TODO
        MenuType.SELECT,
        "Doctor Availability",
        "Set unavailable dates & times"
    )),
    DOCTOR_ACCEPT_APPOINTMENTS(new MenuBuilder(
        MenuType.SELECT,
        "Accept or Decline Appointment Requests",
        "Please select an appointment to accept/reject:"
    )),
    DOCTOR_CANCEL_CONFIRMED(new MenuBuilder(
        MenuType.SELECT,
        "View/Cancel Upcoming Appointments",
        "Please select an appointment to cancel:"
    )),
    // DOCTOR_ADD_OUTCOME(new MenuBuilder(
    //     MenuType.SELECT,
    //     "Past Appointment Outcome Records",
    //     null
    // )),
    // TODO: revamp appointment scheduler
    SELECT_PATIENT_APPOINTMENT(new MenuBuilder(
        MenuType.SELECT,
        "Edit Appointment",
        "Select an appointment to edit:"
    )),
    EDIT_PATIENT_APPOINTMENT(new MenuBuilder(
        MenuType.SELECT,
        "Edit Appointment",
        "Select a field to edit:"
    )),
    DOCTOR_ACCEPT_OR_DECLINE_APPOINTMENT(new MenuBuilder(
        MenuType.SELECT,
        "Accept or Decline Pending Appointment",
        null,
        true
    ));


    // Transitions
    static {
        Menu.LANDING.setNextMenu(Menu.LOGIN_USERNAME);
        Menu.LOGIN_USERNAME
            .setNextMenu(Menu.LOGIN_PASSWORD)
            .setNextAction((userInput, args) -> new HashMap<String, Object>() {{
                put("username", userInput);
            }});
        Menu.LOGIN_PASSWORD
            .setParseUserInput(false)
            .setNextAction((userInput, args) -> {
                UserService.login((String) args.get("username"), userInput);
                MenuService.getCurrentMenu().setNextMenu(() -> Menu.getUserMainMenu()); // pass in menugenerator
                return null;
            }).setExitMenu(()-> Menu.LOGIN_USERNAME);
        Menu.PATIENT_MAIN_MENU // exit menu should be itself
            .setOptionGenerator(() -> new ArrayList<>(List.of(
                new Option(
                        "View Medical Record", 
                        "(view( )?)?medical(( )?record)?", 
                        true
                    ).setNextMenu(() -> PATIENT_VIEW_MEDICAL_RECORD),
                new Option(
                        "Edit Contact Information", 
                        "(edit( )?)?contact(( )?info(rmation)?)?", 
                        true
                    ).setNextMenu(() -> PATIENT_EDIT_MEDICAL_RECORD),
                new Option(
                        "View Available Appointments", 
                        "view( )?(available( )?)?appointment(s)?", 
                        true
                    ).setNextMenu(() -> PATIENT_VIEW_AVAIL_APPOINTMENTS),
                new Option(
                        "Schedule an Appointment", 
                        "^schedule( )?(a(n)?( )?)?appointment(s)?",
                        true
                    ).setNextMenu(() -> PATIENT_APPOINTMENT_SELECTION_TYPE)
                    .setNextAction((a,b) -> new HashMap<String, Object>() {{
                            put("yearValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
                            put("monthValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
                            put("dayValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
                            put("hourValidator", DateTimeUtil.DateConditions.FUTURE.toString());
                        }}
                    ),
                new Option(
                        "Reschedule an Appointment", 
                        "^Reschedule( )?(a(n)?( )?)?appointment(s)?",
                        true
                    ).setNextMenu(() -> PATIENT_RESCHEDULE_SELECTION)
                    .setNextAction((a,b) -> new HashMap<String, Object>() {{
                            put("yearValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
                            put("monthValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
                            put("dayValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
                            put("hourValidator", DateTimeUtil.DateConditions.FUTURE.toString());
                        }}
                    ),
                new Option(
                        "Cancel an Appointment", 
                        "^Cancel( )?(a(n)?( )?)?appointment(s)?",
                        true
                    ).setNextMenu(() -> PATIENT_CANCEL_SELECTION)
                    .setNextAction((a,b) -> new HashMap<String, Object>()),
                new Option(
                        "View Scheduled Appointments", 
                        "view( )?(scheduled( )?)?appointment(s)?|(view( )?)?confirmed", 
                        true
                    ).setNextMenu(() -> PATIENT_VIEW_CONFIRMED_APPOINTMENTS),
                new Option(
                        "View Appointment Outcomes", 
                        "view( )?(appointment( )?)?outcomes(s)?|(view( )?)?history", 
                        true
                    ).setNextMenu(() -> PATIENT_VIEW_OUTCOMES)
            ))).shouldAddLogoutOptions();
        Menu.PATIENT_VIEW_MEDICAL_RECORD
            .setDisplayGenerator(() -> {
                Patient patient = Menu.getTargetPatientFromArgs();
                System.out.println();
                System.out.println("Patient Information");
                Menu.printLineBreak(10);
                patient.print();

                System.out.println("\nAppointment History");
                Menu.printLineBreak(10);
                List<Appointment> appointments = AppointmentService.getAllAppointmentsForPatient(patient.getRoleId());
                AppointmentDisplay.printAppointmentDetails(appointments);
            })
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.PATIENT_EDIT_MEDICAL_RECORD
            .setOptionGenerator(() -> {
                Patient patient = Menu.getTargetPatientFromArgs();
                List<Option> result = new ArrayList<>();
                if (
                    List.of(Doctor.class).contains(UserService.getCurrentUser().getClass())
                ) {
                    result.addAll(new ArrayList<>(List.of(
                        // TODO: blood type, diagnosis, appointment records
                        new EditOption(
                            String.format("Blood type: %s", patient.getBloodType()),
                            "blood|type|blood(( )?type)?|(blood( )?)?type",
                            true,
                            (input, args) -> {
                                patient.setBloodType((String) input);
                                return null;
                            }
                        ),
                        new Option(
                            "Edit Appointment",
                            "(edit( )?)?appointment",
                            true
                        ).setNextMenu(() -> SELECT_PATIENT_APPOINTMENT)
                        .setNextAction((userInput, args) -> args)
                    )));
                }
                if (
                    List.of(Patient.class, Doctor.class).contains(UserService.getCurrentUser().getClass())
                ) {
                    result.addAll(new ArrayList<>(List.of(
                        new EditOption(
                            String.format("Mobile Number: +65%d", patient.getMobileNumber()),
                            "mobile(( )?number)?",
                            true,
                            (input, args) -> {
                                patient.setMobileNumber((String) input);
                                return args;
                            }
                        ),
                        new EditOption(
                            String.format("Home Number: +65%d", patient.getHomeNumber()),
                            "home(( )?number)?",
                            true,
                            (input, args) -> {
                                patient.setHomeNumber((String) input);
                                return args;
                            }
                        ),
                        new EditOption(
                            String.format("Email: %s", patient.getEmail()),
                            "email",
                            true,
                            (input, args) -> {
                                patient.setEmail((String) input);
                                return args;
                            }
                        )
                    )));
                }
                return result;
            }).shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.PATIENT_VIEW_AVAIL_APPOINTMENTS
            .setDisplayGenerator(() -> {
                    Map<Doctor, List<Timeslot>> timeslotsByDoctor = AppointmentService.getAvailableAppointmentSlotsByDoctor(LocalDateTime.now());
                    // Get tomorrow's timeslots if hospital is closed today
                    if (timeslotsByDoctor.isEmpty()) {
                        timeslotsByDoctor = AppointmentService.getAvailableAppointmentSlotsByDoctor(
                            LocalDateTime.of(LocalDateTime.now().plusDays(1).toLocalDate(), LocalTime.of(0, 0))
                        );
                    }
                    if (!timeslotsByDoctor.isEmpty()) {
                        AppointmentDisplay.printAvailableTimeslots(timeslotsByDoctor);
                    } else {
                        System.out.println("No available timeslots for today and tomorrow.");
                    }       
                })
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.PATIENT_VIEW_CONFIRMED_APPOINTMENTS
            .setDisplayGenerator(() -> {
                Patient patient = (Patient) UserService.getCurrentUser();
                    List<Appointment> appointments = AppointmentService.getAllAppointmentsForPatient(patient.getRoleId())
                        .stream()
                        .filter(appointment -> AppointmentStatus.isIn(appointment.getAppointmentStatus(), 
                            AppointmentStatus.CONFIRMED,
                            AppointmentStatus.PENDING)
                        )
                        .collect(Collectors.toList());
                    if (!appointments.isEmpty()) {
                        AppointmentDisplay.printAppointmentDetails(appointments);
                    } else {
                        System.out.println("No appointments scheduled. Start scheduling one today.\n");
                    }
            }).shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.PATIENT_APPOINTMENT_SELECTION_TYPE
            .setOptionGenerator(() -> {
                List<Option> options = new ArrayList<>(Arrays.asList(
                    new Option("Tomorrow", "tmr|tomorrow", true)
                        .setNextAction((userInput, args) -> {
                            LocalDate tmr = LocalDate.now().plusDays(1);
                            args.put("year", String.valueOf(tmr.getYear()));
                            args.put("month", String.valueOf(tmr.getMonthValue()));
                            args.put("day", String.valueOf(tmr.getDayOfMonth()));
                            return args;
                        }).setNextMenu(Menu.INPUT_APPOINTMENT_HOUR), 
                    new Option("Custom", "custom", true)
                    .setNextAction((userInput, args) -> args).setNextMenu(Menu.INPUT_APPOINTMENT_YEAR)
                ));
                if (LocalTime.now().isBefore(Timeslot.lastSlotStartTime)) {
                    options.add(0, new Option("Today", "today", true)
                        .setNextAction((userInput, args) -> {
                            LocalDate today = LocalDate.now();
                            args.put("year", String.valueOf(today.getYear()));
                            args.put("month", String.valueOf(today.getMonthValue()));
                            args.put("day", String.valueOf(today.getDayOfMonth()));
                            return args;
                        }).setNextMenu(Menu.INPUT_APPOINTMENT_HOUR)
                    );
                }
                return options;
            }).addMainMenuOption();
        Menu.INPUT_APPOINTMENT_YEAR
            .setOptionGenerator(() -> {
                LocalDateTime now = LocalDateTime.now();
                // Do not show this year if today is last day of the year and current time exceeds
                // last time slot
                int currentYear = now.getYear() + (
                    now.equals(lastDayOfYear()) && now.toLocalTime().isAfter(Timeslot.lastSlotStartTime) ?
                    1 : 0
                );
                return IntStream.range(currentYear, currentYear+2)
                    .mapToObj(year -> new Option(
                            Integer.toString(year), Integer.toString(year), true
                        ).setNextAction((input, args) -> {
                            if (args.isEmpty()) {
                                args = new HashMap<>();
                            }
                            args.put("year", Integer.toString(year));
                            return args;
                        }).setNextMenu(INPUT_APPOINTMENT_MONTH)
                    ).collect(Collectors.toList());
            }).shouldAddMainMenuOption();
        Menu.INPUT_APPOINTMENT_MONTH
            .setOptionGenerator(() -> {
                int selectedYear = Integer.parseInt(
                    (String) Menu.INPUT_APPOINTMENT_MONTH.dataFromPreviousMenu.get("year")
                );
                LocalDateTime now = LocalDateTime.now();
                // Do not show this month if today is last day of the month and current time exceeds
                // last time slot
                int startMonth = selectedYear < now.getYear()+1 ?
                    now.getMonthValue() + ((
                        now.equals(lastDayOfMonth()) &&
                        now.toLocalTime().isAfter(Timeslot.lastSlotStartTime)
                    ) ? 1 : 0) : 1;
                return IntStream.range(startMonth, 13)
                    .mapToObj(month -> {
                        Month currentMonth = Month.of(month);
                        return new Option(
                                currentMonth.toString(),
                                String.format(
                                    "%s(%s)?",
                                    currentMonth.toString().substring(0, 3),
                                    currentMonth.toString().substring(3, currentMonth.toString().length())
                                ), true
                            ).setNextAction((input, args) -> {
                                if (args.isEmpty()) {
                                    args = new HashMap<>();
                                }
                                args.put("month", Integer.toString(month));
                                args.put(
                                    "startDay",
                                    Integer.toString(
                                        selectedYear < now.getYear()+1 && month < now.getMonthValue()+1 ?
                                        now.getDayOfMonth() : 1 + (
                                            now.toLocalTime().isAfter(Timeslot.lastSlotStartTime) ? 1 : 0
                                )));
                                args.put("endDay", Integer.toString(now.with(lastDayOfMonth()).getDayOfMonth()));
                                INPUT_APPOINTMENT_DAY.label = String.format(
                                    "Enter a day from %d to %d:",
                                    Integer.parseInt((String) args.get("startDay")),
                                    Integer.parseInt((String) args.get("endDay"))
                                );
                                return args;
                            }).setNextMenu(INPUT_APPOINTMENT_DAY);
                    }).collect(Collectors.toList());
            }).shouldAddMainMenuOption();
        Menu.INPUT_APPOINTMENT_DAY
            .setNextMenu(Menu.INPUT_APPOINTMENT_HOUR)
            .setNextAction((userInput, args) -> {
                int startDay = Integer.parseInt((String) args.get("startDay"));
                int endDay = Integer.parseInt((String) args.get("endDay"));
                int input;
                try {
                    input = Integer.parseInt(userInput);
                } catch (Exception e) {
                    throw new Exception(String.format(
                        "Please enter a valid number between %d and %d (inclusive)",
                        startDay,
                        endDay
                    ));
                }
                if (input < startDay || input > endDay) {
                    throw new Exception(String.format(
                        "Please enter a date between %d and %d (inclusive)",
                        startDay,
                        endDay
                    ));
                }

                args.put("day", Integer.toString(input));
                return args;
            });
        Menu.INPUT_APPOINTMENT_HOUR
            .setOptionGenerator(() -> {
                LocalDateTime now = LocalDateTime.now();
                LocalDate selectedDate = LocalDate.of(
                    Integer.parseInt((String) Menu.INPUT_APPOINTMENT_HOUR.dataFromPreviousMenu.get("year")),
                    Integer.parseInt((String) Menu.INPUT_APPOINTMENT_HOUR.dataFromPreviousMenu.get("month")),
                    Integer.parseInt((String) Menu.INPUT_APPOINTMENT_HOUR.dataFromPreviousMenu.get("day"))
                );
                boolean isToday = (
                    selectedDate.isEqual(now.toLocalDate()) ||
                    selectedDate.isBefore(now.toLocalDate())
                );
                if (isToday && now.toLocalTime().isAfter(Timeslot.lastSlotStartTime)) {
                    MenuService.setCurrentMenu(Menu.getUserMainMenu());
                    throw new Exception("No appointments remaining today.");
                }
                return IntStream.range(
                        !isToday || now.toLocalTime().isBefore(Timeslot.firstSlotStartTime) ?
                            Timeslot.firstSlotStartTime.getHour() :
                            now.getHour() + 1,
                        Timeslot.lastSlotStartTime.getHour() + 1
                    ).mapToObj(hour -> new Option(
                            DateTimeUtil.printLongDateTime(LocalDateTime.of(
                                now.toLocalDate(),
                                LocalTime.of(hour, 0)
                            )),
                            String.format("%02d00", hour),
                            true
                        ).setNextAction((input, args) -> {
                            args.put("dateTime", LocalDateTime.of(selectedDate, LocalTime.of(hour, 0)));
                            return args;
                        }).setNextMenu(INPUT_APPOINTMENT_DOCTOR)
                    ).collect(Collectors.toList());
            }).addMainMenuOption();
        Menu.INPUT_APPOINTMENT_DOCTOR
            .setOptionGenerator(() -> {
                if (
                    MenuService.getCurrentMenu().dataFromPreviousMenu != null &&
                    MenuService.getCurrentMenu().dataFromPreviousMenu.containsKey("dateTime")
                ) {
                    LocalDateTime selectedDateTime = 
                        (LocalDateTime) MenuService.getCurrentMenu().dataFromPreviousMenu.get("dateTime");

                    if (
                        AppointmentService
                            .getAllAppointmentsForPatient(Menu.getTargetPatientFromArgs().getRoleId())
                            .stream()
                            .filter(appointment ->
                                appointment.getTimeslot().isEqual(selectedDateTime) &&
                                !appointment.getAppointmentStatus().equals(AppointmentStatus.CANCELLED)
                            ).findFirst()
                            .isPresent()
                    ) {
                        MenuService.setCurrentMenu(MenuService.getCurrentMenu().getExitMenu());
                        throw new Exception("Appointment already exists at this time");
                    }
                        
                    List<Doctor> availableDoctors = AppointmentService
                        .getAvailableDoctorsAtTimeslot(selectedDateTime);
                    if (availableDoctors != null && !availableDoctors.isEmpty()) {
                        return IntStream.range(0, availableDoctors.size())
                            .mapToObj(doctorIndex -> {
                                Doctor doctor = availableDoctors.get(doctorIndex);
                                return new Option(
                                    doctor.getName(),
                                    doctor.getName(),
                                    true
                                ).setNextMenu(Menu.getUserMainMenu())
                                .setNextAction((input, args) -> {
                                    if (args.get("currentAppointment") != null) {
                                        AppointmentService.rescheduleAppointment(
                                            ((Patient) UserService.getCurrentUser()).getRoleId(),
                                            doctor.getRoleId(),
                                            selectedDateTime,
                                            (Appointment) args.get("currentAppointment")
                                        );
                                    } else {
                                        AppointmentService.scheduleAppointment(
                                            ((Patient) UserService.getCurrentUser()).getRoleId(),
                                            doctor.getRoleId(),
                                            selectedDateTime
                                        );
                                    }
                                    return args;
                                });
                            }).collect(Collectors.toList());
                    }
                }
                MenuService.getCurrentMenu().setNextMenu(() -> Menu.getUserMainMenu());
                throw new Exception("No doctors available.");
            }).addMainMenuOption()
            .setExitMenu(() -> Menu.getUserMainMenu());
        Menu.PATIENT_RESCHEDULE_SELECTION
            .setOptionGenerator(() -> {
                Patient patient = (Patient) UserService.getCurrentUser();
                List<Appointment> appointments = AppointmentService.getAllAppointmentsForPatient(patient.getRoleId())
                    .stream()
                    .filter(appointment -> new ArrayList<Appointment.AppointmentStatus>(){{
                        add(AppointmentStatus.CONFIRMED);
                        add(AppointmentStatus.PENDING);
                    }}.contains(appointment.getAppointmentStatus()))
                    .collect(Collectors.toList());
                if (appointments != null && !appointments.isEmpty()) {
                    return IntStream.range(0, appointments.size())
                        .mapToObj(apptIdx -> {
                            Appointment appointment = appointments.get(apptIdx);
                            return new Option(
                                DateTimeUtil.printLongDateTime(appointment.getTimeslot()),
                                DateTimeUtil.printShortDateTime(appointment.getTimeslot()),
                                true
                            ).setNextMenu(Menu.PATIENT_APPOINTMENT_SELECTION_TYPE)
                            .setNextAction((input, args) -> {
                                args.put("currentAppointment", appointment);
                                return args;
                            });
                        }).collect(Collectors.toList());
                }
                MenuService.getCurrentMenu().setNextMenu(() -> Menu.getUserMainMenu());
                throw new Exception("No appointments available.");
            }).setExitMenu(() -> Menu.getUserMainMenu());
        Menu.PATIENT_CANCEL_SELECTION
            .setOptionGenerator(() -> {
                Patient patient = (Patient) UserService.getCurrentUser();
                List<Appointment> appointments = AppointmentService.getAllAppointmentsForPatient(patient.getRoleId())
                    .stream()
                    .filter(appointment -> appointment.getAppointmentStatus() == Appointment.AppointmentStatus.CONFIRMED)
                    .collect(Collectors.toList());
                if (appointments != null && !appointments.isEmpty()) {
                    return IntStream.range(0, appointments.size())
                        .mapToObj(apptIdx -> {
                            Appointment appointment = appointments.get(apptIdx);
                            return new Option(
                                DateTimeUtil.printLongDateTime(appointment.getTimeslot()),
                                DateTimeUtil.printShortDateTime(appointment.getTimeslot()),
                                true
                            ).setNextMenu(() -> Menu.PATIENT_MAIN_MENU)
                            .setNextAction((input, args) -> {
                                AppointmentService.cancelAppointment(appointment);
                                return args;
                            });
                        }).collect(Collectors.toList());
                }
                MenuService.getCurrentMenu().setNextMenu(() -> Menu.getUserMainMenu());
                throw new Exception("No appointments available.");
            }).setExitMenu(() -> Menu.getUserMainMenu());
        Menu.PATIENT_VIEW_OUTCOMES
            .setDisplayGenerator(() -> {
                Patient patient = (Patient) UserService.getCurrentUser();
                List<AppointmentOutcomeRecord> outcomeRecords = AppointmentService.getAppointmentRecordsByPatientId(patient.getRoleId());
                if (!outcomeRecords.isEmpty()) {
                    outcomeRecords.stream().forEach(outcomeRecord -> 
                        AppointmentDisplay.printAppointmentOutcomeDetails(outcomeRecord)
                    );
                } else {
                    System.out.println("No appointment outcomes found. Start scheduling an appointment today.\n");
                }
            }).setExitMenu(() -> Menu.getUserMainMenu())
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.DOCTOR_MAIN_MENU // exit menu should be itself
            .setOptionGenerator(() -> new ArrayList<>(List.of(
                new Option(
                        "View Patient's Medical Record", 
                        "view( )?(patient(\\'s)?( )?')?(medical( )?)?record", 
                        true
                    ).setNextMenu(() -> SELECT_PATIENT_VIEW_MEDICAL_RECORD),
                new Option(
                        "Update Patient's Medical Record", 
                        "(edit( )?)?(patient( )?('s)?)?(medical( )?)?record", 
                        true
                    ).setNextMenu(() -> SELECT_PATIENT_EDIT_MEDICAL_RECORD),
                new Option(
                        "View Personal Schedule", 
                        "(view( )?)?(personal( )?)?schedule", 
                        true
                    ).setNextMenu(() -> DOCTOR_VIEW_SCHEDULE),
                new Option(
                        "Set Appointment Availability", 
                        "(set( )?)?(appointment( )?)?availability", 
                        true
                    ).setNextMenu(() -> DOCTOR_SET_AVAILABILITY),
                new Option(
                        "Accept or Decline Appointment Requests", 
                        "accept|decline|(appointment)?( )?requests", 
                        true
                    ).setNextMenu(() -> DOCTOR_ACCEPT_APPOINTMENTS),
                new Option(
                        "View/Cancel Upcoming Appointments", 
                        "cancel|upcoming", 
                        true
                    ).setNextMenu(() -> DOCTOR_CANCEL_CONFIRMED)
            ))).shouldAddLogoutOptions();
        Menu.SELECT_PATIENT_VIEW_MEDICAL_RECORD
            .setPatientListOptionGenerator(() -> PATIENT_VIEW_MEDICAL_RECORD)
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.SELECT_PATIENT_EDIT_MEDICAL_RECORD
            .setPatientListOptionGenerator(() -> PATIENT_EDIT_MEDICAL_RECORD)
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.SELECT_PATIENT_APPOINTMENT
            .setOptionGenerator(() -> {
                List<Option> options = AppointmentService
                    .getAllAppointmentsForPatient(Menu.getTargetPatientFromArgs().getRoleId())
                    .stream()
                    .filter(appointment -> appointment.getAppointmentStatus().equals(AppointmentStatus.COMPLETED))
                    .map(appointment -> 
                        new Option(
                            String.format(
                                "%s (Status: %s)",
                                DateTimeUtil.printLongDateTime(appointment.getTimeslot()),
                                appointment.getAppointmentStatus().toString()
                            ),
                            DateTimeUtil.printLongDateTime(appointment.getTimeslot()),
                            true
                        ).setNextAction((userInput, args) -> new HashMap<String, Object>(){{
                            put("appointmentId", Integer.toString(appointment.getAppointmentId()));
                        }}).setNextMenu(() -> EDIT_PATIENT_APPOINTMENT)
                    ).collect(Collectors.toList());
                    if (options.isEmpty()) {
                        throw new Exception("No confirmed appointments found.");
                    }
                return options;
            }).shouldAddMainMenuOption()
            .shouldAddLogoutOptions();;
        Menu.EDIT_PATIENT_APPOINTMENT
            .setOptionGenerator(() -> {
                Appointment appointment = null;
                try {
                    appointment = AppointmentService.getAppointment(
                        Integer.parseInt(
                            (String) MenuService.getCurrentMenu().dataFromPreviousMenu.get("appointmentId")
                        )
                    );
                } catch (Exception e) {}
                if (appointment == null) {
                    throw new Exception("Appointment not found");
                }
                return new ArrayList<>(List.of(
                    // TODO: redirect to appointment accept
                    // new Option(
                    //     appointment.getAppointmentStatus().toString(),
                    //     appointment.getAppointmentStatus().toString(),
                    //     true
                    // ).setNextAction((userInput, args) -> args)
                    // .setNextMenu(() -> CHANGE_APPOINTMENT_STATUS)),
                    // TODO: redirect to update prescription
                    // new Option(appointment., null, false)
                ));
            }).shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.DOCTOR_VIEW_SCHEDULE
            .setOptionGenerator(() -> {
                // AppointmentService.getAllEvents().stream().forEach(event -> System.out.println(event.getDoctorId())); // BUG due to updated Appointment Service code
                System.out.println(UserService.getCurrentUser().getRoleId());
                List<Option> options = AppointmentService.getAllAppointmentsForDoctor(UserService.getCurrentUser().getRoleId())
                    .stream()
                    .filter(event ->
                        event.getDoctorId() == UserService.getCurrentUser().getRoleId()
                        // &&
                        // event.getTimeslot().plusHours(Timeslot.timeslotLengthInHours)
                        //     .isAfter(LocalDateTime.now())
                    ).sorted(Comparator.comparing(DoctorEvent::getTimeslot))
                    .map(event -> new Option(
                            DateTimeUtil.printLongDateTime(event.getTimeslot()),
                            "\n",
                            true
                        ).setNextAction((userInput, args) -> {
                            MenuService.setCurrentMenu(Menu.getUserMainMenu());
                            throw new Exception("");
                        })
                    ).collect(Collectors.toList());
                if (options == null || options.isEmpty()) {
                    MenuService.setCurrentMenu(Menu.getUserMainMenu());
                    throw new Exception("No upcoming events scheduled.");
                }
                return options;
            }).setNextMenu(() -> Menu.getUserMainMenu())
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.DOCTOR_SET_AVAILABILITY
            // .setOptionGenerator(() -> new ArrayList<>(List.of(
            //     new Option(
            //             "View Unavailable Periods", 
            //             "view(( )?unavailable)?(( )?periods)?", 
            //             true
            //         ).setNextMenu(() -> DOCTOR_VIEW_UNAVAILABLE),
            //     new Option(
            //             "Add Unavailable Periods", 
            //             "add(( )?unavailable)?(( )?periods)?", 
            //             true
            //         ).setNextMenu(() -> DOCTOR_ADD_UNAVAILABLE),
            //     new Option(
            //             "Delete Unavailable Periods", 
            //             "delete(( )?unavailable)?(( )?periods)?", 
            //             true
            //         ).setNextMenu(() -> DOCTOR_DELETE_UNAVAILABLE),
            //     new Option(
            //             "Edit Unavailable Periods", 
            //             "edit(( )?unavailable)?(( )?periods)?", 
            //             true
            //         ).setNextMenu(() -> DOCTOR_EDIT_UNAVAILABLE)
            // ))).setNextMenu(() -> Menu.getUserMainMenu())
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.DOCTOR_ACCEPT_APPOINTMENTS
            .setOptionGenerator(() -> {
                List<Option> options = AppointmentService
                    .getAppointmentByStatus(AppointmentStatus.PENDING)
                    .stream()
                    .filter(appointment ->
                        appointment.getDoctorId() == UserService.getCurrentUser().getRoleId()
                    ).sorted(Comparator.comparing(Appointment::getTimeslot))
                    .map(appointment -> {
                        Patient patient = UserService.findUserByIdAndType(
                            appointment.getPatientId(),
                            Patient.class,
                            true
                        );
                        String timeslot = DateTimeUtil.printLongDateTime(appointment.getTimeslot());
                        return new Option(
                            patient == null ? timeslot : String.format(
                                "%s - %s (P%d)",
                                timeslot,
                                patient.getName(),
                                patient.getRoleId()
                            ),
                            timeslot,
                            true
                        ).setNextAction((userInput, args) -> {
                            args = args == null ? new HashMap<>() : args;
                            args.put("appointmentId", Integer.toString(appointment.getAppointmentId()));
                            return args;
                        }).setNextMenu(() -> DOCTOR_ACCEPT_OR_DECLINE_APPOINTMENT);
                }).collect(Collectors.toList());
                if (options == null || options.isEmpty()) {
                    MenuService.setCurrentMenu(Menu.getUserMainMenu());
                    throw new Exception("No pending appointments.");
                }
                return options;
            })
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.DOCTOR_ACCEPT_OR_DECLINE_APPOINTMENT
            .setOptionGenerator(() -> new ArrayList<>(List.of(
                new Option("Accept", "accept", true)
                    .setNextMenu(() -> DOCTOR_ACCEPT_APPOINTMENTS)
                    .setNextAction((input, args) -> {
                        AppointmentService
                            .getAppointment(Integer.parseInt((String)args.get("appointmentId")))
                            .confirm();
                        return null;
                    }),
                new Option("Decline", "decline", true)
                    .setNextMenu(() -> DOCTOR_ACCEPT_APPOINTMENTS)
                    .setNextAction((input, args) -> {
                        AppointmentService
                            .getAppointment(Integer.parseInt((String)args.get("appointmentId")))
                            .cancel();
                        return null;
                    })
            ))).setExitMenu(() -> Menu.getUserMainMenu())
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.DOCTOR_CANCEL_CONFIRMED
            .setOptionGenerator(() -> {
                List<Option> options = AppointmentService
                    .getAppointmentByStatus(AppointmentStatus.CONFIRMED)
                    .stream()
                    .filter(appointment ->
                        appointment.getDoctorId() == UserService.getCurrentUser().getRoleId()
                    ).sorted(Comparator.comparing(Appointment::getTimeslot))
                    .map(appointment -> {
                        Patient patient = UserService.findUserByIdAndType(
                            appointment.getPatientId(),
                            Patient.class,
                            true
                        );
                        String timeslot = DateTimeUtil.printLongDateTime(appointment.getTimeslot());
                        return new Option(
                            patient == null ? timeslot : String.format(
                                "%s - %s (P%d)",
                                timeslot,
                                patient.getName(),
                                patient.getRoleId()
                            ),
                            timeslot,
                            true
                        ).setNextAction((userInput, args) -> {
                            appointment.cancel();
                            return null;
                        }).setNextMenu(() -> Menu.getUserMainMenu());
                }).collect(Collectors.toList());
                if (options == null || options.isEmpty()) {
                    MenuService.setCurrentMenu(Menu.getUserMainMenu());
                    throw new Exception("No upcoming appointments.");
                }
                return options;
            })
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
    }
    // Init END

    // Helper START
    private enum MenuType {
        DISPLAY,
        INPUT,
        SELECT
    }

    public interface DisplayGenerator {
        void apply() throws Exception;
    }

    public interface ThrowableBlankFunction<R, E extends Exception> {
        R apply() throws E;
    }

    private interface OptionGenerator extends ThrowableBlankFunction<List<Option>, Exception> {}
    private interface MenuGenerator extends ThrowableBlankFunction<Menu, Exception> {}

    public interface ThrowableBiFunction<T, U, R, E extends Exception> {
        R apply(T t, U u) throws E;
    }

    private interface NextAction extends ThrowableBiFunction<
        String, // user input
        Map<String, Object>, // data from prev menu
        Map<String, Object>, // return updated data
        Exception
    > {}

    private enum DisplayMode {
        MATCH_FOUND,
        NO_MATCH_FOUND,
        MULTIPLE_MATCHES_FOUND,
        INITIAL
    }

    private static class Option {
        private final String label;
        private final String matchPattern;
        private final boolean isNumberedOption;
        private MenuGenerator nextMenuGenerator;
        private MenuGenerator exitMenuGenerator;
        protected NextAction nextAction = (input, args) -> args;
    
        public Option(
            String label,
            String matchPattern,
            boolean isNumberedOption
        ) {
            this.label = label;
            this.matchPattern = matchPattern;
            this.isNumberedOption = isNumberedOption;
        };

        protected MenuGenerator getNextMenuGenerator() {
            return this.nextMenuGenerator;
        }

        protected MenuGenerator getExitMenuGenerator() {
            return this.exitMenuGenerator;
        }

        protected NextAction getNextAction() {
            return this.nextAction;
        }
    
        protected final Option setNextMenu(Menu nextMenu) {
            return this.setNextMenu(() -> nextMenu);
        }

        protected Option setNextMenu(MenuGenerator nextMenuGenerator) {
            this.nextMenuGenerator = nextMenuGenerator;
            return this;
        }

        private Option setNextAction(NextAction nextAction) {
            this.nextAction = nextAction;
            return this;
        }
    }

    /**
     * The constructor expects a Next Action middleware, which gets called after selection to update the behaviour of the edit menu immediately before rendering it
     * subsequent calls to setNextAction will not work since setNextAction in Option is private
     * The Edit Option's next menu is then the Edit Menu
     * The Edit Option's error menu is then the 
     * The Edit Menus's error menu is itself, ie it will keep asking for user input 
     */
    private static class EditOption extends Option {

        public EditOption(
            String label,
            String matchPattern,
            boolean isNumberedOption,
            NextAction finalNextAction
        ) {
            super(label, matchPattern, isNumberedOption);
            Menu currentMenu = MenuService.getCurrentMenu();
            this.nextAction = ((userInput, args) -> {
                // edit menu updated
                Menu.EDIT
                    .setNextAction((innerInput, innerArgs) -> {
                        finalNextAction.apply(innerInput, innerArgs);
                        return innerArgs;
                    })
                    .setNextMenu(() -> currentMenu)
                    .setExitMenu(() -> currentMenu);
                // return any args that might be required
                return args;
            });
            this.setNextMenu(Menu.EDIT); // Will always redirect to Edit Menu
        }
    }

    private static Patient getTargetPatientFromArgs() throws Exception {
        Patient patient = (Patient) (
            MenuService.getCurrentMenu().dataFromPreviousMenu != null &&
            MenuService.getCurrentMenu().dataFromPreviousMenu.containsKey("patientId") ?
                UserService.findUserByIdAndType(
                    Integer.parseInt(
                        (String) MenuService.getCurrentMenu().dataFromPreviousMenu.get("patientId")
                    ),
                    Patient.class,
                    true
                ) : (
                    Patient.class.equals(UserService.getCurrentUser().getClass()) ?
                        UserService.getCurrentUser() : null
                )
        );
        if (patient == null) {
            throw new Exception("No patient found.");
        }
        return patient;
    }
    // Helper END




    // Builder START
    private static class MenuBuilder {

        private final MenuType menuType;
        private final String title;
        private final String label;
        private final boolean requiresConfirmation;
        
        MenuBuilder(MenuType menuType, String title, String label, boolean requiresConfirmation) {
            this.menuType = menuType;
            this.title = title;
            this.label = label;
            this.requiresConfirmation = requiresConfirmation;
        }

        MenuBuilder(MenuType menuType, String title, String label) {
            this(menuType, title, label, false);
        }
    }
    // Builder END

    private final String title;
    private String label;
    private final MenuType menuType;
    private boolean parseUserInput = true;
    // Transitions & Actions START
    private MenuGenerator nextMenuGenerator;
    private MenuGenerator exitMenuGenerator;
    private NextAction nextAction = (input, args) -> args;
    private Map<String, Object> dataFromPreviousMenu;
    private String userInput;
    // Transitions & Actions END
    // Options START
    private List<Option> options;
    private List<Option> matchingOptions;
    private OptionGenerator optionGenerator;
    private DisplayMode displayMode = DisplayMode.INITIAL;
    private DisplayGenerator displayGenerator;
    private boolean shouldHaveMainMenuOption;
    private boolean shouldHaveLogoutOption;
    private final boolean requiresConfirmation;
    // Options END
        
    Menu(MenuBuilder menuBuilder) {
        this.menuType = menuBuilder.menuType;
        this.title = menuBuilder.title;
        this.label = menuBuilder.label;
        this.requiresConfirmation = menuBuilder.requiresConfirmation;
    }

    public Menu setDisplayGenerator(DisplayGenerator displayGenerator) {
        this.displayGenerator = displayGenerator;
        return this;
    }

    public void display() throws Exception {
        if (this.menuType == MenuType.SELECT) {
            if (this.optionGenerator != null) {
                this.options = optionGenerator.apply();
                if (
                    this.options != null &&
                    this.matchingOptions != null &&
                    this.options.size() >= this.matchingOptions.size()
                ) {
                     // refresh options after editing
                    this.matchingOptions = this.getNumberedOptions(true);
                }
            }

            if (this.shouldHaveMainMenuOption) {
                this.addMainMenuOption();
            }

            if (this.shouldHaveLogoutOption) {
                this.addLogoutOptions();
            }

            if (this.options == null || this.options.size() < 1) {
                throw new Error("Menu with type select should have at least one option");
            }
        }

        if (!(this.title == null || this.title.length() < 1)) {
            System.out.println(this.title);
            Menu.printLineBreak(50);
        }

        if (!(this.label == null || this.label.length() < 1)) {
            System.out.print("\n" + this.label + (
                this.menuType == MenuType.INPUT ? " " : "\n\n"
            ));
        } else if (
            this.menuType == MenuType.SELECT &&
            !this.getNumberedOptions(true).isEmpty()
        ) {
            System.out.println("");
            switch (this.displayMode) {
                case NO_MATCH_FOUND -> System.out.println("No option matched your selection. Please try again:");
                case MULTIPLE_MATCHES_FOUND -> System.out.println("Please be more specific:");
                case INITIAL -> System.out.println("Please select an option:");
                default -> { break; }
            }
            System.out.println("");
        }

        if (this.displayGenerator != null) {
            this.displayGenerator.apply();
        }

        if (this.menuType == MenuType.SELECT) {

            // Display numbered options
            List<Option> matches = (
                    this.matchingOptions == null || this.matchingOptions.size() < 1
                ) ? this.getNumberedOptions(true) : this.matchingOptions;
            IntStream.range(0, matches.size())
                .forEach(optionIndex -> System.out.println(String.format(
                    "%d. %s",
                    optionIndex + 1,
                    matches.get(optionIndex).label
                )));

            System.out.println("");

            // Display un-numbered options
            this.getNumberedOptions(false).forEach(option -> System.out.println(option.label));

            System.out.println("");
        }
    }

    private static void printLineBreak(int length) {
        IntStream.range(0, length).forEach(n -> System.out.print("-"));
        System.out.println();
    }

    public Menu setParseUserInput(boolean parseUserInput) {
        this.parseUserInput = parseUserInput;
        return this;
    }

    // BUG the exitMenu cannot be current menu
    private void setConfirmMenu(MenuGenerator nextMenu, MenuGenerator exitMenu, NextAction nextAction) {
        Menu.CONFIRM.setOptionGenerator(() -> Arrays.asList(
            new Option("Yes (Y)", "yes|y|yes( )?\\(?y\\)?", false)
                .setNextMenu(nextMenu).setNextAction(nextAction),
            new Option("No (N)", "no|n|no( )?\\(?n\\)?", false)
                .setNextMenu(exitMenu).setNextAction((input, args) -> args)
        )).setExitMenu(exitMenu);
    }
    
    /**
     * @param userInput
     * @return
     * @throws Exception
     * Next state (transition + action) handling START
     * Input: userInput (called from MenuService.handleUserInput)
     * Output: Returns next menu to MenuService along with any form data from current menu 
     */
    public Menu handleUserInput(String userInput) throws Exception {

        if (!(this.menuType == MenuType.DISPLAY || userInput.length() > 0)) {
            throw new Exception("Please type something in:");
        }

        if (this.parseUserInput) {
            userInput = userInput.trim().toLowerCase();
        }

        System.out.printf("MenuType: %s%n", this.menuType); // IMPT DEBUG

        if (this.menuType == MenuType.INPUT && this.requiresConfirmation) {
            String trueInput = userInput;
            System.out.printf("Input Next Menu: %s%n, Input Exit Menu: %s%n", this.getNextMenu(), this.getExitMenu()); // TEST
            this.setConfirmMenu(
                this.getNextMenuGenerator(),
                this.getExitMenuGenerator(),
                (input, args) -> this.nextAction.apply(trueInput, args) // input MenuType.INPUT should have all nextAction() set, unlike MenuType.OPTION
            );
            Menu.CONFIRM.setDataFromPreviousMenu(this.dataFromPreviousMenu);
            // this.setDataFromPreviousMenu(null);
            return Menu.CONFIRM;
        }

        if (this.menuType == MenuType.SELECT) {
            this.matchingOptions = (ArrayList<Option>) this.getFilteredOptions(userInput, true);
            List<Option> unNumberedMatches = this.getFilteredOptions(userInput, false);
            int numberOfMatches = unNumberedMatches.size() + this.matchingOptions.size();
            Option option;
            if (numberOfMatches < 1) {
                this.displayMode = DisplayMode.NO_MATCH_FOUND;
                System.out.println("No option match found"); // TODO REMOVE
            } else if (numberOfMatches > 1) {
                this.displayMode = DisplayMode.MULTIPLE_MATCHES_FOUND;
            } else {
                try {
                    unNumberedMatches.addAll(this.matchingOptions);
                    option = unNumberedMatches.get(0);

                    if (this.requiresConfirmation && !option.isNumberedOption) {
                        this.setConfirmMenu(option.getNextMenuGenerator(), option.getExitMenuGenerator(), option.getNextAction());
                        Menu.CONFIRM.setDataFromPreviousMenu(this.dataFromPreviousMenu);
                        // this.setDataFromPreviousMenu(null);
                        return Menu.CONFIRM;
                    }

                    this.setNextMenu(option.getNextMenuGenerator());
                    this.setNextAction(option.getNextAction());
                } catch (Exception e) {
                    this.setNextMenu(this);
                    throw e;
                } catch (Error e) {
                    System.out.println("Something went wrong. Please contact your administrator and try again.");
                    System.out.println("Exiting application...");
                    throw new ExitApplication();
                }
            }
        }
        
        try {
            System.out.printf("Before Executing Action: %s%n", this);
            Map<String, Object> argsForNext = this.setUserInput(userInput).executeNextAction(); // Options dont use user input since they are called within next action methods. Exceptions caught in here will propagate back to App.java which will render exitMenu
            System.out.println(argsForNext); // TODO: remove test
            System.out.printf("After Executing Action: %s%n", this.getNextMenu());
            // this.setDataFromPreviousMenu(null);
            return this.getNextMenu().setDataFromPreviousMenu(argsForNext); // only pass args if no exceptions caught
        } catch (Exception e) {
            if (this.equals(Menu.CONFIRM)) {
                MenuService.setCurrentMenu(
                    this.exitMenuGenerator == null ?
                        Menu.getUserMainMenu() :
                        this.exitMenuGenerator.apply()
                            .setDataFromPreviousMenu(this.dataFromPreviousMenu)
                );
            }
            throw e;
        }
    }

    private Menu setUserInput(String userInput) {
        this.userInput = userInput;
        return this;
    }
    
    private Menu setNextAction(NextAction nextAction) {
        this.nextAction = nextAction;
        return this;
    }

    /**
     * @return
     * @throws Exception
     * 
     */
    private Map<String, Object> executeNextAction() throws Exception {
        if (this.nextAction == null) {
            return null;
        }
        return this.nextAction.apply(this.userInput, this.dataFromPreviousMenu);    
    }

    public MenuGenerator getNextMenuGenerator() throws Exception {
        return this.nextMenuGenerator;
    }

    public MenuGenerator getExitMenuGenerator() throws Exception {
        return this.exitMenuGenerator == null ? () -> this : this.exitMenuGenerator;
    }

    public Menu getNextMenu() throws Exception { // Menu only
        return this.nextMenuGenerator.apply();
    }

    public Menu getExitMenu() throws Exception { // Menu only
        return this.getExitMenuGenerator().apply();
    }

    private Menu setNextMenu(Menu nextMenu) {
        return setNextMenu(() -> nextMenu);
    }

    private Menu setNextMenu(MenuGenerator nextMenuGenerator) {
        this.nextMenuGenerator = nextMenuGenerator;
        return this;
    }

    private Menu setExitMenu(MenuGenerator exitMenuGenerator) {
        this.exitMenuGenerator = exitMenuGenerator;
        return this;
    }

    public Menu setDataFromPreviousMenu(Map<String, Object> dataFromPreviousMenu) {
        this.dataFromPreviousMenu = dataFromPreviousMenu;
        return this;
    }

    private static Menu getUserMainMenu() {
        try {
            // TODO: set remaining user types' menus
            if (Patient.class.equals(UserService.getCurrentUser().getClass())) {
                return Menu.PATIENT_MAIN_MENU;
            }
            if (Doctor.class.equals(UserService.getCurrentUser().getClass())) {
                return Menu.DOCTOR_MAIN_MENU;
            }
            throw new Exception();
        } catch (Exception e) {
            UserService.logout();
            return Menu.LOGIN_USERNAME;
        }
    }
    // Next state (transition + action) handling END

    // Options handling START
    // Options handling - builder START
    private Menu setOptionGenerator(OptionGenerator optionGenerator) {
        this.optionGenerator = optionGenerator;
        return this;
    }

    private Menu setPatientListOptionGenerator(MenuGenerator nextMenu) {
        return this.setOptionGenerator(() -> {
            List<Option> userOptions = UserService.getAllUserByType(Patient.class)
                .stream()
                .map(patient -> new Option(
                        String.format("%s (P%d)", patient.getName(), patient.getRoleId()),
                        String.format(
                            "%s|\\(?P?%d\\)?|%s( )?\\(?P?%d\\)?",
                            patient.getName(),
                            patient.getRoleId(),
                            patient.getName(),
                            patient.getRoleId()
                        ),
                        true
                    ).setNextMenu(nextMenu)
                    .setNextAction((userinput, args) -> new HashMap<String, Object>() {{
                        put("patientId", Integer.toString(patient.getRoleId()));
                    }})
                ).collect(Collectors.toList());
            if (userOptions != null && !userOptions.isEmpty()) {
                return userOptions;
            }
            MenuService.getCurrentMenu().setNextMenu(() -> Menu.getUserMainMenu());
            throw new Exception("No patients found.");
        });
    }

    private Menu addOption(Option option) {
        return this.addOptions(new ArrayList<>(List.of(option)));
    }

    private Menu addOptions(List<Option> options) {
        if (this.menuType != MenuType.SELECT) {
            throw new Error("Cannot add option to menu type select.");
        }

        if (this.options == null) {
            this.options = new ArrayList<>();
        }

        this.options.addAll(options); // append options

        return this;
    }

    private Menu setOptions(List<Option> options) {
        this.options = options;
        return this;
    }

    private boolean optionExists(String optionLabel) {
        return this.options != null &&
            this.options.stream().anyMatch(option -> optionLabel.equals(option.label));
    }

    private void addLogoutOptions() {
        String logoutLabel = "Logout (LO)";
        if (!this.optionExists(logoutLabel)) {
            this.addOption(new Option(
                    logoutLabel,
                    "^LO$|log( )?out(( )?\\(LO\\))?",
                    false
                ).setNextAction((input, args) -> {
                    UserService.logout();
                    MenuService.getCurrentMenu().displayMode = DisplayMode.INITIAL;
                    return null;
                }).setNextMenu(Menu.LOGIN_USERNAME)
            );
        }

        String exitLabel = "Exit Application (E)";
        if (!this.optionExists(exitLabel)) {
            this.addOption(new Option(
                exitLabel,
                "^E$|exit(( )?((app)?plication)?)?(( )?\\(E\\))?",
                false
            ).setNextAction((input, args) -> { throw new ExitApplication(); }));
        }
    }

    private Menu addMainMenuOption() {
        // String backLabel = "Back (B)";
        String mainMenuLabel = "Main Menu (M)";
        if (!this.optionExists(mainMenuLabel)) {
            this.addOption(new Option(
                mainMenuLabel,
                    // "^B$|back(( )?\\(B\\))?",
                    "^M$|main|menu|(main|menu|main menu)(( )?\\(M\\))?",
                    false
                ).setNextMenu(() -> Menu.getUserMainMenu())
            );
        }
        return this;
    }

    private Menu shouldAddMainMenuOption() {
        this.shouldHaveMainMenuOption = true;
        return this;
    }

    private Menu shouldAddLogoutOptions() {
        this.shouldHaveLogoutOption = true;
        return this;
    }
    // Options handling - builder END

    // Options handling - display & matching user input START
    private List<Option> getNumberedOptions(boolean getNumbered) {
        return IntStream.range(0, this.options.size())
            .filter(optionsIndex -> getNumbered == this.options.get(optionsIndex).isNumberedOption)
            .mapToObj((int optionsIndex) -> this.options.get(optionsIndex))
            .collect(Collectors.toList());
    }

    private List<Option> getFilteredOptions(String userInput, boolean numbered) {
        List<Option> filteredOptions = this.getNumberedOptions(numbered);
        return IntStream.range(0, filteredOptions.size())
            .mapToObj(optionsIndex -> {
                Option option = filteredOptions.get(optionsIndex);
                Pattern matchPattern = Pattern.compile(
                    numbered ? 
                        String.join(
                            "|",
                            String.format("^%d(\\.)?$", optionsIndex+1),
                            option.matchPattern,
                            String.format("%d[ ]+(\\.)?%s",
                                optionsIndex+1,
                                option.matchPattern
                            )
                        ) : option.matchPattern,
                    Pattern.CASE_INSENSITIVE
                );
                Matcher matcher = matchPattern.matcher(userInput);

                boolean optionFound = matcher.find();
                if(optionFound) {
                    System.out.println(option.label);
                    return option;
                }
                return null;
                // return matcher.find() ? option : null;
            }).filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    // Options handling - display & matching user input END
    // Options handling END
}
