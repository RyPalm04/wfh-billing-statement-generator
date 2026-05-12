package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.models.statement.StatementContext;
import com.palmer.billingstatementgenerator.models.lineitems.CashAdvanceLineItem;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.util.List;

public class TabSixFxmlController extends GridTabController<CashAdvanceLineItem> {

    @Override
    protected List<CashAdvanceLineItem> getItems() {
        return StatementContext.current().getCashAdvances();
    }

    @Override
    protected CheckBox addItemRow(CashAdvanceLineItem item, int row) {
        CheckBox cb = buildCheckBox(item.getCatalog().getName(), row, itemsGrid);
        TextField provider = buildTextField(18, 1, row, itemsGrid);
        TextField amount = buildPriceField(item.amountProperty(), row, itemsGrid);

        cb.selectedProperty().bindBidirectional(item.selectedProperty());
        provider.textProperty().bindBidirectional(item.providerProperty());
        wireTextFieldToCheckBox(provider, cb);
        wireTextFieldToCheckBox(amount, cb);

        return cb;
    }
}