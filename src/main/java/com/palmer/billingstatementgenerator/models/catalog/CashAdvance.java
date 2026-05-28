package com.palmer.billingstatementgenerator.models.catalog;

/**
 * Immutable catalog record representing a cash advance item (e.g. flowers, death certificates,
 * clergy honorarium). Cash advances have no default cost — the user enters the amount
 * and an optional provider on the Cash Advance Items tab.
 */
public record CashAdvance(int id, int sortOrder, String name) {
}
