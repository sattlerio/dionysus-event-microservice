package io.sattler.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserToEvent {

    private long id;

    private long eventId;

    @JsonProperty("user_id")
    private String userId;

    @JsonCreator
    public UserToEvent() {}

    public UserToEvent(String userId) {
        this.userId = userId;
    }

    public UserToEvent(Long eventId, String userId) {
        this.eventId = eventId;
        this.userId = userId;
    }
}
