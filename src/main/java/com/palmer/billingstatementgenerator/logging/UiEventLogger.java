package com.palmer.billingstatementgenerator.logging;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import org.slf4j.Logger;

/**
 * Abstract base for scene-level event loggers.
 * Each subclass owns one event type and provides INFO and DEBUG message variants.
 * The currently focused node is tracked so subclasses can suppress logging
 * for sensitive controls such as password fields.
 * All messages are emitted at INFO level; DEBUG vs INFO determines verbosity,
 * not the log call itself.
 */
public abstract class UiEventLogger {

    private final EventType<? extends Event> eventType;
    private Node nodeInFocus;

    /**
     * @param eventType
     *         the JavaFX event type this logger handles
     */
    protected UiEventLogger(EventType<? extends Event> eventType) {
        this.eventType = eventType;
    }

    /**
     * Returns the event type this logger handles.
     *
     * @return the event type
     */
    public EventType<? extends Event> getEventType() {
        return eventType;
    }

    /**
     * Returns {@code true} if INFO or DEBUG logging is active for this logger.
     *
     * @return whether logging is currently enabled
     */
    public boolean isEnabled() {
        Logger logger = getLogger();
        return logger != null && (logger.isInfoEnabled() || logger.isDebugEnabled());
    }

    /**
     * Logs the event if {@link #shouldLog()} permits.
     * Selects the DEBUG message when DEBUG is enabled, INFO message otherwise.
     *
     * @param event
     *         the event to log
     */
    public void log(Event event) {
        if (!shouldLog()) {
            return;
        }
        Logger logger = getLogger();
        if (logger == null) {
            return;
        }
        try {
            String message;
            if (logger.isDebugEnabled()) {
                message = debugMessage(event);
            } else if (logger.isInfoEnabled()) {
                message = infoMessage(event);
            } else {
                return;
            }
            if (message != null) {
                logger.info(message);
            }
        } catch (Exception ex) {
            logger.warn("Failed to log UI event: {}", ex.getMessage());
        }
    }

    /**
     * Returns {@code true} if this event should be logged.
     * Override to suppress events under specific conditions.
     *
     * @return whether to proceed with logging
     */
    protected boolean shouldLog() {
        return true;
    }

    /**
     * Builds the INFO-level log message.
     *
     * @param event
     *         the event to describe
     *
     * @return the message, or {@code null} to suppress
     */
    protected String infoMessage(Event event) {
        return null;
    }

    /**
     * Builds the DEBUG-level log message with additional detail.
     *
     * @param event
     *         the event to describe
     *
     * @return the message, or {@code null} to suppress
     */
    protected String debugMessage(Event event) {
        return null;
    }

    /**
     * Returns the SLF4J logger for this instance.
     *
     * @return the logger
     */
    protected abstract Logger getLogger();

    /**
     * Sets the node that currently holds scene focus.
     *
     * @param nodeInFocus
     *         the focused node, or {@code null}
     */
    public void setNodeInFocus(Node nodeInFocus) {
        this.nodeInFocus = nodeInFocus;
    }

    /**
     * Returns the node that currently holds scene focus.
     *
     * @return the focused node, or {@code null}
     */
    protected Node getNodeInFocus() {
        return nodeInFocus;
    }
}