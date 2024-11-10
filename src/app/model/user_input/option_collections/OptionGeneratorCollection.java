package app.model.user_input.option_collections;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import app.constants.exceptions.ExitApplication;
import app.model.appointments.Appointment;
import app.model.appointments.Appointment.AppointmentStatus;
import app.model.appointments.AppointmentDisplay;
import app.model.appointments.AppointmentOutcomeRecord;
import app.model.appointments.Timeslot;
import app.model.user_input.FunctionalInterfaces.NextAction;
import app.model.user_input.InputMenu;
import app.model.user_input.MenuState;
import app.model.user_input.NewMenu;
import app.model.user_input.Option;
import app.model.user_input.OptionMenu;
import app.model.users.Patient;
import app.model.users.staff.Doctor;
import app.service.AppointmentService;
import app.service.MenuService;
import app.service.UserService;
import app.utils.DateTimeUtil;
import java.util.Arrays;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

public class OptionGeneratorCollection {

    public static List<Option> generateConfirmOptions(NextAction nextAction, MenuState nextState, MenuState exitState) {
        // Define options for confirmation
        Option yesOption = new Option(
            "yes|y|yes( )?\\(?y\\)?", 
            false,
            Map.of("Other Options", "Y", "Action", "Confirm"))
            .setNextMenuState(nextState)
            .setNextAction(nextAction);
    
        Option noOption = new Option(
                "no|n|no( )?\\(?n\\)?",
                false,
                Map.of("Other Options", "N", "Action", "Cancel")
            )
            .setNextMenuState(exitState)
            .setNextAction((formData) -> formData);
    
        // Return a list containing both options
        return Arrays.asList(yesOption, noOption);
    }

    public static List<Option> generateLogoutAndExitOptions() {
        List<Option> options = new ArrayList<>();

        // Logout option
        options.add(new Option(
            "^LO$|log( )?out(( )?\\(LO\\))?",
            false,
            Map.of("Other Options", "LO", "Action", "Logout")
        ).setNextAction((formData) -> {
            UserService.logout();
            return null;
        }).setNextMenuState(MenuState.LOGIN_USERNAME));

        // Exit option
        options.add(new Option(
            "^E$|exit(( )?((app)?plication)?)?(( )?\\(E\\))?",
            false,
            new LinkedHashMap<>() {{
                put("Other Options", "E");
                put("Action", "Exit Application");
            }}
        ).setNextAction((formData) -> {
            throw new ExitApplication();
        }));

        return options;
    }

    public static List<Option> generateMainMenuOption() {
        List<Option> options = new ArrayList<>();

        // Main MenuState option
        options.add(new Option(
            "^M$|main|menu|(main|menu|main menu)(( )?\\(M\\))?",
            false,
            new LinkedHashMap<>() {{
                put("Other Options", "M");
                put("Action", "Return to main menu");
            }}
        ).setNextMenuState(MenuState.getUserMainMenuState()));

        // Additional options can be added to this list as needed

        return options;
    }

