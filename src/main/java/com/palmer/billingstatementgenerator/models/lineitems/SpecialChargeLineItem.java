package com.palmer.billingstatementgenerator.models.lineitems;

import com.palmer.billingstatementgenerator.models.catalog.SpecialCharge;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;

/**
 * Mutable line item wrapping a {@link SpecialCharge} catalog entry. Adds JavaFX properties
 * for selection state, optional description, and effective price. The price defaults to
 * the catalog's {@code defaultCost}; items with no default cost require the user to enter
 * a value before the statement can proceed.
 */
public class SpecialChargeLineItem {
    private final SpecialCharge catalog;
    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private final StringProperty description = new SimpleStringProperty("");
    private final ObjectProperty<BigDecimal> price;

    public SpecialChargeLineItem(SpecialCharge catalog) {
        this.catalog = catalog;
        this.price = new SimpleObjectProperty<>(catalog.defaultCost());
    }

    public SpecialCharge getCatalog() {
        return catalog;
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean value) {
        selected.set(value);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String value) {
        description.set(value);
    }

    public ObjectProperty<BigDecimal> priceProperty() {
        return price;
    }

    public BigDecimal getPrice() {
        return price.get();
    }

    public void setPrice(BigDecimal value) {
        price.set(value);
    }
}
