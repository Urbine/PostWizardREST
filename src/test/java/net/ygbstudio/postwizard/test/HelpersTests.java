package net.ygbstudio.postwizard.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;
import net.ygbstudio.postwizard.dto.ClientPostMeta;
import net.ygbstudio.postwizard.utils.Helpers;
import net.ygbstudio.postwizard.utils.Reflection;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Helpers utility class.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public class HelpersTests {

  @Test
  void getPropertiesFromResources() {
    Properties localProps = Helpers.getPropertiesFromResources("ApplicationProperties.properties");
    assertThat(localProps, notNullValue());
  }

  @Test
  void getTransformClassFieldsTest() {
    List<? extends String> transformFields =
        Reflection.getTransformClassFields(ClientPostMeta.class, Field::getName).toList();
    assertThat(transformFields, notNullValue());
    assertThat(transformFields.getFirst(), equalTo("post_id"));
    assertThat(transformFields.getLast(), equalTo("_yoast_wpseo_metadesc"));
  }
}
