package org.randomcoder.xml;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;

import org.apache.commons.logging.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * Various convenience methods to get data in and out of DOM, pretty print, etc.
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
public final class XmlUtils
{
	private static final Log logger = LogFactory.getLog(XmlUtils.class);
	
	private XmlUtils() {}
	
	/**
	 * Parse the given input source into a DOM object.
	 * @param source input source
	 * @return dom representation
	 * @throws SAXException if parsing fails
	 * @throws IOException if an I/O error occurs
	 */
	public static Document parseXml(InputSource source)
	throws SAXException, IOException
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			
			DocumentBuilder db = dbf.newDocumentBuilder();
			return db.parse(source);			
		}
		catch (ParserConfigurationException e)
		{
			logger.error("Caught exception", e);
			throw new RuntimeException("XML parser configuration problem", e);
		}		
	}
	
  /**
   * Writes a DOM object to the given Result.
   * @param doc Document to convert
   * @param result destination
   * @param publicId public id of dtd or null if not specified
   * @param systemId system id of dtd or null if not specified
   * @throws TransformerException if transformation fails
   */
  public static void writeXml(Document doc, Result result, String publicId, String systemId)
  throws TransformerException
	{
		Transformer trans = getTransformer(true, publicId, systemId, null);
		trans.transform(new DOMSource(doc), result);
	}
  	
  /**
	 * Gets a Transformer object suitable for writing DOM objects.
	 * @param indent whether to indent output
	 * @param publicId public id of dtd or null if not specified
	 * @param systemId system id of dtd or null if not specified
	 * @param xsl XSL source or null if not needed
	 * @return Transformer configured to output using the given parameters
	 */
  public static Transformer getTransformer(boolean indent, String publicId, String systemId, Source xsl)
	{
		try
		{
			TransformerFactory factory = TransformerFactory.newInstance();

			// try to set indent amount using JDK 1.5+ property
			try
			{
				if (indent) factory.setAttribute("indent-number", new Integer(2));
			}
			catch (Exception ignored)
			{}

			Transformer trans = (xsl == null) ? factory.newTransformer() : factory.newTransformer(xsl);
			
			trans.setOutputProperty(OutputKeys.METHOD, "xml");
			
			if (publicId != null) trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, publicId);
			if (systemId != null) trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemId);

			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");

			// try to set indent amount using Xalan property
			try
			{
				if (indent) trans.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
			}
			catch (Exception ignored) {}

			return trans;
		}
		catch (TransformerConfigurationException e)
		{
			// make this unchecked since there's nothing other than environment that should cause this
			logger.error("Caught exception", e);
			throw new RuntimeException("Transformer configuration problem", e);
		}
	}    
  
  /**
   * Log XML to the given log object.
   * @param log log to output to
   * @param doc document to write
   */
  public static void logXml(Log log, Document doc)
  {
  	logXml(log, null, doc, null, null);
  }
  
  /**
   * Log XML to the given log object.
   * @param log log to output to
   * @param doc document to write
   * @param publicId public id for dtd or null for none
   * @param systemId system id for dtd or null for none
   */
  public static void logXml(Log log, Document doc, String publicId, String systemId)
  {
  	logXml(log, null, doc, publicId, systemId);
  }
  
  /**
   * Log XML to the given log object.
   * @param log log to output to
   * @param message message to add
   * @param doc document to write
   */
  public static void logXml(Log log, String message, Document doc)
  {
  	logXml(log, message, doc, null, null);  	
  }
  
  /**
   * Log XML to the given log object.
   * @param log log to output to
   * @param message message to add
   * @param doc document to write
   * @param publicId public id for dtd or null for none
   * @param systemId system id for dtd or null for none
   */
  public static void logXml(Log log, String message, Document doc, String publicId, String systemId)
	{
  	if (!log.isDebugEnabled()) return;
  	
		try
		{
			StringWriter writer = new StringWriter();
			if (message != null) writer.write(message);
			writer.write("\n");
			writeXml(doc, new StreamResult(writer), publicId, systemId);
			writer.close();
			log.debug(writer.toString());
		}
		catch (Exception ignored) {}
	}
  
  /**
   * Pretty-print (indent) a DOM document.
   * @param input DOM input
   * @param output stream result
   * @throws TransformerException if transformation fails
   */
  public static void prettyPrint(Document input, StreamResult output)
  throws TransformerException
  {
  	prettyPrint(input, output, null);
  }
  
  /**
   * Pretty-print (indent) a DOM document.
   * @param input DOM input
   * @param output stream result
   * @param docType DTD to output
   * @throws TransformerException if transformation fails
   */
	public static void prettyPrint(Document input, StreamResult output, DocumentType docType)
	throws TransformerException
	{
    String publicId = null;
    String systemId = null;
    if (docType != null)
    {
        publicId = docType.getPublicId();
        systemId = docType.getSystemId();
    }
    
    StreamSource xsl = new StreamSource(XmlUtils.class.getResourceAsStream("pretty-print.xsl"));
    Transformer t = getTransformer(true, publicId, systemId, xsl);

    DOMSource src = new DOMSource(input);
    t.transform(src, output);            		
	}
}