package com.palmer.billingstatementgenerator.models.statement

import com.palmer.billingstatementgenerator.models.catalog.CashAdvance
import com.palmer.billingstatementgenerator.models.catalog.Merchandise
import com.palmer.billingstatementgenerator.models.catalog.Service
import com.palmer.billingstatementgenerator.models.catalog.ServicePackage
import com.palmer.billingstatementgenerator.models.catalog.SpecialCharge
import com.palmer.billingstatementgenerator.models.lineitems.CashAdvanceLineItem
import com.palmer.billingstatementgenerator.models.lineitems.MerchandiseLineItem
import com.palmer.billingstatementgenerator.models.lineitems.ServiceLineItem
import com.palmer.billingstatementgenerator.models.lineitems.SpecialChargeLineItem
import spock.lang.Specification

class StatementCalculatorSpec extends Specification {

    private static ServicePackage pkg(BigDecimal cost) {
        new ServicePackage(1, 1, "Package", cost)
    }

    private static ServiceLineItem serviceItem(BigDecimal cost, boolean selected = false) {
        def item = new ServiceLineItem(new Service(1, 1, "Service", cost, false))
        item.selected = selected
        item
    }

    private static MerchandiseLineItem merchItem(BigDecimal cost, boolean selected = false, boolean taxable = false) {
        def item = new MerchandiseLineItem(new Merchandise(1, 1, "Merchandise", cost, false, taxable, Merchandise.PricingMode.FLAT))
        item.selected = selected
        item
    }

    private static SpecialChargeLineItem chargeItem(BigDecimal cost, boolean selected = false) {
        def item = new SpecialChargeLineItem(new SpecialCharge(1, 1, "Charge", cost, false))
        item.selected = selected
        item
    }

    private static CashAdvanceLineItem advanceItem(BigDecimal amount, boolean selected = false) {
        def item = new CashAdvanceLineItem(new CashAdvance(1, 1, "Advance"))
        item.amount = amount
        item.selected = selected
        item
    }

    def "servicesTotal is zero with empty statement"() {
        expect:
        StatementCalculator.servicesTotal(new Statement()) == 0.00G
    }

    def "servicesTotal includes package cost when no services are selected"() {
        given:
        def stmt = new Statement()
        stmt.selectedPackage = pkg(4500.00G)

        expect:
        StatementCalculator.servicesTotal(stmt) == 4500.00G
    }

    def "servicesTotal sums only selected service items when no package is set"() {
        given:
        def stmt = new Statement()
        stmt.services.addAll(
                serviceItem(750.00G, true),
                serviceItem(300.00G, false)
        )

        expect:
        StatementCalculator.servicesTotal(stmt) == 750.00G
    }

    def "servicesTotal skips selected services with null defaultCost"() {
        given:
        def stmt = new Statement()
        stmt.services.addAll(
                serviceItem(500.00G, true),
                serviceItem(null, true)
        )

        expect:
        StatementCalculator.servicesTotal(stmt) == 500.00G
    }

    def "servicesTotal combines package cost with selected services"() {
        given:
        def stmt = new Statement()
        stmt.selectedPackage = pkg(2000.00G)
        stmt.services.add(serviceItem(500.00G, true))

        expect:
        StatementCalculator.servicesTotal(stmt) == 2500.00G
    }

    def "merchandiseTotal is zero when no items are selected"() {
        given:
        def stmt = new Statement()
        stmt.merchandise.add(merchItem(3000.00G, false))

        expect:
        StatementCalculator.merchandiseTotal(stmt) == 0.00G
    }

    def "merchandiseTotal sums selected items using their effective price"() {
        given:
        def stmt = new Statement()
        def overridden = merchItem(500.00G, true)
        overridden.price = 450.00G
        stmt.merchandise.addAll(
                merchItem(3000.00G, true),
                overridden,
                merchItem(1200.00G, false)
        )

        expect:
        StatementCalculator.merchandiseTotal(stmt) == 3450.00G
    }

    def "specialChargesTotal is zero when no charges are selected"() {
        given:
        def stmt = new Statement()
        stmt.specialCharges.add(chargeItem(50.00G, false))

        expect:
        StatementCalculator.specialChargesTotal(stmt) == 0.00G
    }

