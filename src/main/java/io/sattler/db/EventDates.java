package io.sattler.db;

import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

public class EventDates {

    @NotNull
    private Long id;

    @NotNull
    private Long eventId;

    private String name;

    @NotNull
    private DateTime startDate;

    @NotNull
    private DateTime endDate;
}
