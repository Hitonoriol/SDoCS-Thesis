package hitonoriol.stressstrain.gui.calcscreen;

import java.io.File;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;

import hitonoriol.stressstrain.gui.calcscreen.MainScreenController.PlateDescriptor;
import hitonoriol.stressstrain.resources.Locale;
import hitonoriol.stressstrain.resources.Resources;
import javafx.scene.control.Button;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Report {
	@JsonIgnore
	private String name;
	@JsonIgnore
	private File path;
	private PlateDescriptor plateParameters;

	public final static String fileExtension = ".ssr";
	
	public Report() {}
	
	public Report(PlateDescriptor plateParameters) {
		this.plateParameters = plateParameters;
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
		if (Resources.serialize(reportFile, this)) {
			this.path = reportFile;
			return true;
		}
		return false;
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
