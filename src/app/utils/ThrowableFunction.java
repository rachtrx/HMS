package app.utils;

/**
* Callback function with exceptions.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-21
*/
@FunctionalInterface
public interface ThrowableFunction<T, R, E extends Exception> {
    R apply(T t) throws E;
}
