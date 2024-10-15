package app.model.users.staff;

public class Pharmacist extends Staff {

    public Pharmacist(String pharmacistId, String name, char gender, String age) {
        super(pharmacistId, name, gender, age, pharmacistId);
        this.staffRole = 'P';
    }

    public void displayUserMenu() {
        // TODO
    }
}