package org.randomcoder.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class UserNotFoundExceptionTest {
	@Test
	public void testUserNotFoundException() {
		try {
			throw new UserNotFoundException();
		} catch (UserNotFoundException e) {
			assertNull(e.getMessage());
		}
	}

	@Test
	public void testUserNotFoundExceptionString() {
		try {
			throw new UserNotFoundException("test-message");
		} catch (UserNotFoundException e) {
			assertEquals("test-message", e.getMessage());
		}
	}
}
