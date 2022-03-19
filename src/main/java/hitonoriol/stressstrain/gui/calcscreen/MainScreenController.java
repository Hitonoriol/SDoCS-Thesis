package hitonoriol.stressstrain.gui.calcscreen;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import hitonoriol.stressstrain.analyzer.StressStrainAnalyzer;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class MainScreenController implements Initializable {
	private StressStrainAnalyzer analyzer = new StressStrainAnalyzer();

	/* Sections of the main window */
	@FXML
	private VBox root;
	@FXML
	private TitledPane inputPane;
	@FXML
	private TabPane resultPane;
	private Tab addTab = new Tab("+");

	/* Initiates the stress strain state calculation with current parameters */
	@FXML
	private Button calcBtn;

	/* Plate parameter input fields */
	@FXML
	private Spinner<Integer> nField;
	@FXML
	private Spinner<Double> q1Field, q2Field;
	@FXML
	private Spinner<Double> kpiField;
	@FXML
	private CheckBox usl1Check, usl2Check;

	@FXML
	private Label nLabel, qLabel, kpiLabel, uslLabel;

	@Override
	@FXML
	public void initialize(URL location, ResourceBundle resources) {
		Arrays.asList(nLabel, qLabel, kpiLabel, uslLabel)
				.forEach(lbl -> lbl.maxWidthProperty().bind(inputPane.widthProperty()));
		inputPane.prefHeightProperty().bind(root.heightProperty());
		nField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1, 1));
		initTabListener();
	}

	private void initTabListener() {
		ObservableList<Tab> tabs = resultPane.getTabs();
		addTab.setClosable(false);
		resultPane.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldTab, newTab) -> {
					if (newTab == addTab) {
						tabs.add(resultPane.getTabs().size() - 1, newTab());
						resultPane.getSelectionModel().select(tabs.size() - 2);
					} else {
						restoreInputs(((ReportTab) newTab).getPlateDescriptor());
					}
				});
		tabs.add(addTab);
	}

	/* Called when `calcBtn` is pressed */
	@FXML
	private void calculate(ActionEvent event) {
		event.consume();
		analyzer.calcStressStrainState(nField.getValue(),
				q1Field.getValue().floatValue(), q2Field.getValue().floatValue(),
				kpiField.getValue().floatValue(),
				usl1Check.isSelected(), usl2Check.isSelected());

		ObservableList<Tab> tabs = resultPane.getTabs();
		ReportTab tab;
		if (tabs.isEmpty())
			tabs.add(tab = newTab());
		else
			tab = (ReportTab) resultPane.getSelectionModel().getSelectedItem();

		tab.refreshContents(new PlateDescriptor(this));
	}

	private ReportTab newTab() {
		ReportTab tab = new ReportTab(new PlateDescriptor(this), analyzer);
		ObservableList<Tab> tabs = resultPane.getTabs();
		tab.setText("Report " + tabs.size());
		return tab;
	}

	void restoreInputs(PlateDescriptor inputs) {
		nField.getValueFactory().setValue(inputs.n);
		q1Field.getValueFactory().setValue((double) inputs.q1);
		q2Field.getValueFactory().setValue((double) inputs.q2);
		kpiField.getValueFactory().setValue((double) inputs.kpi);
		usl1Check.setSelected(inputs.usl1);
		usl1Check.setSelected(inputs.usl2);
	}

	static class PlateDescriptor {
		int n;
		float q1, q2;
		float kpi;
		boolean usl1, usl2;

		public PlateDescriptor(MainScreenController controller) {
			n = controller.nField.getValue();
			q1 = controller.q1Field.getValue().floatValue();
			q2 = controller.q2Field.getValue().floatValue();
			kpi = controller.kpiField.getValue().floatValue();
			usl1 = controller.usl1Check.isSelected();
			usl2 = controller.usl2Check.isSelected();
		}
	}
}
