package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.models.StatementContext;
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
        buildPriceLabel(item.getCatalog().getDefaultCost(), 2, row, itemsGrid);
        cb.selectedProperty().bindBidirectional(item.selectedProperty());

        if (item.getCatalog().isDescriptionRequired()) {
            TextField desc = buildTextField(row, itemsGrid);
            desc.textProperty().bindBidirectional(item.descriptionProperty());
            wireTextFieldToCheckBox(desc, cb);
        }

        return cb;
    }
}