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

package org.openhealthtools.openxds;

import org.openhealthtools.openpixpdq.impl.v2.hl7.HL7Util;
import org.openhealthtools.openpixpdq.api.IMessageStoreLogger;
import org.openhealthtools.openpixpdq.api.MessageStore;
import org.openhealthtools.openexchange.actorconfig.net.IConnectionDescription;
import org.openhealthtools.openexchange.audit.IheAuditTrail;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;


/**
 *
 * @author <a href="mailto:wenzhi.li@misys.com">Wenzhi Li</a>
 *
 */
public class BaseIheActor {
	  
	/** The IHE Audit Trail for this actor. */
	private IheAuditTrail auditTrail = null;
	
	/**The logger used to persist(store) raw inbound and outbound messages.*/
	private IMessageStoreLogger storeLogger = null;
	
	/** The main connection this actor will be using */
	protected IConnectionDescription connection = null;
	
	/**
	 * Creates a new IHE Actor.
	 * 
	 */
	public BaseIheActor() {		 
	}
	
	/**
	 * Creates a new IHE Actor.
	 * 
	 */
	public BaseIheActor(IConnectionDescription connection) {
		this.connection = connection;
	}

	/**
	 * Creates a new HL7 Actor
	 * 
	 */
	public BaseIheActor(IConnectionDescription connection, IheAuditTrail auditTrail) {
		this.auditTrail = auditTrail;
		this.connection = connection;
	}
	
	/**
	 *  Starts this actor. It must be called once for each actor when the program starts. 
	 */  
	public void start() {
		//if (auditTrail != null) auditTrail.start();  
	}

	/**
	 * Stops this actor. It must be called once for each actor just before the program quits. 
	 */ 
	public void stop() {
		//if (auditTrail != null) auditTrail.stop();  	  
	}
  
	/**
	 * Returns a useful name for this Actor so that it can be put into
	 * debugging and logging messages.
	 * 
	 * @returns a useful name for this Actor
	 */
	public String getName() {
		if (connection != null) {
			return connection.getDescription();
		} else {
			return "unnamed";
		}
	}

	/**
	 * Sets the logger to store message. 
	 * 
	 * @param storeLogger the storeLogger to set
	 */
	public void setStoreLogger(IMessageStoreLogger storeLogger) {
		this.storeLogger = storeLogger;
	}

	/**
	 * Initiates a <code>MessageStore</code> instance, and log the 
	 * initial message, either in-bound message or out-bound message.
	 * 
	 * @param message the initial message to log
	 * @param isInbound whether the message is an in-bound message or out-bound message 
	 * @return a <code>MessageStore</code>
	 * @throws HL7Exception
	 */
	public MessageStore initMessageStore(Message message, boolean isInbound) 
	throws HL7Exception {
		if (storeLogger == null)
			return null;
		
		MessageStore ret = new MessageStore(); 
		if (message != null) {
			String encodedMessage = HL7Util.encodeMessage(message);
		    if (isInbound)
		    	ret.setInMessage( encodedMessage );
		    else 
		    	ret.setOutMessage( encodedMessage );
		}
	    return ret;
	}
	
	/**
	 * Persists the <code>MessageStore</code> log, and save the return message
	 * which could be either in-bound or out-bound.
	 * 
	 * @param message the last message to save and log
	 * @param isInbound whether the message is an in-bound message or out-bound message 
	 * @param msgStore the <code>MessageStore</code> instance to hold the log data
	 * @throws HL7Exception if the message could not be encoded 
	 */
	public void saveMessageStore(Message message, boolean isInbound, MessageStore msgStore) 
	throws HL7Exception {
		if (msgStore == null || storeLogger == null )
			return;
		
	    if (message != null) {
		    String encodedMessage = HL7Util.encodeMessage(message);
		    if (isInbound) 
			    msgStore.setInMessage( encodedMessage );
		    else
		    	msgStore.setOutMessage( encodedMessage );
	    }		
	    storeLogger.saveLog( msgStore );
	
	}

	/**
	 * Gets the main <code>IConnectionDescription</code> of this actor.
	 * 
	 * @return the main connection
	 */
	public IConnectionDescription getConnection() {
		return connection;
	}
	
	/**
	 * Gets the Audit Trail of this actor.
	 * 
	 * @return the auditTrail
	 */
	public IheAuditTrail getAuditTrail() {
		return auditTrail;
	}

}
