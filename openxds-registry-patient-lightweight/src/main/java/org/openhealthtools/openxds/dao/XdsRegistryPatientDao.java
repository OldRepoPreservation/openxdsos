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

package org.openhealthtools.openxds.dao;

import org.openhealthtools.openxds.registry.PersonIdentifier;
import org.openhealthtools.openxds.registry.api.RegistryPatientException;
/**
 * This interface defines the operations of Patient Manager in the
 * XDS Registry.
 * 
 * @author <a href="mailto:Rasakannu.Palaniyandi@misys.com">Raja</a>
 */
public interface XdsRegistryPatientDao {
	

	/* (non-Javadoc)
	 * @see org.openhealthtools.openxds.registry.api.XdsRegistryPatientService#isValidPatient()
	 */
	public PersonIdentifier getPersonById(PersonIdentifier personId) throws RegistryPatientException;
	
		
	/* (non-Javadoc)
	 * @see org.openhealthtools.openxds.registry.api.XdsRegistryPatientService#createPatient()
	 */
	public void savePersonIdentifier(PersonIdentifier newPersonIdentifier) throws RegistryPatientException;
	
	/* (non-Javadoc)
	 * @see org.openhealthtools.openxds.registry.api.XdsRegistryPatientService#updatePatient()
	 */
	public void updatePersonIdentifier(PersonIdentifier updatePersonIdentifier) throws RegistryPatientException;
	
	/* (non-Javadoc)
	 * @see org.openhealthtools.openxds.registry.api.XdsRegistryPatientService#mergePatients()
	 */	
	public void mergePersonIdentifier(PersonIdentifier mergePersonIdentifier) throws RegistryPatientException;
	
}
