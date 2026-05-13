package com.palmer.billingstatementgenerator.views.tabs;

import com.palmer.billingstatementgenerator.views.controllers.BaseController;
import com.palmer.billingstatementgenerator.views.controllers.GridTabController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * A {@link Tab} subclass used for all tabs in the billing statement generator.
 * Supports two construction modes: loading content from an FXML file, or
 * accepting a pre-built {@link GridTabController} or arbitrary view node.
 * Wires the tab's selected state to the controller's {@link BaseController#onShow()}
 * and {@link BaseController#onHide()} lifecycle methods.
 */
public class GeneratorTabs extends Tab {
    private static final Logger log = LoggerFactory.getLogger(GeneratorTabs.class);
    /**
     * The root grid pane that holds the tab's content.
     */
    protected GridPane grid = new GridPane();
    /**
     * The controller associated with this tab's content.
     */
    private BaseController controller;

    /**
     * Private constructor. Use the static factory methods to create instances.
     *
     * @param tabTitle
     *         the text label displayed on the tab
     */
    private GeneratorTabs(String tabTitle) {
        super(tabTitle);
        configureGrid();
    }

    /**
     * Creates a {@link GeneratorTabs} by loading its content from an FXML file.
     * The FXML's controller must extend {@link BaseController}.
     *
     * @param title
     *         the tab label text
     * @param fxmlPath
     *         the classpath resource path to the FXML file
     *
     * @return a configured {@link GeneratorTabs} instance
     */
    public static GeneratorTabs fromFxml(String title, String fxmlPath) {
        GeneratorTabs tab = new GeneratorTabs(title);
        tab.loadFxml(fxmlPath);
        return tab;
    }

    /**
     * Creates a {@link GeneratorTabs} by building its content from a {@link GridTabController}.
     * Calls {@link GridTabController#buildView()} to construct the grid content.
     *
     * @param title
     *         the tab label text
     * @param controller
     *         the grid tab controller to build the view from
     *
     * @return a configured {@link GeneratorTabs} instance
     */
    public static GeneratorTabs fromController(String title, GridTabController<?> controller) {
        GeneratorTabs tab = new GeneratorTabs(title);
        tab.loadController(controller);
        return tab;
    }

    /**
     * Creates a {@link GeneratorTabs} from a pre-built view node and a {@link BaseController}.
     * Used for tabs whose views are built entirely in code rather than via FXML or a grid controller
     * (e.g. the Instructions and Summary tabs).
     *
     * @param title
     *         the tab label text
     * @param controller
     *         the controller to associate with this tab
     * @param view
     *         the pre-built view node to display as tab content
     *
     * @return a configured {@link GeneratorTabs} instance
     */
    public static GeneratorTabs fromController(String title, BaseController controller, javafx.scene.Node view) {
        GeneratorTabs tab = new GeneratorTabs(title);
        tab.controller = controller;
        GridPane.setConstraints(view, 0, 0);
        tab.grid.getChildren().add(view);
        tab.wireLifecycle();
        return tab;
    }

    /**
     * Loads the FXML at the given resource path, merges its column constraints and
     * children into the tab's grid, and wires the controller lifecycle.
     *
     * @param fxmlPath
     *         the classpath resource path to the FXML file
     *
     * @throws RuntimeException
     *         if the FXML cannot be loaded
     */
    private void loadFxml(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            GridPane loaded = loader.load();
            grid.getColumnConstraints().addAll(loaded.getColumnConstraints());
            grid.getChildren().addAll(loaded.getChildren());
            Object ctrl = loader.getController();
            if (ctrl instanceof BaseController) {
                controller = (BaseController) ctrl;
                wireLifecycle();
            }
        } catch (IOException e) {
            log.error("Failed to load FXML: {}", fxmlPath, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Builds the view from a {@link GridTabController} and adds it to the tab's grid.
     *
     * @param ctrl
     *         the grid tab controller to build from
     */
    private void loadController(GridTabController<?> ctrl) {
        controller = ctrl;
        GridPane built = ctrl.buildView();
        GridPane.setConstraints(built, 0, 0);
        grid.getChildren().add(built);
        wireLifecycle();
    }

    /**
     * Wires the tab's selected property to the controller's
     * {@link BaseController#onShow()} and {@link BaseController#onHide()} methods.
     */
    private void wireLifecycle() {
        this.selectedProperty().addListener((obs, wasSel, isSel) -> {
            if (isSel) {
                controller.onShow();
            } else {
                controller.onHide();
            }
        });
    }

    /**
     * Configures the tab's root grid pane with standard alignment, spacing, and padding.
     */
    private void configureGrid() {
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(12);
        grid.setVgap(16);
        grid.setPadding(new Insets(24));
        this.setContent(grid);
    }

    /**
     * Returns the controller associated with this tab.
     *
     * @return the {@link BaseController} for this tab
     */
    public BaseController getController() {
        return controller;
    }
}