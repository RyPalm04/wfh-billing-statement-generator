package com.palmer.billingstatementgenerator.models.statement;

import com.palmer.billingstatementgenerator.models.catalog.ServicePackage;
import com.palmer.billingstatementgenerator.models.lineitems.CashAdvanceLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.MerchandiseLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.ServiceLineItem;
import com.palmer.billingstatementgenerator.models.lineitems.SpecialChargeLineItem;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Aggregate root for a billing statement. Holds all user-entered service information
 * and observable collections of line items for each billing category.
 *
 * <p>A single instance is managed by {@link StatementContext} for the lifetime of the
 * application session. Financial totals are computed on demand by {@link StatementCalculator}
 * rather than stored here.</p>
 */
public class Statement {
    private final IntegerProperty controlNumber = new SimpleIntegerProperty();
    private final StringProperty servicesForName = new SimpleStringProperty("");
    private final ObjectProperty<LocalDate> dateOfDeath = new SimpleObjectProperty<>();
    private final StringProperty placeOfDeath = new SimpleStringProperty("");
    private final ObjectProperty<LocalDate> serviceDate = new SimpleObjectProperty<>();
    private final StringProperty reasonForEmbalming = new SimpleStringProperty("");
    private final ObjectProperty<BigDecimal> salesTaxRate = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final ObjectProperty<ServicePackage> selectedPackage = new SimpleObjectProperty<>();
    private final ObservableList<ServiceLineItem> services = FXCollections.observableArrayList();
    private final ObservableList<MerchandiseLineItem> merchandise = FXCollections.observableArrayList();
    private final ObservableList<SpecialChargeLineItem> specialCharges = FXCollections.observableArrayList();
    private final ObservableList<CashAdvanceLineItem> cashAdvances = FXCollections.observableArrayList();
    private final ObjectProperty<BigDecimal> payment = new SimpleObjectProperty<>(BigDecimal.ZERO);

    public IntegerProperty controlNumberProperty() {
        return controlNumber;
    }

    public int getControlNumber() {
        return controlNumber.get();
    }

    public void setControlNumber(int value) {
        controlNumber.set(value);
    }

    public StringProperty servicesForNameProperty() {
        return servicesForName;
    }

    public String getServicesForName() {
        return servicesForName.get();
    }

    public void setServicesForName(String value) {
        servicesForName.set(value);
    }

    public ObjectProperty<LocalDate> dateOfDeathProperty() {
        return dateOfDeath;
    }

    public LocalDate getDateOfDeath() {
        return dateOfDeath.get();
    }

    public void setDateOfDeath(LocalDate value) {
        dateOfDeath.set(value);
    }

    public StringProperty placeOfDeathProperty() {
        return placeOfDeath;
    }

    public String getPlaceOfDeath() {
        return placeOfDeath.get();
    }

    public void setPlaceOfDeath(String value) {
        placeOfDeath.set(value);
    }

    public ObjectProperty<LocalDate> serviceDateProperty() {
        return serviceDate;
    }

    public LocalDate getServiceDate() {
        return serviceDate.get();
    }

    public void setServiceDate(LocalDate value) {
        serviceDate.set(value);
    }

    public StringProperty reasonForEmbalmingProperty() {
        return reasonForEmbalming;
    }

    public String getReasonForEmbalming() {
        return reasonForEmbalming.get();
    }

    public void setReasonForEmbalming(String value) {
        reasonForEmbalming.set(value);
    }

    public ObjectProperty<ServicePackage> selectedPackageProperty() {
        return selectedPackage;
    }

    public ServicePackage getSelectedPackage() {
        return selectedPackage.get();
    }

    public void setSelectedPackage(ServicePackage value) {
        selectedPackage.set(value);
    }

    public ObservableList<ServiceLineItem> getServices() {
        return services;
    }

    public ObservableList<MerchandiseLineItem> getMerchandise() {
        return merchandise;
    }

    public ObservableList<SpecialChargeLineItem> getSpecialCharges() {
        return specialCharges;
    }

    public ObservableList<CashAdvanceLineItem> getCashAdvances() {
        return cashAdvances;
    }

    public ObjectProperty<BigDecimal> paymentProperty() {
        return payment;
    }

    public BigDecimal getPayment() {
        return payment.get();
    }

    public void setPayment(BigDecimal value) {
        payment.set(value);
    }

    public ObjectProperty<BigDecimal> salesTaxRateProperty() {
        return salesTaxRate;
    }

    public BigDecimal getSalesTaxRate() {
        return salesTaxRate.get();
    }

    public void setSalesTaxRate(BigDecimal value) {
        salesTaxRate.set(value);
    }
}
