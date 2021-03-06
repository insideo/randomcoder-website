package org.randomcoder.bo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class AppInfoBusinessImplTest {
  private AppInfoBusinessImpl info;

  @Before public void setUp() throws IOException {
    info = new AppInfoBusinessImpl();
    info.setPropertyFile(new ClassPathResource("/version-test.properties"));
  }

  @After public void tearDown() {
    info = null;
  }

  @Test public void testGetObject() throws Exception {
    assertEquals("Test-Application", info.getApplicationName());
    assertEquals("1.0.0", info.getApplicationVersion());
  }
}