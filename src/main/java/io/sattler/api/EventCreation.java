package io.sattler.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sattler.db.Event;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import javax.swing.text.html.Option;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventCreation {

    @NotEmpty
    @JsonProperty("company_id")
    private String companyId;

    @NotEmpty
    @JsonProperty("name")
    private String eventName;

    @NotEmpty
    @JsonProperty("venue")
    private String venue;

    @NotEmpty
    @JsonProperty("default_currency_id")
    private String defaultCurrencyId;

    @NotNull
    @JsonProperty("multidayevent")
    private Boolean multiDayEvent;

    @NotNull
    @JsonProperty("multilanguage")
    private Boolean multiLanguage;

    @NotNull
    @JsonProperty("start_date")
    private DateTime startDate;

    @NotNull
    @JsonProperty("end_date")
    private DateTime endDate;

    @JsonProperty("default_language_id")
    private Optional<String> defaultLanguageId;

    @JsonProperty("location")
    @NotNull
    private EventLocation eventLocation;

    @JsonProperty("language_ids")
    private Optional<Set<String>> languageIds;


    public EventCreation() {}

    public String getCompanyId() {
        return companyId;
    }

    public String getEventName() {
        return eventName;
    }

    public Boolean getMultiDayEvent() {
        return multiDayEvent;
    }

    public Boolean getMultiLanguage() {
        return multiLanguage;
    }

    public Optional<Set<String>> getLanguageIds() {
        return languageIds;
    }

    public Optional<String> getDefaultLanguageId() {
        return defaultLanguageId;
    }

    public String getDefaultCurrencyId() {
        return defaultCurrencyId;
    }

    public String getVenue() {
        return venue;
    }

    public EventLocation getEventLocation() {
        return eventLocation;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setDefaultCurrencyId(String defaultCurrencyId) {
        this.defaultCurrencyId = defaultCurrencyId;
    }

    public void setDefaultLanguageId(Optional<String> defaultLanguageId) {
        this.defaultLanguageId = defaultLanguageId;
    }

    public void setMultiDayEvent(Boolean multiDayEvent) {
        this.multiDayEvent = multiDayEvent;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public void setMultiLanguage(Boolean multiLanguage) {
        this.multiLanguage = multiLanguage;
    }

    public void setEventLocation(EventLocation eventLocation) {
        this.eventLocation = eventLocation;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public void setLanguageIds(Optional<Set<String>> languageIds) {
        this.languageIds = languageIds;
    }
}

