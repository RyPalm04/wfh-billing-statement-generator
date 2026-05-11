package com.palmer.billingstatementgenerator.views;

import com.palmer.billingstatementgenerator.views.tabs.*;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MainView {
	private TabPane view;
	private TabTwo tabTwo;
	private TabThree tabThree;
	private TabFour tabFour;
	private TabFive tabFive;
	private TabSix tabSix;

	public MainView() {
		createAndConfigurePane();
		setActions();
	}

	public Parent asParent() {
		return view;
	}

	private void createAndConfigurePane() {
		tabTwo = new TabTwo("SERVICE INFORMATION", false, true, false);
		tabThree = new TabThree("SERVICES, FACILITIES, AND TRANSPORTATION");
		tabFour = new TabFour("MERCHANDISE");
		tabFive = new TabFive("SPECIAL CHARGES");
		tabSix = new TabSix("CASH ADVANCE ITEM");
		view = new TabPane(tabTwo, tabThree, tabFour, tabFive, tabSix);
		view.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		view.setSide(Side.LEFT);
	}

	private void setActions() {
		tabTwo.getNextButton().setOnAction(e -> view.getSelectionModel().selectNext());
		tabThree.getPrevButton().setOnAction(e -> view.getSelectionModel().selectPrevious());
		tabThree.getNextButton().setOnAction(e -> view.getSelectionModel().selectNext());
		tabFour.getPrevButton().setOnAction(e -> view.getSelectionModel().selectPrevious());
		tabFour.getNextButton().setOnAction(e -> view.getSelectionModel().selectNext());
		tabFive.getPrevButton().setOnAction(e -> view.getSelectionModel().selectPrevious());
		tabFive.getNextButton().setOnAction(e -> view.getSelectionModel().selectNext());
		tabSix.getPrevButton().setOnAction(e -> view.getSelectionModel().selectPrevious());
		// TabSix's next button is "Generate PDF" — wired inside TabSix itself.
	}

	public void fitWindowToLargestTab() {
		if (view.getScene() == null) {
			return;
		}
		Window window = view.getScene().getWindow();
		if (!(window instanceof Stage)) {
			return;
		}
		Stage stage = (Stage) window;

		int originalIndex = view.getSelectionModel().getSelectedIndex();
		double maxWidth = 0;
		double maxHeight = 0;

		for (int i = 0; i < view.getTabs().size(); i++) {
			view.getSelectionModel().select(i);
			view.getScene().getRoot().applyCss();
			view.getScene().getRoot().layout();
			stage.sizeToScene();
			maxWidth = Math.max(maxWidth, stage.getWidth());
			maxHeight = Math.max(maxHeight, stage.getHeight());
		}

		view.getSelectionModel().select(originalIndex);
		stage.setMinWidth(maxWidth);
		stage.setMinHeight(maxHeight);
		stage.setWidth(maxWidth);
		stage.setHeight(maxHeight);
	}
}
