package net.ygbstudio.postdirector.test;

import static org.hamcrest.CoreMatchers.equalTo;
// Hamcrest imports
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
// Java imports
import java.util.Properties;

// JUnit imports
import org.junit.jupiter.api.Test;

import net.ygbstudio.postdirector.dto.ClientPostMeta;
// Local imports 
import net.ygbstudio.postdirector.utils.Helpers;

public class HelpersTests {
	
	@Test
	void getPropertiesFromResources() {
		Properties localProps = Helpers.getPropertiesFromResources("ApplicationProperties.properties");
		assertThat(localProps, notNullValue());
	}
	
	@Test
	void getJsonBPropertyValuesTest() {
		List<String> jsonFields = Helpers.getJsonBPropertyValues(ClientPostMeta.class);
		assertThat(jsonFields, notNullValue());
		assertThat(jsonFields.getFirst(), equalTo("post_id"));
		assertThat(jsonFields.getLast(), equalTo("embed"));
	}
}
