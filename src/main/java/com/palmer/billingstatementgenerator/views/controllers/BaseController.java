package com.palmer.billingstatementgenerator.views.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.util.converter.NumberStringConverter;

import java.util.Arrays;

/**
 * Base controller providing common helpers for FXML controllers.
 */
public abstract class BaseController {

    protected void configTextFieldForInts(TextField... fields) {
        Arrays.stream(fields).forEach(field -> field.setTextFormatter(new TextFormatter<Integer>((Change c) -> {
            if (c.getControlNewText().matches("-?\\d*")) {
                return c;
            }
            return null;
        })));
    }

    protected void bindIntegerTextField(TextField field, IntegerProperty prop) {
        Bindings.bindBidirectional(field.textProperty(), prop, new NumberStringConverter());
    }
}
