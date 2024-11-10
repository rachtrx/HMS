package app.model.user_input;


import app.model.user_input.FunctionalInterfaces.NextAction;
import app.model.user_input.menu_collections.AdminMenuCollection;
import app.model.user_input.menu_collections.MenuCollection;
import app.model.user_input.menu_collections.PatientMenuCollection;
import app.model.user_input.menu_collections.PharmacistMenuCollection;
import app.model.user_input.option_collections.OptionGeneratorCollection;
import app.model.users.Patient;
import app.model.users.staff.Admin;
import app.model.users.staff.Pharmacist;
import app.service.MenuService;
import app.service.UserService;
import java.util.Map;
import java.util.function.Supplier;

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
    SELECT_PATIENT_APPOINTMENT(PatientMenuCollection::getPatientMainMenu), // getSelectPatientAppointmentMenu

    // // DOCTOR
    // DOCTOR_MAIN_MENU(MenuRepository::getDoctorMainMenu),
    // DOCTOR_VIEW_SCHEDULE(MenuRepository::getDoctorViewScheduleMenu),
    // DOCTOR_SET_AVAILABILITY(MenuRepository::getDoctorSetAvailabilityMenu),
    // DOCTOR_ACCEPT_APPOINTMENTS(MenuRepository::getDoctorAcceptAppointmentsMenu),
    // DOCTOR_CANCEL_CONFIRMED(MenuRepository::getDoctorCancelConfirmedMenu),
    // EDIT_PATIENT_APPOINTMENT(MenuRepository::getEditPatientAppointmentMenu),
    // DOCTOR_ACCEPT_OR_DECLINE_APPOINTMENT(MenuRepository::getDoctorAcceptOrDeclineAppointmentMenu),

    // // PHARMACIST
    PHARMACIST_MAIN_MENU(PharmacistMenuCollection::getPharmacistMainMenu),
    PHARMACIST_VIEW_OUTCOME_RECORDS(PharmacistMenuCollection::getPharmacistViewOutcomeRecordsMenu),
    PHARMACIST_UPDATE_OUTCOMES(PharmacistMenuCollection::getPharmacistUpdateOutcomesMenu),
    PHARMACIST_UPDATE_PRESCRIPTIONS(PharmacistMenuCollection::getPharmacistUpdatePrescriptionsMenu),
    PHARMACIST_HANDLE_PRESCRIPTION(PharmacistMenuCollection::getPharmacistHandlePrescriptionMenu),
    PHARMACIST_ADD_REQUEST(PharmacistMenuCollection::getPharmacistAddRequestMenu),
    PHARMACIST_ADD_COUNT(PharmacistMenuCollection::getPharmacistAddCountMenu),

    // // ADMIN
    ADMIN_MAIN_MENU(AdminMenuCollection::getAdminMainMenu),
    ADMIN_VIEW_APPOINTMENTS(AdminMenuCollection::getAdminViewAppointmentsMenu),
    ADMIN_VIEW_USERS(AdminMenuCollection::getAdminViewUsersMenu),
    ADMIN_SELECT_USER_EDIT(AdminMenuCollection::getAdminSelectUserEditMenu),
    ADMIN_SELECT_USER_DELETE(AdminMenuCollection::getAdminSelectUserDeleteMenu),
    ADMIN_EDIT_USER(AdminMenuCollection::getAdminEditUserMenu),
    ADMIN_ADD_USER_TYPE(AdminMenuCollection::getAdminAddUserTypeMenu),
    ADMIN_ADD_USER_NAME(AdminMenuCollection::getAdminAddUserNameMenu),
    ADMIN_ADD_PASSWORD(AdminMenuCollection::getAdminAddPasswordMenu),
    ADMIN_ADD_NAME(AdminMenuCollection::getAdminAddNameMenu),
    ADMIN_ADD_GENDER(AdminMenuCollection::getAdminAddGenderMenu),
    ADMIN_ADD_DOB(AdminMenuCollection::getAdminAddDobMenu),
    ADMIN_ADD_MOBILE_NO(AdminMenuCollection::getAdminAddMobileNoMenu),
    ADMIN_ADD_HOME_NO(AdminMenuCollection::getAdminAddHomeNoMenu),
    ADMIN_ADD_EMAIL(AdminMenuCollection::getAdminAddEmailMenu),
    ADMIN_ADD_BLOODTYPE(AdminMenuCollection::getAdminAddBloodTypeMenu),
    ADMIN_ADD_MEDICATION(AdminMenuCollection::getAdminAddMedicationMenu),
    ADMIN_ADD_INITIAL_STOCK(AdminMenuCollection::getAdminAddInitialStockMenu),
    ADMIN_ADD_LOW_LEVEL_ALERT(AdminMenuCollection::getAdminAddLowLevelAlertMenu),
    ADMIN_EDIT_INVENTORY(AdminMenuCollection::getAdminEditInventoryMenu),
    ADMIN_EDIT_MEDICATION(AdminMenuCollection::getAdminEditMedicationMenu),
    ADMIN_VIEW_REQUEST(AdminMenuCollection::getAdminViewRequestMenu),
    APPROVE_REPLENISH_REQUEST(AdminMenuCollection::getApproveReplenishRequestMenu),
    REJECT_REPLENISH_REQUEST(AdminMenuCollection::getRejectReplenishRequestMenu),

    VIEW_INVENTORY(MenuCollection::getViewInventoryMenu);
    

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
            ((InputMenu) menu).getInput()
                .setNextAction((NextAction) formValues.get("nextAction"))
                .setNextMenuState((MenuState) formValues.get("nextState"))
                .setExitMenuState((MenuState) formValues.get("exitState"));
        } else if (this == MenuState.CONFIRM && menu instanceof OptionMenu) {
            ((OptionMenu) menu).setOptionGenerator(() -> OptionGeneratorCollection.generateConfirmOptions(
                (NextAction) formValues.get("nextAction"), 
                (MenuState) formValues.get("nextState"), 
                (MenuState) formValues.get("exitState")
            ));
        } else if (this == MenuState.INPUT_APPOINTMENT_DAY) {
            menu.setLabel(String.format(
                "Enter a day from %d to %d:",
                Integer.parseInt((String) formValues.get("startDay")),
                Integer.parseInt((String) formValues.get("endDay"))
            ));
        } else if (this == MenuState.ADMIN_ADD_DOB) {
            if (formValues == null || !formValues.containsKey("role")) {
                throw new IllegalArgumentException("User not found");
            } else {
                boolean isPatient = ((String) formValues.get("role")).equals(Patient.class.getSimpleName());
                ((InputMenu) menu).getInput().setNextMenuState(isPatient ? MenuState.ADMIN_ADD_MOBILE_NO : MenuState.ADMIN_VIEW_USERS);
            }
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
        Patient.class, MenuState.PATIENT_MAIN_MENU,
        // Doctor.class, MenuState.DOCTOR_MAIN_MENU,
        Pharmacist.class, MenuState.PHARMACIST_MAIN_MENU,
        Admin.class, MenuState.ADMIN_MAIN_MENU
    );
}
