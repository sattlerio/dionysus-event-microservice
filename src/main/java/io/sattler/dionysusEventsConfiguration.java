package io.sattler;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.DatabaseConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.*;

public class dionysusEventsConfiguration extends Configuration {

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

     @JsonProperty("database")
    public void setDatabaseFactory(DataSourceFactory factory) { this.database = factory; }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() { return database; }

}
