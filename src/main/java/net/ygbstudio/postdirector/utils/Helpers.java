package net.ygbstudio.postdirector.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.stream.Stream;

// Java imports
import java.util.List;
import java.util.Objects;
import java.util.Properties;

// Jakarta imports
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;

/**
 * Utility class for reusable logic in the project.
 * <p>
 * This class contains methods that can be used throughout the application
 * to perform common tasks, such as retrieving JSON-B property values from
 * annotated classes or other utility functions.
 * It is designed to be a utility class, so it should not be instantiated.
 * 
 * @author Yoham Gabriel @ YGB Studio
 * 
 */
public final class Helpers implements Util {

	private Helpers() {
		throw new AssertionError("Cannot instantiate utility class");
	}

	/**
	 * Retrieves the values of the JsonbProperty annotations from the fields
	 * of the specified class via reflection, excluding any fields that are marked
	 * with
	 * JsonbTransient.
	 * 
	 * @param annotatedClass The class to inspect for JsonbProperty annotations.
	 * @return A list of strings representing the values of the JsonbProperty
	 *         annotations.
	 */
	public static List<String> getJsonBPropertyValues(Class<?> annotatedClass) {
		
		return Stream.of(annotatedClass.getDeclaredFields())
				.filter(field -> !field.isAnnotationPresent(JsonbTransient.class)
						&& field.isAnnotationPresent(JsonbProperty.class))
				.map(field -> field.getAnnotation(JsonbProperty.class).value())
				.toList();
	}

	/**
	 * Loads properties from a specified property file located in the resources
	 * directory.
	 * 
	 * @param propertyFileName The name of the property file to load.
	 * @return A {@link Properties} object containing the loaded properties.
	 * @throws FileNotFoundException if the property file is not found in the
	 *                               resources.
	 * @throws RuntimeException      if an error occurs while reading the property
	 *                               file.
	 */
	public static Properties getPropertiesFromResources(String propertyFileName) {
		Properties properties = new Properties();
		Logger propertiesLogger = Logger.getLogger("Helpers: getPropertiesFromResources");

		try (InputStream in = Helpers.class.getResourceAsStream("/" + propertyFileName)) {
			if (Objects.isNull(in)) {
				propertiesLogger.warning("Properties file not found...");
				throw new FileNotFoundException("Property file '" + propertyFileName + "' not found in resources.");
			}
			propertiesLogger.info("Properties file loaded successfully...");
			properties.load(in);
		} catch (IOException ioex) {
			propertiesLogger.warning("Error while loading property file. " + ioex.getMessage());
			throw new RuntimeException("Error loading properties file: " + propertyFileName, ioex);
		}

		return properties;
	}

}
