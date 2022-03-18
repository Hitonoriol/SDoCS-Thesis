package hitonoriol.stressstrain.gui.calcscreen;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import hitonoriol.stressstrain.analyzer.StressStrainAnalyzer;
import hitonoriol.stressstrain.gui.calcscreen.MainScreenController.PlateDescriptor;
import hitonoriol.stressstrain.resources.Locale;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

class ReportTab extends Tab {
	private VBox contents = new VBox();
	private TextFlow reportText = new TextFlow();

	private LineChart<Number, Number> plot;

	private StressStrainAnalyzer analyzer;
	private PlateDescriptor plateDescriptor;

	private Button saveReportBtn = new Button(Locale.get("SAVE_TEXT"));

	ReportTab(PlateDescriptor input, StressStrainAnalyzer analyzer) {
		this.analyzer = analyzer;
		this.plateDescriptor = input;
		refreshContents();
	}
	
	void refreshContents(PlateDescriptor input) {
		this.plateDescriptor = input;
		refreshContents();
	}
	
	void refreshContents() {
		ScrollPane scroll = new ScrollPane(contents);
		scroll.setFitToWidth(true);
		setContent(scroll);
		createPlot();

		addEntry(Locale.get("REPORT"), reportText);
		addEntry(Locale.get("PLOT"), plot);
		contents.getChildren().add(saveReportBtn);
	}

	private void addEntry(String title, Node node) {
		TitledPane container = new TitledPane(title, node);
		container.maxWidth(Double.MAX_VALUE);
		contents.getChildren().add(container);
	}

	private void createPlot() {
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		plot = new LineChart<Number, Number>(xAxis, yAxis);
		plot.setTitle("");

		plotAll("Zo", xySupplier -> analyzer.forEachZOPoint(xySupplier));
		plotAll("DZo", xySupplier -> analyzer.forEachDZOPoint(xySupplier));
	}

	private void plotAll(String name, Consumer<BiConsumer<Float, Float>> populator) {
		XYChart.Series<Number, Number> series = new XYChart.Series<>();
		ObservableList<Data<Number, Number>> data = series.getData();
		populator.accept((x, y) -> data.add(new Data<Number, Number>(x, y)));
		series.setName(name);
		plot.getData().add(series);
	}
	
	PlateDescriptor getPlateDescriptor() {
		return plateDescriptor;
	}
}
