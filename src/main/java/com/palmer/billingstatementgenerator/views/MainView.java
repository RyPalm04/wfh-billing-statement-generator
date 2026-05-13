package com.palmer.billingstatementgenerator.views;

import com.palmer.billingstatementgenerator.models.statement.StatementCalculator;
import com.palmer.billingstatementgenerator.models.statement.StatementContext;
import com.palmer.billingstatementgenerator.pdf.PdfGenerator;
import com.palmer.billingstatementgenerator.views.controllers.*;
import com.palmer.billingstatementgenerator.views.tabs.GeneratorTabs;

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
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Objects;

public class MainView {
	private static final String FXML_BASE = "/com/palmer/billingstatementgenerator/views/";

	private BorderPane root;
	private TabPane tabPane;
	private Button prevButton;
	private Button nextButton;
	private Button clearButton;

	private GeneratorTabs tabTwo;
	private SummaryTabController summaryController;

	public MainView() {
		createTabs();
		createButtonBar();
		createLayout();
		wireTabs();
	}

	public Parent asParent() {
		return root;
	}

	private void createTabs() {
		InstructionsTabController instructionsController = new InstructionsTabController();
		ScrollPane instructionsView = instructionsController.buildView(
				() -> tabPane.getSelectionModel().select(1)
		);
		GeneratorTabs tabInstructions = GeneratorTabs.fromController(
				"INSTRUCTIONS", instructionsController, instructionsView);

		tabTwo = GeneratorTabs.fromFxml("SERVICE INFORMATION", FXML_BASE + "service_information.fxml");
		ServicesController threeCtrl = new ServicesController();
		threeCtrl.setTotalSupplier(() -> StatementCalculator.servicesTotal(StatementContext.current()));

		MerchandiseController fourCtrl = new MerchandiseController();
		fourCtrl.setTotalSupplier(() -> StatementCalculator.merchandiseTotal(StatementContext.current()));

		SpecialChargesController fiveCtrl = new SpecialChargesController();
		fiveCtrl.setTotalSupplier(() -> StatementCalculator.specialChargesTotal(StatementContext.current()));

		CashAdvanceController sixCtrl = new CashAdvanceController();
		sixCtrl.setTotalSupplier(() -> StatementCalculator.cashAdvancesTotal(StatementContext.current()));

		GeneratorTabs tabThree = GeneratorTabs.fromController("SERVICES, FACILITIES & TRANSPORTATION", threeCtrl);
		GeneratorTabs tabFour  = GeneratorTabs.fromController("MERCHANDISE", fourCtrl);
		GeneratorTabs tabFive  = GeneratorTabs.fromController("SPECIAL CHARGES", fiveCtrl);
		GeneratorTabs tabSix   = GeneratorTabs.fromController("CASH ADVANCE ITEMS", sixCtrl);

		summaryController = new SummaryTabController();
		ScrollPane summaryView = summaryController.buildView(i -> tabPane.getSelectionModel().select(i));
		GeneratorTabs summaryTab = GeneratorTabs.fromController("SUMMARY", summaryController, summaryView);

		tabPane = new TabPane(tabInstructions, tabTwo, tabThree, tabFour, tabFive, tabSix, summaryTab);
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
	}

	private void createButtonBar() {
		prevButton  = new Button("Previous");
		clearButton = new Button("Clear Selections");
		nextButton  = new Button("Next");
		clearButton.getStyleClass().add("button-clear");
	}

	private HBox buildButtonBar() {
		HBox bar = new HBox(prevButton, leftSpacer(), clearButton, rightSpacer(), nextButton);
		bar.setPadding(new Insets(12, 24, 12, 24));
		bar.setAlignment(Pos.CENTER);
		bar.getStyleClass().add("button-bar");
		return bar;
	}

	private Region leftSpacer() {
		Region r = new Region();
		HBox.setHgrow(r, Priority.ALWAYS);
		return r;
	}

	private Region rightSpacer() {
		Region r = new Region();
		HBox.setHgrow(r, Priority.ALWAYS);
		return r;
	}

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

	private void wireTabs() {
		updateButtonBar((GeneratorTabs) tabPane.getTabs().get(0));
		tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
			if (newTab instanceof GeneratorTabs) {
				updateButtonBar((GeneratorTabs) newTab);
			}
		});
	}

	private void updateButtonBar(GeneratorTabs tab) {
		int index   = tabPane.getTabs().indexOf(tab);
		boolean isFirst = index == 0;
		boolean isLast  = index == tabPane.getTabs().size() - 1;

		root.setBottom(isFirst ? null : buildButtonBar());

		prevButton.setDisable(index == 0);
		prevButton.setOnAction(e -> tabPane.getSelectionModel().selectPrevious());

		if (isLast) {
			nextButton.setText("Generate PDF");
			nextButton.setOnAction(e -> PdfGenerator.export(root.getScene().getWindow()));
			clearButton.setText("Reset");
			clearButton.getStyleClass().add("button-reset");
			clearButton.setOnAction(e -> showResetDialog());
		} else {
			nextButton.setText("Next");
			nextButton.setOnAction(e -> tabPane.getSelectionModel().selectNext());
			clearButton.setText("Clear Selections");
			clearButton.getStyleClass().remove("button-reset");
			tab.getController().setClearButton(clearButton);
		}
	}

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

	private void clearAllSelections() {
		tabPane.getTabs().stream()
				.filter(t -> t instanceof GeneratorTabs)
				.map(t -> ((GeneratorTabs) t).getController())
				.filter(Objects::nonNull)
				.filter(c -> c != summaryController)
				.forEach(BaseController::reset);
		summaryController.refresh();
	}

	private void rebuildView() {
		createTabs();
		createLayout();
		wireTabs();
	}
}