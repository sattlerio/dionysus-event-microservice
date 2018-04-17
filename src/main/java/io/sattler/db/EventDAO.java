package io.sattler.db;

import org.joda.time.DateTime;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(EventMapper.class)
public interface EventDAO {

    @GetGeneratedKeys(columnName = "id")
    @SqlUpdate("INSERT INTO events (name, company_id, event_uuid, street_number, street, city, country_id, postal_code, latitude, longitude, location_name, multiday_event, start_date, end_date, multi_language, default_language_id, default_currency_id) VALUES (:name, :company_id, :event_uuid, :street_number, :street, :city, :country_id, :postal_code, :latitude, :longitude, :location_name, :multiday_event, :start_date, :end_date, :multi_language, :default_language_id, :default_currency_id)")
    public long createEvent(@Bind("name") String name,
                           @Bind("company_id") String company_id,
                           @Bind("event_uuid") String event_uuid,
                           @Bind("street_number") String street_number,
                           @Bind("street") String street,
                           @Bind("city") String city,
                           @Bind("country_id") String country_id,
                           @Bind("postal_code") String postal_code,
                           @Bind("latitude") Double latitude,
                           @Bind("longitude") Double longitude,
                           @Bind("location_name") String location_name,
                           @Bind("multiday_event") Boolean multiday_event,
                           @Bind("start_date") DateTime startDate,
                           @Bind("end_date") DateTime endDate,
                           @Bind("multi_language") Boolean multiLanguage,
                           @Bind("default_language_id") String defaultLanguageId,
                           @Bind("default_currency_id") String defaultCurrencyId
                           );

    @SqlUpdate("INSERT INTO event_languages (event_id, language_id) VALUES (:event_id, :language_id)")
    public long createEventLanguage(@Bind("event_id") Long eventId,
                                   @Bind("language_id") String languageId);

    @SqlUpdate("INSERT INTO event_dates (event_id, start_date, end_date, name) VALUES (:event_id, :start_date, :end_date, :name)")
    public long createEventDate(@Bind("event_id") Long eventId,
                               @Bind("start_date") DateTime startDate,
                               @Bind("end_date") DateTime endDate,
                               @Bind("name") String name);
}
