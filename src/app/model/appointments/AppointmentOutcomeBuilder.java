package app.model.appointments;

import java.util.List;

import app.model.Builder;

public class AppointmentOutcomeBuilder extends Builder<AppointmentOutcomeRecord> {

    private Prescription prescription;

    public AppointmentOutcomeBuilder(List<String> row, Prescription prescription) throws Exception {
        super(row);
        this.prescription = prescription;
    }

    public AppointmentOutcomeBuilder(AppointmentOutcomeRecord instance) {
        super(instance);
    }
    
    @Override
    public AppointmentOutcomeRecord deserialize() throws Exception {
        return new AppointmentOutcomeRecord(this.row, this.prescription);
    }
}
