package hitonoriol.stressstrain.gui;

import hitonoriol.stressstrain.analyzer.StressStrainAnalyzer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;

public class MainScreenController {
	private StressStrainAnalyzer analyzer = new StressStrainAnalyzer();

	/* Sections of the main window */
	@FXML
	private TitledPane inputPane;
	@FXML
	private TabPane resultPane;

	/* Initiates the stress strain state calculation with current parameters */
	@FXML
	private Button calcBtn;

	/* Plate parameter input fields */
	@FXML
	private Spinner<Integer> nField;
	@FXML
	private Spinner<Float> q1Field, q2Field;
	@FXML
	private Spinner<Float> kpiField;
	@FXML
	private CheckBox usl1Check, usl2Check;

	/* Called when `calcBtn` is pressed */
	@FXML
	private void calculate(ActionEvent event) {
		event.consume();
		analyzer.calcStressStrainState(nField.getValue(),
				q1Field.getValue(), q2Field.getValue(),
				kpiField.getValue(),
				usl1Check.isSelected(), usl2Check.isSelected());
	}

	/* TODO: Save / restore `inputPane` state when switching tabs */
	@FXML
	private void switchTab() {

	}
}
