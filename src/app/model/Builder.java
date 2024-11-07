package app.model;

import java.util.List;

public abstract class Builder<T extends ISerializable> {
    public abstract T deserialize () throws Exception;

    protected List<String> row;
    protected T instance;

    public void setSubRows() {};

    public Builder(List<String> row) throws Exception {
        this.row = row;
    }

    public Builder(T instance) {
        this.instance = instance;
    }

    public List<String> getRow() {
        return row;
    }

    public T getInstance() {
        return instance;
    }

    public Builder<T> buildInstance() throws Exception {
        this.setSubRows();
        this.instance = this.deserialize();
        return this;
    }

    public Builder<T> buildRow() {
        this.row = this.serialize();
        this.setSubRows();
        return this;
    }

    public List<String> getSubRow(int start, int end) {
        return this.row.subList(start, Math.min(end, this.row.size()));
    }

    public List<String> serialize() {
        return this.instance.serialize();
    }
}
