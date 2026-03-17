package com.operator.subscriber.dao;

import com.operator.subscriber.entity.Tariff;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TariffJdbcClientDao {
    private final JdbcClient jdbcClient;

    public TariffJdbcClientDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Tariff> findAllActive() {
        return jdbcClient.sql("""
                        SELECT id, name, description, price, speed_mbps, active
                        FROM tariffs
                        WHERE active = true
                        ORDER BY price ASC, id ASC
                        """)
                .query(Tariff.class)
                .list();
    }

    public Optional<Tariff> findById(long id) {
        return jdbcClient.sql("""
                        SELECT id, name, description, price, speed_mbps, active
                        FROM tariffs
                        WHERE id = :id
                        """)
                .param("id", id)
                .query(Tariff.class)
                .optional();
    }
}

