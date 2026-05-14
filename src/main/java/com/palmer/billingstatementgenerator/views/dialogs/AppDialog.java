package com.palmer.billingstatementgenerator.views.dialogs;

import com.palmer.billingstatementgenerator.logging.WorkflowEventTracker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Abstract base for application modal dialogs. Subclasses define their content
 * via {@link #buildContent()} and display themselves by calling {@link #open()}.
 *
 * <p>The owner window and event tracker are registered once via
 * {@link #configure(Window, WorkflowEventTracker)} at startup and shared by all
 * dialogs, so call sites need no knowledge of the UI hierarchy.</p>
 */
public abstract class AppDialog extends Stage {

    private static final String STYLESHEET =
            "/com/palmer/billingstatementgenerator/css/style.css";

    private static Window defaultOwner;
    private static WorkflowEventTracker defaultTracker;

    /**
     * Registers the application owner window and event tracker. Must be called once
     * before any dialog is opened, typically when the main stage becomes visible.
     *
     * @param owner
     *         the primary application {@link Window}
     * @param tracker
     *         the active {@link WorkflowEventTracker}, or {@code null}
     */
    public static void configure(Window owner, WorkflowEventTracker tracker) {
        defaultOwner = owner;
        defaultTracker = tracker;
    }

    /**
     * Builds and returns the dialog's content node. Called once by {@link #open()}
     * immediately before the dialog is shown, so button handlers may safely
     * reference {@link #close()} via {@code this}.
     *
     * @return the root content {@link VBox} for this dialog
     */
    protected abstract VBox buildContent();

    /**
     * Configures the stage, attaches the stylesheet and event tracker, then shows
     * the dialog and blocks until it is closed.
     */
    public void open() {
        initStyle(StageStyle.UNDECORATED);
        initModality(Modality.APPLICATION_MODAL);
        initOwner(defaultOwner);
        setResizable(false);

        Scene scene = new Scene(buildContent());
        scene.getStylesheets().add(AppDialog.class.getResource(STYLESHEET).toExternalForm());
        if (defaultTracker != null) {
            defaultTracker.attachTo(scene);
        }
        setScene(scene);
        showAndWait();
    }

    /**
     * Builds the standard titled content box: a {@link VBox} with a
     * {@code "splash-title"} heading, 32 px padding, centered alignment,
     * and the {@code "splash-container"} style applied.
     *
     * @param title
     *         the dialog heading text
     * @param children
     *         body nodes to display below the title
     * @return the assembled content {@link VBox}
     */
    protected static VBox contentBox(String title, Node... children) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("splash-title");

        Node[] all = new Node[children.length + 1];
        all[0] = titleLabel;
        System.arraycopy(children, 0, all, 1, children.length);

        VBox box = new VBox(20, all);
        box.setPadding(new Insets(32));
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("splash-container");
        return box;
    }
}