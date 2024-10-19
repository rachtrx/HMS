package app.model.appointments;

import app.constants.exceptions.InvalidTimeslotException;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
* Timeslot validator for appointment.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class Timeslot {
    public static final LocalTime firstSlotStartTime = LocalTime.of(9, 0);
    public static final LocalTime lastSlotStartTime = LocalTime.of(16, 0);
    public static final int timeslotLengthInHours = 1;
    private LocalDateTime timeslotDateTime;

    public Timeslot(LocalDateTime timeslotDateTime) throws InvalidTimeslotException {
        this.validateTimeslotDateTime(timeslotDateTime);
        this.timeslotDateTime = timeslotDateTime;
    }

    public LocalDateTime getTimeslotDateTime() {
        return timeslotDateTime;
    }

    public void setDateTime(LocalDateTime timeslotDateTime) throws InvalidTimeslotException {
        this.validateTimeslotDateTime(timeslotDateTime);
        this.timeslotDateTime = timeslotDateTime;
    }

    private void validateTimeslotDateTime(LocalDateTime timeslotDateTime) throws InvalidTimeslotException {
        LocalTime desiredTime = LocalTime.of(
            timeslotDateTime.getHour(),
            timeslotDateTime.getMinute()
        );
        if (
            desiredTime.isBefore(Timeslot.firstSlotStartTime) ||
            desiredTime.isAfter(Timeslot.lastSlotStartTime.plusHours(Timeslot.timeslotLengthInHours))
        ) {
            throw new InvalidTimeslotException("Timeslot does not exist");
        }
    }
}
