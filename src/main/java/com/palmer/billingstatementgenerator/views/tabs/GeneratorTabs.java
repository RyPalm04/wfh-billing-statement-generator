package com.palmer.billingstatementgenerator.views.tabs;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class GeneratorTabs extends Tab {
    protected static final NumberFormat DOLLAR_FORMATTER = NumberFormat.getCurrencyInstance();

    private final boolean showPrev;
    private final boolean showNext;
    private final boolean showClear;
    protected Button nextButton = new Button("Next");
    protected Button prevButton = new Button("Previous");
    protected Button clearButton = new Button("Clear Selections");
    protected GridPane grid = new GridPane();
    protected GridPane root = new GridPane();

    public GeneratorTabs(String tabTitle) {
        this(tabTitle, true, true, true);
    }
    public GeneratorTabs(String tabTitle, boolean showPrev, boolean showNext, boolean showClear) {
        super(tabTitle);
        this.showPrev = showPrev;
        this.showNext = showNext;
        this.showClear = showClear;
        createAndConfigurePanel();
        configureGrid();
        addForm();
    }

    public static class Row<T> {
        public final T value;
        public final CheckBox checkBox;
        public final TextField descriptionField;
        public final Label priceLabel;

        Row(T value, CheckBox checkBox, TextField descriptionField, Label priceLabel) {
            this.value = value;
            this.checkBox = checkBox;
            this.descriptionField = descriptionField;
            this.priceLabel = priceLabel;
        }
    }

    protected void createAndConfigurePanel() {
        root.getStyleClass().add("form-grid");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(15));
        root.setHgap(7);
        root.setVgap(10);

        GridPane.setConstraints(grid, 1, 0);
        GridPane.setConstraints(prevButton, 0, 1);
        GridPane.setConstraints(nextButton, 2, 1);
        GridPane.setConstraints(clearButton, 1, 1);
        GridPane.setHalignment(clearButton, HPos.CENTER);

        root.getChildren().add(grid);

        if (showPrev) {
            root.getChildren().add(prevButton);
        }

        if (showNext) {
            root.getChildren().add(nextButton);
        }

        if (showClear) {
            root.getChildren().add(clearButton);
        }

        this.setContent(root);
    }

    protected void addGridElements(Node... nodes) {
        grid.getChildren().addAll(nodes);
    }

    public Button getNextButton() {
        return nextButton;
    }

    public Button getPrevButton() {
        return prevButton;
    }

    private void configureGrid() {
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(7);
        grid.setVgap(10);
    }

    protected abstract void addForm();

    protected <T> List<Row<T>> addCheckboxRows(
            List<T> values,
            Function<T, String> nameFn,
            Predicate<T> needsDescription) {
        List<Row<T>> rows = new ArrayList<>(values.size());
        int rowIdx = 0;
        for (T value : values) {
            CheckBox cb = new CheckBox(nameFn.apply(value));
            GridPane.setConstraints(cb, 0, rowIdx);
            addGridElements(cb);

            TextField description = null;
            if (needsDescription.test(value)) {
                description = newDescriptionField();
                GridPane.setConstraints(description, 1, rowIdx);
                addGridElements(description);
            }

            rows.add(new Row<>(value, cb, description, null));
            rowIdx++;
        }
        wireClearButton(extractCheckboxes(rows));
        return rows;
    }

    protected <T> List<Row<T>> addCheckboxRowsWithPrices(
            List<T> values,
            Function<T, String> nameFn,
            Function<T, BigDecimal> costFn,
            Predicate<T> needsDescription) {
        List<Row<T>> rows = new ArrayList<>(values.size());
        int rowIdx = 0;
        for (T value : values) {
            CheckBox cb = new CheckBox(nameFn.apply(value));
            GridPane.setConstraints(cb, 0, rowIdx);
            addGridElements(cb);

            TextField description = null;
            if (needsDescription.test(value)) {
                description = newDescriptionField();
                GridPane.setConstraints(description, 1, rowIdx);
                addGridElements(description);
            }

            BigDecimal cost = costFn.apply(value);
            String priceText = cost == null
                    ? ""
                    : String.format("%14s", DOLLAR_FORMATTER.format(cost));
            Label price = new Label(priceText);
            GridPane.setConstraints(price, 2, rowIdx);
            addGridElements(price);

            rows.add(new Row<>(value, cb, description, price));
            rowIdx++;
        }
        wireClearButton(extractCheckboxes(rows));
        return rows;
    }

    private TextField newDescriptionField() {
        TextField field = new TextField();
        field.setPrefColumnCount(18);
        return field;
    }

    private static List<CheckBox> extractCheckboxes(List<? extends Row<?>> rows) {
        List<CheckBox> result = new ArrayList<>(rows.size());
        for (Row<?> row : rows) {
            result.add(row.checkBox);
        }
        return result;
    }

    private void wireClearButton(List<CheckBox> checkBoxes) {
        Observable[] selectedProperties = checkBoxes.stream()
                .map(CheckBox::selectedProperty)
                .toArray(Observable[]::new);
        clearButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> checkBoxes.stream().noneMatch(CheckBox::isSelected),
                selectedProperties));
        clearButton.setOnAction(e -> checkBoxes.forEach(cb -> cb.setSelected(false)));
    }
}
