package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.models.Statement;
import com.palmer.billingstatementgenerator.models.StatementContext;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

/**
 * FXML controller for TabTwo. Binds controls to the current Statement.
 */
public class TabTwoFxmlController extends BaseController {

    @FXML private TextField controlNumberField;
    @FXML private TextField servicesForField;
    @FXML private DatePicker dateOfDeathPicker;
    @FXML private TextField placeOfDeathField;
    @FXML private DatePicker serviceDatePicker;

    @FXML
    private void initialize() {
        Statement stmt = StatementContext.current();

        // configure and bind using helpers from BaseController
        configTextFieldForInts(controlNumberField);
        bindIntegerTextField(controlNumberField, stmt.controlNumberProperty());

        servicesForField.textProperty().bindBidirectional(stmt.servicesForNameProperty());
        dateOfDeathPicker.valueProperty().bindBidirectional(stmt.dateOfDeathProperty());
        placeOfDeathField.textProperty().bindBidirectional(stmt.placeOfDeathProperty());
        serviceDatePicker.valueProperty().bindBidirectional(stmt.serviceDateProperty());
    }
}
