package hitonoriol.stressstrain;

import hitonoriol.stressstrain.resources.Locale;
import hitonoriol.stressstrain.resources.Prefs;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Analyzer extends Application {
	private static Analyzer application;

	public Analyzer() {
		application = this;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = Locale.loadFXML("/MainWindow.fxml");
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

		primaryStage.setTitle("Stress Strain State Analyzer");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setMinWidth(primaryStage.getWidth());
		primaryStage.setMinHeight(primaryStage.getHeight());
	}

	public static void main(String args[]) {
		Prefs.init();
		launch(args);
	}

	public static Analyzer app() {
		return application;
	}
}
