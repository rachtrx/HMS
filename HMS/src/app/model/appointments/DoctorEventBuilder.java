package app.model.appointments;

import java.util.List;

import app.model.Builder;

public class DoctorEventBuilder extends Builder<DoctorEvent> {

    public DoctorEventBuilder(List<String> row) throws Exception {
        super(row);
    }

    public DoctorEventBuilder(DoctorEvent instance) {
        super(instance);
    }
    
    @Override
    public DoctorEvent deserialize() throws Exception {
        return new DoctorEvent(this.row);
    }
}
