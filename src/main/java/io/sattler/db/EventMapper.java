package io.sattler.db;

import org.joda.time.DateTime;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventMapper implements ResultSetMapper<Event> {

    @Override
    public Event map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Event(
                r.getLong("id"),
                r.getString("event_uuid"),
                r.getString("name"),
                r.getString("company_id"),
                r.getString("street_number"),
                r.getString("street"),
                r.getString("city"),
                r.getString("country_id"),
                r.getString("postal_code"),
                r.getDouble("latitude"),
                r.getDouble("longitude"),
                r.getString("location_name"),
                r.getBoolean("multiday_event"),
                r.getString("default_currency_id"),
                r.getBoolean("multi_language"),
                r.getString("default_language_id"),
                r.getTimestamp("start_date"),
                DateTime.parse(r.getDate("end_date").toString())
        );
    }

}
