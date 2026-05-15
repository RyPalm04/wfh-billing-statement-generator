package com.palmer.billingstatementgenerator.dao;

import com.palmer.billingstatementgenerator.models.catalog.SpecialCharge;
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
 * DAO for loading {@link SpecialCharge} records
 * from the database. Results are returned in {@code sort_order} sequence.
 */
public class SpecialChargeDao {
    private static final Logger log = LoggerFactory.getLogger(SpecialChargeDao.class);
    private static final String SELECT_ALL =
            "SELECT id, sort_order, name, default_cost, requires_description " +
                    "FROM special_charges ORDER BY sort_order";

    private final DataSource dataSource;

    /**
     * @param dataSource
     *         the application data source
     */
    public SpecialChargeDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns all special charges ordered by {@code sort_order}.
     *
     * @return a non-null, possibly empty list of {@link SpecialCharge} objects
     *
     * @throws RuntimeException
     *         if the query fails
     */
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
            log.error("Failed to load special charges", e);
            throw new RuntimeException("Failed to load special charges", e);
        }
    }
}
