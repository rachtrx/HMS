package app.service;

import app.constants.exceptions.ItemNotFoundException;

/**
* CRUD functionality for appointments.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public interface BaseService<T> {
    public void create(T value);
    public T read();
    public void update(T targetValue, T updatedValue) throws ItemNotFoundException;
    public void delete(T targetValue) throws ItemNotFoundException;
}
