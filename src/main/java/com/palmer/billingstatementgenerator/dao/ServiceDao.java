package com.palmer.billingstatementgenerator.dao;

import com.palmer.billingstatementgenerator.models.catalog.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceDao {
    private static final String SELECT_ALL =
            "SELECT id, sort_order, name, default_cost, included_in_package " +
            "FROM services ORDER BY sort_order";

    private final DataSource dataSource;

    public ServiceDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Service> findAll() {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            List<Service> result = new ArrayList<>();
            while (rs.next()) {
                result.add(new Service(
                        rs.getInt("id"),
                        rs.getInt("sort_order"),
                        rs.getString("name"),
                        rs.getBigDecimal("default_cost"),
                        rs.getBoolean("included_in_package")));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load services", e);
        }
    }
}
