package com.palmer.billingstatementgenerator.models.catalog;

import java.math.BigDecimal;

/**
 * Immutable catalog record representing a funeral service or facility offering
 * (e.g. embalming, transfer, graveside service). Loaded from the database at startup
 * and wrapped in a {@link com.palmer.billingstatementgenerator.models.lineitems.ServiceLineItem}
 * for user selection.
 */
public class Service {
    private final int id;
    private final int sortOrder;
    private final String name;
    private final BigDecimal defaultCost;
    private final boolean includedInPackage;

    public Service(int id, int sortOrder, String name, BigDecimal defaultCost, boolean includedInPackage) {
        this.id = id;
        this.sortOrder = sortOrder;
        this.name = name;
        this.defaultCost = defaultCost;
        this.includedInPackage = includedInPackage;
    }

    public int getId() { return id; }
    public int getSortOrder() { return sortOrder; }
    public String getName() { return name; }
    public BigDecimal getDefaultCost() { return defaultCost; }
    public boolean isIncludedInPackage() { return includedInPackage; }
}
