package app.model.user_input;

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
    public interface MenuGenerator extends ThrowableBlankFunction<NewMenu, Exception> {}

    @FunctionalInterface
    public interface NextAction<T, E extends Exception> {
        T apply(T t) throws E;
    }
}
