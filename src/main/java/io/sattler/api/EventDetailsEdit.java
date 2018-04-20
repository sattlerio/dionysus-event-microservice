package io.sattler.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sattler.db.EventCurrencies;
import io.sattler.db.EventLanguages;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDetailsEdit {

    @NotNull
    @JsonProperty("multi_language")
    private Boolean multiLanguage;

    @JsonProperty("default_language_id")
    private String defaultLanguageId;

    @JsonProperty("event_languages")
    @Valid
    private Set<EventLanguages> languages;

    @JsonProperty("event_currencies")
    @Valid
    private Set<EventCurrencies> currencies;

    @JsonCreator
    public EventDetailsEdit() {}

    public String getDefaultLanguageId() {
        return defaultLanguageId;
    }

    public Boolean getMultiLanguage() {
        return multiLanguage;
    }

    public Set<EventCurrencies> getCurrencies() {
        return currencies;
    }

    public Set<EventLanguages> getLanguages() {
        return languages;
    }
}
