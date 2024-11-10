package app.model.user_input.option_collections;

import app.constants.BloodType;
import app.constants.Gender;
import app.constants.exceptions.ExitApplication;
import app.model.appointments.Appointment;
import app.model.appointments.AppointmentOutcomeRecord;
import app.model.appointments.Prescription;
import app.model.appointments.Prescription.PrescriptionStatus;
import app.model.appointments.Timeslot;
import app.model.inventory.Medication;
import app.model.inventory.Request;
import app.model.user_input.FunctionalInterfaces.NextAction;
import app.model.user_input.MenuState;
import app.model.user_input.Option;
import app.model.user_input.Option.OptionType;
import app.model.user_input.option_collections.OptionGeneratorCollection.Control;
import app.model.users.Patient;
import app.model.users.staff.Admin;
import app.model.users.staff.Doctor;
import app.model.users.staff.Pharmacist;
import app.model.users.staff.Staff;
import app.service.AppointmentService;
import app.service.MedicationService;
import app.service.UserService;
import app.service.UserService.SortFilter;
import app.utils.DateTimeUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OptionGeneratorCollection {

    public enum Control {
        ADD,
        EDIT,
        DELETE,
        APPROVE,
        REJECT,
        NONE
    }

    public static List<Option> generateConfirmOptions(NextAction nextAction, MenuState nextState, MenuState exitState) {
        // Define options for confirmation
        Option yesOption = new Option(
            "yes|y|yes( )?\\(?y\\)?", 
            OptionType.UNNUMBERED,
            Map.of("Select", "Y", "Action", "Confirm"))
            .setNextMenuState(nextState)
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

    public static List<Option> generatePatientMenuOptions() {
        return new ArrayList<>(List.of(
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
                    OptionType.NUMBERED,
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
    }

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
        }).setNextMenuState(MenuState.PATIENT_VIEW_MEDICAL_RECORD)
        .setExitMenuState(MenuState.getUserMainMenuState())
        .setEditRedirect(true);
    }
    
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
        }).setNextMenuState(MenuState.PATIENT_VIEW_MEDICAL_RECORD)
        .setExitMenuState(MenuState.getUserMainMenuState())
        .setEditRedirect(true);
    }
    
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
        }).setNextMenuState(MenuState.PATIENT_VIEW_MEDICAL_RECORD)
        .setExitMenuState(MenuState.getUserMainMenuState())
        .setEditRedirect(true);
    }
    
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
        }).setNextMenuState(MenuState.PATIENT_VIEW_MEDICAL_RECORD)
        .setExitMenuState(MenuState.getUserMainMenuState())
        .setEditRedirect(true);
    }
    
    public static Option getEditAppointmentOption() {
        return new Option(
            "(edit( )?)?appointment",
            OptionType.UNNUMBERED,
            new LinkedHashMap<>() {{
                put("Select", "A");
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
                        OptionType.NUMBERED,
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
                OptionType.NUMBERED,
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
                    OptionType.NUMBERED,
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
                    OptionType.NUMBERED,
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
                OptionType.NUMBERED,
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

    public static List<Option> generatePharmacistMenuOptions() {
        return new ArrayList<>(List.of(
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
    }

    public static List<Option> generatePharmacistOutcomesOptions(List<Appointment> appointments, boolean isUpdate) {
        return appointments.stream()
        .map(appointment -> {
            AppointmentOutcomeRecord outcome = appointment.getAppointmentOutcome();
            Option option = new Option(
                String.valueOf(appointment.getAppointmentId()), 
                isUpdate ? OptionType.NUMBERED : OptionType.DISPLAY,
                new LinkedHashMap<>() {{
                    put("Patient ID", String.valueOf(appointment.getPatientId()));
                    put("Doctor ID", String.valueOf(appointment.getDoctorId()));
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

    public static List<Option> generatePharmacistUpdatePrescriptionOptions(List<Prescription> prescriptions) {
        List<Option> options = prescriptions.stream()
            .map(prescription -> new Option(
                "Update Prescription " + prescription.getOutcomeId(),  // Title for each option
                OptionType.NUMBERED,  // Mark as a numbered option
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
    
        if (options.isEmpty()) {
            throw new IllegalArgumentException("No prescriptions to update.");
        }
        return options;
    }

    public static List<Option> getPharmacistHandleStatusOptions(Prescription p) {
        // Generate options for each medication order
        List<Option> medicationOrderOptions = p.getMedicationOrders().stream()
            .<Option>map(order -> new Option(
                "Medication Order Details",  // Option title
                OptionType.DISPLAY,  // Mark as a display option
                new LinkedHashMap<>() {{
                    put("Order ID", String.valueOf(order.getId()));
                    put("Prescription ID", String.valueOf(order.getPrescriptionId()));
                    put("Medication ID", String.valueOf(order.getMedicationId()));
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

    public static List<Option> getPharmacistMedicationOptions(List<Medication> medications) {
        return medications.stream()
            .map(medication -> new Option(
                medication.getName(),
                Option.OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Medication ID", String.valueOf(medication.getId()));
                    put("Medication Name", medication.getName());
                    put("Available Stock", String.valueOf(medication.getStock()));
                }}
            ).setNextAction(formValues -> {
                Map<String, Object> newFormValues = new HashMap<>();
                newFormValues.put("medicationId", medication.getId());
                return newFormValues;
            }).setNextMenuState(MenuState.PHARMACIST_ADD_COUNT))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<Option> getMedicationDisplayOptions(Control ctl) {
        boolean isAdmin = UserService.getCurrentUser() instanceof Admin;

        List<Option> options = MedicationService.getAllMedications().stream()
            .map(medication -> {
                Option option = new Option(
                    medication.getName(),
                    ctl == Control.EDIT ? OptionType.NUMBERED : OptionType.DISPLAY,
                    new LinkedHashMap<>() {{
                        put("Medication ID", String.valueOf(medication.getId()));
                        put("Name", medication.getName());
                        put("Stock", String.valueOf(medication.getStock()));
                        put("Low Alert Level", String.valueOf(medication.getLowAlertLevel()));
                    }}
                );

                if (isAdmin && ctl == Control.EDIT) {
                    option.setNextAction(formData -> {
                        formData.put("medication", medication);
                        return formData;
                    })
                    .setNextMenuState(MenuState.ADMIN_EDIT_MEDICATION);
                }

                return option;
            })
            .collect(Collectors.toList());

        if (isAdmin && ctl == Control.NONE) {
            options.add(new Option(
                "ADD( )",
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

    public static List<Option> generateAdminMainMenuOptions() {
        return new ArrayList<>(List.of(
            new Option(
                "(view( )?)?user(s)?",
                OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Action", "View Users");
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
    }

    public static List<Option> generateAdminAppointmentsView(List<Appointment> appointments) {
        return appointments.stream()
        .map(appointment -> new Option(
            String.valueOf(appointment.getAppointmentId()),
            OptionType.DISPLAY,
            new LinkedHashMap<>() {{
                put("Appointment ID", String.valueOf(appointment.getAppointmentId()));
                put("Timeslot", DateTimeUtil.printLongDateTime(appointment.getTimeslot()));
                String patientName = UserService.findUserByIdAndType(appointment.getPatientId(), Patient.class, true).getName();
                put("Patient Name", patientName);
                Doctor doctor = UserService.findUserByIdAndType(appointment.getDoctorId(), Doctor.class, true);
                put("Doctor Name", doctor == null ? "No doctor assigned" : doctor.getName());
                put("Status", appointment.getAppointmentStatus().toString());
            }}
        ))
        .collect(Collectors.toList());
    }

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
                    put("Action", "Sort Role Asc");
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
                    put("Action", "Sort Role Asc");
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
             .setEditRedirect(true)
        );
    }

    public static List<Option> getRoleOptions() {
        return new ArrayList<>(List.of(
            createRoleOption(Patient.class),
            createRoleOption(Doctor.class),
            createRoleOption(Pharmacist.class),
            createRoleOption(Admin.class)
        ));
    }

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

    public static List<Option> getGenderOptions() {
        return Arrays.stream(Gender.values())
            .map(gender -> new Option(
                gender.name(),
                OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Blood Type", gender.name());
                }}
            ).setNextAction((formValues) -> {
                formValues.put("gender", gender.name());
                return formValues;
            }).setNextMenuState(MenuState.ADMIN_ADD_DOB))
            .collect(Collectors.toList());
    }

    public static List<Option> getBloodTypeOptions() {
        return Arrays.stream(BloodType.values())
            .map(bloodType -> new Option(
                bloodType.name(),
                Option.OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Blood Type", bloodType.name());
                }}
            ).setNextAction((formValues) -> {
                String input = bloodType.name();
                Patient.create(
                    (String) formValues.get("userName"),
                    (String) formValues.get("password"),
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

    public static List<Option> generateMedicationOptions() {
        List<Option> options = MedicationService.getAllMedications().stream()
            .map(medication -> new Option(
                medication.getName(),
                OptionType.NUMBERED,
                new LinkedHashMap<>() {{
                    put("Medication ID", String.valueOf(medication.getId()));
                    put("Name", medication.getName());
                    put("Stock", String.valueOf(medication.getStock()));
                    put("Low Alert Level", String.valueOf(medication.getLowAlertLevel()));
                }}
            ).setNextMenuState(MenuState.ADMIN_EDIT_MEDICATION)
             .setNextAction((formValues) -> {
                 Map<String, Object> newFormValues = new HashMap<>();
                 newFormValues.put("medication", medication);
                 return newFormValues;
             }))
            .collect(Collectors.toList());
    
        return options;
    }

    public static List<Option> generateMedicationEditOptions(Medication medication) throws Exception {
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

    public static List<Option> getRequestOptions(Control ctl) {
        List<Option> options = MedicationService.getAllMedications().stream()
            .flatMap(medication -> medication.getRequestList().stream()
                .filter(request -> request.getStatus() == Request.Status.PENDING)
                .map(request -> {
                    Option option = new Option(
                        String.valueOf(request.getId()),
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
                        });
                    } else if (ctl == Control.REJECT) {
                        option.setNextAction((formValues) -> {
                            request.setStatus(Request.Status.REJECTED);
                            return null;
                        });
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
    //         OptionType.NUMBERED,
    //         displayFields
    //     ).setNextMenuState(nextMenuState)
    //      .setNextAction((fd) -> {
    //          formData = new HashMap<String, Object>();
    //          formData.put("patientId", Integer.toString(p.getRoleId()));
    //          return formData;
    //      });
    // }
}