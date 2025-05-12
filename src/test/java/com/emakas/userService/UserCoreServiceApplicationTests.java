package com.emakas.userService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = UserCoreServiceApplicationTests.class)
class UserCoreServiceApplicationTests {


	@Test
	void contextLoads() {

	}

	@Test
	void validToken() {}

	@Test
	void expiredToken() {}

	@Test
	void unsignedToken() {}

	@Test
	void audienceCheck() {}

	@Test
	void scopeTest() {}
}
