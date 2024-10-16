package app.model.users.staff;

public class Admin extends Staff {

    public Admin(String adminId, String name, char gender, String age) {
        super(adminId, name, gender, age, adminId);
        this.staffRole = 'A';
    }

    public void displayUserMenu() {
        // TODO
    }
}