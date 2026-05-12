package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.models.statement.StatementContext;
import com.palmer.billingstatementgenerator.models.lineitems.SpecialChargeLineItem;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.util.List;

public class TabFiveFxmlController extends GridTabController<SpecialChargeLineItem> {

    @Override
    protected List<SpecialChargeLineItem> getItems() {
        return StatementContext.current().getSpecialCharges();
    }

    @Override
    protected CheckBox addItemRow(SpecialChargeLineItem item, int row) {
        CheckBox cb = buildCheckBox(item.getCatalog().getName(), row, itemsGrid);
        cb.selectedProperty().bindBidirectional(item.selectedProperty());

        if (item.getCatalog().getDefaultCost() == null) {
            TextField priceField = buildPriceField(item.priceProperty(), row, itemsGrid);
            wireTextFieldToCheckBox(priceField, cb);
        } else {
            buildPriceLabel(item.getCatalog().getDefaultCost(), row, itemsGrid);
        }

        if (item.getCatalog().isDescriptionRequired()) {
            TextField desc = buildTextField(18, 1, row, itemsGrid);
            desc.textProperty().bindBidirectional(item.descriptionProperty());
            wireTextFieldToCheckBox(desc, cb);
        }

        return cb;
    }
}