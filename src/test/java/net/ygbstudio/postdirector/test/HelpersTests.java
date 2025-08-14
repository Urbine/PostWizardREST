package net.ygbstudio.postdirector.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;
import net.ygbstudio.postdirector.dto.ClientPostMeta;
import net.ygbstudio.postdirector.utils.Helpers;
import net.ygbstudio.postdirector.utils.Reflection;
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
  void getJsonBPropertyValuesTest() {
    List<String> jsonFields = Reflection.getJsonBPropertyValues(ClientPostMeta.class);
    assertThat(jsonFields, notNullValue());
    assertThat(jsonFields.getFirst(), equalTo("postID"));
    assertThat(jsonFields.getLast(), equalTo("yoastMetaDesc"));
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
