package com.palmer.billingstatementgenerator.models.statement;

import com.palmer.billingstatementgenerator.models.lineitems.CashAdvanceLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.MerchandiseLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.ServiceLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.SpecialChargeLineItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class StatementCalculator {

    private StatementCalculator() {}

    public static BigDecimal servicesTotal(Statement stmt) {
        BigDecimal packageCost = stmt.getSelectedPackage() != null
            ? stmt.getSelectedPackage().getDefaultCost()
            : BigDecimal.ZERO;

        BigDecimal servicesTotal = stmt.getServices().stream()
            .filter(ServiceLineItem::isSelected)
            .map(s -> s.getCatalog().getDefaultCost())
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return safeAdd(packageCost, servicesTotal);
    }

    public static BigDecimal merchandiseTotal(Statement stmt) {
        return stmt.getMerchandise().stream()
            .filter(MerchandiseLineItem::isSelected)
            .map(MerchandiseLineItem::getPrice)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal specialChargesTotal(Statement stmt) {
        return stmt.getSpecialCharges().stream()
            .filter(SpecialChargeLineItem::isSelected)
            .map(SpecialChargeLineItem::getPrice)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal cashAdvancesTotal(Statement stmt) {
        return stmt.getCashAdvances().stream()
            .filter(CashAdvanceLineItem::isSelected)
            .map(CashAdvanceLineItem::getAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal salesTax(Statement stmt) {
        BigDecimal taxableTotal = stmt.getMerchandise().stream()
            .filter(MerchandiseLineItem::isSelected)
            .filter(m -> m.getCatalog().isSalesTaxable())
            .map(MerchandiseLineItem::getPrice)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return taxableTotal.multiply(stmt.getSalesTaxRate())
            .setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal subtotal(Statement stmt) {
        return safeAdd(
            servicesTotal(stmt),
            merchandiseTotal(stmt),
            specialChargesTotal(stmt),
            cashAdvancesTotal(stmt),
            salesTax(stmt)
        );
    }

    public static BigDecimal finalTotal(Statement stmt) {
        BigDecimal sub = subtotal(stmt);
        BigDecimal payment = stmt.getPayment() != null ? stmt.getPayment() : BigDecimal.ZERO;
        return sub.subtract(payment).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal safeAdd(BigDecimal... values) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal v : values) {
            if (v != null) total = total.add(v);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}