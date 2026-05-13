package com.palmer.billingstatementgenerator.dao;

import com.palmer.billingstatementgenerator.models.catalog.Merchandise;
import com.palmer.billingstatementgenerator.models.catalog.Merchandise.PricingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * DAO for loading {@link com.palmer.billingstatementgenerator.models.catalog.Merchandise} records
 * from the database. Results are returned in {@code sort_order} sequence.
 * The {@code pricing_mode} column is mapped to {@link com.palmer.billingstatementgenerator.models.catalog.Merchandise.PricingMode}.
 */
public class MerchandiseDao {
    private static final Logger log = LoggerFactory.getLogger(MerchandiseDao.class);
    private static final String SELECT_ALL =
            "SELECT id, sort_order, name, default_cost, requires_description, sales_taxable, pricing_mode " +
            "FROM merchandise ORDER BY sort_order";

    private final DataSource dataSource;

    /** @param dataSource the application data source */
    public MerchandiseDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns all merchandise items ordered by {@code sort_order}.
     *
     * @return a non-null, possibly empty list of {@link Merchandise} objects
     * @throws RuntimeException if the query fails
     */
    public List<Merchandise> findAll() {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            List<Merchandise> result = new ArrayList<>();
            while (rs.next()) {
                result.add(new Merchandise(
                        rs.getInt("id"),
                        rs.getInt("sort_order"),
                        rs.getString("name"),
                        rs.getBigDecimal("default_cost"),
                        rs.getBoolean("requires_description"),
                        rs.getBoolean("sales_taxable"),
                        PricingMode.valueOf(rs.getString("pricing_mode").toUpperCase(Locale.ROOT))));
            }
            return result;
        } catch (SQLException e) {
            log.error("Failed to load merchandise", e);
            throw new RuntimeException("Failed to load merchandise", e);
        }
    }
}
