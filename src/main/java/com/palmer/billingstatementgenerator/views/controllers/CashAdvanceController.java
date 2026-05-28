package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.models.lineitems.CashAdvanceLineItem;
import com.palmer.billingstatementgenerator.models.statement.StatementContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * Controller for the Cash Advance Items tab.
 * Renders a grid of cash advance line items, each with a provider text field
 * and an editable amount field. Either field being populated will select
 * the corresponding checkbox automatically.
 */
public class CashAdvanceController extends GridTabController<CashAdvanceLineItem> {

    /**
     * Returns the list of cash advance line items from the current statement.
     *
     * @return the list of {@link CashAdvanceLineItem} objects
     */
    @Override
    protected List<CashAdvanceLineItem> getItems() {
        return StatementContext.current().getCashAdvances();
    }

    /**
     * Builds a row for the given cash advance line item, consisting of a checkbox,
     * a provider text field, and an editable amount price field. Both the provider
     * and amount fields are wired to auto-select the checkbox when populated.
     *
     * @param item
     *         the cash advance line item to render
     * @param row
     *         the grid row index
     *
     * @return the {@link CheckBox} created for this row
     */
    @Override
    protected CheckBox addItemRow(CashAdvanceLineItem item, int row) {
        CheckBox cb = buildCheckBox(item.getCatalog().name(), row, itemsGrid);
        TextField provider = buildTextField(18, 1, row, itemsGrid);
        provider.setId(toId(item.getCatalog().name()) + "_provider");
        TextField amount = buildPriceField(item.amountProperty(), row, itemsGrid);
        amount.setId(toId(item.getCatalog().name()) + "_amount");

        cb.selectedProperty().bindBidirectional(item.selectedProperty());
        provider.textProperty().bindBidirectional(item.providerProperty());
        wireTextFieldToCheckBox(provider, cb);
        wireTextFieldToCheckBox(amount, cb);
        addValidationPair(cb, item.amountProperty());

        return cb;
    }
}