/**
 *  Copyright (c) 2009-2010 Misys Open Source Solutions (MOSS) and others
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
import java.util.Collection;
import java.util.Comparator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openhealthtools.openexchange.actorconfig.IActorDescription;
import org.openhealthtools.openexchange.actorconfig.IheConfigurationException;
import org.openhealthtools.openexchange.config.BootStrapProperties;
import org.openhealthtools.openexchange.config.ConfigurationException;
import org.openhealthtools.openexchange.config.PropertyFacade;
import org.openhealthtools.openxds.XdsFactory;
import org.openhealthtools.openxds.configuration.XdsConfigurationLoader;

/**
 *The starting servlet.Main functionality is load properties
 *
 *@author <a href="mailto:anilkumar.reddy@misys.com">Anil</a>
 */
 public class ConfigServlet extends HttpServlet  {
   static final long serialVersionUID = 1L;
   
    private XdsConfigurationLoader loader;
    private Collection actors;
    
	public ConfigServlet() {
	} 
	
	/* 
	 * Destroys all Actors 
	 */
	public void destroy() {
		try {
			loader.resetConfiguration(null, null);
		} catch (IheConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public void init() throws ServletException {
		try {
			String[] propertyFiles = BootStrapProperties.getPropertyFiles(new String[]{"openxds.properties"});
			PropertyFacade.loadProperties(propertyFiles);
		} catch(ConfigurationException e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
		
		try {
			XdsFactory.getInstance();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
		
		//Last, load actor configuration
        String actorDir = PropertyFacade.getString("ihe.actors.dir");
        String actorFile = null; 
        
        File dir = new File(actorDir);
        if (!dir.exists()) {
        	return;
        }

        actorFile = dir.getAbsolutePath();
    	//remove the current . folder from the path
    	actorFile = actorFile.replace(File.separator+"."+File.separator, File.separator);
    	actorFile = actorFile + File.separator + "IheActors.xml";

	    //Start up the servers
		loader = XdsConfigurationLoader.getInstance();
        try {
            loader.loadConfiguration(actorFile, false);
            actors = loader.getActorDescriptions();
            loader.resetConfiguration(actors);
        } catch (IheConfigurationException e) {
            e.printStackTrace();
        }
		
	}   
	class TypeComparator implements Comparator {

		public int compare(Object first, Object second) {
			try {
				IActorDescription f = (IActorDescription) first;
				IActorDescription s = (IActorDescription) second;
				return f.getHumanReadableType().compareToIgnoreCase(s.getHumanReadableType());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		//do nothing
	}
}