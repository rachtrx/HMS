package app.model;

import app.db.DatabaseManager;
import java.util.List;

public interface ISerializable {
    
    public List<String> serialize();

    default void update(Object instance) {
        DatabaseManager.update(instance);
    }

    default void add(Object instance) {
        DatabaseManager.add(instance);
    }
}
