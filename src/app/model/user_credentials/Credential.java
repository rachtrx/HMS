package app.model.user_credentials;

public abstract class Credential<T> {

    private T value;
    abstract protected void validate(T value) throws Exception;

    public Credential(T value) {
        this.value = value;
    }

    public void setValue(T value) throws Exception {
        this.validate(value);
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }
}