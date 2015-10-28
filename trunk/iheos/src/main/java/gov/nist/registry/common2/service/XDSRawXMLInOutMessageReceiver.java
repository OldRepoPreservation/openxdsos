package gov.nist.registry.common2.service;


import gov.nist.registry.common2.registry.SoapActionFactory;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.engine.MessageReceiver;

public class XDSRawXMLInOutMessageReceiver extends   AbstractXDSRawXMLINoutMessageReceiver

implements MessageReceiver {

	public void validate_action(MessageContext msgContext, MessageContext newmsgContext) {
		String in_action = msgContext.getWSAAction();
		
		String out_action = SoapActionFactory.getResponseAction(in_action);
		if (out_action == null) {
			newmsgContext.setFailureReason(new Exception("Unknown action <" + in_action + ">"));
			return;
		}
		newmsgContext.setWSAAction(out_action);
	}
}
