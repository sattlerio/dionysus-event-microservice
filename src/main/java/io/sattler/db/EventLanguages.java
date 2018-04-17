package io.sattler.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class EventLanguages {

    private Long id;

    private long eventId;

    @JsonProperty("language_id")
    @NotEmpty
    private String languageId;

    public long getEventId() {
        return eventId;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    @JsonCreator
    public EventLanguages() {}
}
