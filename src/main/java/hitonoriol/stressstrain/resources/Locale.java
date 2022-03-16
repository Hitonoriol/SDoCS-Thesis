package hitonoriol.stressstrain.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;

import javafx.scene.Parent;

public class Locale {
	private final static Locale instance = new Locale();

	private Map<String, String> locMap = new HashMap<>();
	private StringSubstitutor substitutor = new StringSubstitutor(locMap, "{", "}");

	private Locale() {}

	public static void loadLanguage(String lang) {
		instance.locMap = Resources.loadMap("/language/" + lang + ".json", String.class, String.class);
	}

	/* Called from UI */
	public static void loadLanguage(Language lang) {
		String langName = lang.name().toLowerCase();
		loadLanguage(langName);
		Prefs.values().updateLanguage(langName);
	}
	
	/* Read a localized file from classpath, replacing localization placeholders with values from currently loaded locale */
	public static String readFile(String file) {
		String src;
		try {
			src = new String(instance.getClass().getResourceAsStream(file).readAllBytes());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return instance.substitutor.replace(src);
	}
	
	/* Load a localized FXML file from classpath */
	public static Parent loadFXML(String file) {
		try {
			return Resources.fxmlLoader().load(new ByteArrayInputStream(readFile(file).getBytes()));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static enum Language {
		EN, RU, UA
	}
}
