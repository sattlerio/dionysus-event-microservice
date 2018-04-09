package io.sattler.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

public class EventDates {

    private Long id;

    private Long eventId;

    @JsonProperty("name")
    private String name;

    @NotNull
    @JsonProperty("start_date")
    @Future
    private DateTime startDate;

    @NotNull
    @Future
    @JsonProperty("end_date")
    private DateTime endDate;

    public DateTime getEndDate() {
        return endDate;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public String getName() {
        return name;
    }

    @JsonCreator
    public EventDates() {}
}
