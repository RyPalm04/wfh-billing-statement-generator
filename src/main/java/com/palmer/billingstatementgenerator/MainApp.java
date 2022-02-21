package com.palmer.billingstatementgenerator;

import com.palmer.billingstatementgenerator.controllers.MainController;
import com.palmer.billingstatementgenerator.models.MainWindowModel;
import com.palmer.billingstatementgenerator.views.MainView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		MainWindowModel model = new MainWindowModel();
		MainController controller = new MainController(model);
		MainView view = new MainView(controller, model);

		Scene scene = new Scene(view.asParent(), 400, 400);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
