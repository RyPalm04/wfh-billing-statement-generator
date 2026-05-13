package com.palmer.billingstatementgenerator.logging;

import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs {@link KeyEvent#KEY_PRESSED} events.
 * Suppresses logging when a {@link PasswordField} holds focus.
 * INFO level records the key character; DEBUG adds shift and ctrl modifier state.
 */
public class KeyStrokeLogger extends UiEventLogger {

    private static final Logger logger = LoggerFactory.getLogger(KeyStrokeLogger.class);

    /** Constructs a logger for KEY_PRESSED events. */
    public KeyStrokeLogger() {
        super(KeyEvent.KEY_PRESSED);
    }

    @Override
    protected boolean shouldLog() {
        return !(getNodeInFocus() instanceof PasswordField);
    }

    @Override
    protected String infoMessage(javafx.event.Event event) {
        return "Key Pressed '" + keyText(event) + "'";
    }

    @Override
    protected String debugMessage(javafx.event.Event event) {
        KeyEvent ke = (KeyEvent) event;
        return String.format("Key Pressed %s [shift=%s, ctrl=%s]",
                keyText(event), ke.isShiftDown(), ke.isControlDown());
    }

    private String keyText(javafx.event.Event event) {
        KeyEvent ke = (KeyEvent) event;
        KeyCode code = ke.getCode();
        if (!ke.isShortcutDown() && (code.isDigitKey() || code.isLetterKey())) {
            return ke.getText();
        }
        return code.toString();
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}