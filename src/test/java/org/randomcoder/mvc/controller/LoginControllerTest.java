package org.randomcoder.mvc.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LoginControllerTest {
  private LoginController c;

  @Before public void setUp() {
    c = new LoginController();
  }

  @After public void tearDown() {
    c = null;
  }

  @Test public void testLogin() {
    assertEquals("login", c.login());
  }

  @Test public void testLoginError() {
    assertEquals("login-error", c.loginError());
  }
}