package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.models.lineitems.SpecialChargeLineItem;
import com.palmer.billingstatementgenerator.models.statement.StatementContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * Controller for the Special Charges tab.
 * Renders a grid of special charge line items. Items with no default cost
 * display an editable price field; items with a default cost display a
 * read-only price label. Items requiring a description also render a text field.
 */
public class SpecialChargesController extends GridTabController<SpecialChargeLineItem> {

    /**
     * Returns the list of special charge line items from the current statement.
     *
     * @return the list of {@link SpecialChargeLineItem} objects
     */
    @Override
    protected List<SpecialChargeLineItem> getItems() {
        return StatementContext.current().getSpecialCharges();
    }

    /**
     * Builds a row for the given special charge line item. Items without a
     * default cost render an editable price field; others render a read-only label.
     * Items marked as requiring a description also render a description text field.
     *
     * @param item
     *         the special charge line item to render
     * @param row
     *         the grid row index
     *
     * @return the {@link CheckBox} created for this row
     */
    @Override
    protected CheckBox addItemRow(SpecialChargeLineItem item, int row) {
        CheckBox cb = buildCheckBox(item.getCatalog().getName(), row, itemsGrid);
        cb.selectedProperty().bindBidirectional(item.selectedProperty());

        if (item.getCatalog().isDescriptionRequired()) {
            TextField desc = buildTextField(18, 1, row, itemsGrid);
            desc.textProperty().bindBidirectional(item.descriptionProperty());
            wireTextFieldToCheckBox(desc, cb);
        }

        if (item.getCatalog().getDefaultCost() == null) {
            TextField priceField = buildPriceField(item.priceProperty(), row, itemsGrid);
            wireTextFieldToCheckBox(priceField, cb);
            addValidationPair(cb, item.priceProperty());
        } else {
            buildPriceLabel(item.getCatalog().getDefaultCost(), row, itemsGrid);
        }

        return cb;
    }
}