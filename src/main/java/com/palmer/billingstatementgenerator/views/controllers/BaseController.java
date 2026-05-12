package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.util.BigDecimalCurrencyConverter;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.GridPane;
import javafx.util.converter.NumberStringConverter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

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

    protected void onClearButtonSet() {
    }

    protected void onNextButtonSet() {
    }

    public void reset() {
    }

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
        tf.textProperty().addListener((obs, oldV, newV) -> {
            cb.setSelected(newV != null && !newV.trim().isEmpty());
            refreshTotal();
        });

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
        clearButton.setOnAction(e -> clearAll(checkBoxes));
    }

    protected void clearAll(List<CheckBox> checkBoxes) {
        checkBoxes.forEach(cb -> cb.setSelected(false));
    }

    protected void configTextFieldForInts(TextField... fields) {
        Arrays.stream(fields).forEach(field -> field.setTextFormatter(new TextFormatter<Integer>((Change c) -> {
            if (c.getControlNewText().matches("-?\\d*")) return c;
            return null;
        })));
    }

    protected void bindIntegerTextField(TextField field, IntegerProperty prop) {
        Bindings.bindBidirectional(field.textProperty(), prop, new NumberStringConverter() {
            @Override
            public String toString(Number value) {
                if (value == null || value.intValue() == 0) return "";
                return value.intValue() + "";
            }

            @Override
            public Number fromString(String value) {
                if (value == null || value.trim().isEmpty()) return 0;
                try {
                    return Integer.parseInt(value.trim());
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        });
    }

    protected TextField buildPriceField(ObjectProperty<BigDecimal> priceProperty, int row, GridPane grid) {
        TextField priceField = new TextField();
        priceField.setPrefColumnCount(10);
        priceField.getStyleClass().add("price-field");
        priceField.setPromptText("$0.00");
        priceField.setAlignment(Pos.CENTER_RIGHT);
        GridPane.setConstraints(priceField, 2, row);
        grid.getChildren().add(priceField);

        TextFormatter<BigDecimal> formatter = new TextFormatter<>(
                new BigDecimalCurrencyConverter(),
                priceProperty.get(),
                change -> {
                    String newText = change.getControlNewText().replaceAll("[^\\d.]", "");
                    return newText.matches("\\d*\\.?\\d{0,2}") ? change : null;
                }
        );

        priceField.setTextFormatter(formatter);
        formatter.valueProperty().bindBidirectional(priceProperty);

        return priceField;
    }

    protected void refreshTotal() {}

    public void onShow() {
    }

    public void onHide() {
    }
}