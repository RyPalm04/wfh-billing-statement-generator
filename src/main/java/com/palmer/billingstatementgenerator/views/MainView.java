package com.palmer.billingstatementgenerator.views;

import com.palmer.billingstatementgenerator.dao.StatementDao;
import com.palmer.billingstatementgenerator.db.Database;
import com.palmer.billingstatementgenerator.models.statement.SavedStatementSummary;
import com.palmer.billingstatementgenerator.models.statement.Statement;
import com.palmer.billingstatementgenerator.models.statement.StatementCalculator;
import com.palmer.billingstatementgenerator.models.statement.StatementContext;
import com.palmer.billingstatementgenerator.pdf.PdfGenerator;
import com.palmer.billingstatementgenerator.views.controllers.BaseController;
import com.palmer.billingstatementgenerator.views.controllers.CashAdvanceController;
import com.palmer.billingstatementgenerator.views.controllers.InstructionsTabController;
import com.palmer.billingstatementgenerator.views.controllers.MerchandiseController;
import com.palmer.billingstatementgenerator.views.controllers.ServicesController;
import com.palmer.billingstatementgenerator.views.controllers.SpecialChargesController;
import com.palmer.billingstatementgenerator.views.controllers.SummaryTabController;
import com.palmer.billingstatementgenerator.views.tabs.GeneratorTabs;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
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
                getClass().getResourceAsStream("/com/palmer/billingstatementgenerator/img/wfh splash logo.jpg")));
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

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox header = new HBox(logoView, headerSpacer, closeBtn);
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

        prevButton.disableProperty().unbind();
        nextButton.disableProperty().unbind();
        saveButton.disableProperty().unbind();

        prevButton.setDisable(index == 1);
        saveButton.disableProperty().bind(StatementContext.dirtyProperty().not());
        saveButton.setOnAction(e -> saveCurrentStatement());

        BooleanBinding invalid = tab.getController().hasInvalidSelections();

        prevButton.setOnAction(e -> {
            if (invalid.get()) {
                showIncompleteAlert();
            } else {
                tabPane.getSelectionModel().selectPrevious();
            }
        });

        if (isLast) {
            nextButton.setText("Generate PDF");
            nextButton.setOnAction(e -> {
                if (invalid.get()) {
                    showIncompleteAlert();
                } else {
                    PdfGenerator.export(root.getScene().getWindow());
                }
            });
            clearButton.disableProperty().unbind();
            clearButton.setDisable(true);
            resetButton.setVisible(true);
            resetButton.setOnAction(e -> showResetDialog());
        } else {
            nextButton.setText("Next");
            nextButton.setOnAction(e -> {
                if (invalid.get()) {
                    showIncompleteAlert();
                } else {
                    tabPane.getSelectionModel().selectNext();
                }
            });
            resetButton.setVisible(false);
            tab.getController().setClearButton(clearButton);
        }
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
            if (e.isAltDown()) {
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

    private Scene buildDialogScene(javafx.scene.Parent content) {
        Scene scene = new Scene(content);
        scene.getStylesheets().add(getClass().getResource(
                "/com/palmer/billingstatementgenerator/css/style.css").toExternalForm());
        return scene;
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
     * Displays a modal alert when the user attempts to navigate away from a tab
     * that has checked items missing a required price or description.
     */
    private void showIncompleteAlert() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(root.getScene().getWindow());
        dialog.setTitle("Incomplete Items");

        Label title = new Label("Incomplete Items");
        title.getStyleClass().add("splash-title");

        Label message = new Label(
                "One or more selected items are missing a required price or description.\n" +
                        "Please complete all selections before continuing.");
        message.getStyleClass().add("splash-subtitle");
        message.setWrapText(true);
        message.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        message.setMaxWidth(320);

        Button ok = new Button("Got It");
        ok.setId("okButton");
        ok.setOnAction(e -> dialog.close());

        VBox content = new VBox(20, title, message, ok);
        content.setPadding(new Insets(32));
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("splash-container");

        Scene scene = buildDialogScene(content);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    /**
     * Displays a modal reset dialog offering New Statement, Open Existing,
     * Clear Selections, or Cancel. New Statement and Open Existing prompt for
     * unsaved changes first if the current statement is dirty.
     */
    private void showResetDialog() {
        boolean done = false;
        while (!done) {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(root.getScene().getWindow());
            dialog.setTitle("Reset Statement");

            Label title = new Label("Reset Statement");
            title.getStyleClass().add("splash-title");

            Label message = new Label("How would you like to proceed?");
            message.getStyleClass().add("splash-subtitle");

            boolean[] choseNew = {false};
            boolean[] choseOpen = {false};
            boolean[] choseClear = {false};

            Button newStatement = new Button("New Statement");
            newStatement.setId("newStatementButton");
            newStatement.setOnAction(e -> {
                choseNew[0] = true;
                dialog.close();
            });

            Button openExisting = new Button("Open Existing");
            openExisting.setId("openExistingButton");
            openExisting.setOnAction(e -> {
                choseOpen[0] = true;
                dialog.close();
            });

            Button clearSelections = new Button("Clear Selections");
            clearSelections.setId("clearSelectionsButton");
            clearSelections.setOnAction(e -> {
                choseClear[0] = true;
                dialog.close();
            });

            Button cancel = new Button("Cancel");
            cancel.setId("cancelButton");
            cancel.getStyleClass().add("button-clear");
            cancel.setOnAction(e -> dialog.close());

            HBox buttons = new HBox(12, newStatement, openExisting, clearSelections, cancel);
            buttons.setAlignment(Pos.CENTER);

            VBox content = new VBox(20, title, message, buttons);
            content.setPadding(new Insets(32));
            content.setAlignment(Pos.CENTER);
            content.getStyleClass().add("splash-container");

            Scene scene = new Scene(content);
            scene.getStylesheets().add(getClass().getResource(
                    "/com/palmer/billingstatementgenerator/css/style.css").toExternalForm());
            dialog.setScene(scene);
            dialog.setResizable(false);
            dialog.showAndWait();

            if (choseNew[0]) {
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
            } else if (choseOpen[0]) {
                Runnable doOpen = () -> {
                    boolean opened = showOpenDialog();
                    if (!opened) showResetDialog();
                };
                if (StatementContext.isDirty()) {
                    showUnsavedChangesDialog(doOpen);
                } else {
                    boolean opened = showOpenDialog();
                    if (!opened) continue;
                }
                done = true;
            } else if (choseClear[0]) {
                clearAllSelections();
                tabPane.getSelectionModel().select(2);
                done = true;
            } else {
                done = true;
            }
        }
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
     * Displays a modal "Open Existing Statement" dialog listing all saved statements.
     * Returns {@code true} if the user opened a statement, {@code false} if they cancelled.
     */
    private boolean showOpenDialog() {
        List<SavedStatementSummary> summaries = new StatementDao(Database.get()).findAll();
        if (summaries.isEmpty()) {
            return false;
        }

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(root.getScene().getWindow());
        dialog.setTitle("Open Statement");

        Label title = new Label("Open Statement");
        title.getStyleClass().add("splash-title");

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter tsFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");

        ListView<SavedStatementSummary> list = new ListView<>(FXCollections.observableArrayList(summaries));
        list.setPrefHeight(220);
        list.setPrefWidth(520);
        list.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(SavedStatementSummary item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String date = item.getServiceDate() != null ? dateFmt.format(item.getServiceDate()) : "—";
                    String saved = item.getSavedAt() != null ? tsFmt.format(item.getSavedAt()) : "—";
                    setText(String.format("#%d  %-30s  Service: %-12s  Saved: %s",
                            item.getControlNumber(), item.getServicesForName(), date, saved));
                }
            }
        });

        boolean[] opened = {false};

        Button open = new Button("Open");
        open.setId("openButton");
        open.setDisable(true);
        list.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> open.setDisable(n == null));

        open.setOnAction(e -> {
            SavedStatementSummary selected = list.getSelectionModel().getSelectedItem();
            if (selected != null) {
                opened[0] = true;
                dialog.close();
                int id = selected.getId();
                Platform.runLater(() -> {
                    StatementContext.load(id);
                    rebuildView();
                    tabPane.getSelectionModel().select(1);
                });
            }
        });

        Button cancel = new Button("Cancel");
        cancel.setId("cancelButton");
        cancel.getStyleClass().add("button-clear");
        cancel.setOnAction(e -> dialog.close());

        HBox buttons = new HBox(12, open, cancel);
        buttons.setAlignment(Pos.CENTER);

        VBox content = new VBox(16, title, list, buttons);
        content.setPadding(new Insets(32));
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("splash-container");

        Scene scene = buildDialogScene(content);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
        return opened[0];
    }

    /**
     * Displays a modal "unsaved changes" warning and runs {@code onDiscard} if the
     * user confirms they want to discard changes.
     *
     * @param onDiscard
     *         the action to run if the user chooses to discard
     */
    private void showUnsavedChangesDialog(Runnable onDiscard) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(root.getScene().getWindow());
        dialog.setTitle("Unsaved Changes");

        Label title = new Label("Unsaved Changes");
        title.getStyleClass().add("splash-title");

        Label message = new Label("You have unsaved changes. Save before continuing?");
        message.getStyleClass().add("splash-subtitle");

        Button save = new Button("Save & Continue");
        save.setId("saveContinueButton");
        save.setOnAction(e -> {
            dialog.close();
            saveCurrentStatement();
            onDiscard.run();
        });

        Button discard = new Button("Discard");
        discard.setId("discardButton");
        discard.getStyleClass().add("button-reset");
        discard.setOnAction(e -> {
            dialog.close();
            onDiscard.run();
        });

        Button cancel = new Button("Cancel");
        cancel.setId("cancelButton");
        cancel.getStyleClass().add("button-clear");
        cancel.setOnAction(e -> dialog.close());

        HBox buttons = new HBox(12, save, discard, cancel);
        buttons.setAlignment(Pos.CENTER);

        VBox content = new VBox(20, title, message, buttons);
        content.setPadding(new Insets(32));
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("splash-container");

        Scene scene = buildDialogScene(content);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    /**
     * Called by the application after startup completes. Shows the launch dialog
     * if there are saved statements, otherwise navigates directly.
     *
     * @param firstLaunch
     *         whether this is the first time the app has been launched
     */
    public void onAppReady(boolean firstLaunch) {
        boolean done = false;
        while (!done) {
            List<SavedStatementSummary> saved = new StatementDao(Database.get()).findAll();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(root.getScene().getWindow());
            dialog.setTitle("Wright Funeral Home");

            Label title = new Label("Wright Funeral Home");
            title.getStyleClass().add("splash-title");

            Label message = new Label("What would you like to do?");
            message.getStyleClass().add("splash-subtitle");

            boolean[] choseNew = {false};
            boolean[] choseOpen = {false};

            Button newStatement = new Button("New Statement");
            newStatement.setId("newStatementButton");
            newStatement.setOnAction(e -> {
                choseNew[0] = true;
                dialog.close();
            });

            Button openExisting = new Button("Open Existing");
            openExisting.setId("openExistingButton");
            openExisting.setDisable(saved.isEmpty());
            openExisting.setOnAction(e -> {
                choseOpen[0] = true;
                dialog.close();
            });

            HBox buttons = new HBox(16, newStatement, openExisting);
            buttons.setAlignment(Pos.CENTER);

            VBox content = new VBox(20, title, message, buttons);
            content.setPadding(new Insets(32));
            content.setAlignment(Pos.CENTER);
            content.getStyleClass().add("splash-container");

            Scene scene = new Scene(content);
            scene.getStylesheets().add(getClass().getResource(
                    "/com/palmer/billingstatementgenerator/css/style.css").toExternalForm());
            dialog.setScene(scene);
            dialog.setResizable(false);
            dialog.showAndWait();

            if (choseNew[0]) {
                skipInstructions();
                done = true;
            } else if (choseOpen[0]) {
                done = showOpenDialog();
            } else {
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