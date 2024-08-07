package com.emakas.userService;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = UserBaseServiceApplicationTests.class)
class UserBaseServiceApplicationTests {


	@Test
	void contextLoads() {

	}

	@Test
	void validToken() {}

	@Test
	void expiredToken() {}

	@Test
	void unsignedToken() {}
}
