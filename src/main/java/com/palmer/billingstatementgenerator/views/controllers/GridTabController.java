package com.palmer.billingstatementgenerator.views.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class GridTabController<T> extends BaseController {

    @FXML protected GridPane itemsGrid;

    @FXML
    protected void initialize() {
        AtomicInteger row = new AtomicInteger(0);

        List<CheckBox> checkBoxes = getItems().stream()
            .map(item -> addItemRow(item, row.getAndIncrement()))
            .collect(Collectors.toList());

        if (clearButton != null) {
            wireClear(checkBoxes);
        }
    }

    @Override
    protected void onClearButtonSet() {
        if (itemsGrid != null) {
            wireClear(collectCheckBoxes(itemsGrid));
        }
    }

    protected void wireClear(List<CheckBox> checkBoxes) {
        wireClearButton(checkBoxes);
    }

    protected abstract List<T> getItems();
    protected abstract CheckBox addItemRow(T item, int row);
}