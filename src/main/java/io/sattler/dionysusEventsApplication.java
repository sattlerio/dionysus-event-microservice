package io.sattler;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.DatabaseConfiguration;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.sattler.db.EventDAO;
import io.sattler.resources.EventResource;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class dionysusEventsApplication extends Application<dionysusEventsConfiguration> {

    public static void main(final String[] args) throws Exception {
        new dionysusEventsApplication().run(args);
    }

    @Override
    public String getName() {
        return "dionysusEvents";
    }

    @Override
    public void initialize(final Bootstrap<dionysusEventsConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        new ResourceConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        bootstrap.addBundle(new MigrationsBundle<dionysusEventsConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(dionysusEventsConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });

    }

    @Override
    public void run(final dionysusEventsConfiguration configuration,
                    final Environment environment) {
        try {
            DBIFactory factory = new DBIFactory();
            final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "postgresql");

            EventDAO eventDAO = jdbi.onDemand(EventDAO.class);

            EventResource event = new EventResource(eventDAO);

            environment.jersey().register(event);

        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(dionysusEventsApplication.class);
            log.error("not possible to run application - going to die uaaaaaaahhh!");
            log.error(e.toString());
            log.error(e.getMessage());
        }
    }

}
