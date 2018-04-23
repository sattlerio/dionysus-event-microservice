package io.sattler.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import java.util.Date;

public class EventDateEdit {

    @JsonProperty("start_date")
    @NotNull
    private DateTime startDate;

    @JsonProperty("end_date")
    @NotNull
    private DateTime endDate;

    @JsonProperty("multidayevent")
    private Boolean multiDayEvent;

    @JsonCreator
    public EventDateEdit() {};

    public EventDateEdit (DateTime startDate, DateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.multiDayEvent = false;
    }

    public EventDateEdit(DateTime startDate, DateTime endDate, Boolean multiDayEvent) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.multiDayEvent = multiDayEvent;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public Boolean getMultiDayEvent() {
        if (multiDayEvent == null)  {
            return false;
        }
        return multiDayEvent;
    }

}
