package com.palmer.billingstatementgenerator.views.controllers;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class GridTabController<T> extends BaseController {

    protected GridPane itemsGrid;
    private List<CheckBox> checkBoxes = new ArrayList<>();

    public GridPane buildView() {
        itemsGrid = new GridPane();
        itemsGrid.setHgap(7);
        itemsGrid.setVgap(5);
        itemsGrid.getColumnConstraints().addAll(buildColumnConstraints());

        AtomicInteger row = new AtomicInteger(0);
        List<CheckBox> checkBoxes = getItems().stream()
                .map(item -> addItemRow(item, row.getAndIncrement()))
                .collect(Collectors.toList());

        if (clearButton != null) {
            wireClear(checkBoxes);
        }

        totalLabel = new Label("Total: $0.00");
        totalLabel.getStyleClass().add("tab-total-label");
        refreshTotal();

        VBox container = new VBox(12, itemsGrid, new Separator(), totalLabel);
        container.setAlignment(Pos.CENTER_RIGHT);
        container.setPadding(new Insets(0, 8, 0, 0));

        // wrap in a GridPane to return consistent type
        GridPane wrapper = new GridPane();
        GridPane.setConstraints(container, 0, 0);
        wrapper.getChildren().add(container);
        return wrapper;
    }

    private List<ColumnConstraints> buildColumnConstraints() {
        ColumnConstraints col0 = new ColumnConstraints();
        col0.setMinWidth(200);
        col0.setHgrow(Priority.ALWAYS);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(80);
        col2.setHalignment(HPos.RIGHT);

        return List.of(col0, col1, col2);
    }

    @Override
    protected void onClearButtonSet() {
        if (itemsGrid != null) {
            wireClear(itemsGrid.getChildren().stream()
                    .filter(n -> n instanceof CheckBox)
                    .map(n -> (CheckBox) n)
                    .collect(Collectors.toList()));
        }
    }

    protected void wireClear(List<CheckBox> checkBoxes) {
        wireClearButton(checkBoxes);
    }

    @Override
    protected void clearAll(List<CheckBox> checkBoxes) {
        checkBoxes.forEach(cb -> cb.setSelected(false));
        clearTextFields();
    }

    private void clearTextFields() {
        if (itemsGrid == null) {
            return;
        }

        itemsGrid.getChildren().stream()
                .filter(n -> n instanceof TextField)
                .map(n -> (TextField) n)
                .forEach(field -> {
                    if (field.getTextFormatter() != null) {
                        field.getTextFormatter().setValue(null);
                    } else {
                        field.setText("");
                    }
                });
    }

    @Override
    public void reset() {
        clearAll(checkBoxes);
        refreshTotal();
    }

    private Supplier<BigDecimal> totalSupplier;
    private Label totalLabel;

    public void setTotalSupplier(Supplier<BigDecimal> totalSupplier) {
        this.totalSupplier = totalSupplier;
    }

    @Override
    protected void refreshTotal() {
        if (totalSupplier == null || totalLabel == null) return;
        totalLabel.setText("Total: " + DOLLAR_FORMATTER.format(
                totalSupplier.get() != null ? totalSupplier.get() : BigDecimal.ZERO));
    }

    protected abstract List<T> getItems();

    protected abstract CheckBox addItemRow(T item, int row);
}