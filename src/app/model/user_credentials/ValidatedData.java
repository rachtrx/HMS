package app.model.user_credentials;
/**
* Validator template.
*
* @author Rachmiel Teo (@rachtrx)
* @version 1.0
* @since 2024-10-17
*/
public abstract class ValidatedData<T, K> {

    private T value;
    abstract protected T validate(K value) throws Exception;

    public ValidatedData(K value) throws Exception {
        this.setValue(value);
    }

    protected final void setValue(K value) throws Exception {
        this.value = this.validate(value);
    }

    protected T getValue() {
        return this.value;
    }
}