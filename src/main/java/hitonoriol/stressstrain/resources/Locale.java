package hitonoriol.stressstrain.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;

public class Locale {
	private final static Locale instance = new Locale();

	private final static String PREFIX = "{", SUFFIX = "}";
	private Map<String, String> locMap = new HashMap<>();
	private StringSubstitutor substitutor = new StringSubstitutor(locMap, PREFIX, SUFFIX);

	private Locale() {
	}

	public static void loadLanguage(String lang) {
		if (!instance.locMap.isEmpty())
			Prefs.values().updateLanguage(lang);

		instance.locMap.clear();
		instance.locMap.putAll(Resources.loadMap("/language/" + lang + ".json", String.class, String.class));
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
	public static <T> T loadFXML(String file) {
		try {
			return Resources.fxmlLoader().load(new ByteArrayInputStream(readFile(file).getBytes()));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/* Get localization variable value by name without substitution */
	public static String get(String str) {
		return instance.locMap.getOrDefault(str, str);
	}

	public static enum Language {
		EN, RU, UA
	}
}
