package com.operator.subscriber.dao;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class NativeJdbcMsisdnLookupDao {
    private final DataSource dataSource;

    public NativeJdbcMsisdnLookupDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean existsByMsisdn(String msisdn) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM subscribers WHERE msisdn = ? LIMIT 1")) {
            ps.setString(1, msisdn);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to lookup subscriber by msisdn", e);
        }
    }
}

