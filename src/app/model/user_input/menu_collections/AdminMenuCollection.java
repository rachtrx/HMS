package app.model.user_input.menu_collections;

import app.model.appointments.Appointment;
import app.model.inventory.Medication;
import app.model.user_input.InputMenu;
import app.model.user_input.MenuState;
import app.model.user_input.NewMenu;
import app.model.user_input.OptionMenu;
import app.model.user_input.option_collections.OptionGeneratorCollection;
import app.model.user_input.option_collections.OptionGeneratorCollection.Control;
import app.model.users.Patient;
import app.model.users.User;
import app.model.users.staff.Admin;
import app.model.users.staff.Doctor;
import app.model.users.staff.Pharmacist;
import app.model.users.staff.Staff;
import app.model.users.user_credentials.Email;
import app.model.users.user_credentials.Password;
import app.model.users.user_credentials.PhoneNumber;
import app.model.users.user_credentials.Username;
import app.service.AppointmentService;
import app.service.UserService;
import app.service.UserService.SortFilter;
import app.utils.DateTimeUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminMenuCollection {
    
    public static NewMenu getAdminMainMenu() {
        return new OptionMenu("Admin Main Menu", null)
            .setOptionGenerator(OptionGeneratorCollection::generateAdminMainMenuOptions)
            .shouldAddLogoutOptions().shouldAddMainMenuOption();
    }
    public static NewMenu getAdminViewAppointmentsMenu() {
        return new OptionMenu("All Appointments", null)
            .setOptionGenerator(() -> {
                List<Appointment> appointments = AppointmentService.getAllAppointments()
                    .stream()
                    .collect(Collectors.toList());
                if (!appointments.isEmpty()) {
                    return OptionGeneratorCollection.generateAdminAppointmentsView(appointments);
                } else {
                    System.out.println("No appointments scheduled.\n");
                    return new ArrayList<>();
                }
            });
    }
    public static NewMenu getAdminViewUsersMenu() {
        OptionMenu menu = new OptionMenu("All Staff", null);
        return setUserOptionGenerator(menu, Control.NONE);
    }

    public static NewMenu getAdminSelectUserEditMenu() {
        OptionMenu menu = new OptionMenu("Select a Staff to edit", null);
        return setUserOptionGenerator(menu, Control.EDIT); 
    }
    public static NewMenu getAdminSelectUserDeleteMenu() {
        OptionMenu menu = new OptionMenu("Select a Staff to delete", null);
        return setUserOptionGenerator(menu, Control.DELETE);
    }

    private static NewMenu setUserOptionGenerator(OptionMenu menu, Control ctl) {
        menu
            .setOptionGenerator(() -> {
                List<Staff> sortedUsers;
                SortFilter filter = SortFilter.ROLE;
                boolean asc = true;
                Map<String, Object> formValues = menu.getFormData();
                if (
                    formValues != null && formValues.containsKey("filter") && formValues.containsKey("asc")
                ) {
                    filter = (SortFilter) formValues.get("filter");
                    asc = (Boolean) formValues.get("asc");
                } else {
                    menu.getFormData().put("filter", filter);
                    menu.getFormData().put("asc", asc);
                }
                sortedUsers = UserService.getSortedUsers(filter, asc);
                return OptionGeneratorCollection.generateStaffListView(sortedUsers, filter, asc, ctl);
            }).shouldAddLogoutOptions().shouldAddMainMenuOption();

        return menu;
    }

    public static NewMenu getAdminEditUserMenu() {
        OptionMenu menu = new OptionMenu("User details", "Select a field to edit");
        menu.setOptionGenerator(() -> {
            Map<String, Object> formValues = menu.getFormData();
            if (formValues == null || !formValues.containsKey("user")) throw new IllegalArgumentException("Staff not found");
            Staff staff = (Staff) formValues.get("user");
            return OptionGeneratorCollection.generateUserFieldsEditOptions(staff);
        });
        return menu;
    }
    public static NewMenu getAdminAddUserTypeMenu() {
        System.out.println("Creating user type menu");
        return new OptionMenu("User roles", "Select a role").setOptionGenerator(
            OptionGeneratorCollection::getRoleOptions
        ).shouldAddMainMenuOption();
    }
    public static NewMenu getAdminAddUserNameMenu() {
        InputMenu menu = new InputMenu("Username", "Enter Username").setParseUserInput(false);

        menu.getInput()
            .setNextAction((formValues) -> {
                String input = (String) menu.getFormData().get("input");
                Username u = new Username(input);
                formValues.put("username", input);
                return formValues;
            })
            .setNextMenuState(MenuState.ADMIN_ADD_PASSWORD);

        return menu;
    }
    public static NewMenu getAdminAddPasswordMenu() {
        InputMenu menu = new InputMenu("Password", "Enter Password").setParseUserInput(false);

        menu.getInput()
            .setNextAction((formValues) -> {
                String input = (String) menu.getFormData().get("input");
                Password p = new Password(input);
                formValues.put("password", input);
                return formValues;
            })
            .setNextMenuState(MenuState.ADMIN_ADD_NAME);

        return menu;
    }
    public static NewMenu getAdminAddNameMenu() {
        InputMenu menu = new InputMenu("Name", "Enter Name");

        menu.getInput()
            .setNextAction((formValues) -> {
                String input = (String) menu.getFormData().get("input");
                formValues.put("name", input);
                return formValues;
            })
            .setNextMenuState(MenuState.ADMIN_ADD_GENDER);
        
        return menu;
    }
    public static NewMenu getAdminAddGenderMenu() {
        return new OptionMenu("Gender", "Enter Gender")
            .setOptionGenerator(OptionGeneratorCollection::getGenderOptions);
    }

    public static NewMenu getAdminAddDobMenu() {
        InputMenu menu = new InputMenu("Date of Birth", "Enter Date of Birth");
    
        menu.getInput()
            .setNextAction(formValues -> {
                String input = (String) menu.getFormData().get("input");
                String role = (String) formValues.get("role");

                LocalDate dob = DateTimeUtil.parseShortDate(input);
                if (dob.isAfter(LocalDate.now())) {
                    throw new IllegalArgumentException("Date of Birth cannot be in the future.");
                }
    
                if (role.equals(Patient.class.getSimpleName())) {
                    formValues.put("dob", input);
                    return formValues;
                }

                String userName = (String) formValues.get("username");
                String password = (String) formValues.get("password");
                String name = (String) formValues.get("name");
                String gender = (String) formValues.get("gender");
    
                // Unified handling for Doctor, Pharmacist, and Admin roles
                User user = switch (role) {
                    case "Doctor" -> Doctor.create(userName, password, name, gender, input);
                    case "Pharmacist" -> Pharmacist.create(userName, password, name, gender, input);
                    case "Admin" -> Admin.create(userName, password, name, gender, input);
                    default -> throw new Exception("Type not found!");
                };
                
                UserService.addUsers(List.of(user));
                return null;
            });
        
        return menu;
    }

    public static NewMenu getAdminAddMobileNoMenu() {
        InputMenu menu = new InputMenu("Mobile No.", "Enter Mobile No.");

        menu.getInput()
            .setNextAction((formValues) -> {
                String input = (String) menu.getFormData().get("input");
                PhoneNumber mNumber = new PhoneNumber(input);
                formValues.put("mobile", input);
                return formValues;
            })
            .setNextMenuState(MenuState.ADMIN_ADD_HOME_NO);
        
        return menu;
    }
    public static NewMenu getAdminAddHomeNoMenu() {
        InputMenu menu = new InputMenu("Home No.", "Enter Home No.");

        menu.getInput()
            .setNextAction((formValues) -> {
                String input = (String) menu.getFormData().get("input");
                PhoneNumber hNumber = new PhoneNumber(input);
                formValues.put("home", input);
                return formValues;
            })
            .setNextMenuState(MenuState.ADMIN_ADD_EMAIL);

        return menu;
    }
    public static NewMenu getAdminAddEmailMenu() {
        InputMenu menu = new InputMenu("Email", "Enter Email");

        menu.getInput()
            .setNextAction((formValues) -> {
                String input = (String) menu.getFormData().get("input");
                Email email = new Email(input);
                formValues.put("email", input);
                return formValues;
            })
            .setNextMenuState(MenuState.ADMIN_ADD_BLOODTYPE);
        return menu;
    }
    
    public static NewMenu getAdminAddBloodTypeMenu() {
        return new OptionMenu("Blood Type", "Enter Blood Type")
            .setOptionGenerator(OptionGeneratorCollection::getBloodTypeOptions);
    }

    public static NewMenu getAdminAddMedicationMenu() {
        InputMenu menu = new InputMenu("New Medication Name", "Enter new medication name");

        menu.getInput()
            .setNextAction((formValues) -> {
                String input = (String) menu.getFormData().get("input");
                formValues.put("name", input);
                return formValues;
            })
            .setNextMenuState(MenuState.ADMIN_ADD_INITIAL_STOCK);

        return menu;
    }

    public static NewMenu getAdminAddInitialStockMenu() {
        InputMenu menu = new InputMenu("Stock level", "Set stock level");

        menu.getInput()
            .setNextAction((formValues) -> {
                String input = (String) menu.getFormData().get("input");
                formValues.put("stock", input);
                return formValues;
            })
            .setNextMenuState(MenuState.ADMIN_ADD_LOW_LEVEL_ALERT);

        return menu;
    }

    public static NewMenu getAdminAddLowLevelAlertMenu() {
        InputMenu menu = new InputMenu("Low level alert", "Set low level alert");

        menu.getInput()
            .setNextAction((formValues) -> {
                String input = (String) menu.getFormData().get("input");
                formValues.put("stock", input);
                return formValues;
            })
            .setNextMenuState(MenuState.VIEW_INVENTORY);

        return menu;
    }

    public static NewMenu getAdminEditInventoryMenu() {
        OptionMenu menu = new OptionMenu("Update Inventory", "Select a medication to edit");
        MenuCollection.setOptionGeneratorForInventory(menu);
        return menu.shouldAddLogoutOptions().shouldAddMainMenuOption();
    }

    public static NewMenu getAdminEditMedicationMenu() {
        OptionMenu menu = new OptionMenu("Medication details", "Select a field to edit");
        menu
            .setOptionGenerator(() -> {
                Map<String, Object> formValues = menu.getFormData();

                if (formValues == null || !formValues.containsKey("medication")) {
                    throw new IllegalArgumentException("Medication not found");
                }
                return OptionGeneratorCollection.generateMedicationEditOptions((Medication) formValues.get("medication"));
            }).shouldAddLogoutOptions().shouldAddMainMenuOption();

        return menu;
    }
    public static NewMenu getAdminViewRequestMenu() {
        return new OptionMenu("Replenish requests", "Select a request to approve or reject")
            .shouldAddLogoutOptions()
            .shouldAddMainMenuOption()
            .setOptionGenerator(() -> OptionGeneratorCollection.getRequestOptions(Control.NONE));
        
    }
    public static NewMenu getApproveReplenishRequestMenu() {
        return new OptionMenu("Handle request", null)
            .shouldAddLogoutOptions()
            .shouldAddMainMenuOption()
            .setOptionGenerator(() -> OptionGeneratorCollection.getRequestOptions(Control.APPROVE));
    }

    public static NewMenu getRejectReplenishRequestMenu() {
        return new OptionMenu("Handle request", null)
            .shouldAddLogoutOptions()
            .shouldAddMainMenuOption()
            .setOptionGenerator(() -> OptionGeneratorCollection.getRequestOptions(Control.REJECT));
    }
}
