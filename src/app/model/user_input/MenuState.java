package app.model.user_input;


import app.model.user_input.FunctionalInterfaces.NextAction;
import app.model.user_input.menu_collections.AdminMenuCollection;
import app.model.user_input.menu_collections.DoctorMenuCollection;
import app.model.user_input.menu_collections.MenuCollection;
import app.model.user_input.menu_collections.PatientMenuCollection;
import app.model.user_input.menu_collections.PharmacistMenuCollection;
import app.model.user_input.option_collections.OptionGeneratorCollection;
import app.model.users.Patient;
import app.model.users.staff.Admin;
import app.model.users.staff.Doctor;
import app.model.users.staff.Pharmacist;
import app.service.MenuService;
import app.service.UserService;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum MenuState {

    EDIT(MenuCollection::getEditMenu),
    CONFIRM(MenuCollection::getConfirmMenu),
    LANDING(MenuCollection::getLandingMenu),
    LOGIN_USERNAME(MenuCollection::getLoginUsernameMenu),
    LOGIN_PASSWORD(MenuCollection::getLoginPasswordMenu),
    TIMESLOT_SELECTION_TYPE(MenuCollection::getTimeSlotSelectionMenu), // 
    INPUT_APPOINTMENT_YEAR(MenuCollection::getInputAppointmentYearMenu),
    INPUT_APPOINTMENT_MONTH(MenuCollection::getInputAppointmentMonthMenu),
    INPUT_APPOINTMENT_DAY(MenuCollection::getInputAppointmentDayMenu),
    INPUT_APPOINTMENT_HOUR(MenuCollection::getInputAppointmentHourMenu),
    VIEW_INVENTORY(MenuCollection::getViewInventoryMenu),
    CHANGE_PASSWORD(MenuCollection::getChangePasswordMenu),

    // PATIENT
    PATIENT_MAIN_MENU(PatientMenuCollection::getPatientMainMenu),
    PATIENT_VIEW_MEDICAL_RECORD(PatientMenuCollection::getPatientViewMedicalRecordMenu), // 
    PATIENT_EDIT_MEDICAL_RECORD(PatientMenuCollection::getPatientEditMedicalRecordMenu),
    PATIENT_VIEW_AVAIL_APPOINTMENTS(PatientMenuCollection::getPatientViewAvailAppointmentsMenu),
    PATIENT_VIEW_AVAIL_APPOINTMENTS_DOCTOR(PatientMenuCollection::getPatientViewAvailAppointmentsDoctorMenu),
    PATIENT_VIEW_CONFIRMED_APPOINTMENTS(PatientMenuCollection::getPatientViewConfirmedAppointmentsMenu),
    
    INPUT_DOCTOR(PatientMenuCollection::getDoctorSelectionMenu),
    INPUT_APPOINTMENT_DOCTOR(PatientMenuCollection::getAppointmentSelectDoctorMenu),
    PATIENT_RESCHEDULE_SELECTION(PatientMenuCollection::getPatientRescheduleSelectionMenu),
    PATIENT_CANCEL_SELECTION(PatientMenuCollection::getPatientCancelSelectionMenu),
    PATIENT_VIEW_OUTCOMES(PatientMenuCollection::getPatientViewOutcomesMenu),
    
    // // DOCTOR
    DOCTOR_MAIN_MENU(DoctorMenuCollection::getDoctorMainMenu),
    // MOVE THIS INTO OPTIONS OF UPCOMING // DOCTOR_SET_UNAVAILABILITY(DoctorMenuCollection::getDoctorSetAvailabilityMenu),
    DOCTOR_VIEW_UPCOMING_EVENTS(DoctorMenuCollection::getDoctorViewEventsMenu), // VIEW SCHEDULE
    // Availability
    DOCTOR_VIEW_UPCOMING_UNAVAILABILITY(DoctorMenuCollection::getDoctorViewUnAvailMenu), // ADD option redirects to timeslot menu
    DOCTOR_EDIT_UPCOMING_UNAVAILABILITY(DoctorMenuCollection::getDoctorEditUnAvailMenu),
    DOCTOR_DELETE_UPCOMING_UNAVAILABILITY(DoctorMenuCollection::getDoctorDelUnAvailMenu),
    
    DOCTOR_VIEW_UPCOMING_APPOINTMENTS(DoctorMenuCollection::getDoctorViewApptMenu),
    DOCTOR_CANCEL_UPCOMING_APPOINTMENTS(DoctorMenuCollection::getDoctorCancelApptMenu),
    DOCTOR_HANDLE_UPCOMING_APPOINTMENTS(DoctorMenuCollection::getDoctorHandleApptsMenu),
    DOCTOR_HANDLE_UPCOMING_APPOINTMENT(DoctorMenuCollection::getDoctorHandleApptMenu), // choose accept or reject
    // VIEW OR SELECT THE MEDICAL RECORD
    DOCTOR_VIEW_PAST_PATIENTS(DoctorMenuCollection::getSelectPatientViewMenu),
    DOCTOR_EDIT_PAST_PATIENT(DoctorMenuCollection::getPatientEditMenu), // select the patient to edit, put into args
    // given the patient
    DOCTOR_VIEW_PAST_APPOINTMENTS(DoctorMenuCollection::getDoctorPastApptViewMenu), // IMPT select the record to view, to include edit option and add option
    DOCTOR_VIEW_RECORDS(DoctorMenuCollection::getDoctorOutcomesViewMenu),
    VIEW_RECORD(DoctorMenuCollection::getOutcomeViewMenu), // to include edit options

    DOCTOR_ADD_RECORDS(DoctorMenuCollection::getDoctorOutcomesAddMenu), // select the record to add
    DOCTOR_ADD_SERVICE_TYPE(DoctorMenuCollection::getDoctorServiceAddMenu),
    DOCTOR_ADD_NOTES(DoctorMenuCollection::getDoctorNotesAddMenu),
    DOCTOR_ADD_MEDICATION(DoctorMenuCollection::getDoctorMedicationAddMenu),
    DOCTOR_ADD_QUANTITY(DoctorMenuCollection::getDoctorQuantityAddMenu),

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
    REJECT_REPLENISH_REQUEST(AdminMenuCollection::getRejectReplenishRequestMenu);

    private static final Map<Class<?>, MenuState> USER_MENU_MAP = Map.of(
        Patient.class, MenuState.PATIENT_MAIN_MENU,
        Doctor.class, MenuState.DOCTOR_MAIN_MENU,
        Pharmacist.class, MenuState.PHARMACIST_MAIN_MENU,
        Admin.class, MenuState.ADMIN_MAIN_MENU
    );

    public static final Set<MenuState> RESET_MENUS = Stream.concat(
        USER_MENU_MAP.values().stream(),
        Stream.of(MenuState.LOGIN_USERNAME, MenuState.LANDING)
    ).collect(Collectors.toSet());
    private final Supplier<Menu> menuProvider;

    MenuState(Supplier<Menu> menuProvider) {
        this.menuProvider = menuProvider;
    }

    public boolean isResetMenu() {
        return RESET_MENUS.contains(this);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Menu getMenu(Map<String, Object> formValues) {

        // Add in any middleware here

        Menu menu = menuProvider.get().setMenuState(this);

        if(menu == null) {
            System.out.println("No Menu Found for" + this);
            return MenuService.getCurrentMenu();
        } else if (this == MenuState.EDIT && menu instanceof InputMenu) {
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
                "Enter a day from %s to %s:",
                (String) formValues.get("startDay"),
                (String) formValues.get("endDay")
            ));
        } else if (this == MenuState.ADMIN_ADD_DOB) {
            if (formValues == null || !formValues.containsKey("role")) {
                throw new IllegalArgumentException("User not found");
            } else {
                boolean isPatient = ((String) formValues.get("role")).equals(Patient.class.getSimpleName());
                ((InputMenu) menu).getInput().setNextMenuState(isPatient ? MenuState.ADMIN_ADD_MOBILE_NO : MenuState.ADMIN_VIEW_USERS);
            }
        } else if (this == MenuState.LOGIN_PASSWORD) {
            if (UserService.getCurrentUser() != null) {
                menu.setLabel("Please enter your new password");
                ((InputMenu) menu).getInput().setExitMenuState(MenuState.LOGIN_PASSWORD);
            } else {
                menu.setLabel("Please enter your password");
                ((InputMenu) menu).getInput().setExitMenuState(MenuState.LOGIN_USERNAME);
            };
        } else if (RESET_MENUS.contains(this)) {
            menu.setPreviousMenu(null);
        }
        return menu.setFormData(formValues);
    }

    public static MenuState getUserMainMenuState() {
        try {
            Class<?> userClass = UserService.getCurrentUser().getClass();
            MenuState mainMenuState = USER_MENU_MAP.getOrDefault(userClass, MenuState.LANDING); // Set default as LANDING
            return mainMenuState; // Use mainMenuState to retrieve the menu
        } catch (Exception e) {
            UserService.logout();
            return MenuState.LOGIN_USERNAME; // Return Menu instance instead of enum
        }
    }
}
