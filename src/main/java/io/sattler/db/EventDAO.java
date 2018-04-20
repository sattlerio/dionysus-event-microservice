package io.sattler.db;

import org.joda.time.DateTime;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;
import java.util.Set;

@RegisterMapper({EventMapper.class})
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

    @SqlQuery("SELECT e.* FROM events e LEFT OUTER JOIN users_2_event u2e ON u2e.event_id = e.id WHERE (u2e.user_id = :user_id OR NOT EXISTS (SELECT 1 FROM users_2_event u2e_check WHERE u2e_check.event_id = e.id)) AND e.company_id = :company_id  ORDER BY e.start_date DESC")
    public List<Event> getBasicEventInformationWithPermission(@Bind("user_id") String userId,
                                                             @Bind("company_id") String companyId);

    @SqlQuery("SELECT e.* FROM events e  WHERE e.company_id = :company_id  ORDER BY e.start_date DESC")
    public List<Event> getBasicEventInformation(@Bind("company_id") String companyId);

    @SqlQuery("SELECT e.* FROM events e LEFT OUTER JOIN users_2_event u2e ON u2e.event_id = e.id WHERE (u2e.user_id = :user_id OR NOT EXISTS (SELECT 1 FROM users_2_event u2e_check WHERE u2e_check.event_id = e.id)) AND e.company_id = :company_id AND e.event_uuid = :event_id ORDER BY e.start_date DESC")
    public Event fetchEventByIdWithPermission(@Bind("user_id") String userId,
                                              @Bind("company_id") String companyId,
                                              @Bind("event_id") String eventId);

    @SqlQuery("SELECT e.* FROM events e  WHERE e.company_id = :company_id AND event_uuid = :event_id ORDER BY e.start_date DESC")
    public Event fetchEventById(@Bind("company_id") String companyId,
                                      @Bind("event_id") String eventId);

    @RegisterMapper(EventLanguageMapper.class)
    @SqlQuery("SELECT el.language_id as language_id FROM event_languages el WHERE event_id = :event_id")
    public Set<EventLanguages> fetchLanguagesFromEvent(@Bind("event_id") Long eventId);

    @RegisterMapper(EventCurrencyMapper.class)
    @SqlQuery("SELECT ec.currency_id as currency_id FROM event_currencies ec WHERE event_id = :event_id")
    public Set<EventCurrencies> fetchCurrenciesFromEvent(@Bind("event_id") Long eventId);

    @RegisterMapper(UserToEventMapper.class)
    @SqlQuery("SELECT COUNT(*) FROM events e WHERE e.event_uuid = :event_id AND (EXISTS (SELECT 1 FROM users_2_event u2e WHERE u2e.event_id = e.id AND u2e.user_id = :user_id) OR NOT EXISTS (SELECT 1 FROM users_2_event u2e_check WHERE u2e_check.event_id = e.id))")
    public Long userPermission(@Bind("event_id") String eventId,
                               @Bind("user_id") String userId);

    @SqlUpdate("DELETE FROM event_currencies ec USING events e WHERE e.event_uuid = :event_id AND e.id = ec.event_id  AND (EXISTS (SELECT 1 FROM users_2_event u2e WHERE u2e.event_id = e.id AND u2e.user_id = :user_id) OR NOT EXISTS (SELECT 1 FROM users_2_event u2e_check WHERE u2e_check.event_id = e.id))")
    public void removeCurrenciesFromEvent(@Bind("event_id") String eventId,
                                          @Bind("user_id") String userId);

    @SqlUpdate("DELETE FROM event_languages el USING events e WHERE e.event_uuid = :event_id AND e.id = el.event_id  AND (EXISTS (SELECT 1 FROM users_2_event u2e WHERE u2e.event_id = e.id AND u2e.user_id = :user_id) OR NOT EXISTS (SELECT 1 FROM users_2_event u2e_check WHERE u2e_check.event_id = e.id))")
    public void removeLanguagesFromEvent(@Bind("event_id") String eventId,
                                         @Bind("user_id") String userId);

    @GetGeneratedKeys(columnName = "id")
    @SqlUpdate("INSERT INTO event_currencies (event_id, currency_id) VALUES (:event_id, :currency_id)")
    public long createEventCurrency(@Bind("event_id") Long eventId,
                                    @Bind("currency_id") String currencyId);

    @SqlUpdate("UPDATE events SET default_language_id = :default_language_id WHERE company_id = :company_id AND event_uuid = :event_uuid")
    public void updateEventDefaultLanguage(@Bind("default_language_id") String defaultLanguageId,
                                           @Bind("company_id") String companyId,
                                           @Bind("event_uuid") String eventId);

    @SqlUpdate("UPDATE events SET multi_language = :multi_language WHERE company_id = :company_id AND event_uuid = :event_uuid")
    public void updateEventMultiLanguage(@Bind("multi_language") Boolean multiLangugae,
                                           @Bind("company_id") String companyId,
                                           @Bind("event_uuid") String eventId);

    @SqlUpdate("UPDATE events SET name = :name, location_name = :venue WHERE company_id = :company_id AND event_uuid = :event_uuid")
    public void updateEventBasicDetails(@Bind("name") String eventName,
                                        @Bind("venue") String venue,
                                        @Bind("company_id") String companyId,
                                        @Bind("event_uuid") String eventId);

    @SqlUpdate("UPDATE events SET name = :name, location_name = :venue, street_number = :street_number, street = :street, city = :city, country_id = :country_id, postal_code = :postal_code, latitude = :latitude, longitude = :longitude WHERE company_id = :company_id AND event_uuid = :event_uuid")
    public void updateEventBasicDetailsWithLocation(@Bind("name") String eventName,
                                                    @Bind("venue") String venue,
                                                    @Bind("street_number") String streetNumber,
                                                    @Bind("street") String street,
                                                    @Bind("city") String city,
                                                    @Bind("country_id") String countryId,
                                                    @Bind("postal_code") String postalCode,
                                                    @Bind("latitude") Double latitude,
                                                    @Bind("longitude") Double longitude,
                                                    @Bind("company_id") String companyId,
                                                    @Bind("event_uuid") String eventId);

}
