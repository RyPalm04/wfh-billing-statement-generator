package com.palmer.billingstatementgenerator.views;

import com.palmer.billingstatementgenerator.dao.StatementDao;
import com.palmer.billingstatementgenerator.db.Database;
import com.palmer.billingstatementgenerator.logging.WorkflowEventTracker;
import com.palmer.billingstatementgenerator.models.statement.SavedStatementSummary;
import com.palmer.billingstatementgenerator.models.statement.Statement;
import com.palmer.billingstatementgenerator.models.statement.StatementCalculator;
import com.palmer.billingstatementgenerator.models.statement.StatementContext;
import com.palmer.billingstatementgenerator.views.controllers.BaseController;
import com.palmer.billingstatementgenerator.views.controllers.CashAdvanceController;
import com.palmer.billingstatementgenerator.views.controllers.InstructionsTabController;
import com.palmer.billingstatementgenerator.views.controllers.MerchandiseController;
import com.palmer.billingstatementgenerator.views.controllers.ServicesController;
import com.palmer.billingstatementgenerator.views.controllers.SpecialChargesController;
import com.palmer.billingstatementgenerator.views.controllers.SummaryTabController;
import com.palmer.billingstatementgenerator.views.dialogs.AppDialog;
import com.palmer.billingstatementgenerator.views.dialogs.StartupDialog;
import com.palmer.billingstatementgenerator.views.dialogs.IncompleteAlertDialog;
import com.palmer.billingstatementgenerator.views.dialogs.OpenStatementDialog;
import com.palmer.billingstatementgenerator.views.dialogs.PdfDialog;
import com.palmer.billingstatementgenerator.views.dialogs.ResetStatementDialog;
import com.palmer.billingstatementgenerator.views.dialogs.SettingsDialog;
import com.palmer.billingstatementgenerator.views.dialogs.UnsavedChangesDialog;
import com.palmer.billingstatementgenerator.views.tabs.GeneratorTabs;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The main application view, responsible for assembling the full UI layout.
 * Manages the tab pane, header, button bar, and navigation between tabs.
 * Owns the shared Previous, Clear, and Next buttons and rewires them
 * reactively as the user navigates between tabs.
 */
public class MainView {
    private static final Logger log = LoggerFactory.getLogger(MainView.class);
    private static final String FXML_BASE = "/com/palmer/billingstatementgenerator/views/";

    private BorderPane root;
    private TabPane tabPane;
    private Button prevButton;
    private Button nextButton;
    private Button clearButton;
    private Button saveButton;
    private Button resetButton;

    /**
     * Controller for the summary tab, held for refresh and reset operations.
     */
    private SummaryTabController summaryController;

    /**
     * Event tracker used to extend logging to modal dialog scenes.
     */
    private WorkflowEventTracker eventTracker;

    /**
     * Constructs the main view by creating tabs, the button bar, the layout,
     * and wiring tab selection behavior.
     */
    public MainView() {
        createTabs();
        createButtonBar();
        createLayout();
        wireTabs();
        wireTabDisabling();
    }

    /**
     * Returns the root node of the main view for embedding in a {@link javafx.scene.Scene}.
     *
     * @return the root {@link Parent} node
     */
    public Parent asParent() {
        return root;
    }

    /**
     * Returns the workflow tab pane, used by external components to observe
     * or pass tab context to loggers and other coordinators.
     *
     * @return the main {@link TabPane}
     */
    public TabPane getTabPane() {
        return tabPane;
    }

    /**
     * Sets the event tracker so that modal dialog scenes created by this view
     * are also covered by UI activity logging.
     *
     * @param eventTracker
     *         the active {@link WorkflowEventTracker}
     */
    public void setEventTracker(WorkflowEventTracker eventTracker) {
        this.eventTracker = eventTracker;
        AppDialog.configure(root.getScene().getWindow(), eventTracker);
    }

