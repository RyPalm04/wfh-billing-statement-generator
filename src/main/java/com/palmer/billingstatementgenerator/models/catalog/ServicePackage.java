package com.palmer.billingstatementgenerator.models.catalog;

import java.math.BigDecimal;

/**
 * Immutable catalog record representing a pre-defined service package
 * (e.g. Full Burial, Cremation). A package bundles a set of services at a fixed price
 * and is selected via the combo box on the Services tab.
 */
public record ServicePackage(int id, int sortOrder, String name, BigDecimal defaultCost, boolean legacyPackage) {
}
