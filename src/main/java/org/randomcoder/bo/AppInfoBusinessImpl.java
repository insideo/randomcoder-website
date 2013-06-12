package org.randomcoder.bo;

import java.io.*;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * JavaBean which holds application information.
 */
@Component("appInfoBusiness")
public class AppInfoBusinessImpl implements AppInfoBusiness
{
	private static final Log logger = LogFactory.getLog(AppInfoBusinessImpl.class);

	private static final String APP_NAME_PROPERTY = "application.name";
	private static final String APP_VERSION_PROPERTY = "application.version";
	private static final String DEFAULT_APP_NAME = "Randomcoder Website";
	private static final String DEFAULT_APP_VERSION = "Unknown";

	private String applicationName;
	private String applicationVersion;

	/**
	 * Sets the location of the property file.
	 * 
	 * @param propertyFile
	 *            property file location
	 * @throws IOException
	 *             if an error occurs
	 */
	@Inject
	@Value("${version.location}")
	public void setPropertyFile(Resource propertyFile) throws IOException
	{
		Properties p = new Properties();
		InputStream is = null;

		try
		{
			is = propertyFile.getInputStream();
			p.load(is);
		}
		finally
		{
			if (is != null)
			{
				is.close();
			}
		}

		String appName = p.getProperty(APP_NAME_PROPERTY);
		if (appName == null || appName.contains("${"))
			appName = DEFAULT_APP_NAME;

		String appVersion = p.getProperty(APP_VERSION_PROPERTY);
		if (appVersion == null || appVersion.contains("${"))
			appVersion = DEFAULT_APP_VERSION;

		logger.info("Starting application: " + appName + "/" + appVersion);

		this.applicationName = appName;
		this.applicationVersion = appVersion;
	}

	@Override
	public String getApplicationName()
	{
		return applicationName;
	}

	@Override
	public String getApplicationVersion()
	{
		return applicationVersion;
	}
}