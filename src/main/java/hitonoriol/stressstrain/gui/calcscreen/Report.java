package hitonoriol.stressstrain.gui.calcscreen;

import java.io.File;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;

import hitonoriol.stressstrain.Util;
import hitonoriol.stressstrain.analyzer.StressStrainAnalyzer;
import hitonoriol.stressstrain.gui.calcscreen.MainScreenController.PlateDescriptor;
import hitonoriol.stressstrain.resources.Locale;
import hitonoriol.stressstrain.resources.Resources;
import hitonoriol.stressstrain.util.Table;
import javafx.scene.control.Button;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Report {
	@JsonIgnore
	private String name;
	@JsonIgnore
	private File path;
	@JsonIgnore
	StressStrainAnalyzer analyzer;
	private PlateDescriptor plateParameters;

	public final static String fileExtension = ".ssr";
	public final static String outTableName = "characteristics.txt", biasTableName = "coupling-errors.txt";

	public Report() {
	}

	public Report(StressStrainAnalyzer analyzer, PlateDescriptor plateParameters) {
		this.plateParameters = plateParameters;
		this.analyzer = analyzer;
	}

	public ReportTab restore(MainScreenController mainScreen) {
		ReportTab tab = mainScreen.newTab();
		tab.calculateAndRefresh(plateParameters);
		tab.setText(name);
		checkTab(tab);
		mainScreen.addTab(tab);
		return tab;
	}

	public boolean save(File reportFile) {
		File reportParent = reportFile.getParentFile();
		String reportName = reportFile.getName(), reportNoExt = Util.removeFileExtension(reportName);
		
		Util.out("parent: [%s] name: [%s] noExt: [%s]", reportParent, reportName, reportNoExt);
		
		/* Create a new directory named the same as the report before saving */
		if (!reportParent.getName().equals(reportNoExt)) {
			String newDir = reportParent.getAbsolutePath() + "/" + reportNoExt;
			new File(newDir).mkdirs();
			reportFile = new File(newDir + "/" + reportName);
		}
		
		if (Resources.serialize(reportFile, this)) {
			this.path = reportFile;
			saveTables();
			return true;
		}
		return false;
	}

	private void saveTables() {
		Table outTable = new Table(analyzer.getStressStrainTable(), StressStrainAnalyzer.OUT_TABLE_HEADER);
		Table biasTable = new Table(analyzer.getCouplingErrorTable(), StressStrainAnalyzer.BIAS_TABLE_HEADER);
		String prefix = path.getParent() + "/";
		Resources.write(prefix + outTableName, outTable.render());
		Resources.write(prefix + biasTableName, biasTable.render());
	}

	@JsonIgnore
	public boolean isSaved() {
		return path != null && path.exists();
	}

	@JsonIgnore
	public File getPath() {
		return path;
	}

	void checkTab(ReportTab tab) {
		if (isSaved()) {
			Button saveBtn = tab.getSaveReportBtn();
			saveBtn.setDisable(true);
			saveBtn.setText(Locale.get("SAVED_TO") + " \"" + getPath().getAbsolutePath() + "\"");
		}
	}

	public static void restore(MainScreenController mainScreen, File file) {
		Report report = Resources.deserialize(file, Report.class);
		report.name = file.getName();
		report.path = file;
		report.restore(mainScreen);
	}
}
