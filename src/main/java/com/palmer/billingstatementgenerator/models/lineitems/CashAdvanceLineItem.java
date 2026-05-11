package com.palmer.billingstatementgenerator.models.lineitems;

import com.palmer.billingstatementgenerator.models.catalog.CashAdvance;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;

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
