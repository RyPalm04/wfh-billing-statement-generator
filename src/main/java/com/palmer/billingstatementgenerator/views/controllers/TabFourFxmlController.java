package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.models.StatementContext;
import com.palmer.billingstatementgenerator.models.lineitems.MerchandiseLineItem;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.util.List;

public class TabFourFxmlController extends GridTabController<MerchandiseLineItem> {

    @Override
    protected List<MerchandiseLineItem> getItems() {
        return StatementContext.current().getMerchandise();
    }

    @Override
    protected CheckBox addItemRow(MerchandiseLineItem item, int row) {
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