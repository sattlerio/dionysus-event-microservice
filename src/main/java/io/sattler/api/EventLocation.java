package io.sattler.api;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventLocation {

    @NotEmpty
    @JsonProperty("city")
    private String city;

    private String venue;

    @NotEmpty
    @JsonProperty("country_id")
    private String countryId;

    @NotEmpty
    @JsonProperty("latitude")
    private Double latitude;

    @NotEmpty
    @JsonProperty("longitude")
    private Double longitude;

    @NotEmpty
    @JsonProperty("postal_code")
    private String zip;

    @NotEmpty
    @JsonProperty("street")
    private String street;

    @JsonProperty("street_number")
    private Optional<String> streetNumber;

    @JsonCreator
    public EventLocation() {}

    public String getStreet() {
        return street;
    }

    public String getCountryId() {
        return countryId;
    }

    public String getCity() {
        return city;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getVenue() {
        return venue;
    }

    public String getZip() {
        return zip;
    }

    public Optional<String> getStreetNumber() {
        return streetNumber;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setStreetNumber(Optional<String> streetNumber) {
        this.streetNumber = streetNumber;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
}