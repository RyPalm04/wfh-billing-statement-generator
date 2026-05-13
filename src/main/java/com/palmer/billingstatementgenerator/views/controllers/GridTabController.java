package com.palmer.billingstatementgenerator.views.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
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

/**
 * Abstract base controller for tabs that display a grid of selectable line items.
 * Handles building the item grid, managing checkboxes, wiring the clear button,
 * clearing selections, and displaying a running total at the bottom of the tab.
 *
 * @param <T>
 *         the line item type displayed in this tab
 */
public abstract class GridTabController<T> extends BaseController {

    /**
     * Per-row flags that are true when a checkbox is selected but its price is missing or zero.
     */
    private final List<ObservableBooleanValue> invalidFlags = new ArrayList<>();
    /**
     * The grid pane containing the line item rows.
     */
    protected GridPane itemsGrid;
    /**
     * The list of checkboxes corresponding to each line item row.
     */
    private List<CheckBox> checkBoxes = new ArrayList<>();
    /**
     * Supplies the calculated total for this tab's selections.
     */
    private Supplier<BigDecimal> totalSupplier;

    /**
     * Label displayed at the bottom of the tab showing the running total.
     */
    private Label totalLabel;

    /**
     * Builds the full tab view including the items grid, separator, and total label.
     *
     * @return a {@link GridPane} wrapper containing the complete tab content
     */
    public GridPane buildView() {
        itemsGrid = new GridPane();
        itemsGrid.setHgap(7);
        itemsGrid.setVgap(5);
        itemsGrid.getColumnConstraints().addAll(buildColumnConstraints());

        AtomicInteger row = new AtomicInteger(0);
        checkBoxes = getItems().stream()
                .map(item -> addItemRow(item, row.getAndIncrement()))
                .collect(Collectors.toList());

        checkBoxes.forEach(cb ->
                cb.selectedProperty().addListener((obs, oldVal, newVal) -> refreshTotal())
        );

        if (clearButton != null) {
            configureClearButton(checkBoxes);
        }

        totalLabel = new Label("Total: $0.00");
        totalLabel.getStyleClass().add("tab-total-label");
        refreshTotal();

        VBox container = new VBox(12, itemsGrid, new Separator(), totalLabel);
        container.setAlignment(Pos.CENTER_RIGHT);
        container.setPadding(new Insets(0, 8, 0, 0));

        GridPane wrapper = new GridPane();
        GridPane.setConstraints(container, 0, 0);
        wrapper.getChildren().add(container);
        return wrapper;
    }

    /**
     * Builds the standard three-column constraints for the items grid:
     * column 0 (item name, grows), column 1 (description/quantity, fixed),
     * column 2 (price, right-aligned, fixed).
     *
     * @return a list of {@link ColumnConstraints} for the items grid
     */
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

    /**
     * Called when the clear button is injected. Wires the button to the
     * current checkboxes if the grid has already been built.
     */
    @Override
    protected void onClearButtonSet() {
        if (itemsGrid != null) {
            configureClearButton(checkBoxes);
        }
    }

    /**
     * Clears all checkbox selections, clears all text fields, and refreshes the total.
     */
    @Override
    protected void clearAll() {
        checkBoxes.forEach(cb -> cb.setSelected(false));
        clearTextFields();
        super.clearAll();
    }

    /**
     * Clears all text fields in the items grid. Price fields are cleared via
     * their {@link javafx.scene.control.TextFormatter}; plain text fields are set to empty.
     */
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

    /**
     * Resets the tab by clearing all selections. Delegates to {@link #clearAll()}.
     */
    @Override
    public void reset() {
        clearAll();
    }

    /**
     * Sets the supplier used to calculate the tab total.
     * The supplier is called whenever the total label needs to be refreshed.
     *
     * @param totalSupplier
     *         a {@link Supplier} returning the current tab total
     */
    public void setTotalSupplier(Supplier<BigDecimal> totalSupplier) {
        this.totalSupplier = totalSupplier;
    }

    /**
     * Refreshes the total label using the configured {@link #totalSupplier}.
     * Does nothing if either the supplier or the label has not been initialized.
     */
    @Override
    protected void refreshTotal() {
        if (totalSupplier == null || totalLabel == null) {
            return;
        }
        totalLabel.setText("Total: " + DOLLAR_FORMATTER.format(
                totalSupplier.get() != null ? totalSupplier.get() : BigDecimal.ZERO));
    }

    /**
     * Registers a checkbox/price pair for validation. The nav buttons are disabled
     * while the checkbox is selected but the price is null or zero.
     *
     * @param cb
     *         the checkbox to observe
     * @param priceProperty
     *         the price property that must be greater than zero when checked
     */
    protected void addValidationPair(CheckBox cb, ObservableValue<BigDecimal> priceProperty) {
        invalidFlags.add(Bindings.createBooleanBinding(
                () -> cb.isSelected() && (priceProperty.getValue() == null
                        || priceProperty.getValue().compareTo(BigDecimal.ZERO) <= 0),
                cb.selectedProperty(), priceProperty));
    }

    /**
     * Returns a binding that is {@code true} when any checked item is missing a required price.
     */
    @Override
    public BooleanBinding hasInvalidSelections() {
        if (invalidFlags.isEmpty()) {
            return Bindings.createBooleanBinding(() -> false);
        }
        ObservableBooleanValue[] deps = invalidFlags.toArray(new ObservableBooleanValue[0]);
        return Bindings.createBooleanBinding(
                () -> invalidFlags.stream().anyMatch(ObservableBooleanValue::get), deps);
    }

    /**
     * Returns the list of line items to display in this tab.
     *
     * @return the list of items of type {@code T}
     */
    protected abstract List<T> getItems();

    /**
     * Builds and adds a single row to the items grid for the given line item.
     *
     * @param item
     *         the line item to render
     * @param row
     *         the grid row index
     *
     * @return the {@link CheckBox} created for this row
     */
    protected abstract CheckBox addItemRow(T item, int row);
}