    /**
     * Creates and configures all tabs, wiring total suppliers to the grid tab controllers
     * and assembling the tab pane.
     */
    private void createTabs() {
        InstructionsTabController instructionsController = new InstructionsTabController();
        ScrollPane instructionsView = instructionsController.buildView(
                i -> tabPane.getSelectionModel().select(i)
        );
        GeneratorTabs instructionsTab = GeneratorTabs.fromController(
                "INSTRUCTIONS", instructionsController, instructionsView);

        GeneratorTabs serviceInformationTab = GeneratorTabs.fromFxml("SERVICE INFORMATION", FXML_BASE + "service_information.fxml");

        ServicesController servicesController = new ServicesController();
        servicesController.setTotalSupplier(() -> StatementCalculator.servicesTotal(StatementContext.current()));

        MerchandiseController merchandiseController = new MerchandiseController();
        merchandiseController.setTotalSupplier(() -> StatementCalculator.merchandiseTotal(StatementContext.current()));

        SpecialChargesController specialChargesController = new SpecialChargesController();
        specialChargesController.setTotalSupplier(() -> StatementCalculator.specialChargesTotal(StatementContext.current()));

        CashAdvanceController cashAdvanceController = new CashAdvanceController();
        cashAdvanceController.setTotalSupplier(() -> StatementCalculator.cashAdvancesTotal(StatementContext.current()));

        GeneratorTabs servicesTab = GeneratorTabs.fromController("SERVICES, FACILITIES & TRANSPORTATION", servicesController);
        GeneratorTabs merchandiseTab = GeneratorTabs.fromController("MERCHANDISE", merchandiseController);
        GeneratorTabs specialChargesTab = GeneratorTabs.fromController("SPECIAL CHARGES", specialChargesController);
        GeneratorTabs cashAdvanceTab = GeneratorTabs.fromController("CASH ADVANCE ITEMS", cashAdvanceController);

        summaryController = new SummaryTabController();
        ScrollPane summaryView = summaryController.buildView(i -> tabPane.getSelectionModel().select(i));
        GeneratorTabs summaryTab = GeneratorTabs.fromController("SUMMARY", summaryController, summaryView);

        tabPane = new TabPane(instructionsTab, serviceInformationTab, servicesTab, merchandiseTab, specialChargesTab, cashAdvanceTab, summaryTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    }

    /**
     * Creates the shared navigation buttons and applies their base style classes.
     */
    private void createButtonBar() {
        prevButton = new Button("Previous");
        prevButton.setId("prevButton");
        clearButton = new Button("Clear Selections");
        clearButton.setId("clearButton");
        saveButton = new Button("Save");
        saveButton.setId("saveButton");
        nextButton = new Button("Next");
        nextButton.setId("nextButton");
        resetButton = new Button("Reset");
        resetButton.setId("resetButton");
        clearButton.getStyleClass().add("button-clear");
        saveButton.getStyleClass().add("button-save");
        resetButton.getStyleClass().add("button-reset");
        resetButton.setVisible(false);
    }

    /**
     * Builds the bottom button bar HBox with Previous, Clear, and Next buttons
     * separated by flexible spacers.
     *
     * @return the configured button bar {@link HBox}
     */
    private HBox buildButtonBar() {
        HBox rightGroup = new HBox(8, resetButton, saveButton, nextButton);
        HBox bar = new HBox(prevButton, spacer(), clearButton, spacer(), rightGroup);
        bar.setPadding(new Insets(12, 24, 12, 24));
        bar.setAlignment(Pos.CENTER);
        bar.getStyleClass().add("button-bar");
        return bar;
    }

    /**
     * Creates a horizontally growing spacer region for use in the button bar.
     *
     * @return a {@link Region} with horizontal grow priority set to ALWAYS
     */
    private Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    /**
     * Assembles the main layout with the logo header at the top,
     * tab pane in the center, and button bar at the bottom.
     */
    private void createLayout() {
        Image logo = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/img/wfh splash logo.jpg")));
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(80);
        logoView.setPreserveRatio(true);
        logoView.setOpacity(0.9);

        Button closeBtn = new Button("✕");
        closeBtn.setId("closeButton");
        closeBtn.getStyleClass().add("button-close");
        closeBtn.setOnAction(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            if (StatementContext.isDirty()) {
                showUnsavedChangesOnClose(stage);
            } else {
                stage.close();
            }
        });

        Button settingsBtn = new Button("⚙");
        settingsBtn.setId("settingsButton");
        settingsBtn.getStyleClass().add("button-close");
        settingsBtn.setOnAction(e -> new SettingsDialog().open());

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox header = new HBox(logoView, headerSpacer, settingsBtn, closeBtn);
        header.setPadding(new Insets(10, 16, 10, 16));
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("app-header");

        root = new BorderPane();
        root.setTop(header);
        root.setCenter(tabPane);
        root.setBottom(buildButtonBar());
    }

