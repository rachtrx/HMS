package app.model.user_input;

import app.model.user_input.Field.NextAction;

public class Input extends Field {
    
    private String value;

    public Input() {};

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
