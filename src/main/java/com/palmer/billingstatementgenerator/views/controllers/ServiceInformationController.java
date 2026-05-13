package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.models.statement.Statement;
import com.palmer.billingstatementgenerator.models.statement.StatementContext;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 * FXML controller for the Service Information tab.
 * Binds form fields to the current {@link Statement} for capturing
 * basic information about the deceased and the service.
 */
public class ServiceInformationController extends BaseController {

    /**
     * Field for entering the statement control number.
     */
    @FXML
    private TextField controlNumberField;

    /**
     * Field for entering the name of the person services are for.
     */
    @FXML
    private TextField servicesForField;

    /**
     * Date picker for selecting the date of death.
     */
    @FXML
    private DatePicker dateOfDeathPicker;

    /**
     * Field for entering the place of death.
     */
    @FXML
    private TextField placeOfDeathField;

    /**
     * Date picker for selecting the date of the service.
     */
    @FXML
    private DatePicker serviceDatePicker;

    /**
     * Initializes the form by binding all fields bidirectionally
     * to the current {@link Statement} properties.
     */
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