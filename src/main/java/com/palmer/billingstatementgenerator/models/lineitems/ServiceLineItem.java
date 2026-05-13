package com.palmer.billingstatementgenerator.models.lineitems;

import com.palmer.billingstatementgenerator.models.catalog.Service;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

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
