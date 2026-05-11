package com.palmer.billingstatementgenerator.dao;

import com.palmer.billingstatementgenerator.models.catalog.SpecialCharge;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpecialChargeDao {
    private static final String SELECT_ALL =
            "SELECT id, sort_order, name, default_cost, requires_description " +
            "FROM special_charges ORDER BY sort_order";

    private final DataSource dataSource;

    public SpecialChargeDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<SpecialCharge> findAll() {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            List<SpecialCharge> result = new ArrayList<>();
            while (rs.next()) {
                result.add(new SpecialCharge(
                        rs.getInt("id"),
                        rs.getInt("sort_order"),
                        rs.getString("name"),
                        rs.getBigDecimal("default_cost"),
                        rs.getBoolean("requires_description")));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load special charges", e);
        }
    }
}
