package com.palmer.billingstatementgenerator.util;

import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class BigDecimalCurrencyConverter extends StringConverter<BigDecimal> {
    private static final NumberFormat DOLLAR_FORMATTER = NumberFormat.getCurrencyInstance();

    @Override
    public String toString(BigDecimal value) {
        return value == null ? "" : DOLLAR_FORMATTER.format(value);
    }

    @Override
    public BigDecimal fromString(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        String raw = text.replaceAll("[^\\d.]", "");
        return raw.isEmpty() ? null : new BigDecimal(raw);
    }
}