package app.model.inventory;

import java.util.List;
import app.model.Builder;

public class RequestBuilder extends Builder<Request> {
    public RequestBuilder(List<String> row) throws Exception {
        super(row);
    }

    public RequestBuilder(Request instance) {
        super(instance);
    }

    @Override
    public Request deserialize() throws Exception {
        return new Request(this.row);
    }
}
