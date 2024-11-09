package app.model.user_input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import app.constants.exceptions.ExitApplication;
import app.model.user_input.FunctionalInterfaces.NextAction;
import app.model.user_input.Option;
import app.model.user_input.old.Menu;
import app.model.users.Patient;
import app.service.MenuService;
import app.service.UserService;
import app.utils.DateTimeUtil;
import java.util.Arrays;

public class OptionGeneratorCollection {

    public static OptionTable generateConfirmOptions(NextAction nextAction, MenuState nextState, MenuState exitState) {
    
        List<String> unNumberedHeaders = Arrays.asList("Letter", "Action");
        List<Integer> unNumberedColumnWidths = Arrays.asList(10, 20);
    
        // Define options for confirmation
        Option yesOption = new Option("Y", "yes|y|yes( )?\\(?y\\)?", false)
            .setNextMenuState(nextState)
            .setNextAction(nextAction);
        
        Option noOption = new Option("N", "no|n|no( )?\\(?n\\)?", false)
            .setNextMenuState(exitState)
            .setNextAction((formData) -> formData);
    
        List<Option> numberedOptions = new ArrayList<>();
        List<Option> unNumberedOptions = Arrays.asList(yesOption, noOption);
    
        OptionTable optionTable = new OptionTable(new ArrayList<>(), new ArrayList<>(), numberedOptions, unNumberedOptions);
    
        optionTable.setUnNumberedHeaders(unNumberedHeaders, unNumberedColumnWidths);
    
        return optionTable;
    }

    public static List<Option> generateLogoutAndExitOptions() {
        List<Option> options = new ArrayList<>();

        // Logout option display fields
        Map<String, String> logoutDisplayFields = new LinkedHashMap<>();
        logoutDisplayFields.put("Key", "Logout (LO)");
        logoutDisplayFields.put("Action", "Log out of the application");

        // Logout option
        options.add(new Option(
            "Logout (LO)",
            "^LO$|log( )?out(( )?\\(LO\\))?",
            false,
            logoutDisplayFields
        ).setNextAction((formData) -> {
            UserService.logout();
            MenuService.getCurrentMenu().displayMode = DisplayMode.INITIAL;
            return null;
        }).setNextMenuState(Menu.LOGIN_USERNAME));

        // Exit option display fields
        Map<String, String> exitDisplayFields = new LinkedHashMap<>();
        exitDisplayFields.put("Key", "Exit Application (E)");
        exitDisplayFields.put("Action", "Exit the application");

        // Exit option
        options.add(new Option(
            "Exit Application (E)",
            "^E$|exit(( )?((app)?plication)?)?(( )?\\(E\\))?",
            false,
            exitDisplayFields
        ).setNextAction((input, args) -> {
            throw new ExitApplication();
        }));

        return options;
    }

    public static List<Option> generatePatientMenuOptions() {
        return new ArrayList<>(List.of(
            new Option(
                    "View Medical Record", 
                    "(view( )?)?medical(( )?record)?", 
                    true
                ).setNextMenuState(MenuState.PATIENT_MAIN_MENU), // PATIENT_VIEW_MEDICAL_RECORD
            // new Option(
            //         "Edit Contact Information", 
            //         "(edit( )?)?contact(( )?info(rmation)?)?", 
            //         true
            //     ).setNextMenuState(MenuState.PATIENT_EDIT_MEDICAL_RECORD),
            // new Option(
            //         "View Available Appointments", 
            //         "view( )?(available( )?)?appointment(s)?", 
            //         true
            //     ).setNextMenuState(MenuState.PATIENT_VIEW_AVAIL_APPOINTMENTS),
            // new Option(
            //         "Schedule an Appointment", 
            //         "^schedule( )?(a(n)?( )?)?appointment(s)?",
            //         true
            //     ).setNextMenuState(MenuState.PATIENT_APPOINTMENT_SELECTION_TYPE)
            //     .setNextAction((formData) -> new HashMap<String, Object>() {{
            //             put("yearValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
            //             put("monthValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
            //             put("dayValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
            //             put("hourValidator", DateTimeUtil.DateConditions.FUTURE.toString());
            //         }}
            //     ),
            // new Option(
            //         "Reschedule an Appointment", 
            //         "^Reschedule( )?(a(n)?( )?)?appointment(s)?",
            //         true
            //     ).setNextMenuState(MenuState.PATIENT_RESCHEDULE_SELECTION)
            //     .setNextAction((formData) -> new HashMap<String, Object>() {{
            //             put("yearValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
            //             put("monthValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
            //             put("dayValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
            //             put("hourValidator", DateTimeUtil.DateConditions.FUTURE.toString());
            //         }}
            //     ),
            // new Option(
            //         "Cancel an Appointment", 
            //         "^Cancel( )?(a(n)?( )?)?appointment(s)?",
            //         true
            //     ).setNextMenuState(MenuState.PATIENT_CANCEL_SELECTION)
            //     .setNextAction((formData) -> new HashMap<String, Object>()),
            // new Option(
            //         "View Scheduled Appointments", 
            //         "view( )?(scheduled( )?)?appointment(s)?|(view( )?)?confirmed", 
            //         true
            //     ).setNextMenuState(MenuState.PATIENT_VIEW_CONFIRMED_APPOINTMENTS),
            // new Option(
            //         "View Appointment Outcomes", 
            //         "view( )?(appointment( )?)?outcomes(s)?|(view( )?)?history", 
            //         true
            //     ).setNextMenuState(MenuState.PATIENT_VIEW_OUTCOMES)
        ));
    }

    public static List<Option> generateEditPatientDetailsOptions(Patient patient) {
        List<Option> options = new ArrayList<>();

        // Option for editing the patient's blood type
        options.add(new Option(
            String.format("Blood type: %s", patient.getBloodType()),
            "blood|type|blood(( )?type)?|(blood( )?)?type",
            true
        ).setNextAction((formData) -> {
            args.set("nextMenu", )
            patient.setBloodType((String) input);
            return null;
        }).setNextMenuState(Menu.EDIT)); // Redirect to the Edit menu after action

        // Option for editing appointments
        options.add(new Option(
            "Edit Appointment",
            "(edit( )?)?appointment",
            true
        ).setNextMenuState(MenuState.Menu.SELECT_PATIENT_APPOINTMENT)
         .setNextAction((userInput, args) -> args)); // Forward args if needed

        return options;
    }

    public static List<Option> generatePatientOptions(NewMenu nextMenu) {
        return UserService.getAllUserByType(Patient.class)
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
                ).setNextMenuState(nextMenu)
                .setNextAction((userInput, args) -> {
                    var actionData = new HashMap<String, Object>();
                    actionData.put("patientId", Integer.toString(patient.getRoleId()));
                    return actionData;
                })
            )
            .collect(Collectors.toList());
    }
}