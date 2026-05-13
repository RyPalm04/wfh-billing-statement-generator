package com.palmer.billingstatementgenerator.models.catalog;

import java.math.BigDecimal;

/**
 * Immutable catalog record representing a merchandise item (e.g. casket, urn, vault).
 * Items may have a fixed default cost or require the user to enter a price.
 * Taxable items are included in the sales tax calculation.
 * Items with {@link PricingMode#PER_UNIT} display a quantity spinner instead of a price field.
 */
public class Merchandise {
    public enum PricingMode { FLAT, PER_UNIT }

    private final int id;
    private final int sortOrder;
    private final String name;
    private final BigDecimal defaultCost;
    private final boolean descriptionRequired;
    private final boolean salesTaxable;
    private final PricingMode pricingMode;

    public Merchandise(int id, int sortOrder, String name, BigDecimal defaultCost,
                       boolean descriptionRequired, boolean salesTaxable, PricingMode pricingMode) {
        this.id = id;
        this.sortOrder = sortOrder;
        this.name = name;
        this.defaultCost = defaultCost;
        this.descriptionRequired = descriptionRequired;
        this.salesTaxable = salesTaxable;
        this.pricingMode = pricingMode;
    }

    public int getId() { return id; }
    public int getSortOrder() { return sortOrder; }
    public String getName() { return name; }
    public BigDecimal getDefaultCost() { return defaultCost; }
    public boolean isDescriptionRequired() { return descriptionRequired; }
    public boolean isSalesTaxable() { return salesTaxable; }
    public PricingMode getPricingMode() { return pricingMode; }
}
