package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.util.BigDecimalCurrencyConverter;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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

/**
 * Abstract base class for all FXML controllers in the billing statement generator.
 * Provides shared UI building utilities, button lifecycle hooks, and common field
 * binding helpers used across all tab controllers.
 */
public abstract class BaseController {

    /**
     * Currency formatter for displaying monetary values.
     */
    protected static final NumberFormat DOLLAR_FORMATTER = NumberFormat.getCurrencyInstance();

    /**
     * The Clear Selections button, injected by the parent view.
     */
    protected Button clearButton;

    /**
     * The Next/Generate PDF button, injected by the parent view.
     */
    protected Button nextButton;

    /**
     * Injects the clear button and triggers {@link #onClearButtonSet()}.
     *
     * @param clearButton
     *         the button to use for clearing selections
     */
    public void setClearButton(Button clearButton) {
        this.clearButton = clearButton;
        onClearButtonSet();
    }

    /**
     * Injects the next button and triggers {@link #onNextButtonSet()}.
     *
     * @param nextButton
     *         the button to use for advancing to the next tab
     */
    public void setNextButton(Button nextButton) {
        this.nextButton = nextButton;
        onNextButtonSet();
    }

    /**
     * Called after the clear button is injected. Override to wire
     * clear button behavior specific to the controller.
     */
    protected void onClearButtonSet() {
    }

    /**
     * Called after the next button is injected. Override to wire
     * next button behavior specific to the controller.
     */
    protected void onNextButtonSet() {
    }

    /**
     * No-op in the base class. Override to reset the controller to its default state.
     */
    public void reset() {
    }

    /**
     * Builds a {@link CheckBox} and adds it to column 0 of the specified row in the grid.
     *
     * @param label
     *         the display text for the checkbox
     * @param row
     *         the grid row index
     * @param grid
     *         the target {@link GridPane}
     *
     * @return the constructed {@link CheckBox}
     */
    protected CheckBox buildCheckBox(String label, int row, GridPane grid) {
        CheckBox cb = new CheckBox(label);
        cb.setId(toId(label) + "_cb");
        GridPane.setConstraints(cb, 0, row);
        grid.getChildren().add(cb);
        return cb;
    }

    protected static String toId(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]+", "_").replaceAll("^_|_$", "");
    }

    /**
     * Builds a read-only price {@link Label} and adds it to column 2 of the specified row.
     * Displays a blank label if cost is null.
     *
     * @param cost
     *         the monetary value to display, or null for a blank label
     * @param row
     *         the grid row index
     * @param grid
     *         the target {@link GridPane}
     */
    protected void buildPriceLabel(BigDecimal cost, int row, GridPane grid) {
        Label price = new Label(cost != null ? DOLLAR_FORMATTER.format(cost) : "");
        price.getStyleClass().add("price-label");
        GridPane.setConstraints(price, 2, row);
        grid.getChildren().add(price);
    }

    /**
     * Builds a {@link TextField} and adds it to the specified column and row in the grid.
     *
     * @param columnCount
     *         the preferred column count for sizing
     * @param column
     *         the grid column index
     * @param row
     *         the grid row index
     * @param grid
     *         the target {@link GridPane}
     *
     * @return the constructed {@link TextField}
     */
    protected TextField buildTextField(int columnCount, int column, int row, GridPane grid) {
        TextField tf = new TextField();
        tf.setPrefColumnCount(columnCount);
        GridPane.setConstraints(tf, column, row);
        grid.getChildren().add(tf);
        return tf;
    }

    /**
     * Wires a {@link TextField} to a {@link CheckBox} such that the checkbox is
     * selected when the field has a non-empty value, and the tab total is refreshed
     * on every change.
     *
     * @param tf
     *         the text field to observe
     * @param cb
     *         the checkbox to drive
     */
    protected void wireTextFieldToCheckBox(TextField tf, CheckBox cb) {
        tf.textProperty().addListener((obs, oldV, newV) -> {
            cb.setSelected(newV != null && !newV.trim().isEmpty());
            refreshTotal();
        });
        if (!tf.getText().trim().isEmpty()) {
            cb.setSelected(true);
        }
    }

    /**
     * Configures the clear button with a disabled binding and action.
     * The button is disabled when no checkboxes are selected.
     * Override in subclasses to add additional dependencies (e.g. a ComboBox).
     *
     * @param checkBoxes
     *         the list of checkboxes the button should observe and clear
     */
    protected void configureClearButton(List<CheckBox> checkBoxes) {
        Observable[] clearButtonDependencies = checkBoxes.stream()
                .map(CheckBox::selectedProperty)
                .toArray(Observable[]::new);
        clearButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> checkBoxes.stream().noneMatch(CheckBox::isSelected),
                clearButtonDependencies));
        clearButton.setOnAction(e -> clearAll());
    }

    /**
     * Clears all selections and refreshes the tab total.
     * Override in subclasses to add additional clearing behavior.
     */
    protected void clearAll() {
        refreshTotal();
    }

    /**
     * Applies an integer-only {@link TextFormatter} to the given text fields,
     * restricting input to digits and an optional leading minus sign.
     *
     * @param fields
     *         one or more text fields to configure
     */
    protected void configTextFieldForInts(TextField... fields) {
        Arrays.stream(fields).forEach(field -> field.setTextFormatter(new TextFormatter<Integer>((Change c) -> {
            if (c.getControlNewText().matches("-?\\d*")) {
                return c;
            }
            return null;
        })));
    }

    /**
     * Binds a {@link TextField} bidirectionally to an {@link IntegerProperty},
     * displaying a blank field when the value is zero.
     *
     * @param field
     *         the text field to bind
     * @param prop
     *         the integer property to bind to
     */
    protected void bindIntegerTextField(TextField field, IntegerProperty prop) {
        Bindings.bindBidirectional(field.textProperty(), prop, new NumberStringConverter() {
            @Override
            public String toString(Number value) {
                if (value == null || value.intValue() == 0) {
                    return "";
                }
                return value.intValue() + "";
            }

            @Override
            public Number fromString(String value) {
                if (value == null || value.trim().isEmpty()) {
                    return 0;
                }
                try {
                    return Integer.parseInt(value.trim());
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        });
    }

    /**
     * Builds an editable price {@link TextField} bound to the given {@link BigDecimal}
     * property. Input is restricted to valid currency format, and the value is
     * formatted as currency via {@link BigDecimalCurrencyConverter}.
     *
     * @param priceProperty
     *         the property to bind the field's value to
     * @param row
     *         the grid row index
     * @param grid
     *         the target {@link GridPane}
     *
     * @return the constructed price {@link TextField}
     */
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
        formatter.valueProperty().addListener((obs, oldVal, newVal) -> refreshTotal());
        return priceField;
    }

    /**
     * Returns a binding that is {@code true} when any checked item is missing a required price.
     * Always {@code false} in the base class; overridden in {@link GridTabController}.
     *
     * @return a {@link BooleanBinding} indicating whether any checked item is missing a required price
     */
    public BooleanBinding hasInvalidSelections() {
        return Bindings.createBooleanBinding(() -> false);
    }

    /**
     * Refreshes the tab total label. No-op in the base class;
     * overridden in {@link GridTabController} when a total supplier is set.
     */
    protected void refreshTotal() {
    }

    /**
     * Called when this tab becomes selected. Override to perform
     * any refresh or update logic needed on tab activation.
     */
    public void onShow() {
    }

    /**
     * Called when this tab is deselected. Override to perform
     * any cleanup logic needed on tab deactivation.
     */
    public void onHide() {
    }
}