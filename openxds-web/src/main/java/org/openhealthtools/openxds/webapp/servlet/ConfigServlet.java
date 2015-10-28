/**
 *  Copyright (c) 2009-2011 Misys Open Source Solutions (MOSS) and others
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  Contributors:
 *    Misys Open Source Solutions - initial API and implementation
 *    -
 */

package org.openhealthtools.openxds.webapp.servlet;

import java.io.File;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openhealthtools.openexchange.actorconfig.IheConfigurationException;
import org.openhealthtools.openexchange.config.BootStrapProperties;
import org.openhealthtools.openexchange.config.ConfigurationException;
import org.openhealthtools.openexchange.config.PropertyFacade;
import org.openhealthtools.openxds.common.XdsConstants;
import org.openhealthtools.openxds.configuration.XdsConfigurationLoader;

/**
 * The starting servlet.Main functionality is load properties
 *
 */
public class ConfigServlet extends HttpServlet
{
	private final static String OPENXDS_HOME_PROPERTY = "openxds.home";
	private final static String OPENXDS_CONFIG_PROPERTIES = "openxds.properties";

	private static Log log = LogFactory.getLog(ConfigServlet.class);

	private String openXdsHome;

	public ConfigServlet() {
	}

	/*
	 * Destroys all Actors
	 */
	public void destroy() {
		try {
			XdsConfigurationLoader loader = XdsConfigurationLoader.getInstance();
			loader.resetConfiguration(null);
		} catch (IheConfigurationException e) {
			log.error("Failed to destroy OpenXDS actor configuration.", e);
		}
	}

	public void init() {
		configProperties();
		configActors();
	}

	private void configProperties() {
		String home = System.getProperty(OPENXDS_HOME_PROPERTY);
		if (log.isInfoEnabled()) {
			log.info("OpenXDS home is set to: " + home);
		}
		String[] configFiles;
		if (home != null) {
			openXdsHome = home;
			configFiles = new String[] { home + "/" + OPENXDS_CONFIG_PROPERTIES };
		} else {
			configFiles = new String[] { OPENXDS_CONFIG_PROPERTIES };
		}
		try {
			String[] propertyFiles = BootStrapProperties.getPropertyFiles(configFiles);
			PropertyFacade.loadProperties(propertyFiles);
		} catch (ConfigurationException e) {
			log.error("Failed to load OpenXDS properties.", e);
		}
	}

	private void configActors() {
		String actorDir = PropertyFacade.getString(XdsConstants.IHE_ACTORS_DIR);
		if (log.isInfoEnabled()) {
			log.info("Actor Directory is initially set to: " + actorDir);
		}
		String actorFile = null;
		if (actorDir.startsWith("/")) {
			URL repoPath = this.getClass().getClassLoader().getResource(actorDir);
			if (log.isInfoEnabled()) {
				log.info("Loading actor configuration from: " + repoPath);
			}
			if (repoPath != null) {
				actorFile = repoPath.getPath();				
			}
		} else {
			File home = new File(openXdsHome);
			if (!home.exists() || !home.isDirectory() || !home.canRead()) {
				log.error("In order to configure OpenXDS using a relative path, you must specify the openxds.home Java system parameter." );
				throw new RuntimeException("Configuration is invalid; please check the log for more information.");
			}
			File dir = new File(home, actorDir);
			if (!dir.exists() || !dir.isDirectory() || !dir.canRead()) {
				log.error("In order to configure OpenXDS using a relative path, you must specify the openxds.home Java system parameter." );
				throw new RuntimeException("Configuration is invalid; please check the log for more information.");
			}
			if (log.isInfoEnabled()) {
				log.info("Loading actor configuration from: " + dir.getPath());
			}
			actorFile = dir.getPath();
		}

		try {
			if (actorFile != null) {
				// remove the current . folder from the path
				actorFile = actorFile.replace(File.separator + "." + File.separator, File.separator);
				actorFile = actorFile + File.separator + "IheActors.xml";
			} else {
				actorFile = System.getProperty(XdsConstants.IHE_ACTORS_FILE);
			}

			if (actorFile != null) {

				// Start up the actors
				XdsConfigurationLoader loader;
				if (openXdsHome == null) {
					loader = XdsConfigurationLoader.getInstance();
				} else {
					loader = XdsConfigurationLoader.getInstance(openXdsHome);
				}

				log.info("Loading actor configuration from " + actorFile);

				loader.loadConfiguration(actorFile, true);
			}
		} catch (IheConfigurationException e) {
			log.fatal("Failed to load OpenXDS actor configuration", e);
		} catch (Exception e) {
			log.fatal("Failed to load OpenXDS actor configuration", e);
		}

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		// do nothing
	}
}