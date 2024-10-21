package app.model.user_input.menus_test;

public class TestMenuService {
    
    public static enum MenuStatus {
        CREATE_APPOINTMENT {
            @Override
            public String toString() {
                return "CREATE_APPOINTMENT";
            }
        }
    };

    public static BaseMenu currentMenu;
    public static BaseOption selectedOption;

    public static BaseMenu resolve() {
        switch(currentMenu.state) {
            case CREATE_APPOINTMENT:
                return new BaseMenu();
        }
    }

    public static BaseMenu reject() {
        switch(currentMenu.state) {
            case CREATE_APPOINTMENT:
                return new LoginMenu();
        }
    }

    public static run(BaseOption option) {
        switch(currentMenu.state) {
            case CREATE_APPOINTMENT:
                AppointmentService.createAppointment(option);
                
        }
    }
}
