package com.palmer.billingstatementgenerator.models.lineitems;

import com.palmer.billingstatementgenerator.models.catalog.Service;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;

/**
 * Mutable line item wrapping a {@link Service} catalog entry with JavaFX properties
 * for selection state and package membership. One instance exists per service for the
 * lifetime of the current {@link com.palmer.billingstatementgenerator.models.statement.Statement}.
 *
 * <p>When {@code inPackage} is {@code true} the service is covered by the selected
 * package price and must not be counted in the individual services total.</p>
 */
public class ServiceLineItem {
    private final Service catalog;
    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private final StringProperty description = new SimpleStringProperty("");
    private final ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final BooleanProperty inPackage = new SimpleBooleanProperty(false);

    public ServiceLineItem(Service catalog) {
        this.catalog = catalog;
    }

    public Service getCatalog() {
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

    public BooleanProperty inPackageProperty() {
        return inPackage;
    }

    public boolean isInPackage() {
        return inPackage.get();
    }

    public void setInPackage(boolean value) {
        inPackage.set(value);
    }
}
