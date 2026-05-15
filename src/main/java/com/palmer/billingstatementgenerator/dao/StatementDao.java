package com.palmer.billingstatementgenerator.dao;

import com.palmer.billingstatementgenerator.models.catalog.ServicePackage;
import com.palmer.billingstatementgenerator.models.lineitems.CashAdvanceLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.MerchandiseLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.ServiceLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.SpecialChargeLineItem;
import com.palmer.billingstatementgenerator.models.statement.SavedStatementSummary;
import com.palmer.billingstatementgenerator.models.statement.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * DAO for persisting and loading {@link Statement}
 * records. Each save operation writes the statement header and all selected line items
 * atomically within a single transaction.
 */
public class StatementDao {

    private static final Logger log = LoggerFactory.getLogger(StatementDao.class);

    private static final String INSERT_STATEMENT =
            "INSERT INTO saved_statements (control_number, services_for_name, date_of_death, " +
                    "place_of_death, service_date, reason_for_embalming, package_id, sales_tax_rate, payment) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_STATEMENT =
            "UPDATE saved_statements SET services_for_name = ?, date_of_death = ?, place_of_death = ?, " +
                    "service_date = ?, reason_for_embalming = ?, package_id = ?, sales_tax_rate = ?, " +
                    "payment = ?, saved_at = CURRENT_TIMESTAMP WHERE id = ?";

    private static final String DELETE_LINE_ITEMS =
            "DELETE FROM saved_statement_services WHERE statement_id = ?; " +
                    "DELETE FROM saved_statement_merchandise WHERE statement_id = ?; " +
                    "DELETE FROM saved_statement_special_charges WHERE statement_id = ?; " +
                    "DELETE FROM saved_statement_cash_advances WHERE statement_id = ?";

    private static final String INSERT_SERVICE =
            "INSERT INTO saved_statement_services (statement_id, service_id, in_package) VALUES (?, ?, ?)";

    private static final String INSERT_MERCHANDISE =
            "INSERT INTO saved_statement_merchandise (statement_id, merchandise_id, price, description) " +
                    "VALUES (?, ?, ?, ?)";

    private static final String INSERT_SPECIAL_CHARGE =
            "INSERT INTO saved_statement_special_charges (statement_id, special_charge_id, price, description) " +
                    "VALUES (?, ?, ?, ?)";

    private static final String INSERT_CASH_ADVANCE =
            "INSERT INTO saved_statement_cash_advances (statement_id, cash_advance_id, amount, provider) " +
                    "VALUES (?, ?, ?, ?)";

    private static final String SELECT_ALL_SUMMARIES =
            "SELECT id, control_number, services_for_name, service_date, saved_at " +
                    "FROM saved_statements ORDER BY saved_at DESC";

    private static final String SELECT_STATEMENT =
            "SELECT id, control_number, services_for_name, date_of_death, place_of_death, " +
                    "service_date, reason_for_embalming, package_id, sales_tax_rate, payment " +
                    "FROM saved_statements WHERE id = ?";

    private static final String SELECT_SERVICES =
            "SELECT service_id, in_package FROM saved_statement_services WHERE statement_id = ?";

    private static final String SELECT_MERCHANDISE =
            "SELECT merchandise_id, price, description FROM saved_statement_merchandise WHERE statement_id = ?";

    private static final String SELECT_SPECIAL_CHARGES =
            "SELECT special_charge_id, price, description FROM saved_statement_special_charges WHERE statement_id = ?";

    private static final String SELECT_CASH_ADVANCES =
            "SELECT cash_advance_id, amount, provider FROM saved_statement_cash_advances WHERE statement_id = ?";

    private static final String SELECT_MAX_CONTROL_NUMBER =
            "SELECT MAX(control_number) FROM saved_statements";

    private final DataSource dataSource;

