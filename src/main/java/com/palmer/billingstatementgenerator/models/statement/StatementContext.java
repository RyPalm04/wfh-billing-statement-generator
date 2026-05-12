package com.palmer.billingstatementgenerator.models.statement;

import com.palmer.billingstatementgenerator.dao.*;
import com.palmer.billingstatementgenerator.db.Database;
import com.palmer.billingstatementgenerator.models.lineitems.CashAdvanceLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.MerchandiseLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.ServiceLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.SpecialChargeLineItem;
import com.palmer.billingstatementgenerator.util.AppConfig;

public final class StatementContext {
    private static Statement current;

    private StatementContext() {}

    public static synchronized void init() {
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
    }

    public static Statement current() {
        if (current == null) {
            throw new IllegalStateException("StatementContext.init() has not been called");
        }
        return current;
    }
}
