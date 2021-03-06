package io.sattler.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Event {

    private Long id;

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("active")
    private Boolean active;

    @NotEmpty
    @JsonProperty("event_name")
    private String eventName;

    @NotEmpty
    @JsonProperty("company_id")
    private String companyId;

    @NotEmpty
    @JsonProperty("street_number")
    private String streetNumber;

    @NotEmpty
    @JsonProperty("street")
    private String street;

    @NotEmpty
    @JsonProperty("city")
    private String city;

    @NotEmpty
    @JsonProperty("country_id")
    @Length(min = 2, max = 2, message = "The country ID must have only 2 characters")
    private String countryId;

    @NotEmpty
    @JsonProperty("postal_code")
    private String postalCode;

    @JsonProperty("latitude")
    @NotNull
    @Min(-90)
    @Max(90)
    private Double latitude;

    @JsonProperty("longitude")
    @Min(-180)
    @Max(180)
    @NotNull
    private Double longitude;

    @JsonProperty("location_name")
    private String locationName;

    @JsonProperty("multi_day_event")
    @NotNull
    private Boolean multiDayEvent;

    @JsonProperty("start_date")
    @Future
    private DateTime startDate;

    @JsonProperty("end_date")
    @Future
    private DateTime endDate;

    @JsonProperty("event_dates")
    @Valid
    private Set<EventDates> eventDates;

    @JsonProperty("event_languages")
    @Valid
    private Set<EventLanguages> eventLanguages;

    @JsonProperty("default_currency_id")
    @NotEmpty
    private String defaultCurrencyId;

    @JsonProperty("multi_language")
    @NotNull
    private Boolean multiLanguage;

    @JsonProperty("default_language_id")
    private String defaultLanguageId;

    @JsonProperty("users")
    @Valid
    private Set<UserToEvent> users;

    @JsonProperty("currencies")
    @Valid
    private Set<EventCurrencies> eventCurrencies;

    @JsonCreator
    public Event() {
        this.eventId = UUID.randomUUID().toString();
    }

    public Event(Long id, String eventId, String eventName, String companyId, String streetNumber,
                 String street, String city, String countryId, String postalCode,
                 Double latitude, Double longitude, String locationName,
                 Boolean multiDayEvent,
                 String defaultCurrencyId, Boolean multiLanguage, String defaultLanguageId,
                 Timestamp startDate, Timestamp endDate) {
        this.id = id;
        this.eventId = eventId;
        this.eventName = eventName;
        this.companyId = companyId;
        this.streetNumber = streetNumber;
        this.street = street;
        this.city = city;
        this.countryId = countryId;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.multiDayEvent = multiDayEvent;
        this.defaultCurrencyId = defaultCurrencyId;
        this.multiLanguage = multiLanguage;
        this.defaultLanguageId = defaultLanguageId;
        this.startDate = new DateTime(startDate.getTime());
        this.endDate = new DateTime(endDate.getTime());
        this.active = false;
    }

    public Boolean getMultiDayEvent() {
        return multiDayEvent;
    }

    public Timestamp getStartDate() {
        return new Timestamp(this.startDate.toDateTime(DateTimeZone.UTC).getMillis());
    }

    public Timestamp getEndDate() {
        return new Timestamp(this.endDate.toDateTime(DateTimeZone.UTC).getMillis());
    }

    public Set<EventDates> getEventDates() {
        return eventDates;
    }

    public String getEventName() {
        return eventName;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public String getCity() {
        return city;
    }

    public Long getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getCountryId() {
        return countryId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getStreet() {
        return street;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getDefaultCurrencyId() {
        return defaultCurrencyId;
    }

    public Boolean getMultiLanguage() {
        return multiLanguage;
    }

    public String getDefaultLanguageId() {
        return defaultLanguageId;
    }

    public Set<EventLanguages> getEventLanguages() {
        return eventLanguages;
    }

    public void setEventLanguages(Set<EventLanguages> eventLanguages) {
        this.eventLanguages = eventLanguages;
    }

    public Set<EventCurrencies> getEventCurrencies() {
        return eventCurrencies;
    }

    public void setEventCurrencies(Set<EventCurrencies> eventCurrencies) {
        this.eventCurrencies = eventCurrencies;
    }

    /*
    public Event(String eventName, String streetNumber, String street,
                 String city, String countryId, String postalCode, Double latitude,
                 Double longitude, String locationName, Boolean multiDayEvent,
                 DateTime startDate, DateTime endDate, EventDates[] eventDates,
                 String defaultLanguage) {

        this.eventName = eventName;
        this.eventId = UUID.randomUUID().toString();
        this.streetNumber = streetNumber;
        this.street = street;
        this.city = city;
        this.countryId = countryId;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.multiDayEvent = multiDayEvent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventDates = eventDates;
        this.defaultLanguage = defaultLanguage;
    }
    */

}
