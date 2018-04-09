package io.sattler.db;

import javax.validation.constraints.NotNull;

public class EventHost {

    @NotNull
    private Long id;

    @NotNull Long eventId;

    @NotNull
    private Long name;

    private String logoUrl;

    private String website;
}
