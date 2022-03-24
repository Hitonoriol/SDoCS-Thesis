package hitonoriol.stressstrain.gui.calcscreen;

import java.io.File;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;

import hitonoriol.stressstrain.gui.calcscreen.MainScreenController.PlateDescriptor;
import hitonoriol.stressstrain.resources.Resources;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Report {
	@JsonIgnore
	private String name;
	private PlateDescriptor plateParameters;

	public Report(PlateDescriptor plateParameters) {
		this.plateParameters = plateParameters;
	}

	public void restore(MainScreenController mainScreen) {
		ReportTab tab = mainScreen.newTab();
		tab.refreshContents(plateParameters);
		tab.setText(name);
	}

	public void save(String path) {
		Resources.serialize(Resources.reportDir.getAbsolutePath() + "/" + path, this);
	}

	public static void restore(MainScreenController mainScreen, String path) {
		Report report = Resources.deserialize(path, Report.class);
		report.name = new File(path).getName();
		report.restore(mainScreen);
	}
}
