package com.palmer.billingstatementgenerator.models.catalog;

public class CashAdvance {
    private final int id;
    private final int sortOrder;
    private final String name;

    public CashAdvance(int id, int sortOrder, String name) {
        this.id = id;
        this.sortOrder = sortOrder;
        this.name = name;
    }

    public int getId() { return id; }
    public int getSortOrder() { return sortOrder; }
    public String getName() { return name; }
}
