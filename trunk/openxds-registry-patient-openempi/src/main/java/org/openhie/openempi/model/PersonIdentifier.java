/**
 *
 * Copyright (C) 2002-2012 "SYSNET International, Inc."
 * support@sysnetint.com [http://www.sysnetint.com]
 *
 * This file is part of OpenEMPI.
 *
 * OpenEMPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openhie.openempi.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PersonIdentifier entity.
 * 
 * @author <a href="mailto:yimin.xie@sysnetint.com">Yimin Xie</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class PersonIdentifier implements java.io.Serializable
{
	private static final long serialVersionUID = 1943429923033311936L;

	private Integer personIdentifierId;
	private IdentifierDomain identifierDomain;
	private String identifier;
	private Date dateCreated;
	private Date dateVoided;

	/** default constructor */
	public PersonIdentifier() {
	}

	@XmlElement
	public Integer getPersonIdentifierId() {
		return this.personIdentifierId;
	}

	public void setPersonIdentifierId(Integer personIdentifierId) {
		this.personIdentifierId = personIdentifierId;
	}

	@XmlElement
	public IdentifierDomain getIdentifierDomain() {
		return this.identifierDomain;
	}

	public void setIdentifierDomain(IdentifierDomain identifierDomain) {
		this.identifierDomain = identifierDomain;
	}

	@XmlElement
	public String getIdentifier() {
		return this.identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@XmlElement
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@XmlElement
	public Date getDateVoided() {
		return this.dateVoided;
	}

	public void setDateVoided(Date dateVoided) {
		this.dateVoided = dateVoided;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof PersonIdentifier))
			return false;
		PersonIdentifier castOther = (PersonIdentifier) other;
		if (other == this) {
			return true;
		}
		if (personIdentifierId == castOther.personIdentifierId) {
			return true;
		}
		return new EqualsBuilder().append(identifierDomain,
				castOther.identifierDomain)
				.append(identifier, castOther.identifier)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(personIdentifierId)
			.append(identifierDomain)
			.append(identifier).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("personIdentifierId", personIdentifierId).append("identifierDomain",
				identifierDomain).append("identifier", identifier).append("dateCreated",
				dateCreated).append("dateVoided", dateVoided).toString();
	}

	public Integer hydrate() {
		return getPersonIdentifierId();
	}
}