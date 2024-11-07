package app.model.inventory;

import app.model.Builder;
import java.util.List;

public final class MedicationBuilder extends Builder<Medication> {

    public MedicationBuilder(List<String> row) throws Exception {
        super(row);
    }

    public MedicationBuilder(Medication instance) {
        super(instance);
    }

    @Override
    public Medication deserialize() throws Exception {
        return new Medication(this.row);
    }
}
