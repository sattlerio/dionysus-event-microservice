package io.sattler.db;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserToEventMapper implements ResultSetMapper<UserToEvent> {
    @Override
    public UserToEvent map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new UserToEvent(
                r.getLong("event_id"),
                r.getString("user_id")
        );
    }
}
