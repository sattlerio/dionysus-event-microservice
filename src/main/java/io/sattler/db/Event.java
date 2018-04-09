package io.sattler.db;

import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class Event {

    @NotNull
    private String companyId;

    @NotNull
    private Long id;
    @NotNull
    private String eventId;

    @NotNull
    private String eventName;

    @NotNull
    private String streetNumber;

    @NotNull
    private String street;

    @NotNull
    private String city;

    @NotNull
    private String countryId;

    @NotNull
    private String postalCode;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private String locationName;

    @NotNull
    private boolean multiDayEvent;

    private DateTime startDate;

    private DateTime endDate;

    @NotNull
    private String defaultLanguage;

    public Event(String eventName, String streetNumber, String street,
                 String city, String countryId, String postalCode, Double latitude,
                 Double longitude, String locationName, Boolean multiDayEvent,
                 DateTime startDate, DateTime endDate) {

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
    }

    public Event() {
        this.eventId = UUID.randomUUID().toString();
    }

}
