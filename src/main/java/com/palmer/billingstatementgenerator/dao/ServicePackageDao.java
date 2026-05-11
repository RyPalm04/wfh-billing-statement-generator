package com.palmer.billingstatementgenerator.dao;

import com.palmer.billingstatementgenerator.models.catalog.ServicePackage;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicePackageDao {
    private static final String SELECT_ALL =
            "SELECT id, sort_order, name, default_cost " +
            "FROM service_packages ORDER BY sort_order";

    private final DataSource dataSource;

    public ServicePackageDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<ServicePackage> findAll() {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            List<ServicePackage> result = new ArrayList<>();
            while (rs.next()) {
                result.add(new ServicePackage(
                        rs.getInt("id"),
                        rs.getInt("sort_order"),
                        rs.getString("name"),
                        rs.getBigDecimal("default_cost")));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load service packages", e);
        }
    }
}
