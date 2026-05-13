package com.palmer.billingstatementgenerator.views;

import com.palmer.billingstatementgenerator.models.statement.StatementCalculator;
import com.palmer.billingstatementgenerator.models.statement.StatementContext;
import com.palmer.billingstatementgenerator.pdf.PdfGenerator;
import com.palmer.billingstatementgenerator.views.controllers.*;
import com.palmer.billingstatementgenerator.views.tabs.GeneratorTabs;

import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Objects;

/**
 * The main application view, responsible for assembling the full UI layout.
 * Manages the tab pane, header, button bar, and navigation between tabs.
 * Owns the shared Previous, Clear, and Next buttons and rewires them
 * reactively as the user navigates between tabs.
 */
public class MainView {
	private static final String FXML_BASE = "/com/palmer/billingstatementgenerator/views/";

	private BorderPane root;
	private TabPane tabPane;
	private Button prevButton;
	private Button nextButton;
	private Button clearButton;

    /** Controller for the summary tab, held for refresh and reset operations. */
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
		GeneratorTabs merchandiseTab  = GeneratorTabs.fromController("MERCHANDISE", merchandiseController);
		GeneratorTabs specialChargesTab  = GeneratorTabs.fromController("SPECIAL CHARGES", specialChargesController);
		GeneratorTabs cashAdvanceTab   = GeneratorTabs.fromController("CASH ADVANCE ITEMS", cashAdvanceController);

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
		prevButton  = new Button("Previous");
		clearButton = new Button("Clear Selections");
		nextButton  = new Button("Next");
		clearButton.getStyleClass().add("button-clear");
	}

	/**
	 * Builds the bottom button bar HBox with Previous, Clear, and Next buttons
	 * separated by flexible spacers.
	 *
	 * @return the configured button bar {@link HBox}
	 */
	private HBox buildButtonBar() {
		HBox bar = new HBox(prevButton, leftSpacer(), clearButton, rightSpacer(), nextButton);
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
	private Region leftSpacer() {
		Region r = new Region();
		HBox.setHgrow(r, Priority.ALWAYS);
		return r;
	}

	/**
	 * Creates a horizontally growing spacer region for use in the button bar.
	 *
	 * @return a {@link Region} with horizontal grow priority set to ALWAYS
	 */
	private Region rightSpacer() {
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

		HBox header = new HBox(logoView);
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
	 * @param tab the currently selected {@link GeneratorTabs}
	 */
	private void updateButtonBar(GeneratorTabs tab) {
		int index    = tabPane.getTabs().indexOf(tab);
		boolean isFirst = index == 0;
		boolean isLast  = index == tabPane.getTabs().size() - 1;

		root.setBottom(isFirst ? null : buildButtonBar());

		prevButton.disableProperty().unbind();
		nextButton.disableProperty().unbind();

		BooleanBinding invalid = tab.getController().hasInvalidSelections();

		prevButton.setOnAction(e -> {
			if (invalid.get()) showIncompleteAlert();
			else tabPane.getSelectionModel().selectPrevious();
		});

		if (isLast) {
			nextButton.setText("Generate PDF");
			nextButton.setOnAction(e -> {
				if (invalid.get()) showIncompleteAlert();
				else PdfGenerator.export(root.getScene().getWindow());
			});
			clearButton.setText("Reset");
			clearButton.getStyleClass().add("button-reset");
			clearButton.setOnAction(e -> showResetDialog());
		} else {
			nextButton.setText("Next");
			nextButton.setOnAction(e -> {
				if (invalid.get()) showIncompleteAlert();
				else tabPane.getSelectionModel().selectNext();
			});
			clearButton.setText("Clear Selections");
			clearButton.getStyleClass().remove("button-reset");
			tab.getController().setClearButton(clearButton);
		}
	}

	/**
	 * Wires Alt+Left / Alt+Right to navigate between tabs at the scene level,
	 * so the shortcuts work regardless of which control has focus.
	 *
	 * @param scene the main application {@link Scene}
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

	/**
	 * Sizes the application window to fit the largest tab by iterating through
	 * all tabs, measuring their rendered size, and setting the stage dimensions
	 * to the maximum observed width and height.
	 */
	public void fitWindowToLargestTab() {
		if (root.getScene() == null) return;
		Window window = root.getScene().getWindow();
		if (!(window instanceof Stage)) return;
		Stage stage = (Stage) window;

		int originalIndex = tabPane.getSelectionModel().getSelectedIndex();
		double maxWidth  = 0;
		double maxHeight = 0;

		for (int i = 0; i < tabPane.getTabs().size(); i++) {
			tabPane.getSelectionModel().select(i);
			root.getScene().getRoot().applyCss();
			root.getScene().getRoot().layout();
			stage.sizeToScene();
			maxWidth  = Math.max(maxWidth,  stage.getWidth());
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
		ok.setOnAction(e -> dialog.close());

		VBox content = new VBox(20, title, message, ok);
		content.setPadding(new Insets(32));
		content.setAlignment(Pos.CENTER);
		content.getStyleClass().add("splash-container");

		Scene scene = new Scene(content);
		scene.getStylesheets().add(getClass().getResource(
				"/com/palmer/billingstatementgenerator/css/style.css").toExternalForm());
		dialog.setScene(scene);
		dialog.setResizable(false);
		dialog.showAndWait();
	}

	/**
	 * Displays a modal reset dialog offering three options: Full Reset,
	 * Clear Selections, or Cancel. Full Reset reinitializes the statement
	 * context and rebuilds the entire view. Clear Selections resets all
	 * tab controllers without affecting service information.
	 */
	private void showResetDialog() {
		Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(root.getScene().getWindow());
		dialog.setTitle("Reset Statement");

		Label title = new Label("Reset Statement");
		title.getStyleClass().add("splash-title");

		Label message = new Label("How would you like to proceed?");
		message.getStyleClass().add("splash-subtitle");

		Button fullReset = new Button("Full Reset");
		fullReset.setOnAction(e -> {
			dialog.close();
			StatementContext.init();
			rebuildView();
		});

		Button clearSelections = new Button("Clear Selections");
		clearSelections.setOnAction(e -> {
			dialog.close();
			clearAllSelections();
		});

		Button cancel = new Button("Cancel");
		cancel.getStyleClass().add("button-clear");
		cancel.setOnAction(e -> dialog.close());

		HBox buttons = new HBox(12, fullReset, clearSelections, cancel);
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
	}

	/**
	 * Resets all tab controllers except the summary, then refreshes the summary.
	 * Used by the Clear Selections option in the reset dialog.
	 */
	private void clearAllSelections() {
		tabPane.getTabs().stream()
				.filter(t -> t instanceof GeneratorTabs)
				.map(t -> ((GeneratorTabs) t).getController())
				.filter(Objects::nonNull)
				.filter(c -> c != summaryController)
				.forEach(BaseController::reset);
		summaryController.refresh();
	}

	/**
	 * Rebuilds the entire view after a full reset by recreating tabs,
	 * the layout, and rewiring tab selection behavior.
	 */
	private void rebuildView() {
		createTabs();
		createLayout();
		wireTabs();
	}
}