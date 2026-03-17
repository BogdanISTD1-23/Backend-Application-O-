package com.operator.subscriber.dao;

import com.operator.subscriber.entity.Transaction;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TransactionJdbcTemplateDao {
    private static final RowMapper<Transaction> TX_ROW_MAPPER = new TransactionRowMapper();

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcOperations jdbcOperations;

    public TransactionJdbcTemplateDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcOperations jdbcOperations) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcOperations = jdbcOperations;
    }

    public List<Transaction> findBySubscriberId(long subscriberId) {
        return namedParameterJdbcTemplate.query("""
                        SELECT id, subscriber_id, amount, type, description, created_at
                        FROM transactions
                        WHERE subscriber_id = :subscriberId
                        ORDER BY created_at DESC, id DESC
                        """,
                new MapSqlParameterSource("subscriberId", subscriberId),
                TX_ROW_MAPPER
        );
    }

    public int countForSubscriber(long subscriberId) {
        Integer count = jdbcOperations.queryForObject(
                "SELECT COUNT(*) FROM transactions WHERE subscriber_id = ?",
                Integer.class,
                subscriberId
        );
        return count == null ? 0 : count;
    }

    private static final class TransactionRowMapper implements RowMapper<Transaction> {
        @Override
        public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
            Transaction tx = new Transaction();
            tx.setId(rs.getLong("id"));

            // We intentionally don't fully hydrate Subscriber here to avoid extra queries.
            // The service layer can join/resolve subscriber if needed.
            tx.setAmount(rs.getBigDecimal("amount"));
            tx.setType(Transaction.TransactionType.valueOf(rs.getString("type")));
            tx.setDescription(rs.getString("description"));
            tx.setCreatedAt(rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toLocalDateTime());
            return tx;
        }
    }
}

