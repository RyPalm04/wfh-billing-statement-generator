package com.palmer.billingstatementgenerator.models.lineitems;

import com.palmer.billingstatementgenerator.models.catalog.Service;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Mutable line item wrapping a {@link Service} catalog entry with a JavaFX
 * {@code selected} property. One instance exists per service for the lifetime
 * of the current {@link com.palmer.billingstatementgenerator.models.statement.Statement}.
 */
public class ServiceLineItem {
    private final Service catalog;
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

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
}
