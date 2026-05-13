package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.models.catalog.Merchandise;
import com.palmer.billingstatementgenerator.models.statement.StatementContext;
import com.palmer.billingstatementgenerator.models.lineitems.MerchandiseLineItem;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for the Merchandise tab.
 * Renders a grid of merchandise line items, supporting three display modes:
 * <ul>
 *   <li>Per-unit items (e.g. Memorial Video) — shown with a quantity spinner and computed total</li>
 *   <li>Items with no default cost — shown with an editable price field</li>
 *   <li>Items with a default cost — shown with a read-only price label</li>
 * </ul>
 * Items with a description requirement also render an additional text field.
 */
public class MerchandiseController extends GridTabController<MerchandiseLineItem> {

    /**
     * Returns the list of merchandise line items from the current statement.
     *
     * @return the list of {@link MerchandiseLineItem} objects
     */
    @Override
    protected List<MerchandiseLineItem> getItems() {
        return StatementContext.current().getMerchandise();
    }

    /**
     * Builds a row for the given merchandise line item. The price column
     * is rendered differently based on the item's pricing mode and default cost.
     *
     * @param item the merchandise line item to render
     * @param row  the grid row index
     * @return the {@link CheckBox} created for this row
     */
    @Override
    protected CheckBox addItemRow(MerchandiseLineItem item, int row) {
        CheckBox cb = buildCheckBox(item.getCatalog().getName(), row, itemsGrid);
        cb.selectedProperty().bindBidirectional(item.selectedProperty());

        if (item.getCatalog().getPricingMode() == Merchandise.PricingMode.PER_UNIT) {
            addPerUnitRow(item, cb, row);
        } else if (item.getCatalog().getDefaultCost() == null) {
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

    /**
     * Adds a quantity spinner and computed total price label for a per-unit merchandise item.
     * The spinner is bound to the item's quantity, and the price label updates reactively
     * as the quantity changes. The checkbox is automatically selected when quantity is greater
     * than zero, and the spinner resets to 1 when the checkbox is deselected.
     *
     * @param item the per-unit merchandise line item
     * @param cb   the checkbox for this row
     * @param row  the grid row index
     */
    private void addPerUnitRow(MerchandiseLineItem item, CheckBox cb, int row) {
        Spinner<Integer> spinner = new Spinner<>(1, 999, item.getQuantity());
        spinner.setPrefWidth(70);
        spinner.setEditable(true);
        GridPane.setConstraints(spinner, 1, row);
        itemsGrid.getChildren().add(spinner);

        Label totalPrice = new Label(formatUnitTotal(item));
        totalPrice.getStyleClass().add("price-label");
        GridPane.setConstraints(totalPrice, 2, row);
        itemsGrid.getChildren().add(totalPrice);

        spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            item.setQuantity(newVal);
            BigDecimal unit = item.getCatalog().getDefaultCost();
            BigDecimal total = unit != null ? unit.multiply(BigDecimal.valueOf(newVal)) : BigDecimal.ZERO;
            item.setPrice(total);
            totalPrice.setText(DOLLAR_FORMATTER.format(total));
            cb.setSelected(newVal > 0);
            refreshTotal();
        });

        BigDecimal unit = item.getCatalog().getDefaultCost();
        if (unit != null) {
            item.setPrice(unit.multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        cb.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) spinner.getValueFactory().setValue(1);
        });
    }

    /**
     * Formats the computed total for a per-unit item based on its unit cost and current quantity.
     *
     * @param item the merchandise line item
     * @return a formatted currency string, or an empty string if the unit cost is null
     */
    private String formatUnitTotal(MerchandiseLineItem item) {
        BigDecimal unit = item.getCatalog().getDefaultCost();
        if (unit == null) return "";
        return DOLLAR_FORMATTER.format(unit.multiply(BigDecimal.valueOf(item.getQuantity())));
    }
}