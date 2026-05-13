package com.palmer.billingstatementgenerator.models.catalog;

/**
 * Immutable catalog record representing a cash advance item (e.g. flowers, death certificates,
 * clergy honorarium). Cash advances have no default cost — the user enters the amount
 * and an optional provider on the Cash Advance Items tab.
 */
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
