package com.palmer.billingstatementgenerator.models.statement;

import com.palmer.billingstatementgenerator.dao.*;
import com.palmer.billingstatementgenerator.db.Database;
import com.palmer.billingstatementgenerator.models.lineitems.CashAdvanceLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.MerchandiseLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.ServiceLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.SpecialChargeLineItem;
import com.palmer.billingstatementgenerator.util.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton context holder for the current billing {@link Statement}.
 * Manages the lifecycle of the active statement, including initialization
 * from the database and reinitialization on full reset.
 *
 * <p>Must be initialized via {@link #init()} before {@link #current()} is called.
 * Calling {@link #init()} again creates a fresh statement, replacing any existing one.</p>
 */
public final class StatementContext {

    private static final Logger log = LoggerFactory.getLogger(StatementContext.class);
    private static Statement current;

    private StatementContext() {}

    /**
     * Initializes a new {@link Statement}, loading all catalog data from the database
     * and applying the configured sales tax rate from {@link AppConfig}.
     * Safe to call multiple times; each call replaces the previous statement.
     */
    public static synchronized void init() {
        log.info("Initializing statement context");
        Statement statement = new Statement();
        statement.setSalesTaxRate(AppConfig.getSalesTaxRate());
        new ServiceDao(Database.get()).findAll()
                .forEach(s -> statement.getServices().add(new ServiceLineItem(s)));
        new MerchandiseDao(Database.get()).findAll()
                .forEach(m -> statement.getMerchandise().add(new MerchandiseLineItem(m)));
        new SpecialChargeDao(Database.get()).findAll()
                .forEach(sc -> statement.getSpecialCharges().add(new SpecialChargeLineItem(sc)));
        new CashAdvanceDao(Database.get()).findAll()
                .forEach(ca -> statement.getCashAdvances().add(new CashAdvanceLineItem(ca)));
        current = statement;
        log.debug("Statement context loaded — services={}, merchandise={}, specialCharges={}, cashAdvances={}",
                statement.getServices().size(), statement.getMerchandise().size(),
                statement.getSpecialCharges().size(), statement.getCashAdvances().size());
    }

    /**
     * Returns the current active {@link Statement}.
     *
     * @return the current statement
     * @throws IllegalStateException if {@link #init()} has not been called
     */
    public static Statement current() {
        if (current == null) {
            throw new IllegalStateException("StatementContext.init() has not been called");
        }
        return current;
    }
}