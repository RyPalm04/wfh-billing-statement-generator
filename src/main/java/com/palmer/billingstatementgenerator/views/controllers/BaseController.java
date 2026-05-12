package com.palmer.billingstatementgenerator.views.controllers;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.scene.control.*;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.GridPane;
import javafx.util.converter.NumberStringConverter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseController {
    protected static final NumberFormat DOLLAR_FORMATTER = NumberFormat.getCurrencyInstance();

    protected Button clearButton;
    protected Button nextButton;

    public void setClearButton(Button clearButton) {
        this.clearButton = clearButton;
        onClearButtonSet();
    }

    public void setNextButton(Button nextButton) {
        this.nextButton = nextButton;
        onNextButtonSet();
    }

    // Override in subclasses to react when buttons are injected
    protected void onClearButtonSet() {}
    protected void onNextButtonSet() {}

    protected CheckBox buildCheckBox(String label, int row, GridPane grid) {
        CheckBox cb = new CheckBox(label);
        GridPane.setConstraints(cb, 0, row);
        grid.getChildren().add(cb);
        return cb;
    }

    protected void buildPriceLabel(BigDecimal cost, int row, GridPane grid) {
        Label price = new Label(cost != null ? DOLLAR_FORMATTER.format(cost) : "");
        price.getStyleClass().add("price-label");
        GridPane.setConstraints(price, 2, row);
        grid.getChildren().add(price);
    }

    protected TextField buildTextField(int columnCount, int column, int row, GridPane grid) {
        TextField tf = new TextField();
        tf.setPrefColumnCount(columnCount);
        GridPane.setConstraints(tf, column, row);
        grid.getChildren().add(tf);
        return tf;
    }

    protected void wireTextFieldToCheckBox(TextField tf, CheckBox cb) {
        tf.textProperty().addListener((obs, oldV, newV) ->
                cb.setSelected(newV != null && !newV.trim().isEmpty())
        );
        if (!tf.getText().trim().isEmpty()) {
            cb.setSelected(true);
        }
    }

    protected void wireClearButton(List<CheckBox> checkBoxes) {
        Observable[] deps = checkBoxes.stream()
                                      .map(CheckBox::selectedProperty)
                                      .toArray(Observable[]::new);
        clearButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> checkBoxes.stream().noneMatch(CheckBox::isSelected),
                deps));
        clearButton.setOnAction(e -> {
            checkBoxes.forEach(cb -> cb.setSelected(false));
        });
    }

    protected void configTextFieldForInts(TextField... fields) {
        Arrays.stream(fields).forEach(field -> field.setTextFormatter(new TextFormatter<Integer>((Change c) -> {
            if (c.getControlNewText().matches("-?\\d*")) return c;
            return null;
        })));
    }

    protected void bindIntegerTextField(TextField field, IntegerProperty prop) {
        Bindings.bindBidirectional(field.textProperty(), prop, new NumberStringConverter());
    }

    public void onShow() {}
    public void onHide() {}
}