    /**
     * Wires the tab selection listener to update the button bar whenever
     * the selected tab changes, and sets the initial button bar state.
     */
    private void wireTabs() {
        updateButtonBar((GeneratorTabs) tabPane.getTabs().get(0));
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab instanceof GeneratorTabs) {
                updateButtonBar((GeneratorTabs) newTab);
            }
        });
    }

    /**
     * Updates the button bar state for the given tab. Hides the button bar on the
     * instructions tab, disables Previous on the first data tab, and switches the
     * Next button to "Generate PDF" and the Clear button to "Reset" on the last tab.
     *
     * @param tab
     *         the currently selected {@link GeneratorTabs}
     */
    private void updateButtonBar(GeneratorTabs tab) {
        int index = tabPane.getTabs().indexOf(tab);
        boolean isFirst = index == 0;
        boolean isLast = index == tabPane.getTabs().size() - 1;

        root.setBottom(isFirst ? null : buildButtonBar());

        unbindButtonDisable(prevButton, nextButton, saveButton);

        prevButton.setDisable(index == 1);
        saveButton.disableProperty().bind(StatementContext.dirtyProperty().not());
        saveButton.setOnAction(e -> saveCurrentStatement());

        BooleanBinding invalid = tab.getController().hasInvalidSelections();

        prevButton.setOnAction(e -> {
            if (invalid.get()) {
                new IncompleteAlertDialog().open();
            } else {
                tabPane.getSelectionModel().selectPrevious();
            }
        });

        if (isLast) {
            nextButton.setText("Generate PDF");
            nextButton.setOnAction(e -> {
                if (invalid.get()) {
                    new IncompleteAlertDialog().open();
                } else {
                    new PdfDialog().open();
                }
            });
            clearButton.disableProperty().unbind();
            clearButton.setVisible(false);
            resetButton.setVisible(true);
            resetButton.setOnAction(e -> showResetDialog());
        } else {
            nextButton.setText("Next");
            nextButton.setOnAction(e -> {
                if (invalid.get()) {
                    new IncompleteAlertDialog().open();
                } else {
                    tabPane.getSelectionModel().selectNext();
                }
            });
            resetButton.setVisible(false);
            clearButton.setVisible(true);
            tab.getController().setClearButton(clearButton);
        }
    }

    private void unbindButtonDisable(Button... buttons) {
        Arrays.stream(buttons).forEach(b -> b.disableProperty().unbind());
    }

    /**
     * Skips the Instructions tab and selects the Service Information tab directly.
     * Called on non-first launches.
     */
    public void skipInstructions() {
        tabPane.getSelectionModel().select(1);
    }

    /**
     * Wires Alt+Left / Alt+Right to navigate between tabs at the scene level,
     * so the shortcuts work regardless of which control has focus.
     *
     * @param scene
     *         the main application {@link Scene}
     */
    public void wireKeyNav(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.isShortcutDown() && e.getCode() == KeyCode.COMMA) {
                new SettingsDialog().open();
                e.consume();
            } else if (e.isAltDown()) {
                if (e.getCode() == KeyCode.RIGHT) {
                    tabPane.getSelectionModel().selectNext();
                    e.consume();
                } else if (e.getCode() == KeyCode.LEFT) {
                    tabPane.getSelectionModel().selectPrevious();
                    e.consume();
                }
            }
        });
    }

    /**
     * Sizes the application window to fit the largest tab by iterating through
     * all tabs, measuring their rendered size, and setting the stage dimensions
     * to the maximum observed width and height.
     */
    public void fitWindowToLargestTab() {
        if (root.getScene() == null) {
            return;
        }
        Window window = root.getScene().getWindow();
        if (!(window instanceof Stage)) {
            return;
        }
        Stage stage = (Stage) window;

        int originalIndex = tabPane.getSelectionModel().getSelectedIndex();
        double maxWidth = 0;
        double maxHeight = 0;

        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            tabPane.getSelectionModel().select(i);
            root.getScene().getRoot().applyCss();
            root.getScene().getRoot().layout();
            stage.sizeToScene();
            maxWidth = Math.max(maxWidth, stage.getWidth());
            maxHeight = Math.max(maxHeight, stage.getHeight());
        }

        tabPane.getSelectionModel().select(originalIndex);
        stage.setMinWidth(maxWidth);
        stage.setMinHeight(maxHeight);
        stage.setWidth(maxWidth);
        stage.setHeight(maxHeight);
    }

    /**
     * Saves the current statement. Calls {@link StatementDao#save} if it has never
     * been saved, or {@link StatementDao#update} if it already exists in the database.
     */
    private void saveCurrentStatement() {
        StatementDao dao = new StatementDao(Database.get());
        Integer savedId = StatementContext.getSavedId();
        if (savedId == null) {
            int newId = dao.save(StatementContext.current());
            StatementContext.markSaved(newId);
        } else {
            dao.update(savedId, StatementContext.current());
            StatementContext.markSaved(savedId);
        }
        log.info("Statement saved, id={}", StatementContext.getSavedId());
    }

    /**
     * Shows the reset dialog in a loop until the user makes a terminal choice.
     */
    private void showResetDialog() {
        boolean done = false;
        while (!done) {
            switch (new ResetStatementDialog().open()) {
                case NEW: {
                    Runnable doNew = () -> {
                        StatementContext.init();
                        rebuildView();
                        tabPane.getSelectionModel().select(1);
                    };
                    if (StatementContext.isDirty()) {
                        showUnsavedChangesDialog(doNew);
                    } else {
                        doNew.run();
                    }
                    done = true;
                    break;
                }
                case OPEN: {
                    Runnable doOpen = () -> {
                        if (!showOpenDialog()) {
                            showResetDialog();
                        }
                    };
                    if (StatementContext.isDirty()) {
                        showUnsavedChangesDialog(doOpen);
                    } else {
                        if (!showOpenDialog()) {
                            continue;
                        }
                    }
                    done = true;
                    break;
                }
                case CLEAR:
                    clearAllSelections();
                    tabPane.getSelectionModel().select(2);
                    done = true;
                    break;
                default:
                    done = true;
            }
        }
    }

    /**
     * Shows the open-statement dialog and returns {@code true} if a statement was loaded.
     */
    private boolean showOpenDialog() {
        List<SavedStatementSummary> summaries = new StatementDao(Database.get()).findAll();
        if (summaries.isEmpty()) {
            return false;
        }
        Integer id = new OpenStatementDialog(summaries).open();
        if (id != null) {
            StatementContext.load(id);
            rebuildView();
            tabPane.getSelectionModel().select(1);
            return true;
        }
        return false;
    }

    /**
     * Displays a modal "unsaved changes" warning and runs {@code onDiscard} if the
     * user confirms they want to discard changes.
     *
     * @param onDiscard
     *         the action to run if the user chooses to discard
     */
    private void showUnsavedChangesDialog(Runnable onDiscard) {
        new UnsavedChangesDialog(
                () -> {
                    saveCurrentStatement();
                    onDiscard.run();
                },
                onDiscard
        ).open();
    }

    /**
     * Called by the application after startup completes. Shows the launch dialog
     * and navigates based on the user's choice.
     *
     * @param firstLaunch
     *         whether this is the first time the app has been launched
     */
    public void onAppReady(boolean firstLaunch) {
        boolean done = false;
        while (!done) {
            List<SavedStatementSummary> saved = new StatementDao(Database.get()).findAll();
            switch (new StartupDialog(!saved.isEmpty()).open()) {
                case NEW:
                    if (!firstLaunch) {
                        skipInstructions();
                    }
                    done = true;
                    break;
                case OPEN:
                    done = showOpenDialog();
                    break;
                default:
                    done = true;
            }
        }
    }

    /**
     * Resets all tab controllers except the summary, then refreshes the summary.
     * Used by the Clear Selections option in the reset dialog.
     */
    private void clearAllSelections() {
        log.info("Clearing all selections");
        tabPane.getTabs().stream()
                .filter(t -> t instanceof GeneratorTabs)
                .map(t -> ((GeneratorTabs) t).getController())
                .filter(Objects::nonNull)
                .filter(c -> c != summaryController)
                .forEach(BaseController::reset);
        summaryController.refresh();
    }

    /**
     * Shows the unsaved changes dialog when the application window is closing.
     * If the user saves or discards, the stage is closed. Cancel leaves the app open.
     *
     * @param stage
     *         the primary application stage to close after confirmation
     */
    public void showUnsavedChangesOnClose(Stage stage) {
        showUnsavedChangesDialog(() -> {
            stage.setOnCloseRequest(null);
            stage.close();
        });
    }

    /**
     * Rebuilds the entire view after a full reset by recreating tabs,
     * the layout, and rewiring tab selection behavior.
     */
    private void rebuildView() {
        log.info("Rebuilding view after full reset");
        createTabs();
        root.setCenter(tabPane);
        wireTabs();
        wireTabDisabling();
        if (eventTracker != null) {
            eventTracker.onTabPaneReplaced(tabPane);
        }
    }

    /**
     * Binds the disabled state of all data tabs (indices 2–6) to whether the
     * Service Information tab has valid control number and services-for name.
     * Tabs re-enable automatically as soon as both fields are filled.
     */
    private void wireTabDisabling() {
        Statement stmt = StatementContext.current();
        BooleanBinding infoIncomplete = Bindings.createBooleanBinding(
                () -> stmt.getServicesForName().trim().isEmpty(),
                stmt.servicesForNameProperty()
        );
        for (int i = 2; i < tabPane.getTabs().size(); i++) {
            tabPane.getTabs().get(i).disableProperty().bind(infoIncomplete);
        }
    }
}