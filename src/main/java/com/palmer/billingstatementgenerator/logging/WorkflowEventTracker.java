package com.palmer.billingstatementgenerator.logging;

import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Window;

/**
 * Coordinates UI activity logging for the billing statement workflow.
 * Attaches key stroke and mouse click logging to the window's scene,
 * and wires tab navigation logging to the workflow tab pane.
 * Scene changes are handled automatically — the event filter and focus
 * listener follow the window as its scene changes.
 *
 * <p>Usage:
 * <pre>
 *     new WorkflowEventTracker(mainStage, tabPane);
 * </pre>
 *
 * <p>Individual loggers can be configured in logback independently:
 * <ul>
 *   <li>{@code com.palmer.billingstatementgenerator.logging} — all UI logging</li>
 *   <li>{@code com.palmer.billingstatementgenerator.logging.KeyStrokeLogger}</li>
 *   <li>{@code com.palmer.billingstatementgenerator.logging.MouseClickLogger}</li>
 *   <li>{@code com.palmer.billingstatementgenerator.logging.TabNavigationLogger}</li>
 * </ul>
 */
public class WorkflowEventTracker {

    private final KeyStrokeLogger keyStrokeLogger;
    private final MouseClickLogger mouseClickLogger;
    private final TabNavigationLogger tabNavigationLogger = new TabNavigationLogger();
    private final EventHandler<Event> eventFilter = this::dispatch;
    private final ChangeListener<Node> focusChangeListener =
            (obs, oldNode, newNode) -> handleFocusChange(newNode);
    private final ChangeListener<Scene> sceneChangeListener =
            (obs, oldScene, newScene) -> handleSceneChange(oldScene, newScene);
    private TabPane tabPane;
    private final ChangeListener<Tab> tabChangeListener = (obs, oldTab, newTab) -> {
        if (newTab != null) {
            int step = tabPane.getTabs().indexOf(newTab) + 1;
            tabNavigationLogger.logNavigation(newTab.getText(), step, tabPane.getTabs().size());
        }
    };

    /**
     * Creates a tracker and immediately wires it to the given window and tab pane.
     *
     * @param window
     *         the application window to attach the event filter to
     * @param tabPane
     *         the workflow tab pane used for tab context and navigation logging
     */
    public WorkflowEventTracker(Window window, TabPane tabPane) {
        this.tabPane = tabPane;
        mouseClickLogger = new MouseClickLogger(() -> {
            Tab selected = this.tabPane.getSelectionModel().getSelectedItem();
            return selected != null ? selected.getText() : "";
        });
        keyStrokeLogger = new KeyStrokeLogger();

        tabPane.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);

        Scene scene = window.getScene();
        if (scene != null) {
            attachToScene(scene);
        }
        window.sceneProperty().addListener(sceneChangeListener);
    }

    /**
     * Updates the tracked tab pane after the view is rebuilt.
     * Removes the tab change listener from the old pane and attaches it to the new one.
     *
     * @param newTabPane
     *         the replacement tab pane created by the rebuild
     */
    public void onTabPaneReplaced(TabPane newTabPane) {
        tabPane.getSelectionModel().selectedItemProperty().removeListener(tabChangeListener);
        tabPane = newTabPane;
        newTabPane.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);
    }

    private void dispatch(Event event) {
        if (keyStrokeLogger.getEventType().equals(event.getEventType()) && keyStrokeLogger.isEnabled()) {
            keyStrokeLogger.log(event);
        } else if (mouseClickLogger.getEventType().equals(event.getEventType()) && mouseClickLogger.isEnabled()) {
            mouseClickLogger.log(event);
        }
    }

    private void handleSceneChange(Scene oldScene, Scene newScene) {
        if (oldScene != null) {
            detachFromScene(oldScene);
        }
        if (newScene != null) {
            attachToScene(newScene);
        }
    }

    private void handleFocusChange(Node newFocusOwner) {
        keyStrokeLogger.setNodeInFocus(newFocusOwner);
        mouseClickLogger.setNodeInFocus(newFocusOwner);
    }

    /**
     * Attaches event logging to the given scene.
     * Use this to extend logging to modal dialog scenes that are separate
     * from the main window's scene.
     *
     * @param scene
     *         the scene to attach the event filter and focus listener to
     */
    public void attachTo(Scene scene) {
        scene.addEventFilter(Event.ANY, eventFilter);
        scene.focusOwnerProperty().addListener(focusChangeListener);
    }

    private void attachToScene(Scene scene) {
        attachTo(scene);
    }

    private void detachFromScene(Scene scene) {
        scene.removeEventFilter(Event.ANY, eventFilter);
        scene.focusOwnerProperty().removeListener(focusChangeListener);
    }
}