    public static List<Option> generatePatientMenuOptions() {
        return new ArrayList<>(List.of(
            new Option(
                    "(view( )?)?medical(( )?record)?", 
                    true,
                    new LinkedHashMap<>() {{
                        put("Action", "View Medical Record");
                    }}
                ).setNextMenuState(MenuState.PATIENT_VIEW_MEDICAL_RECORD), // PATIENT_VIEW_MEDICAL_RECORD
            new Option(
                    "(edit( )?)?contact(( )?info(rmation)?)?", 
                    true,
                    new LinkedHashMap<>() {{
                        put("Action", "Edit Medical Record");
                    }}
                ).setNextMenuState(MenuState.PATIENT_EDIT_MEDICAL_RECORD),
            new Option(
                    "view( )?(available( )?)?appointment(s)?", 
                    true,
                    new LinkedHashMap<>() {{
                        put("Action", "View Available Appointments");
                    }}
                ).setNextMenuState(MenuState.PATIENT_VIEW_AVAIL_APPOINTMENTS),
            new Option(
                    "^schedule( )?(a(n)?( )?)?appointment(s)?",
                    true,
                    new LinkedHashMap<>() {{
                        put("Action", "Schedule an Appointment");
                    }}
                ).setNextMenuState(MenuState.PATIENT_APPOINTMENT_SELECTION_TYPE)
                .setNextAction((formData) -> new HashMap<String, Object>() {{
                        put("yearValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
                        put("monthValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
                        put("dayValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
                        put("hourValidator", DateTimeUtil.DateConditions.FUTURE.toString());
                    }}
                ),
            new Option(
                    "^Reschedule( )?(a(n)?( )?)?appointment(s)?",
                    true,
                    new LinkedHashMap<>() {{
                        put("Action", "Reschedule an Appointment");
                    }}
                ).setNextMenuState(MenuState.PATIENT_RESCHEDULE_SELECTION)
                .setNextAction((formData) -> new HashMap<String, Object>() {{
                        put("yearValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
                        put("monthValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
                        put("dayValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
                        put("hourValidator", DateTimeUtil.DateConditions.FUTURE.toString());
                    }}
                ),
            new Option(
                "^Cancel( )?(a(n)?( )?)?appointment(s)?",
                    true,
                    new LinkedHashMap<>() {{
                        put("Action", "Cancel an Appointment");
                    }}
                ).setNextMenuState(MenuState.PATIENT_CANCEL_SELECTION)
                .setNextAction((formData) -> new HashMap<String, Object>()),
            new Option(
                    "view( )?(scheduled( )?)?appointment(s)?|(view( )?)?confirmed", 
                    true,
                    new LinkedHashMap<>() {{
                        put("Action", "View Scheduled Appointments");
                    }}
                ).setNextMenuState(MenuState.PATIENT_VIEW_CONFIRMED_APPOINTMENTS),
            new Option(
                    "view( )?(appointment( )?)?outcomes(s)?|(view( )?)?history", 
                    true,
                    new LinkedHashMap<>() {{
                        put("Action", "View Appointment Outcomes");
                    }}
                ).setNextMenuState(MenuState.PATIENT_VIEW_OUTCOMES)
        ));
    }

    public static Option getEditBloodTypeOption(Patient p) {
        return new Option(
            "blood|type|blood(( )?type)?|(blood( )?)?type",
            true,
            new LinkedHashMap<>() {{
                put("Field", "Blood Type");
                put("Current Value", p.getBloodType());
            }}
        ).setNextAction((formData) -> {
            p.setBloodType((String) formData.get("input"));
            return formData;
        }).setNextMenuState(MenuState.PATIENT_VIEW_MEDICAL_RECORD)
        .setExitMenuState(MenuState.getUserMainMenuState())
        .setEditRedirect(true);
    }
    
    public static Option getEditMobileNumberOption(Patient patient) {
        return new Option(
            "mobile(( )?number)?",
            true,
            new LinkedHashMap<>() {{
                put("Field", "Mobile Number");
                put("Current Value", "+65" + patient.getMobileNumber());
            }}
        ).setNextAction((formData) -> {
            patient.setMobileNumber((String) formData.get("input"));
            return null;
        }).setNextMenuState(MenuState.PATIENT_VIEW_MEDICAL_RECORD)
        .setExitMenuState(MenuState.getUserMainMenuState())
        .setEditRedirect(true);
    }
    
    public static Option getEditHomeNumberOption(Patient patient) {
        return new Option(
            "home(( )?number)?",
            true,
            new LinkedHashMap<>() {{
                put("Field", "Home Number");
                put("Current Value", "+65" + patient.getHomeNumber());
            }}
        ).setNextAction((formData) -> {
            patient.setHomeNumber((String) formData.get("input"));
            return null;
        }).setNextMenuState(MenuState.PATIENT_VIEW_MEDICAL_RECORD)
        .setExitMenuState(MenuState.getUserMainMenuState())
        .setEditRedirect(true);
    }
    
    public static Option getEditEmailOption(Patient patient) {
        return new Option(
            "email",
            true,
            new LinkedHashMap<>() {{
                put("Field", "Email");
                put("Current Value", patient.getEmail());
            }}
        ).setNextAction((formData) -> {
            patient.setEmail((String) formData.get("input"));
            return null;
        }).setNextMenuState(MenuState.PATIENT_VIEW_MEDICAL_RECORD)
        .setExitMenuState(MenuState.getUserMainMenuState())
        .setEditRedirect(true);
    }
    
    public static Option getEditAppointmentOption() {
        return new Option(
            "(edit( )?)?appointment",
            false,
            new LinkedHashMap<>() {{
                put("Other Options", "A");
                put("Action", "Edit Appointment");
            }}
        ).setNextMenuState(MenuState.SELECT_PATIENT_APPOINTMENT)
         .setNextAction((formData) -> formData)
         .setEditRedirect(true);
    }

    public static List<Option> generateEditPatientDetailsOptions(Patient p) {
        List<Option> options = new ArrayList<>();

        if (List.of(Doctor.class).contains(UserService.getCurrentUser().getClass())) {
            options.add(OptionGeneratorCollection.getEditBloodTypeOption(p));
            options.add(OptionGeneratorCollection.getEditAppointmentOption());
        }

        options.add(OptionGeneratorCollection.getEditMobileNumberOption(p));
        options.add(OptionGeneratorCollection.getEditHomeNumberOption(p));
        options.add(OptionGeneratorCollection.getEditEmailOption(p));

        return options;
    }

    public static List<Option> getAppointmentOptionGenerator() {
    List<Option> options = new ArrayList<>(Arrays.asList(
            new Option(
                "tmr|tomorrow",
                true,
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

            new Option("custom", true,
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
                    true,
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

    public static List<Option> getInputAppointmentYearOptionGenerator() {
        LocalDateTime now = LocalDateTime.now();
        // Do not show this year if today is last day of the year and current time exceeds
        // last time slot
        int currentYear = now.getYear() + (
            now.equals(lastDayOfYear()) && now.toLocalTime().isAfter(Timeslot.lastSlotStartTime) ?
            1 : 0
        );
        return IntStream.range(currentYear, currentYear+2)
            .<Option>mapToObj(year -> new Option(
                    Integer.toString(year), true, new LinkedHashMap<>() {{
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

    // Month option generator
    public static List<Option> getInputAppointmentMonthOptionGenerator(Map<String, Object> formValues) {
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
                        true,
                        new LinkedHashMap<>() {{
                            put("Month", currentMonth.toString());
                        }}
                    ).setNextAction((formData) -> {
                        if (formData.isEmpty()) {
                            formData = new HashMap<>();
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

    // Hour option generator
    public static List<Option> getInputAppointmentHourOptionGenerator(Map<String, Object> formValues) {
        LocalDateTime now = LocalDateTime.now();
        int selectedYear = Integer.parseInt((String) formValues.get("year"));
        int selectedMonth = Integer.parseInt((String) formValues.get("month"));
        int selectedDay = Integer.parseInt((String) formValues.get("day"));
        
        LocalDate selectedDate = LocalDate.of(selectedYear, selectedMonth, selectedDay);
        boolean isToday = (selectedDate.isEqual(now.toLocalDate()) || selectedDate.isBefore(now.toLocalDate()));
        
        int startHour = isToday && now.toLocalTime().isAfter(Timeslot.firstSlotStartTime) ? 
            now.getHour() + 1 : Timeslot.firstSlotStartTime.getHour();

        return IntStream.range(startHour, Timeslot.lastSlotStartTime.getHour() + 1)
            .<Option>mapToObj(hour -> new Option(
                String.format("%02d:00", hour), 
                true,
                new LinkedHashMap<>() {{
                    put("Hour", LocalTime.of(hour, 0).toString());
                }}
            ).setNextAction((newFormValues) -> {
                newFormValues.put("dateTime", LocalDateTime.of(selectedDate, LocalTime.of(hour, 0)));
                return newFormValues;
            })
            .setNextMenuState(MenuState.INPUT_APPOINTMENT_DOCTOR))
            .collect(Collectors.toList());
    }

    public static List<Option> getInputDoctorOptionGenerator(List<Doctor> availableDoctors, Patient p, LocalDateTime selectedDateTime) {
        
        return availableDoctors.stream()
            .<Option>map(doctor -> new Option( 
                    doctor.getName(), 
                    true,
                    new LinkedHashMap<>() {{
                        put("Doctor", doctor.getName());
                    }}
                )
                .setNextMenuState(MenuState.getUserMainMenuState())
                .setNextAction((formData) -> {
                    AppointmentService.scheduleAppointment(
                        p.getRoleId(),
                        doctor.getRoleId(),
                        selectedDateTime
                    );
                    return formData;
                })
                .setRequiresConfirmation(true))
            .collect(Collectors.toList());
    }

    public static List<Option> generateRescheduleAppointmentOptions(List<Appointment> appointments) {
        return appointments.stream()
            .<Option>map(appointment -> new Option(
                    DateTimeUtil.printShortDateTime(appointment.getTimeslot()), 
                    true,
                    new LinkedHashMap<>() {{
                        put("DateTime", DateTimeUtil.printLongDateTime(appointment.getTimeslot()));
                    }}
                )
                .setNextMenuState(MenuState.PATIENT_APPOINTMENT_SELECTION_TYPE)
                .setNextAction((formValues) -> {
                    formValues.put("currentAppointment", appointment);
                    return formValues;
                })
            )
            .collect(Collectors.toList());
    }

    public static List<Option> generateCancelAppointmentOptions(List<Appointment> appointments) {
        return appointments.stream()
            .<Option>map(appointment -> new Option(
                DateTimeUtil.printShortDateTime(appointment.getTimeslot()), 
                true,
                new LinkedHashMap<>() {{
                    put("DateTime", DateTimeUtil.printLongDateTime(appointment.getTimeslot()));
                }}
            )
                .setNextMenuState(MenuState.PATIENT_MAIN_MENU)
                .setNextAction((formValues) -> {
                    AppointmentService.cancelAppointment(appointment);
                    return formValues;
                })
            )
            .collect(Collectors.toList());
    }

    // private static Option generatePatientOptions(Patient p, MenuState nextMenuState) {
    //     Map<String, String> displayFields = Map.of(
    //         "Name", p.getName(),
    //         "Patient ID", "P" + p.getRoleId()
    //     );
    
    //     // Create the Option with display fields and actions
    //     return new Option(
    //         String.format(
    //             "%s|\\(?P?%d\\)?|%s( )?\\(?P?%d\\)?",
    //             p.getName(),
    //             p.getRoleId(),
    //             p.getName(),
    //             p.getRoleId()
    //         ),
    //         true,
    //         displayFields
    //     ).setNextMenuState(nextMenuState)
    //      .setNextAction((fd) -> {
    //          formData = new HashMap<String, Object>();
    //          formData.put("patientId", Integer.toString(p.getRoleId()));
    //          return formData;
    //      });
    // }
}