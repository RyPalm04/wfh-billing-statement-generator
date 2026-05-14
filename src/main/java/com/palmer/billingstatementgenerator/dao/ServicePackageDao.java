package com.palmer.billingstatementgenerator.dao;

import com.palmer.billingstatementgenerator.models.catalog.ServicePackage;
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
 * DAO for loading {@link com.palmer.billingstatementgenerator.models.catalog.ServicePackage} records
 * from the database. Results are returned in {@code sort_order} sequence.
 */
public class ServicePackageDao {
    private static final Logger log = LoggerFactory.getLogger(ServicePackageDao.class);
    private static final String SELECT_ALL =
            "SELECT id, sort_order, name, default_cost " +
                    "FROM service_packages ORDER BY sort_order";
    private static final String SELECT_SERVICE_IDS =
            "SELECT service_id FROM packaged_services WHERE package_id = ?";

    private final DataSource dataSource;

    /**
     * @param dataSource
     *         the application data source
     */
    public ServicePackageDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns the IDs of services belonging to the given package.
     *
     * @param packageId
     *         the ID of the service package
     *
     * @return a non-null, possibly empty list of service IDs
     *
     * @throws RuntimeException
     *         if the query fails
     */
    public List<Integer> findServiceIdsForPackage(int packageId) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_SERVICE_IDS)) {
            ps.setInt(1, packageId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Integer> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(rs.getInt("service_id"));
                }
                return result;
            }
        } catch (SQLException e) {
            log.error("Failed to load service IDs for package {}", packageId, e);
            throw new RuntimeException("Failed to load service IDs for package " + packageId, e);
        }
    }

    /**
     * Returns all service packages ordered by {@code sort_order}.
     *
     * @return a non-null, possibly empty list of {@link ServicePackage} objects
     *
     * @throws RuntimeException
     *         if the query fails
     */
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
            log.error("Failed to load service packages", e);
            throw new RuntimeException("Failed to load service packages", e);
        }
    }
}
