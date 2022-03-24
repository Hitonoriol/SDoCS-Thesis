package hitonoriol.stressstrain.gui.calcscreen;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import hitonoriol.stressstrain.Util;
import hitonoriol.stressstrain.analyzer.StressStrainAnalyzer;
import hitonoriol.stressstrain.gui.DoubleFormatter;
import hitonoriol.stressstrain.gui.DoubleFormatter.Format;
import hitonoriol.stressstrain.resources.Locale;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

public class MainScreenController implements Initializable {
	private StressStrainAnalyzer analyzer = new StressStrainAnalyzer();

	/* Sections of the main window */
	@FXML
	private VBox root;
	@FXML
	private VBox sideBar; // Contains `inputPane` & settings pane
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

	/* Settings pane contents */
	@FXML
	private CheckBox sciNotationCheck;
	@FXML
	private TextField qStepField;

	@FXML
	private Label nLabel, qLabel, kpiLabel, uslLabel;

	public MainScreenController() {
		Util.out("Created a new %s", this);
	}

	@Override
	@FXML
	public void initialize(URL location, ResourceBundle resources) {
		/* Bind sidebar height to window height */
		sideBar.prefHeightProperty().bind(root.heightProperty());

		/* Set Integer input filter for segment count field */
		nField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1, 1));
		Pattern intPattern = Pattern.compile("\\d+");
		nField.getEditor().setTextFormatter(new TextFormatter<Integer>(
				c -> intPattern.matcher(c.getControlNewText()).matches() ? c : null));

		initTabListener();
		setDecimalFormat(Format.DECIMAL);

		/* Perform calculations on `Enter` key press */
		inputPane.setOnKeyPressed(keyEvent -> {
			if (keyEvent.getCode().equals(KeyCode.ENTER))
				calculate();
		});

		/* Toggle scientific notation */
		sciNotationCheck.setOnAction(event -> {
			setDecimalFormat(sciNotationCheck.isSelected() ? Format.SCIENTIFIC : Format.DECIMAL);
		});

		/* Change `q1Field` & `q2Field` step amount */
		Consumer<Spinner<Double>> stepSetter = field -> ((DoubleSpinnerValueFactory) field.getValueFactory())
				.setAmountToStepBy(Double.valueOf(qStepField.getText()));
		qStepField.setOnKeyTyped(event -> {
			if (qStepField.getText().isEmpty())
				qStepField.setText("0");
			stepSetter.accept(q1Field);
			stepSetter.accept(q2Field);
		});
	}

	private void initTabListener() {
		ObservableList<Tab> tabs = resultPane.getTabs();
		addTab.setClosable(false);
		resultPane.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldTab, newTab) -> {
					if (newTab == addTab) {
						tabs.add(resultPane.getTabs().size() - 1, newTab());
						resultPane.getSelectionModel().select(tabs.size() - 2);
					} else
						restoreInputs(((ReportTab) newTab).getPlateDescriptor());
				});
		tabs.add(addTab);
	}

	private void setDecimalFormat(Format format) {
		Arrays.asList(q1Field, q2Field, kpiField).forEach(spinner -> setFormatter(spinner, format));
		setFormatter(qStepField, format);
	}

	private void setFormatter(Spinner<Double> spinner, Format format) {
		TextFormatter<Double> formatter = new DoubleFormatter(format, spinner.getValueFactory().getValue());
		spinner.getValueFactory().setConverter(formatter.getValueConverter());
		spinner.getEditor().setTextFormatter(formatter);
	}

	private void setFormatter(TextField field, Format format) {
		TextFormatter<Double> formatter = new DoubleFormatter(format, Double.valueOf(field.getText()));
		field.setTextFormatter(formatter);
	}

	/* Called when `calcBtn` is pressed */
	@FXML
	private void calculate(ActionEvent event) {
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

	private void calculate() {
		calculate(null);
	}

	/* 
	 * Each language menu item is named using the next scheme: `Language [lng]`
	 * where `lng` corresponds to .json localization map in /language/ resource directory 
	 */
	Pattern langPattern = Pattern.compile("\\[(.+?)\\]");

	@FXML
	private void chooseLanguage(ActionEvent event) {
		MenuItem langItem = (MenuItem) event.getTarget();
		Matcher matcher = langPattern.matcher(langItem.getText());
		matcher.find();
		Locale.loadLanguage(matcher.group(1));
	}

	ReportTab newTab() {
		ReportTab tab = new ReportTab(new PlateDescriptor(this), analyzer);
		ObservableList<Tab> tabs = resultPane.getTabs();
		tab.setText(Locale.get("REPORT") + " " + tabs.size());
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

	@JsonAutoDetect(fieldVisibility = Visibility.ANY)
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
