package app.model.inventory;

import app.model.Builder;
import java.util.List;

public final class MedicationBuilder extends Builder<Medication> {

    private List<Request> requests;

    public MedicationBuilder(List<String> row, List<Request> requests) throws Exception {
        super(row);
        this.requests = requests;
    }

    public MedicationBuilder(Medication instance) {
        super(instance);
    }

    @Override
    public Medication deserialize() throws Exception {
        return new Medication(this.row, this.requests);
    }
}
