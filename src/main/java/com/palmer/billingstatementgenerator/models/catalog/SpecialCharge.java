package com.palmer.billingstatementgenerator.models.catalog;

import java.math.BigDecimal;

/**
 * Immutable catalog record representing a special charge (e.g. mileage, cremation fee,
 * grave setup). Items without a default cost require the user to enter a price.
 * Items marked as requiring a description display a text field on the Special Charges tab.
 */
public record SpecialCharge(int id, int sortOrder, String name, BigDecimal defaultCost, boolean descriptionRequired) {
}
