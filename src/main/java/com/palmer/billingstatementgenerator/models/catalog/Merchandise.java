package com.palmer.billingstatementgenerator.models.catalog;

import java.math.BigDecimal;

/**
 * Immutable catalog record representing a merchandise item (e.g. casket, urn, vault).
 * Items may have a fixed default cost or require the user to enter a price.
 * Taxable items are included in the sales tax calculation.
 * Items with {@link PricingMode#PER_UNIT} display a quantity spinner instead of a price field.
 */
public record Merchandise(int id, int sortOrder, String name, BigDecimal defaultCost, boolean descriptionRequired,
                          boolean salesTaxable, PricingMode pricingMode) {

    public enum PricingMode {FLAT, PER_UNIT}
}
