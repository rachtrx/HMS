package app.model.user_input.menu_collections;

import app.model.user_input.Field.NextAction;
import app.model.user_input.InputMenu;
import app.model.user_input.OptionGeneratorCollection;
import app.model.user_input.OptionMenu;
import java.util.Map;

import app.model.user_input.Field.NextAction;

public class PatientMenuCollection {

    public static Menu getPatientMainMenu() {
        return new OptionMenu("Select Option", "Choose an option")
            .setOptionGenerator(OptionGeneratorCollection::generatePatientMenuOptions);
    }

    // public static Menu getPatientEditMedicalRecordMenu(Map<String, Object> formData, NextAction nextAction) {
    //     return new InputMenu(formData, "Select", "", nextAction);
    // }
    
    // public static Menu getPatientAppointmentSelectionTypeMenu(Map<String, Object> formData) {
    //     return new OptionMenu(

    //     )
    // }
}
