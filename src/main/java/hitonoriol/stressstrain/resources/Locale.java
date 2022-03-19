package hitonoriol.stressstrain.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;

import hitonoriol.stressstrain.Util;

public class Locale {
	private final static Locale instance = new Locale();
	private final static Charset UTF8 = Charset.forName("utf8");

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
		instance.locMap.forEach((k, v) -> Util.out("%s: %s", k, v));
	}

	/* Read a localized file from classpath, replacing localization placeholders with values from currently loaded locale */
	public static String readFile(String file) {
		String src;
		try {
			src = new String(instance.getClass().getResourceAsStream(file).readAllBytes(), UTF8);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return localize(src);
	}

	/* Load a localized FXML file from classpath */
	public static <T> T loadFXML(String file) {
		try {
			return Resources.fxmlLoader()
					.load(new ByteArrayInputStream(readFile(file).getBytes(UTF8)));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String localize(String str) {
		return instance.substitutor.replace(str);
	}

	/* Get localization variable value by name without substitution */
	public static String get(String str) {
		return instance.locMap.getOrDefault(str, str);
	}

	public static enum Language {
		EN, RU, UA
	}
}
