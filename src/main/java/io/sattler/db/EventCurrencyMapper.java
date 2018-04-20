package io.sattler.db;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventCurrencyMapper implements ResultSetMapper<EventCurrencies> {
    @Override
    public EventCurrencies map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new EventCurrencies(
                r.getString("currency_id")
        );
    }
}
