package com.palmer.billingstatementgenerator.models.catalog;

import java.math.BigDecimal;

public class ServicePackage {
    private final int id;
    private final int sortOrder;
    private final String name;
    private final BigDecimal defaultCost;

    public ServicePackage(int id, int sortOrder, String name, BigDecimal defaultCost) {
        this.id = id;
        this.sortOrder = sortOrder;
        this.name = name;
        this.defaultCost = defaultCost;
    }

    public int getId() { return id; }
    public int getSortOrder() { return sortOrder; }
    public String getName() { return name; }
    public BigDecimal getDefaultCost() { return defaultCost; }
}
