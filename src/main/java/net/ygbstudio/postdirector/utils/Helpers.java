package net.ygbstudio.postdirector.utils;

// Java imports
import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;


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
	 * of the specified class, excluding any fields that are marked with
	 * JsonbTransient.
	 * 
	 * @param annotatedClass The class to inspect for JsonbProperty annotations.
	 * @return A list of strings representing the values of the JsonbProperty annotations.
	 */
	public static List<String> getJsonBPropertyValues(Class<?> annotatedClass) {
		List<String> annotationValues = new ArrayList<String>();
		
		for (Field field: annotatedClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(JsonbTransient.class)) {
				continue;
			}
			
			if (field.isAnnotationPresent(JsonbProperty.class)) {
				JsonbProperty jsonAnnotation = field.getAnnotation(JsonbProperty.class);
				annotationValues.add(jsonAnnotation.value());
			}
		}
		return annotationValues;
	}

}
