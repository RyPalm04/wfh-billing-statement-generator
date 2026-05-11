package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.models.Statement;

import java.util.Arrays;

import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.converter.NumberStringConverter;

/**
 * Programmatic controller that builds the UI for TabTwo into a provided GridPane.
 */
public class TabTwoController {

    private final GridPane grid;
    private final Statement stmt;

    public TabTwoController(GridPane grid, Statement stmt) {
        this.grid = grid;
        this.stmt = stmt;
    }

    public void build() {
        TextField cNumber = new TextField();
        TextField sFor = new TextField();
        DatePicker dDate = new DatePicker();
        TextField dPlace = new TextField();
        DatePicker sDate = new DatePicker();

        configTextFieldForInts(cNumber);

        Bindings.bindBidirectional(cNumber.textProperty(), stmt.controlNumberProperty(), new NumberStringConverter());
        sFor.textProperty().bindBidirectional(stmt.servicesForNameProperty());
        dDate.valueProperty().bindBidirectional(stmt.dateOfDeathProperty());
        dPlace.textProperty().bindBidirectional(stmt.placeOfDeathProperty());
        sDate.valueProperty().bindBidirectional(stmt.serviceDateProperty());

        Label cNumberLabel = new Label("Control Number");
        Label sForLabel = new Label("Services For");
        Label dDateLabel = new Label("Date of Death");
        Label dPlaceLabel = new Label("Place of Death");
        Label sDateLabel = new Label("Service Date");

        formatLabels(cNumberLabel, sForLabel, dDateLabel, dPlaceLabel, sDateLabel);

        ColumnConstraints leftCol = new ColumnConstraints();
        leftCol.setHgrow(Priority.NEVER);
        leftCol.setHalignment(HPos.RIGHT);

        ColumnConstraints rightCol = new ColumnConstraints();
        rightCol.setHgrow(Priority.SOMETIMES);

        grid.getColumnConstraints().addAll(leftCol, rightCol);

        GridPane.setConstraints(cNumberLabel, 0, 0);
        GridPane.setConstraints(cNumber, 1, 0);
        GridPane.setConstraints(sForLabel, 0, 1);
        GridPane.setConstraints(sFor, 1, 1);
        GridPane.setConstraints(dDateLabel, 0, 2);
        GridPane.setConstraints(dDate, 1, 2);
        GridPane.setConstraints(dPlaceLabel, 0, 3);
        GridPane.setConstraints(dPlace, 1, 3);
        GridPane.setConstraints(sDateLabel, 0, 4);
        GridPane.setConstraints(sDate, 1, 4);

        grid.getChildren().addAll(cNumberLabel, cNumber, sForLabel, sFor, dDateLabel, dDate, dPlaceLabel, dPlace, sDateLabel, sDate);
    }

    private void formatLabels(Label... labels) {
        Arrays.stream(labels).forEach(label -> label.getStyleClass().add("field-label"));
    }

    private void configTextFieldForInts(TextField... fields) {
        Arrays.stream(fields).forEach(field -> field.setTextFormatter(new TextFormatter<Integer>((Change c) -> {
            if (c.getControlNewText().matches("-?\\d*")) {
                return c;
            }
            return null;
        })));
    }
}
