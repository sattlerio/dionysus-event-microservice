package io.sattler.db;

import org.joda.time.DateTime;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventLanguageMapper implements ResultSetMapper<EventLanguages> {
    @Override
    public EventLanguages map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new EventLanguages(
                r.getString("language_id")
        );
    }
}