    /**
     * @param dataSource
     *         the application data source
     */
    public StatementDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns the next available control number, computed as {@code MAX(control_number) + 1}.
     * Returns {@code 1} if no statements have been saved yet.
     *
     * @return the next control number
     *
     * @throws RuntimeException
     *         if the query fails
     */
    public int nextControlNumber() {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_MAX_CONTROL_NUMBER);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() && rs.getObject(1) != null ? rs.getInt(1) + 1 : 1;
        } catch (SQLException e) {
            log.error("Failed to compute next control number", e);
            throw new RuntimeException("Failed to compute next control number", e);
        }
    }

    /**
     * Saves a new statement and all its selected line items within a single transaction.
     * The statement's control number must already be set.
     *
     * @param stmt
     *         the statement to save
     *
     * @return the generated database ID of the saved statement
     *
     * @throws RuntimeException
     *         if the save fails
     */
    public int save(Statement stmt) {
        log.info("Saving new statement, control number {}", stmt.getControlNumber());
        try (Connection c = dataSource.getConnection()) {
            c.setAutoCommit(false);
            try {
                int savedId = insertHeader(c, stmt);
                insertLineItems(c, savedId, stmt);
                c.commit();
                log.info("Saved statement id={}", savedId);
                return savedId;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            log.error("Failed to save statement", e);
            throw new RuntimeException("Failed to save statement", e);
        }
    }

    /**
     * Updates an existing saved statement in place, replacing all line items.
     *
     * @param savedId
     *         the database ID of the statement to update
     * @param stmt
     *         the statement with updated values
     *
     * @throws RuntimeException
     *         if the update fails
     */
    public void update(int savedId, Statement stmt) {
        log.info("Updating statement id={}", savedId);
        try (Connection c = dataSource.getConnection()) {
            c.setAutoCommit(false);
            try {
                updateHeader(c, savedId, stmt);
                deleteLineItems(c, savedId);
                insertLineItems(c, savedId, stmt);
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            log.error("Failed to update statement id={}", savedId, e);
            throw new RuntimeException("Failed to update statement " + savedId, e);
        }
    }

    /**
     * Loads a saved statement into the provided {@link Statement},
     * restoring all header fields and marking the appropriate line items as selected.
     * The statement's line item lists must already be populated with catalog data.
     * Returns the saved package ID (or 0 if none) so the caller can resolve the
     * correct {@link ServicePackage} instance from the existing catalog list.
     *
     * @param savedId
     *         the database ID of the statement to load
     * @param stmt
     *         the statement to populate
     *
     * @return the saved package ID, or 0 if no package was selected
     *
     * @throws RuntimeException
     *         if the load fails
     */
    public int load(int savedId, Statement stmt) {
        log.info("Loading statement id={}", savedId);
        try (Connection c = dataSource.getConnection()) {
            int packageId = loadHeader(c, savedId, stmt);
            loadServices(c, savedId, stmt);
            loadMerchandise(c, savedId, stmt);
            loadSpecialCharges(c, savedId, stmt);
            loadCashAdvances(c, savedId, stmt);
            return packageId;
        } catch (SQLException e) {
            log.error("Failed to load statement id={}", savedId, e);
            throw new RuntimeException("Failed to load statement " + savedId, e);
        }
    }

    /**
     * Returns a list of all saved statement summaries ordered by most recently saved.
     *
     * @return a non-null, possibly empty list of {@link SavedStatementSummary} objects
     *
     * @throws RuntimeException
     *         if the query fails
     */
    public List<SavedStatementSummary> findAll() {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_ALL_SUMMARIES);
             ResultSet rs = ps.executeQuery()) {
            List<SavedStatementSummary> result = new ArrayList<>();
            while (rs.next()) {
                Date serviceDate = rs.getDate("service_date");
                Timestamp savedAt = rs.getTimestamp("saved_at");
                result.add(new SavedStatementSummary(
                        rs.getInt("id"),
                        rs.getInt("control_number"),
                        rs.getString("services_for_name"),
                        serviceDate != null ? serviceDate.toLocalDate() : null,
                        savedAt != null ? savedAt.toLocalDateTime() : null));
            }
            return result;
        } catch (SQLException e) {
            log.error("Failed to load statement summaries", e);
            throw new RuntimeException("Failed to load statement summaries", e);
        }
    }

    private int insertHeader(Connection c,
                             Statement stmt)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(INSERT_STATEMENT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, stmt.getControlNumber());
            ps.setString(2, stmt.getServicesForName());
            ps.setDate(3, stmt.getDateOfDeath() != null ? Date.valueOf(stmt.getDateOfDeath()) : null);
            ps.setString(4, stmt.getPlaceOfDeath());
            ps.setDate(5, stmt.getServiceDate() != null ? Date.valueOf(stmt.getServiceDate()) : null);
            ps.setString(6, stmt.getReasonForEmbalming());
            if (stmt.getSelectedPackage() != null) {
                ps.setInt(7, stmt.getSelectedPackage().getId());
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }
            ps.setBigDecimal(8, stmt.getSalesTaxRate());
            ps.setBigDecimal(9, stmt.getPayment());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    private void updateHeader(Connection c, int savedId,
                              Statement stmt)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(UPDATE_STATEMENT)) {
            ps.setString(1, stmt.getServicesForName());
            ps.setDate(2, stmt.getDateOfDeath() != null ? Date.valueOf(stmt.getDateOfDeath()) : null);
            ps.setString(3, stmt.getPlaceOfDeath());
            ps.setDate(4, stmt.getServiceDate() != null ? Date.valueOf(stmt.getServiceDate()) : null);
            ps.setString(5, stmt.getReasonForEmbalming());
            if (stmt.getSelectedPackage() != null) {
                ps.setInt(6, stmt.getSelectedPackage().getId());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            ps.setBigDecimal(7, stmt.getSalesTaxRate());
            ps.setBigDecimal(8, stmt.getPayment());
            ps.setInt(9, savedId);
            ps.executeUpdate();
        }
    }

    private void deleteLineItems(Connection c, int savedId) throws SQLException {
        for (String sql : DELETE_LINE_ITEMS.split(";")) {
            try (PreparedStatement ps = c.prepareStatement(sql.trim())) {
                ps.setInt(1, savedId);
                ps.executeUpdate();
            }
        }
    }

    private void insertLineItems(Connection c, int savedId,
                                 Statement stmt)
            throws SQLException {
        for (ServiceLineItem item : stmt.getServices()) {
            if (!item.isSelected()) {
                continue;
            }
            try (PreparedStatement ps = c.prepareStatement(INSERT_SERVICE)) {
                ps.setInt(1, savedId);
                ps.setInt(2, item.getCatalog().getId());
                ps.setBoolean(3, item.isInPackage());
                ps.executeUpdate();
            }
        }
        for (MerchandiseLineItem item : stmt.getMerchandise()) {
            if (!item.isSelected()) {
                continue;
            }
            try (PreparedStatement ps = c.prepareStatement(INSERT_MERCHANDISE)) {
                ps.setInt(1, savedId);
                ps.setInt(2, item.getCatalog().getId());
                ps.setBigDecimal(3, item.getPrice());
                ps.setString(4, item.getDescription());
                ps.executeUpdate();
            }
        }
        for (SpecialChargeLineItem item : stmt.getSpecialCharges()) {
            if (!item.isSelected()) {
                continue;
            }
            try (PreparedStatement ps = c.prepareStatement(INSERT_SPECIAL_CHARGE)) {
                ps.setInt(1, savedId);
                ps.setInt(2, item.getCatalog().getId());
                ps.setBigDecimal(3, item.getPrice());
                ps.setString(4, item.getDescription());
                ps.executeUpdate();
            }
        }
        for (CashAdvanceLineItem item : stmt.getCashAdvances()) {
            if (!item.isSelected()) {
                continue;
            }
            try (PreparedStatement ps = c.prepareStatement(INSERT_CASH_ADVANCE)) {
                ps.setInt(1, savedId);
                ps.setInt(2, item.getCatalog().getId());
                ps.setBigDecimal(3, item.getAmount());
                ps.setString(4, item.getProvider());
                ps.executeUpdate();
            }
        }
    }

    private int loadHeader(Connection c, int savedId,
                           Statement stmt)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SELECT_STATEMENT)) {
            ps.setInt(1, savedId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new RuntimeException("Statement not found: " + savedId);
                }
                stmt.setControlNumber(rs.getInt("control_number"));
                stmt.setServicesForName(rs.getString("services_for_name"));
                Date dod = rs.getDate("date_of_death");
                stmt.setDateOfDeath(dod != null ? dod.toLocalDate() : null);
                stmt.setPlaceOfDeath(rs.getString("place_of_death"));
                Date sd = rs.getDate("service_date");
                stmt.setServiceDate(sd != null ? sd.toLocalDate() : null);
                stmt.setReasonForEmbalming(rs.getString("reason_for_embalming"));
                stmt.setSalesTaxRate(rs.getBigDecimal("sales_tax_rate"));
                stmt.setPayment(rs.getBigDecimal("payment"));
                int packageId = rs.getInt("package_id");
                return rs.wasNull() ? 0 : packageId;
            }
        }
    }

    private void loadServices(Connection c, int savedId,
                              Statement stmt)
            throws SQLException {
        Map<Integer, ServiceLineItem> byId = stmt.getServices().stream()
                .collect(Collectors.toMap(i -> i.getCatalog().getId(), Function.identity()));
        stmt.getServices().forEach(i -> {
            i.setSelected(false);
            i.setInPackage(false);
        });
        try (PreparedStatement ps = c.prepareStatement(SELECT_SERVICES)) {
            ps.setInt(1, savedId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ServiceLineItem item = byId.get(rs.getInt("service_id"));
                    if (item != null) {
                        item.setInPackage(rs.getBoolean("in_package"));
                        item.setSelected(true);
                    }
                }
            }
        }
    }

    private void loadMerchandise(Connection c, int savedId,
                                 Statement stmt)
            throws SQLException {
        Map<Integer, MerchandiseLineItem> byId = stmt.getMerchandise().stream()
                .collect(Collectors.toMap(i -> i.getCatalog().getId(), Function.identity()));
        stmt.getMerchandise().forEach(i -> i.setSelected(false));
        try (PreparedStatement ps = c.prepareStatement(SELECT_MERCHANDISE)) {
            ps.setInt(1, savedId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MerchandiseLineItem item = byId.get(rs.getInt("merchandise_id"));
                    if (item != null) {
                        item.setPrice(rs.getBigDecimal("price"));
                        item.setDescription(rs.getString("description"));
                        item.setSelected(true);
                    }
                }
            }
        }
    }

    private void loadSpecialCharges(Connection c, int savedId,
                                    Statement stmt)
            throws SQLException {
        Map<Integer, SpecialChargeLineItem> byId = stmt.getSpecialCharges().stream()
                .collect(Collectors.toMap(i -> i.getCatalog().getId(), Function.identity()));
        stmt.getSpecialCharges().forEach(i -> i.setSelected(false));
        try (PreparedStatement ps = c.prepareStatement(SELECT_SPECIAL_CHARGES)) {
            ps.setInt(1, savedId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SpecialChargeLineItem item = byId.get(rs.getInt("special_charge_id"));
                    if (item != null) {
                        item.setPrice(rs.getBigDecimal("price"));
                        item.setDescription(rs.getString("description"));
                        item.setSelected(true);
                    }
                }
            }
        }
    }

    private void loadCashAdvances(Connection c, int savedId,
                                  Statement stmt)
            throws SQLException {
        Map<Integer, CashAdvanceLineItem> byId = stmt.getCashAdvances().stream()
                .collect(Collectors.toMap(i -> i.getCatalog().getId(), Function.identity()));
        stmt.getCashAdvances().forEach(i -> i.setSelected(false));
        try (PreparedStatement ps = c.prepareStatement(SELECT_CASH_ADVANCES)) {
            ps.setInt(1, savedId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CashAdvanceLineItem item = byId.get(rs.getInt("cash_advance_id"));
                    if (item != null) {
                        item.setAmount(rs.getBigDecimal("amount"));
                        item.setProvider(rs.getString("provider"));
                        item.setSelected(true);
                    }
                }
            }
        }
    }
}