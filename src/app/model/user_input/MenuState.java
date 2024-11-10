package app.model.user_input;


import app.model.user_input.InputMenu;
import app.model.user_input.NewMenu;
import app.model.user_input.OptionMenu;
import app.model.user_input.NewMenu;
import app.model.users.Patient;
import app.model.users.staff.Admin;
import app.model.users.staff.Doctor;
import app.model.users.staff.Pharmacist;
import app.service.UserService;


import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import app.model.user_input.NewMenu;
import app.model.user_input.FunctionalInterfaces.NextAction;
import app.model.user_input.FunctionalInterfaces.OptionGenerator;
import app.model.user_input.option_collections.OptionGeneratorCollection;
import app.model.user_input.menu_collections.PatientMenuCollection;
import app.model.user_input.menu_collections.MenuCollection;
import app.service.MenuService;

import java.util.HashMap;

public enum MenuState {
    EDIT(MenuCollection::getEditMenu),
    CONFIRM(MenuCollection::getConfirmMenu),
    LANDING(MenuCollection::getLandingMenu),
    LOGIN_USERNAME(MenuCollection::getLoginUsernameMenu),
    LOGIN_PASSWORD(MenuCollection::getLoginPasswordMenu),

    // PATIENT
    PATIENT_MAIN_MENU(PatientMenuCollection::getPatientMainMenu),
    // SELECT_PATIENT_VIEW_MEDICAL_RECORD(MenuRepository::getSelectPatientViewMedicalRecordMenu),
    // SELECT_PATIENT_EDIT_MEDICAL_RECORD(MenuRepository::getSelectPatientEditMedicalRecordMenu),
    PATIENT_VIEW_MEDICAL_RECORD(PatientMenuCollection::getPatientViewMedicalRecordMenu), // 
    PATIENT_EDIT_MEDICAL_RECORD(PatientMenuCollection::getPatientEditMedicalRecordMenu),
    PATIENT_VIEW_AVAIL_APPOINTMENTS(PatientMenuCollection::getPatientViewAvailAppointmentsMenu),
    PATIENT_VIEW_CONFIRMED_APPOINTMENTS(PatientMenuCollection::getPatientViewConfirmedAppointmentsMenu),
    PATIENT_APPOINTMENT_SELECTION_TYPE(PatientMenuCollection::getPatientAppointmentSelectionTypeMenu), // 
    INPUT_APPOINTMENT_YEAR(PatientMenuCollection::getInputAppointmentYearMenu),
    INPUT_APPOINTMENT_MONTH(PatientMenuCollection::getInputAppointmentMonthMenu),
    INPUT_APPOINTMENT_DAY(PatientMenuCollection::getInputAppointmentDayMenu),
    INPUT_APPOINTMENT_HOUR(PatientMenuCollection::getInputAppointmentHourMenu),
    INPUT_APPOINTMENT_DOCTOR(PatientMenuCollection::getInputAppointmentDoctorMenu),
    PATIENT_RESCHEDULE_SELECTION(PatientMenuCollection::getPatientRescheduleSelectionMenu),
    PATIENT_CANCEL_SELECTION(PatientMenuCollection::getPatientCancelSelectionMenu),
    PATIENT_VIEW_OUTCOMES(PatientMenuCollection::getPatientViewOutcomesMenu),

    // // DOCTOR
    // DOCTOR_MAIN_MENU(MenuRepository::getDoctorMainMenu),
    // DOCTOR_VIEW_SCHEDULE(MenuRepository::getDoctorViewScheduleMenu),
    // DOCTOR_SET_AVAILABILITY(MenuRepository::getDoctorSetAvailabilityMenu),
    // DOCTOR_ACCEPT_APPOINTMENTS(MenuRepository::getDoctorAcceptAppointmentsMenu),
    // DOCTOR_CANCEL_CONFIRMED(MenuRepository::getDoctorCancelConfirmedMenu),
    SELECT_PATIENT_APPOINTMENT(PatientMenuCollection::getPatientMainMenu); // getSelectPatientAppointmentMenu
    // EDIT_PATIENT_APPOINTMENT(MenuRepository::getEditPatientAppointmentMenu),
    // DOCTOR_ACCEPT_OR_DECLINE_APPOINTMENT(MenuRepository::getDoctorAcceptOrDeclineAppointmentMenu),

    // // PHARMACIST
    // PHARMACIST_MAIN_MENU(MenuRepository::getPharmacistMainMenu),
    // PHARMACIST_VIEW_OUTCOME_RECORDS(MenuRepository::getPharmacistViewOutcomeRecordsMenu),
    // PHARMACIST_UPDATE_OUTCOMES(MenuRepository::getPharmacistUpdateOutcomesMenu),
    // PHARMACIST_UPDATE_PRESCRIPTIONS(MenuRepository::getPharmacistUpdatePrescriptionsMenu),
    // PHARMACIST_HANDLE_PRESCRIPTION(MenuRepository::getPharmacistHandlePrescriptionMenu),
    // PHARMACIST_ADD_REQUEST(MenuRepository::getPharmacistAddRequestMenu),
    // PHARMACIST_ADD_COUNT(MenuRepository::getPharmacistAddCountMenu),

