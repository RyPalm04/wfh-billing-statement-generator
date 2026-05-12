package com.palmer.billingstatementgenerator.views.controllers;

import javafx.geometry.HPos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class GridTabController<T> extends BaseController {

    protected GridPane itemsGrid;

    public GridPane buildView() {
        itemsGrid = new GridPane();
        itemsGrid.setHgap(7);
        itemsGrid.setVgap(5);

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setMinWidth(200);
        col0.setHgrow(Priority.ALWAYS);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(80);
        col2.setHalignment(HPos.RIGHT);

        itemsGrid.getColumnConstraints().addAll(col0, col1, col2);

        AtomicInteger row = new AtomicInteger(0);
        List<CheckBox> checkBoxes = getItems().stream()
                                              .map(item -> addItemRow(item, row.getAndIncrement()))
                                              .collect(Collectors.toList());

        if (clearButton != null) {
            wireClear(checkBoxes);
        }

        return itemsGrid;
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

    protected abstract List<T> getItems();

    protected abstract CheckBox addItemRow(T item, int row);
}