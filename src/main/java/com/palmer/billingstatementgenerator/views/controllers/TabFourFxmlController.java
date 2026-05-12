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

public class TabFourFxmlController extends GridTabController<MerchandiseLineItem> {

    @Override
    protected List<MerchandiseLineItem> getItems() {
        return StatementContext.current().getMerchandise();
    }

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

        // bind quantity
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
            if (!newVal) {
                spinner.getValueFactory().setValue(1);
            }
        });
    }

    private String formatUnitTotal(MerchandiseLineItem item) {
        BigDecimal unit = item.getCatalog().getDefaultCost();
        if (unit == null) return "";
        return DOLLAR_FORMATTER.format(unit.multiply(BigDecimal.valueOf(item.getQuantity())));
    }
}