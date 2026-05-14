package com.palmer.billingstatementgenerator.logging;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Logs {@link MouseEvent#MOUSE_CLICKED} events with the active workflow tab as context.
 * Uses {@link MouseEvent#getPickResult()} to resolve the exact intersected node,
 * then walks up from internal text nodes to reach the parent control.
 * INFO level records the target node; DEBUG adds button, coordinates, and active tab.
 */
public class MouseClickLogger extends UiEventLogger {

    private static final Logger logger = LoggerFactory.getLogger(MouseClickLogger.class);
    private static final int MAX_TEXT_LENGTH = 20;

    private final Supplier<String> activeTabName;

    /**
     * @param activeTabName
     *         supplies the name of the currently selected workflow tab,
     *         included in DEBUG output for correlating clicks to workflow steps
     */
    public MouseClickLogger(Supplier<String> activeTabName) {
        super(MouseEvent.MOUSE_CLICKED);
        this.activeTabName = activeTabName;
    }

    @Override
    protected String infoMessage(Event event) {
        return "CLICK → " + targetLabel(event);
    }

    @Override
    protected String debugMessage(Event event) {
        MouseEvent me = (MouseEvent) event;
        return String.format("CLICK %s (%d,%d) → %s {%s}",
                me.getButton(), (int) me.getSceneX(), (int) me.getSceneY(),
                targetLabel(event), activeTabName.get());
    }

    private String targetLabel(Event event) {
        MouseEvent me = (MouseEvent) event;
        Node picked = me.getPickResult().getIntersectedNode();
        EventTarget target = Optional.<EventTarget>ofNullable(picked).orElseGet(me::getTarget);
        return describe(target);
    }

    private String describe(EventTarget target) {
        if (target == null) {
            return "(unknown)";
        }
        if (target instanceof Text) {
            Node parent = ((Text) target).getParent();
            return parent != null ? describe(parent) : "(text)";
        }
        if (target instanceof Button) {
            return labeled(((Button) target).getText(), (Button) target);
        }
        if (target instanceof CheckBox) {
            return labeled(((CheckBox) target).getText(), (CheckBox) target);
        }
        if (target instanceof TextInputControl) {
            return labeled(truncate(((TextInputControl) target).getText()), (TextInputControl) target);
        }
        if (target instanceof Label) {
            return labeled(truncate(((Label) target).getText()), (Label) target);
        }
        if (target instanceof ComboBox) {
            return labeled("ComboBox", (ComboBox<?>) target);
        }
        if (target instanceof Node) {
            Node node = (Node) target;
            String id = node.getId();
            return (id != null && !id.isEmpty()) ? "#" + id : node.getClass().getSimpleName();
        }
        return target.getClass().getSimpleName();
    }

    private String labeled(String text, Node node) {
        String id = node.getId();
        if (id != null && !id.isEmpty()) {
            return "#" + id;
        }
        return (text != null && !text.isEmpty()) ? text : node.getClass().getSimpleName();
    }

    private String truncate(String text) {
        if (text == null) {
            return "";
        }
        return text.length() <= MAX_TEXT_LENGTH ? text : text.substring(0, MAX_TEXT_LENGTH);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}