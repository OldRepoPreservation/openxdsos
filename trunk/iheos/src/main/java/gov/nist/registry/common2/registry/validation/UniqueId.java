package gov.nist.registry.common2.registry.validation;

import gov.nist.registry.common2.exception.MetadataException;
import gov.nist.registry.common2.registry.Metadata;
import gov.nist.registry.common2.registry.MetadataSupport;
import gov.nist.registry.common2.registry.RegistryErrorList;

import java.util.ArrayList;

public class UniqueId {
	Metadata m;
	RegistryErrorList rel;
	boolean xds_b;

	public UniqueId(Metadata m, RegistryErrorList rel, boolean xds_b) {
		this.m = m;
		this.rel = rel;
		this.xds_b = xds_b;
	}

	public void run() throws MetadataException {
		ArrayList<String> unique_ids = new ArrayList<String>();

		for (String id : m.getFolderIds()) {
			String uid = m.getExternalIdentifierValue(id, "urn:uuid:75df8f67-9973-4fbe-a900-df66cefecc5a");
			if (unique_ids.contains(uid)) 
				rel.add_error(MetadataSupport.XDSRegistryDuplicateUniqueIdInMessage, 
						"UniqueId " + uid + " is not unique within the submission", 
						"validation/UniqueId.java", null);
			validate_format(uid);
		}

		for (String id : m.getSubmissionSetIds()) {
			String uid = m.getExternalIdentifierValue(id, "urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8");
			if (unique_ids.contains(uid)) 
				rel.add_error(MetadataSupport.XDSRegistryDuplicateUniqueIdInMessage, 
						"UniqueId " + uid + " is not unique within the submission", 
						"validation/UniqueId.java", null);
			validate_format(uid);
		}

		for (String id : m.getExtrinsicObjectIds()) {
			String uid = m.getExternalIdentifierValue(id, "urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab");
			if (uid == null) {
				rel.add_error(MetadataSupport.XDSRegistryError, 
						"Document unique ID is null", 
						"validation/UniqueId.java", null);
				return;
			}
			if (unique_ids.contains(uid)) 
				rel.add_error(MetadataSupport.XDSRegistryDuplicateUniqueIdInMessage, 
						"UniqueId " + uid + " is not unique within the submission", 
						"validation/UniqueId.java", null);
			validate_format_for_documents(uid);
		}
	}

	void validate_format(String uid) {
		if ( ! Attribute.is_oid(uid, xds_b))
				rel.add_error(MetadataSupport.XDSRegistryMetadataError,
						"UniqueId " + uid + " is not formatted as an OID",
						"validation/UniqueId.java", null);
	}

	void validate_format_for_documents(String uid) {
		if (uid.length() > 0 && uid.indexOf('^') == uid.length()-1)
			rel.add_error(MetadataSupport.XDSRegistryMetadataError,
					"UniqueId " + uid + ": EXT part is empty but ^ is present",
					"validation/UniqueId.java:validate_format_for_documents", null);
		String[] parts = uid.split("\\^");
		if (parts.length == 2) {
			String oid = parts[0];
			String ext = parts[1];
			if (oid.length() > 64)
				rel.add_error(MetadataSupport.XDSRegistryMetadataError,
						"UniqueId " + uid + ": OID part is larger than the allowed 64 characters",
						"validation/UniqueId.java:validate_format_for_documents", null);
			if (ext.length() > 16)
				rel.add_error(MetadataSupport.XDSRegistryMetadataError,
						"UniqueId " + uid + ": EXT part is larger than the allowed 16 characters",
						"validation/UniqueId.java:validate_format_for_documents", null);
			if (ext.length() == 0)
				rel.add_error(MetadataSupport.XDSRegistryMetadataError,
						"UniqueId " + uid + ": should not have ^ since no EXT is coded",
						"validation/UniqueId.java:validate_format_for_documents", null);
			if ( ! Attribute.is_oid(oid, xds_b))
//			for (int i=0; i<oid.length(); i++) {
//				if ("0123456789.".indexOf(oid.charAt(i)) == -1) {
					rel.add_error(MetadataSupport.XDSRegistryMetadataError,
							"The OID part of UniqueId, " + oid + " is not formatted as an OID (uid = " + uid + " oid = " + oid + " ext = " + ext + ")",
							"validation/UniqueId.java:validate_format_for_documents", null);
//					return;
//				}
//
//			}
		} else {
			if ( ! Attribute.is_oid(uid, xds_b))
//			for (int i=0; i<uid.length(); i++) {
//				if ("0123456789.".indexOf(uid.charAt(i)) == -1) {
					rel.add_error(MetadataSupport.XDSRegistryMetadataError,
							"UniqueId " + uid + " is not formatted as an OID",
							"validation/UniqueId.java:validate_format_for_documents", null);
//					return;
//				}
//
//			}
		}
	}


}
