package com.randomcoder.content;

import java.io.*;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Required;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Simple XHTML content filter.
 * 
 * <p>This class implements a large subset of XHTML (minus dangerous,
 * deprecated, or otherwise undesirable stuff). Tag and attribute names are
 * canonicalized, non-semantic markup is converted to semantic, and disallowed
 * elements, their children, and attributes are removed.</p>
 * 
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
public class XHTMLFilter implements ContentFilter
{
	protected static final Log logger = LogFactory.getLog(XHTMLFilter.class);
	
	private static final String XSL_RESOURCE = "xhtml-to-xhtml.xsl";
	private static final String XSD_RESOURCE = "xhtml1-transitional.xsd";
	private static final String NS_RESOURCE = "namespace.xsd";

	static final String PREFIX = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>Untitled</title></head><body>";
	private static final String SUFFIX = "</body></html>";

	private final Templates templates;
	private final Schema schema;

	private Set<String> allowedClasses = new HashSet<String>();

	/**
	 * Sets a list of allowed CSS class names in the markup.
	 * @param allowedClasses Set of CSS class names
	 */
	@Required
	public void setAllowedClasses(Set<String> allowedClasses)
	{
		this.allowedClasses = allowedClasses;
	}

	/**
	 * Constructs a new XHTML filter
	 * @throws TransformerConfigurationException if transformer factory fails
	 * @throws SAXException if schema validation fails
	 */
	public XHTMLFilter() throws TransformerConfigurationException, SAXException
	{
		// cache templates for later use
		TransformerFactory tFactory = TransformerFactory.newInstance();
		templates = tFactory.newTemplates(new SAXSource(new InputSource(getClass().getResourceAsStream(XSL_RESOURCE))));

		SchemaFactory sFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Source xhtmlSource = new StreamSource(getClass().getResourceAsStream(XSD_RESOURCE));
		Source nsSource = new StreamSource(getClass().getResourceAsStream(NS_RESOURCE));

		schema = sFactory.newSchema(new Source[] { nsSource, xhtmlSource });
	}

	public XMLReader getXMLReader(String contentType) throws SAXException
	{
		return new XHTMLReader(XMLReaderFactory.createXMLReader(), allowedClasses);
	}

	public Templates getXSLTemplates(String contentType)
	{
		return templates;
	}

	public String getPrefix(String contentType)
	{
		return PREFIX;
	}

	public String getSuffix(String contentType)
	{
		return SUFFIX;
	}

	public void validate(String contentType, Reader content) throws InvalidContentException, InvalidContentTypeException, IOException
	{
		Validator validator = schema.newValidator();
		XHTMLErrorHandler handler = new XHTMLErrorHandler();
		validator.setErrorHandler(handler);

		try
		{
			validator.validate(new StreamSource(content));
		}
		catch (SAXException e)
		{
			if (handler.getMessage() != null)
			{
				// we caught it
				throw new InvalidContentException(handler.getMessage(), handler.getLineNumber(), handler.getColumnNumber());
			}
			// something else bad happened
			throw new InvalidContentException(e.getMessage(), 1, 1);
		}
	}
}