    def "specialChargesTotal sums only selected charges"() {
        given:
        def stmt = new Statement()
        stmt.specialCharges.addAll(
                chargeItem(50.00G, true),
                chargeItem(25.00G, false)
        )

        expect:
        StatementCalculator.specialChargesTotal(stmt) == 50.00G
    }

    def "cashAdvancesTotal is zero when no advances are selected"() {
        given:
        def stmt = new Statement()
        stmt.cashAdvances.add(advanceItem(200.00G, false))

        expect:
        StatementCalculator.cashAdvancesTotal(stmt) == 0.00G
    }

    def "cashAdvancesTotal sums only selected advances"() {
        given:
        def stmt = new Statement()
        stmt.cashAdvances.addAll(
                advanceItem(200.00G, true),
                advanceItem(75.00G, true),
                advanceItem(150.00G, false)
        )

        expect:
        StatementCalculator.cashAdvancesTotal(stmt) == 275.00G
    }

    def "cashAdvancesTotal skips selected advances with null amount"() {
        given:
        def stmt = new Statement()
        stmt.cashAdvances.addAll(
                advanceItem(100.00G, true),
                advanceItem(null, true)
        )

        expect:
        StatementCalculator.cashAdvancesTotal(stmt) == 100.00G
    }

    def "salesTax is zero when tax rate is zero"() {
        given:
        def stmt = new Statement()
        stmt.salesTaxRate = 0.00G
        stmt.merchandise.add(merchItem(3000.00G, true, true))

        expect:
        StatementCalculator.salesTax(stmt) == 0.00G
    }

    def "salesTax applies only to selected taxable merchandise"() {
        given:
        def stmt = new Statement()
        stmt.salesTaxRate = 0.08G
        stmt.merchandise.addAll(
                merchItem(1000.00G, true, true),    // taxable, selected   → taxable base 1000
                merchItem(500.00G, true, false),    // non-taxable, selected
                merchItem(250.00G, false, true)     // taxable, not selected
        )

        expect:
        StatementCalculator.salesTax(stmt) == 80.00G
    }

    def "salesTax rounds half-up to two decimal places"() {
        given:
        def stmt = new Statement()
        stmt.salesTaxRate = 0.0825G
        stmt.merchandise.add(merchItem(10.00G, true, true))  // 10 * 0.0825 = 0.8250 → 0.83

        expect:
        StatementCalculator.salesTax(stmt) == 0.83G
    }

    def "subtotal sums all sections plus sales tax"() {
        given:
        def stmt = new Statement()
        stmt.salesTaxRate = 0.10G
        stmt.selectedPackage = pkg(1000.00G)
        stmt.merchandise.add(merchItem(500.00G, true, true))   // 500 merch + 50 tax
        stmt.specialCharges.add(chargeItem(50.00G, true))
        stmt.cashAdvances.add(advanceItem(100.00G, true))
        // 1000 + 500 + 50 + 100 + 50 = 1700.00

        expect:
        StatementCalculator.subtotal(stmt) == 1700.00G
    }

    def "subtotal is zero for empty statement"() {
        expect:
        StatementCalculator.subtotal(new Statement()) == 0.00G
    }

    def "finalTotal subtracts payment from subtotal"() {
        given:
        def stmt = new Statement()
        stmt.selectedPackage = pkg(2000.00G)
        stmt.payment = 500.00G

        expect:
        StatementCalculator.finalTotal(stmt) == 1500.00G
    }

    def "finalTotal equals subtotal when payment is zero"() {
        given:
        def stmt = new Statement()
        stmt.selectedPackage = pkg(2000.00G)
        stmt.payment = BigDecimal.ZERO

        expect:
        StatementCalculator.finalTotal(stmt) == 2000.00G
    }

    def "finalTotal equals subtotal when payment is null"() {
        given:
        def stmt = new Statement()
        stmt.selectedPackage = pkg(2000.00G)
        stmt.payment = null

        expect:
        StatementCalculator.finalTotal(stmt) == 2000.00G
    }
}