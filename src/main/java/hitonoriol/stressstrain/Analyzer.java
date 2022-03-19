package hitonoriol.stressstrain;

import hitonoriol.stressstrain.gui.calcscreen.MainScreenController;
import hitonoriol.stressstrain.resources.Locale;
import hitonoriol.stressstrain.resources.Prefs;
import hitonoriol.stressstrain.resources.Resources;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Analyzer extends Application {
	private static Analyzer application;

	private Stage primaryStage;
	private MainScreenController mainController;

	public Analyzer() {
		application = this;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		FXMLLoader primaryLoader = Resources.newFXMLLoader();
		Scene scene = new Scene(Locale.loadFXML(Resources.MAIN_SCREEN));
		mainController = primaryLoader.getController();
		loadMainScreen(scene);

		primaryStage.setTitle("Stress Strain State Analyzer");
		primaryStage.show();
		primaryStage.setMinWidth(primaryStage.getWidth());
		primaryStage.setMinHeight(primaryStage.getHeight());
	}

	public void loadMainScreen(Scene scene) {
		Resources.loadStyles(scene);
		primaryStage.setScene(scene);
	}

	public MainScreenController mainController() {
		return mainController;
	}

	public static void main(String args[]) {
		Prefs.init();
		launch(args);
	}

	public static Analyzer app() {
		return application;
	}
}
