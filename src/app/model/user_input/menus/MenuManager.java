package app.model.user_input.menus;

public class MenuManager {
    private static final Map<String, Supplier<BaseMenu>> menuMap = new HashMap<>();

    public static BaseMenu getInitialMenu(Patient patient) {
        return new PatientMenu();
    }

    public static BaseMenu getInitialMenu(Doctor doctor) {
        return new DoctorMenu();
    }

    public static BaseMenu getInitialMenu(Pharmacist pharmacist) {
        return new PharmacistMenu();
    }

    public static BaseMenu getInitialMenu(Admin admin) {
        return new AdminMenu();
    }
}
