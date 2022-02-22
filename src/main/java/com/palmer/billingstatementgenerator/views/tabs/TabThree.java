package com.palmer.billingstatementgenerator.views.tabs;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import org.apache.commons.lang3.StringUtils;

public class TabThree extends Tab {

    private static final NumberFormat DOLLAR_FORMATTER = NumberFormat.getCurrencyInstance();
    public static final String PACKAGE_SELECTION = "Package Selection...";
    private GridPane root;
    private Button nextButton;
    private Button prevButton;

    public TabThree() {
        setText("SERVICES, FACILITIES, AND TRANSPORTATION");
        createAndConfigurePanel();
        addForm();
    }

    private void addForm() {
        nextButton = new Button("Next");
        prevButton = new Button("Previous");
        Button clearButton = new Button("Clear Selections");
        GridPane grid = new GridPane();

        final ComboBox<String> packages = new ComboBox<>();
        List<String> packageValues = Arrays.stream(Packages.values())
                                           .map(value -> {
                                               if (!StringUtils.equalsIgnoreCase(value.getName(), PACKAGE_SELECTION)) {
                                                   return String.format("%-15s %14s", value.getName(),
                                                           DOLLAR_FORMATTER.format(value.getCost()));
                                               } else {
                                                   return value.getName();
                                               }
                                           })
                                           .collect(Collectors.toList());
        packages.getItems()
                .setAll(packageValues);
        packages.setStyle("-fx-font-size:14px");
        packages.getSelectionModel().selectFirst();
        packages.setOnAction(e -> {
            if (!StringUtils.equalsIgnoreCase(PACKAGE_SELECTION, packages.getValue())) {
                clearButton.setDisable(false);
            }
        });

        List<CheckBox> checkBoxes = Arrays.stream(Services.values())
                                          .map(value -> new CheckBox(value.getName()))
                                          .collect(Collectors.toList());
        List<Label> prices = Arrays.stream(Services.values())
                                   .map(value -> new Label(DOLLAR_FORMATTER.format(value.getCost())))
                                   .collect(Collectors.toList());

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(7);
        grid.setVgap(10);

        GridPane.setConstraints(packages, 0, 0);

        for (int i = 0; i < checkBoxes.size(); i++) {
            CheckBox checkBox = checkBoxes.get(i);
            GridPane.setConstraints(checkBox, 0, i + 1);
            checkBox
                    .setFont(new Font(14));
            grid.getChildren()
                .add(checkBox);
            checkBox.setOnAction(e -> {
                if (checkBox.isSelected()) {
                    clearButton.setDisable(false);
                }
            });
        }

        for (int i = 0; i < prices.size(); i++) {
            GridPane.setConstraints(prices.get(i), 1, i + 1);
            prices.get(i)
                  .setFont(new Font(14));
            grid.getChildren()
                .add(prices.get(i));
        }

        grid.getChildren()
            .add(packages);

        clearButton.setOnAction(e -> {
            packages.getSelectionModel().selectFirst();
            checkBoxes.forEach(checkBox -> checkBox.setSelected(false));
            clearButton.setDisable(true);
        });
        clearButton.setDisable(StringUtils.equalsIgnoreCase(PACKAGE_SELECTION, packages.getValue()) && checkBoxes.stream()
                                                                                                                 .anyMatch(cb -> !cb.isSelected()));

        GridPane.setConstraints(grid, 1, 0);
        GridPane.setConstraints(prevButton, 0, 1);
        GridPane.setConstraints(nextButton, 2, 1);
        GridPane.setConstraints(clearButton, 1, 1);
        GridPane.setHalignment(clearButton, HPos.CENTER);

        root.getChildren()
            .addAll(grid, prevButton, nextButton, clearButton);
    }

    private void createAndConfigurePanel() {
        root = new GridPane();

        root.setAlignment(Pos.CENTER);
        root.setHgap(7);
        root.setVgap(10);

        this.setContent(root);
    }

    public Button getNextButton() {
        return nextButton;
    }

    public Button getPrevButton() {
        return prevButton;
    }
}

enum Services {
    BASIC("Basic Services", 0.00),
    EMBALMING("Embalming", 0.00),
    OTHER_PREP("Other Preparation", 0.00),
    VISITATION("Visitation", 0.00),
    FUNERAL("Funeral Service", 0.00),
    MEMORIAL("Memorial Service", 0.00),
    GRAVESIDE("Graveside Service", 0.00),
    COACH("Funeral Coach", 0.00),
    PALLBEARER_CAR("Pallbearer Car", 0.00),
    SERVICE_CAR("Service Car", 0.00),
    TRANSFER("Transfer of Remains to Funeral Home", 0.00),
    OTHER_A("Other", 0.00),
    OTHER_B("Other", 0.00);

    private final String name;
    private final Double cost;

    Services(String name, Double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public Double getCost() {
        return cost;
    }

}

enum Packages {
    DEFAULT("Package Selection...", 0.00),
    T1("Traditional One", 0.00),
    T2("Traditional Two", 0.00),
    C1("Cremation One", 0.00),
    C2("Cremation Two", 0.00),
    C3("Cremation Three", 0.00),
    C4("Cremation Four", 0.00),
    C5("Cremation Five", 0.00);

    private final String name;
    private final Double cost;

    Packages(String name, Double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public Double getCost() {
        return cost;
    }
}