    // // ADMIN
    // ADMIN_MAIN_MENU(MenuRepository::getAdminMainMenu),
    // ADMIN_VIEW_APPOINTMENTS(MenuRepository::getAdminViewAppointmentsMenu),
    // ADMIN_VIEW_USERS(MenuRepository::getAdminViewUsersMenu),
    // ADMIN_INPUT_USER_EDIT(MenuRepository::getAdminInputUserEditMenu),
    // ADMIN_INPUT_USER_DELETE(MenuRepository::getAdminInputUserDeleteMenu),
    // ADMIN_EDIT_USER(MenuRepository::getAdminEditUserMenu),
    // ADMIN_ADD_USER_TYPE(MenuRepository::getAdminAddUserTypeMenu),
    // ADMIN_ADD_USER_NAME(MenuRepository::getAdminAddUserNameMenu),
    // ADMIN_ADD_PASSWORD(MenuRepository::getAdminAddPasswordMenu),
    // ADMIN_ADD_NAME(MenuRepository::getAdminAddNameMenu),
    // ADMIN_ADD_GENDER(MenuRepository::getAdminAddGenderMenu),
    // ADMIN_ADD_MOBILE_NO(MenuRepository::getAdminAddMobileNoMenu),
    // ADMIN_ADD_HOME_NO(MenuRepository::getAdminAddHomeNoMenu),
    // ADMIN_ADD_EMAIL(MenuRepository::getAdminAddEmailMenu),
    // ADMIN_ADD_DOB(MenuRepository::getAdminAddDobMenu),
    // ADMIN_ADD_BLOODTYPE(MenuRepository::getAdminAddBloodTypeMenu),
    // VIEW_INVENTORY(MenuRepository::getViewInventoryMenu),
    // ADMIN_UPDATE_INVENTORY(MenuRepository::getAdminUpdateInventoryMenu),
    // ADMIN_ADD_MEDICATION(MenuRepository::getAdminAddMedicationMenu),
    // ADMIN_ADD_INITIAL_STOCK(MenuRepository::getAdminAddInitialStockMenu),
    // ADMIN_ADD_LOW_LEVEL_ALERT(MenuRepository::getAdminAddLowLevelAlertMenu),
    // ADMIN_EDIT_MEDICATION(MenuRepository::getAdminEditMedicationMenu),
    // ADMIN_VIEW_REQUEST(MenuRepository::getAdminViewRequestMenu),
    // HANDLE_REPLENISH_REQUEST(MenuRepository::getHandleReplenishRequestMenu);

    private final Supplier<NewMenu> menuProvider;

    MenuState(Supplier<NewMenu> menuProvider) {
        this.menuProvider = menuProvider;
    }

    public NewMenu getMenu(Map<String, Object> formValues) {

        // Add in any middleware here

        NewMenu menu = menuProvider.get().setMenuState(this);

        if(menu == null) {
            System.out.println("No Menu Found for" + this);
            return MenuService.getCurrentMenu();
        }
    
        if (this == MenuState.EDIT && menu instanceof InputMenu) {
            InputMenu inputMenu = (InputMenu) menu;
            inputMenu.getInput()
                .setNextAction((NextAction) formValues.get("nextAction"))
                .setNextMenuState((MenuState) formValues.get("nextState"))
                .setExitMenuState((MenuState) formValues.get("exitState"));
    
            return inputMenu; // Return InputMenu if this is the EDIT state
        } else if (this == MenuState.CONFIRM && menu instanceof OptionMenu) {
            OptionMenu optionMenu = (OptionMenu) menu;
            optionMenu.setOptionGenerator(() -> OptionGeneratorCollection.generateConfirmOptions(
                (NextAction) formValues.get("nextAction"), 
                (MenuState) formValues.get("nextState"), 
                (MenuState) formValues.get("exitState")
            ));
            return optionMenu;
        } else if (this == MenuState.INPUT_APPOINTMENT_DAY) {
            menu.setLabel(String.format(
                "Enter a day from %d to %d:",
                Integer.parseInt((String) args.get("startDay")),
                Integer.parseInt((String) args.get("endDay"))
            ));
        }
        return menu.setFormData(formValues);
    }

    public static MenuState getUserMainMenuState() {
        try {
            System.out.println("Getting User Main Menu");
            Class<?> userClass = UserService.getCurrentUser().getClass();
            MenuState mainMenuState = USER_MENU_MAP.getOrDefault(userClass, MenuState.LANDING); // Set default as LANDING
            return mainMenuState; // Use mainMenuState to retrieve the menu
        } catch (Exception e) {
            UserService.logout();
            return MenuState.LOGIN_USERNAME; // Return NewMenu instance instead of enum
        }
    }

    private static final Map<Class<?>, MenuState> USER_MENU_MAP = Map.of(
        Patient.class, MenuState.PATIENT_MAIN_MENU
        // Doctor.class, MenuState.DOCTOR_MAIN_MENU,
        // Pharmacist.class, MenuState.PHARMACIST_MAIN_MENU,
        // Admin.class, MenuState.ADMIN_MAIN_MENU
    );
}
