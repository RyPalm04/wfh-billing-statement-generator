package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.models.statement.Statement;
import com.palmer.billingstatementgenerator.models.statement.StatementContext;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class TabTwoFxmlController extends BaseController {

    @FXML private TextField controlNumberField;
    @FXML private TextField servicesForField;
    @FXML private DatePicker dateOfDeathPicker;
    @FXML private TextField placeOfDeathField;
    @FXML private DatePicker serviceDatePicker;

    @FXML
    private void initialize() {
        Statement stmt = StatementContext.current();

        configTextFieldForInts(controlNumberField);
        bindIntegerTextField(controlNumberField, stmt.controlNumberProperty());

        servicesForField.textProperty().bindBidirectional(stmt.servicesForNameProperty());
        dateOfDeathPicker.valueProperty().bindBidirectional(stmt.dateOfDeathProperty());
        placeOfDeathField.textProperty().bindBidirectional(stmt.placeOfDeathProperty());
        serviceDatePicker.valueProperty().bindBidirectional(stmt.serviceDateProperty());
    }
}