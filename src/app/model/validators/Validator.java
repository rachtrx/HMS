package app.model.validators;

public interface Validator<T> {
    public abstract void validate(T value) throws Exception;
}
