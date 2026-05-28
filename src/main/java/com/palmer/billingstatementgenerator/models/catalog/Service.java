package com.palmer.billingstatementgenerator.models.catalog;

import com.palmer.billingstatementgenerator.models.lineitems.ServiceLineItem;

import java.math.BigDecimal;

/**
 * Immutable catalog record representing a funeral service or facility offering
 * (e.g. embalming, transfer, graveside service). Loaded from the database at startup
 * and wrapped in a {@link ServiceLineItem}
 * for user selection.
 */
public record Service(int id, int sortOrder, String name, BigDecimal defaultCost, boolean requiresDescription, boolean includedInPackage) {
}
