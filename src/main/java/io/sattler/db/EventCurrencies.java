package io.sattler.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class EventCurrencies {

    private Long id;

    @JsonProperty("currency_id")
    @NotEmpty
    private String currencyId;

    private Long eventId;

    @JsonCreator
    public EventCurrencies() {}

    public EventCurrencies(String currencyId, Long eventId) {
        this.currencyId = currencyId;
        this.eventId = eventId;
    }

    public EventCurrencies(String currencyId) {
        this.currencyId = currencyId;
    }

    public Long getId() {
        return id;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getCurrencyId() {
        return currencyId;
    }
}
