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

package org.openhealthtools.openxds.xca;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.engine.ListenerManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openhealthtools.common.utils.UnZip;
import org.openhealthtools.common.ws.server.IheHTTPServer;
import org.openhealthtools.openexchange.actorconfig.Transactions;
import org.openhealthtools.openexchange.actorconfig.TransactionsSet;
import org.openhealthtools.openexchange.actorconfig.net.IConnectionDescription;
import org.openhealthtools.openexchange.audit.IheAuditTrail;
import org.openhealthtools.openexchange.config.PropertyFacade;
import org.openhealthtools.openxds.BaseIheActor;
import org.openhealthtools.openxds.registry.XdsRegistryImpl;
import org.openhealthtools.openxds.xca.api.XcaIG;


/**
 * This class represents an XCA Initiating Gateway actor.
 * 
 * @author <a href="mailto:Anilkumar.reddy@misys.com">Anil kumar</a>
 */
public class  XcaIGImpl extends BaseIheActor implements XcaIG {
    /** Logger for problems during SOAP exchanges */
    private static Log log = LogFactory.getLog(XcaIGImpl.class);

    /**The client side of XDS Registry connection*/
	private IConnectionDescription registryClientConnection = null;
    /**The client side of XDS Repository connection*/
	private IConnectionDescription repositoryClientConnection = null;
    /**The client side of XCA Responding Gateway Query connections*/
	private Map<String, IConnectionDescription> rgQueryClientConnections = null;
    /**The client side of XCA Responding Gateway Retrieve connections*/
	private Map<String, IConnectionDescription> rgRetrieveClientConnections = null;

    /** The XCA Responding Gateway Server */    
    IheHTTPServer igServer = null;

    /**
     * Creates a new XCA Responding Gateway actor.
     *
     */
     public XcaIGImpl(IConnectionDescription rgServerConnection, IConnectionDescription registryClientConnection, 
    		 IConnectionDescription repositoryClientConnection, TransactionsSet respondingGateways, IheAuditTrail auditTrail) {
    	 super(rgServerConnection, auditTrail);
         this.connection = rgServerConnection;
         this.registryClientConnection = registryClientConnection;
         this.repositoryClientConnection = repositoryClientConnection;
         
         Collection<Transactions> transactions =  respondingGateways.getAllTransactions();
         for (Transactions transaction : transactions) {
        	 IConnectionDescription query = transaction.getQuery();
        	 IConnectionDescription retrieve = transaction.getRetrieve();
        	 if (query != null) {
        		 if (this.rgQueryClientConnections == null) {
        			 this.rgQueryClientConnections = new HashMap<String, IConnectionDescription>();
        		 }
        		 this.rgQueryClientConnections.put(transaction.getId(), query);
        	 }
        	 if (retrieve != null) {
        		 if (this.rgRetrieveClientConnections == null) {
        			 this.rgRetrieveClientConnections = new HashMap<String, IConnectionDescription>();
        		 }
        		 this.rgRetrieveClientConnections.put(transaction.getId(), retrieve);
        	 }
         }
    }
    
    @Override
	public void start() {
        //call the super one to initiate standard start process
        super.start();

        //start the Responding Gateway server
        if (initXcaIG()) 
            log.info("XCA Initiating Gateway started: " + connection.getDescription() );        	
        else
            log.fatal("XCA Initiating Gateway initialization failed: " + connection.getDescription() );        	
    }
    
    private boolean initXcaIG() {
		boolean isSuccess = false;
        try {
	        String axis2repopath = null;
	        String axis2xmlpath = null;	        	
	        String repo = PropertyFacade.getString("axis2.repo.dir");
	        URL repoPath = XdsRegistryImpl.class.getResource(repo);
	        if (repoPath != null) {
		        axis2repopath = repoPath.getPath();
		        axis2xmlpath = repoPath.getPath() +"/axis2.xml";
	        } else  if (new File(repo).exists()) {
		        axis2repopath = repo;
		        axis2xmlpath = repo +"/axis2.xml";	        	
	        } else {
		        URL axis2repo = XdsRegistryImpl.class.getResource("/axis2repository");
		        URL axis2xml = XdsRegistryImpl.class.getResource("/axis2repository/axis2.xml");
		        axis2repopath = axis2repo.getPath();
		        axis2xmlpath = axis2xml.getPath();
		        if(axis2repopath.contains(".jar")){
		        	UnZip zip =new UnZip();
				    zip.unZip(axis2repopath,repo);
				    axis2repopath = repo;
				    axis2xmlpath = repo +"/axis2.xml"; 	
			    }
	        }
	        ConfigurationContext configctx = ConfigurationContextFactory
	        .createConfigurationContextFromFileSystem(axis2repopath, axis2xmlpath);
	        igServer = new IheHTTPServer(configctx, this); 		
	
	        Runtime.getRuntime().addShutdownHook(new IheHTTPServer.ShutdownThread(igServer));
	        igServer.start();
	        ListenerManager listenerManager = configctx .getListenerManager();
	        TransportInDescription trsIn = new TransportInDescription(Constants.TRANSPORT_HTTP);
	        trsIn.setReceiver(igServer); 
	        if (listenerManager == null) {
	            listenerManager = new ListenerManager();
	            listenerManager.init(configctx);
	        }
	        listenerManager.addListener(trsIn, true);
	        isSuccess = true;
        }catch(AxisFault e) {
        	log.fatal("Failed to start the XCA Initiating Gateway server", e);			
        }

        return isSuccess;
    }
    
    @Override
    public void stop() {
        //stop the Repository Server
        igServer.stop();
        log.info("XCA Initiating Gateway stopped: " + connection.getDescription() );

        //call the super one to initiate standard stop process
        super.stop();
    }

	public Map<String, IConnectionDescription> getRGQueryClientConnections() {
    	return rgQueryClientConnections;
    }

	public Map<String, IConnectionDescription> getRGRetrieveClientConnections() {
    	return rgRetrieveClientConnections;
    }
	
	public IConnectionDescription getRegistryClientConnection() {
		return registryClientConnection;
	}

	public IConnectionDescription getRepositoryClientConnection() {
		return repositoryClientConnection;
	}

}
