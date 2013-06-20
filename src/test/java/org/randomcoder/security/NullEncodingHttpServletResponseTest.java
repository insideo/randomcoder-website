package org.randomcoder.security;

import static org.junit.Assert.*;

import org.junit.*;
import org.springframework.mock.web.MockHttpServletResponse;

@SuppressWarnings({ "javadoc" })
public class NullEncodingHttpServletResponseTest
{
	private NullEncodingHttpServletResponse response;

	@Before
	public void setUp()
	{
		response = new NullEncodingHttpServletResponse(new MockHttpServletResponse());
	}

	@After
	public void tearDown()
	{
		response = null;
	}

	@Test
	public void testEncodeRedirectUrl()
	{
		assertNull(response.encodeRedirectUrl(null));
		assertEquals("test", response.encodeRedirectUrl("test"));
	}

	@Test
	public void testEncodeRedirectURL()
	{
		assertNull(response.encodeRedirectURL(null));
		assertEquals("test", response.encodeRedirectURL("test"));
	}

	@Test
	public void testEncodeUrl()
	{
		assertNull(response.encodeUrl(null));
		assertEquals("test", response.encodeUrl("test"));
	}

	@Test
	public void testEncodeURLString()
	{
		assertNull(response.encodeURL(null));
		assertEquals("test", response.encodeURL("test"));
	}
}
