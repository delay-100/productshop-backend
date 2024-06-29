package com.whitedelay.productshop;

import org.hibernate.validator.internal.constraintvalidators.bv.PatternValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = ProductshopApplicationTests.class)
class ProductshopApplicationTests {

	@Test
	void contextLoads() {
		assertTrue(true);

	}

}
