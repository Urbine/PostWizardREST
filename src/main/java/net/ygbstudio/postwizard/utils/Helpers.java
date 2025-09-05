package net.ygbstudio.postwizard.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.jspecify.annotations.NullMarked;

/**
 * Utility class for reusable logic in the project.
 *
 * <p>This class contains methods that can be used throughout the application to perform common
 * tasks, such as retrieving JSON-B property values from annotated classes or other utility
 * functions. It is designed to be a utility class, so it should not be instantiated.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@NullMarked
public final class Helpers implements Util {

  private Helpers() {
    throw new AssertionError("Cannot instantiate utility class");
  }

  /**
   * Loads properties from a specified property file located in the resources directory.
   *
   * @param propertyFileName The name of the property file to load.
   * @return A {@link Properties} object containing the loaded properties.
   * @throws RuntimeException if an error occurs while reading the property file.
   */
  public static Properties getPropertiesFromResources(String propertyFileName) {
    Properties properties = new Properties();
    Logger propertiesLogger = Logger.getLogger("Helpers: getPropertiesFromResources");

    try (InputStream in = Helpers.class.getResourceAsStream("/" + propertyFileName)) {
      if (Objects.isNull(in)) {
        propertiesLogger.warning("Properties file not found...");
        throw new FileNotFoundException(
            "Property file '" + propertyFileName + "' not found in resources.");
      }
      propertiesLogger.info("Properties file loaded successfully...");
      properties.load(in);
    } catch (IOException ioex) {
      propertiesLogger.warning("Error while loading property file. " + ioex.getMessage());
      throw new RuntimeException("Error loading properties file: " + propertyFileName, ioex);
    }

    return properties;
  }

  /**
   * Writes properties to a specified property file located in the resources directory, updating
   * existing properties or adding new ones.
   *
   * <p>Exceptions are relayed to the caller for handling.
   *
   * @param propertyFileName The name of the property file to write to.
   * @param properties An array of property names to write.
   * @param fileComment A comment to include in the property file.
   * @param values An array of values corresponding to the properties.
   * @throws IOException if an error occurs while writing to the property file.
   * @throws URISyntaxException if the URI of the property file cannot be resolved.
   */
  public static void writePropertyFile(
      String propertyFileName, List<String> properties, List<String> values, String fileComment)
      throws IOException, URISyntaxException {
    Properties propsFromResources = getPropertiesFromResources(propertyFileName);
    URI resourceURI = Helpers.class.getResource("/" + propertyFileName).toURI();
    File propsFile = new File(resourceURI);
    if (propsFile.exists()) {
      try (FileInputStream in = new FileInputStream(propsFile.toString())) {
        propsFromResources.load(in);
      }
    }

    List<Entry<String, String>> propertiesEntries = zip(properties, values).toList();

    propertiesEntries.forEach(
        entry -> propsFromResources.setProperty(entry.getKey(), entry.getValue()));

    try (FileOutputStream out = new FileOutputStream(propsFile)) {
      propsFromResources.store(out, fileComment);
    }
  }

  /**
   * Zips two lists into a stream of map entries, pairing elements from both lists by their indices.
   * Contents are returned as a stream to allow for further processing or collection strategies.
   *
   * <p>The resulting stream will contain entries where the key is from the first list and the value
   * is from the second list.
   *
   * <p>if the lists are of different lengths, the resulting stream will only contain entries up to
   * the length of the shorter list.
   *
   * @param <K>
   * @param <V>
   * @param elementOne | A list of elements to be used as keys in the map entries.
   * @param elementTwo | A list of elements to be used as values in the map entries.
   * @return A stream of map entries where each entry pairs an element from the first list with an
   *     element from the second list.
   */
  public static <K, V> Stream<Map.Entry<K, V>> zip(
      List<? extends K> elementOne, List<? extends V> elementTwo) {
    return IntStream.range(0, Math.min(elementOne.size(), elementTwo.size()))
        .mapToObj(i -> Map.entry(elementOne.get(i), elementTwo.get(i)));
  }
}
