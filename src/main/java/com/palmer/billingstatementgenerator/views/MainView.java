package com.palmer.billingstatementgenerator.views;

import com.palmer.billingstatementgenerator.views.tabs.TabFour;
import com.palmer.billingstatementgenerator.views.tabs.TabThree;
import com.palmer.billingstatementgenerator.views.tabs.TabTwo;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;

public class MainView {
	private TabPane view;
	private TabTwo tabTwo;
	private TabThree tabThree;
	private TabFour tabFour;

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
		view = new TabPane(tabTwo, tabThree, tabFour);
		view.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		view.setSide(Side.LEFT);
	}

	private void setActions() {
		tabTwo.getNextButton().setOnAction(e -> view.getSelectionModel().selectNext());
		tabThree.getPrevButton().setOnAction(e -> view.getSelectionModel().selectPrevious());
		tabThree.getNextButton().setOnAction(e -> view.getSelectionModel().selectNext());
		tabFour.getPrevButton().setOnAction(e -> view.getSelectionModel().selectPrevious());
		tabFour.getNextButton().setOnAction(e -> view.getSelectionModel().selectNext());
	}

}
