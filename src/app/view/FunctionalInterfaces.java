package app.view;

import java.util.List;
import java.util.Map;

public class FunctionalInterfaces {

    @FunctionalInterface
    public interface DisplayGenerator {
        void apply() throws Exception;
    }

    @FunctionalInterface
    public interface ThrowableBlankFunction<R, E extends Exception> {
        R apply() throws E;
    }

    @FunctionalInterface
    public interface OptionGenerator extends ThrowableBlankFunction<List<Option>, Exception> {}

    @FunctionalInterface
    public interface NextAction<E extends Exception> {
        Map<String, Object> apply(Map<String, Object> formData) throws E;
    }
}
