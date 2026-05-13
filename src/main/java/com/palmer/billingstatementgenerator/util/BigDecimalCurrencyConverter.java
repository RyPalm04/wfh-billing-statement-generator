package com.palmer.billingstatementgenerator.util;

import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * A JavaFX {@link StringConverter} for {@link BigDecimal} values formatted as currency.
 * Used with {@link javafx.scene.control.TextFormatter} on price input fields to display
 * values in currency format and parse user input back to {@link BigDecimal}.
 *
 * <p>Formatting uses the system's default currency locale via {@link NumberFormat#getCurrencyInstance()}.
 * Parsing strips all non-numeric characters (except decimal points) before converting.</p>
 */
public class BigDecimalCurrencyConverter extends StringConverter<BigDecimal> {

    private static final NumberFormat DOLLAR_FORMATTER = NumberFormat.getCurrencyInstance();

    /**
     * Converts a {@link BigDecimal} value to a formatted currency string.
     *
     * @param value the value to format, or null
     * @return the formatted currency string, or an empty string if value is null
     */
    @Override
    public String toString(BigDecimal value) {
        return value == null ? "" : DOLLAR_FORMATTER.format(value);
    }

    /**
     * Parses a currency string into a {@link BigDecimal} by stripping non-numeric
     * characters before converting.
     *
     * @param text the input string, potentially containing currency symbols or commas
     * @return the parsed {@link BigDecimal}, or null if the input is blank or unparseable
     */
    @Override
    public BigDecimal fromString(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        String raw = text.replaceAll("[^\\d.]", "");
        return raw.isEmpty() ? null : new BigDecimal(raw);
    }
}