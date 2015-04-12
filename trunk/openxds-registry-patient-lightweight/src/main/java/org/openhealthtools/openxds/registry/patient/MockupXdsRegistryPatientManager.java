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

package org.openhealthtools.openxds.registry.patient;

import org.openhealthtools.openexchange.datamodel.Patient;
import org.openhealthtools.openxds.registry.api.RegistryPatientContext;
import org.openhealthtools.openxds.registry.api.RegistryPatientException;
import org.openhealthtools.openxds.registry.api.XdsRegistryPatientService;

public class MockupXdsRegistryPatientManager implements
		XdsRegistryPatientService {

	public void createPatient(Patient patient, RegistryPatientContext context)
			throws RegistryPatientException {
		// TODO Auto-generated method stub
		
	}

	public boolean isValidPatient(
			org.openhealthtools.openexchange.datamodel.PatientIdentifier pid,
			RegistryPatientContext context) throws RegistryPatientException {
		// TODO Auto-generated method stub
		return false;
	}

	public void mergePatients(Patient survivingPatient, Patient mergePatient,
			RegistryPatientContext context) throws RegistryPatientException {
		// TODO Auto-generated method stub
		
	}

	public void unmergePatients(Patient survivingPatient, Patient mergePatient,
			RegistryPatientContext context) throws RegistryPatientException {
		// TODO Auto-generated method stub
		
	}

	public void updatePatient(Patient patient, RegistryPatientContext context)
			throws RegistryPatientException {
		// TODO Auto-generated method stub
		
	}

}
