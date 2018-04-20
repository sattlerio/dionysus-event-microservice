package io.sattler.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;

public class EventBasicEdit {

    @JsonProperty("event_name")
    @NotEmpty
    private String eventName;

    @JsonProperty("location_name")
    @NotEmpty
    private String venue;

    @JsonProperty("location")
    @Valid
    private EventLocation eventLocation;

    public EventBasicEdit() {}

    public String getVenue() {
        return venue;
    }

    public EventLocation getEventLocation() {
        return eventLocation;
    }

    public String getEventName() {
        return eventName;
    }
}
