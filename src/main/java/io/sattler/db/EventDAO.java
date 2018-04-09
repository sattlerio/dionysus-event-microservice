package io.sattler.db;

import org.joda.time.DateTime;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(EventMapper.class)
public interface EventDAO {

    @SqlUpdate("INSERT INTO events (name, company_id, event_uuid, street_number, street, city, country_id, postal_code, latitude, longitude, location_name, multiday_event, start_date, end_date) VALUES (:name, :company_id, :event_uuid, :street_number, :street, :city, :country_id, :postal_code, :latitude, :longitude, :location_name, :multiday_event, :start_date, :end_date) RETURNING id")
    public int createEvent(@Bind("name") String name,
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
                           @Bind("end_date") DateTime endDate
                           );

    @SqlUpdate("INSERT INTO event_dates (event_id, start_date, end_date, name) VALUES (:event_id, :start_date, :end_date, name)")
    public int createEventDate(@Bind("event_id") Long eventId,
                               @Bind("start_date") DateTime startDate,
                               @Bind("end_date") DateTime endDate,
                               @Bind("name") String name);
}
