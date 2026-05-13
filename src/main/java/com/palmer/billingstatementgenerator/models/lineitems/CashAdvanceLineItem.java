package com.palmer.billingstatementgenerator.models.lineitems;

import com.palmer.billingstatementgenerator.models.catalog.CashAdvance;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;

/**
 * Mutable line item wrapping a {@link CashAdvance} catalog entry. Adds JavaFX properties
 * for selection state, provider name, and the user-entered cash amount. The amount starts
 * null and must be populated by the user; the line item is invalid for navigation purposes
 * if selected but has no amount.
 */
public class CashAdvanceLineItem {
    private final CashAdvance catalog;
    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private final StringProperty provider = new SimpleStringProperty("");
    private final ObjectProperty<BigDecimal> amount = new SimpleObjectProperty<>();

    public CashAdvanceLineItem(CashAdvance catalog) {
        this.catalog = catalog;
    }

    public CashAdvance getCatalog() { return catalog; }

    public BooleanProperty selectedProperty() { return selected; }
    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean value) { selected.set(value); }

    public StringProperty providerProperty() { return provider; }
    public String getProvider() { return provider.get(); }
    public void setProvider(String value) { provider.set(value); }

    public ObjectProperty<BigDecimal> amountProperty() { return amount; }
    public BigDecimal getAmount() { return amount.get(); }
    public void setAmount(BigDecimal value) { amount.set(value); }
}
