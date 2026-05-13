package com.palmer.billingstatementgenerator.dao;

import com.palmer.billingstatementgenerator.models.catalog.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for loading {@link com.palmer.billingstatementgenerator.models.catalog.Service} records
 * from the database. Results are returned in {@code sort_order} sequence.
 */
public class ServiceDao {
    private static final Logger log = LoggerFactory.getLogger(ServiceDao.class);
    private static final String SELECT_ALL =
            "SELECT id, sort_order, name, default_cost, included_in_package " +
            "FROM services ORDER BY sort_order";

    private final DataSource dataSource;

    /** @param dataSource the application data source */
    public ServiceDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns all services ordered by {@code sort_order}.
     *
     * @return a non-null, possibly empty list of {@link Service} objects
     * @throws RuntimeException if the query fails
     */
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
            log.error("Failed to load services", e);
            throw new RuntimeException("Failed to load services", e);
        }
    }
}
