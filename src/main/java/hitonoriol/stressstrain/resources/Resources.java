package hitonoriol.stressstrain.resources;

import java.io.File;
import java.util.HashMap;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import javafx.fxml.FXMLLoader;

public class Resources {
	private final static ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
	private final static TypeFactory typeFactory = mapper.getTypeFactory();
	
	static {
		mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator());
	}
	
	private final static FXMLLoader fxmlLoader = new FXMLLoader();
	
	private final static Resources instance = new Resources();
	private Resources() {}
	
	private static Class<? extends Resources> thisClass() {
		return instance.getClass();
	}
	
	public static MapType getMapType(Class<?> key, Class<?> value) {
		return typeFactory.constructMapType(HashMap.class, key, value);
	}

	public static ObjectWriter getMapWriter(Class<?> key, Class<?> value) {
		return mapper.writerFor(getMapType(key, value));
	}

	public static ObjectReader getMapReader(Class<?> key, Class<?> value) {
		return mapper.readerFor(getMapType(key, value));
	}

	public static <K, V> HashMap<K, V> loadMap(String file, Class<K> keyType, Class<V> valueType) {
		try {
			return mapper.readValue(thisClass().getResourceAsStream(file),
					getMapType(keyType, valueType));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void save(String file, Object object) {
		try {
			mapper.writeValue(new File(file), object);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static FXMLLoader fxmlLoader() {
		return fxmlLoader;
	}
	
	public static ObjectMapper mapper() {
		return mapper;
	}
}
