package com.palmer.billingstatementgenerator.models.catalog;

import java.math.BigDecimal;

/**
 * Immutable catalog record representing a special charge (e.g. mileage, cremation fee,
 * grave setup). Items without a default cost require the user to enter a price.
 * Items marked as requiring a description display a text field on the Special Charges tab.
 */
public class SpecialCharge {
    private final int id;
    private final int sortOrder;
    private final String name;
    private final BigDecimal defaultCost;
    private final boolean descriptionRequired;

    public SpecialCharge(int id, int sortOrder, String name, BigDecimal defaultCost, boolean descriptionRequired) {
        this.id = id;
        this.sortOrder = sortOrder;
        this.name = name;
        this.defaultCost = defaultCost;
        this.descriptionRequired = descriptionRequired;
    }

    public int getId() { return id; }
    public int getSortOrder() { return sortOrder; }
    public String getName() { return name; }
    public BigDecimal getDefaultCost() { return defaultCost; }
    public boolean isDescriptionRequired() { return descriptionRequired; }
}
