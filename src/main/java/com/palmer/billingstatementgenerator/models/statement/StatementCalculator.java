package com.palmer.billingstatementgenerator.models.statement;

import com.palmer.billingstatementgenerator.models.lineitems.CashAdvanceLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.MerchandiseLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.ServiceLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.SpecialChargeLineItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Stateless utility class for calculating financial totals from a {@link Statement}.
 * All methods operate purely on the provided statement and have no side effects.
 * Results are rounded to two decimal places using {@link RoundingMode#HALF_UP}.
 */
public final class StatementCalculator {

    private StatementCalculator() {
    }

    /**
     * Calculates the total for services and facilities: the selected package cost
     * plus the sum of individually selected services that are not covered by the package.
     * Services with {@code inPackage == true} are excluded from the individual sum
     * because their cost is already included in the package price.
     *
     * @param stmt
     *         the statement to calculate from
     *
     * @return the services total, never null
     */
    public static BigDecimal servicesTotal(Statement stmt) {
        BigDecimal packageCost = stmt.getSelectedPackage() != null
                ? stmt.getSelectedPackage().getDefaultCost()
                : BigDecimal.ZERO;

        BigDecimal servicesTotal = stmt.getServices().stream()
                .filter(ServiceLineItem::isSelected)
                .filter(s -> !s.isInPackage())
                .map(s -> s.getCatalog().getDefaultCost())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return safeAdd(packageCost, servicesTotal);
    }

    /**
     * Calculates the total for all selected merchandise items using their effective prices.
     *
     * @param stmt
     *         the statement to calculate from
     *
     * @return the merchandise total, never null
     */
    public static BigDecimal merchandiseTotal(Statement stmt) {
        return stmt.getMerchandise().stream()
                .filter(MerchandiseLineItem::isSelected)
                .map(MerchandiseLineItem::getPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates the total for all selected special charges using their effective prices.
     *
     * @param stmt
     *         the statement to calculate from
     *
     * @return the special charges total, never null
     */
    public static BigDecimal specialChargesTotal(Statement stmt) {
        return stmt.getSpecialCharges().stream()
                .filter(SpecialChargeLineItem::isSelected)
                .map(SpecialChargeLineItem::getPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates the total for all selected cash advance items using their entered amounts.
     *
     * @param stmt
     *         the statement to calculate from
     *
     * @return the cash advances total, never null
     */
    public static BigDecimal cashAdvancesTotal(Statement stmt) {
        return stmt.getCashAdvances().stream()
                .filter(CashAdvanceLineItem::isSelected)
                .map(CashAdvanceLineItem::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates the sales tax on selected merchandise items that are marked as taxable,
     * using the statement's configured sales tax rate.
     *
     * @param stmt
     *         the statement to calculate from
     *
     * @return the sales tax amount, never null
     */
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

    /**
     * Calculates the subtotal as the sum of services, merchandise, special charges,
     * cash advances, and sales tax.
     *
     * @param stmt
     *         the statement to calculate from
     *
     * @return the subtotal, never null
     */
    public static BigDecimal subtotal(Statement stmt) {
        return safeAdd(
                servicesTotal(stmt),
                merchandiseTotal(stmt),
                specialChargesTotal(stmt),
                cashAdvancesTotal(stmt),
                salesTax(stmt)
        );
    }

    /**
     * Calculates the final total as the subtotal minus any down payment.
     *
     * @param stmt
     *         the statement to calculate from
     *
     * @return the final total, never null
     */
    public static BigDecimal finalTotal(Statement stmt) {
        BigDecimal sub = subtotal(stmt);
        BigDecimal payment = stmt.getPayment() != null ? stmt.getPayment() : BigDecimal.ZERO;
        return sub.subtract(payment).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Safely sums an array of {@link BigDecimal} values, ignoring nulls,
     * and returns the result scaled to two decimal places.
     *
     * @param values
     *         the values to sum
     *
     * @return the sum, never null
     */
    private static BigDecimal safeAdd(BigDecimal... values) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal v : values) {
            if (v != null) {
                total = total.add(v);
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}