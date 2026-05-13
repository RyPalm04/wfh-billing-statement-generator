package com.palmer.billingstatementgenerator.dao;

import com.palmer.billingstatementgenerator.models.catalog.CashAdvance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CashAdvanceDao {
    private static final Logger log = LoggerFactory.getLogger(CashAdvanceDao.class);
    private static final String SELECT_ALL =
            "SELECT id, sort_order, name FROM cash_advances ORDER BY sort_order";

    private final DataSource dataSource;

    public CashAdvanceDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<CashAdvance> findAll() {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            List<CashAdvance> result = new ArrayList<>();
            while (rs.next()) {
                result.add(new CashAdvance(
                        rs.getInt("id"),
                        rs.getInt("sort_order"),
                        rs.getString("name")));
            }
            return result;
        } catch (SQLException e) {
            log.error("Failed to load cash advances", e);
            throw new RuntimeException("Failed to load cash advances", e);
        }
    }
}
