package com.palmer.billingstatementgenerator.views.tabs;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TabThree extends GeneratorTabs {

    public static final String PACKAGE_SELECTION = "Package Selection...";

    public TabThree(String tabTitle) {
        super(tabTitle);
    }

    @Override
    protected void addForm() {
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
        packages.getSelectionModel()
                .selectFirst();

        List<CheckBox> checkBoxes = Arrays.stream(Services.values())
                                          .map(value -> new CheckBox(value.getName()))
                                          .collect(Collectors.toList());
        List<Label> prices = Arrays.stream(Services.values())
                                   .map(value -> new Label(DOLLAR_FORMATTER.format(value.getCost())))
                                   .collect(Collectors.toList());

        GridPane.setConstraints(packages, 0, 0);

        for (int i = 0; i < checkBoxes.size(); i++) {
            CheckBox checkBox = checkBoxes.get(i);
            Label price = prices.get(i);
            EventHandler<ActionEvent> clearButtonHandler = e -> clearButton.setDisable(!checkBox.isSelected() && StringUtils.equalsIgnoreCase(PACKAGE_SELECTION, packages.getValue()));
            GridPane.setConstraints(checkBox, 0, i + 1);
            GridPane.setConstraints(price, 1, i + 1);
            checkBox.setFont(new Font(14));
            price.setFont(new Font(14));
            addGridElements(checkBox, price);
            checkBox.setOnAction(clearButtonHandler);
            packages.setOnAction(clearButtonHandler);
        }

        addGridElements(packages);

        clearButton.setOnAction(e -> {
            packages.getSelectionModel()
                    .selectFirst();
            checkBoxes.forEach(checkBox -> checkBox.setSelected(false));
            clearButton.setDisable(true);
        });
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
