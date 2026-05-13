package com.palmer.billingstatementgenerator.util

import spock.lang.Specification

class BigDecimalCurrencyConverterSpec extends Specification {

    def converter = new BigDecimalCurrencyConverter()

    def "toString returns empty string for null"() {
        expect:
        converter.toString(null) == ""
    }

    def "toString round-trips back through fromString"() {
        given:
        def original = 1234.56G

        expect:
        converter.fromString(converter.toString(original)) == original
    }

    def "toString round-trips zero"() {
        expect:
        converter.fromString(converter.toString(BigDecimal.ZERO)) == 0G
    }

    def "fromString returns null for null input"() {
        expect:
        converter.fromString(null) == null
    }

    def "fromString returns null for empty string"() {
        expect:
        converter.fromString("") == null
    }

    def "fromString returns null for blank string"() {
        expect:
        converter.fromString("   ") == null
    }

    def "fromString parses plain numeric string"() {
        expect:
        converter.fromString("1234.56") == 1234.56G
    }

    def "fromString strips currency symbol and commas"() {
        expect:
        converter.fromString('$1,234.56') == 1234.56G
    }

    def "fromString handles whole number with no decimal"() {
        expect:
        converter.fromString("500") == 500G
    }

    def "fromString handles string that becomes empty after stripping"() {
        expect:
        converter.fromString('$') == null
    }
}