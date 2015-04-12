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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Person entity.
 * 
 * @author <a href="mailto:yimin.xie@sysnetint.com">Yimin Xie</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Person implements java.io.Serializable
{
	private static final long serialVersionUID = -6061320465621019356L;

	private Integer personId;
	private String groupNumber;
	// Name related attributes
	private String givenName;
	private String middleName;
	private String familyName;
	private String familyName2;
	private String motherName;
	private String fatherName;
	private String prefix;
	private String suffix;
	private Date dateOfBirth;
	private String birthPlace;
	private String multipleBirthInd;
	private Integer birthOrder;
	private String mothersMaidenName;
	private String ssn;
	private String degree;
	private String maritalStatusCode;
	private String email;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String postalCode;
	private String country;
	private String countryCode;
	private String village;
	private String villageId;
	private String sector;
	private String sectorId;
	private String cell;
	private String cellId;
	private String district;
	private String districtId;
	private String province;

	// Phone attributes
	private String phoneCountryCode;
	private String phoneAreaCode;
	private String phoneNumber;
	private String phoneExt;
	
	private String deathInd;
	private Date deathTime;
	
	private String account;
	private IdentifierDomain accountIdentifierDomain;
	private Set<PersonIdentifier> personIdentifiers = new HashSet<PersonIdentifier>();

	/** default constructor */
	public Person() {
	}

	@XmlElement
	public Integer getPersonId() {
		return this.personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	@XmlElement
	public String getGroupNumber() {
		return this.groupNumber;
	}

	public void setGroupNumber(String groupNumber) {
		this.groupNumber = groupNumber;
	}

	@XmlElement
	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@XmlElement
	public String getSuffix() {
		return this.suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	@XmlElement
	public String getGivenName() {
		return this.givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	@XmlElement
	public String getMiddleName() {
		return this.middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	@XmlElement
	public String getFamilyName() {
		return this.familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	@XmlElement
	public String getFamilyName2() {
		return this.familyName2;
	}

	public void setFamilyName2(String familyName2) {
		this.familyName2 = familyName2;
	}

	@XmlElement
	public String getMotherName() {
		return this.motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	@XmlElement
	public String getFatherName() {
		return this.fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}
	
	@XmlElement
	public String getDegree() {
		return this.degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	@XmlElement
	public String getMothersMaidenName() {
		return this.mothersMaidenName;
	}

	public void setMothersMaidenName(String mothersMaidenName) {
		this.mothersMaidenName = mothersMaidenName;
	}

	@XmlElement
	public Date getDateOfBirth() {
		return this.dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	@XmlElement
	public String getAddress1() {
		return this.address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	@XmlElement
	public String getAddress2() {
		return this.address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	@XmlElement
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@XmlElement
	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@XmlElement
	public String getPostalCode() {
		return this.postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@XmlElement
	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@XmlElement
	public String getCountryCode() {
		return this.countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@XmlElement
	public String getVillage() {
		return this.village;
	}

	public void setVillage(String village) {
		this.village = village;
	}

	@XmlElement
	public String getVillageId() {
		return this.villageId;
	}

	public void setVillageId(String villageId) {
		this.villageId = villageId;
	}
	
	@XmlElement
	public String getSector() {
		return this.sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	@XmlElement
	public String getSectorId() {
		return this.sectorId;
	}

	public void setSectorId(String sectorId) {
		this.sectorId = sectorId;
	}
	
	@XmlElement
	public String getCell() {
		return this.cell;
	}

	public void setCell(String cell) {
		this.cell = cell;
	}

	@XmlElement
	public String getCellId() {
		return this.cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}
	
	@XmlElement
	public String getDistrict() {
		return this.district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	@XmlElement
	public String getDistrictId() {
		return this.districtId;
	}

	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}

	@XmlElement
	public String getProvince() {
		return this.province;
	}

	public void setProvince(String province) {
		this.province = province;
	}
	
	@XmlElement
	public String getMaritalStatusCode() {
		return this.maritalStatusCode;
	}

	public void setMaritalStatusCode(String maritalStatusInd) {
		this.maritalStatusCode = maritalStatusInd;
	}

	@XmlElement
	public String getPhoneCountryCode() {
		return this.phoneCountryCode;
	}

	public void setPhoneCountryCode(String phoneCountryCode) {
		this.phoneCountryCode = phoneCountryCode;
	}

	@XmlElement
	public String getPhoneAreaCode() {
		return this.phoneAreaCode;
	}

	public void setPhoneAreaCode(String phoneAreaCode) {
		this.phoneAreaCode = phoneAreaCode;
	}

	@XmlElement
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@XmlElement
	public String getPhoneExt() {
		return this.phoneExt;
	}

	public void setPhoneExt(String phoneExt) {
		this.phoneExt = phoneExt;
	}
	
	@XmlElement
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@XmlElement
	public String getMultipleBirthInd() {
		return this.multipleBirthInd;
	}

	public void setMultipleBirthInd(String multipleBirthInd) {
		this.multipleBirthInd = multipleBirthInd;
	}

	@XmlElement
	public Integer getBirthOrder() {
		return this.birthOrder;
	}

	public void setBirthOrder(Integer birthOrder) {
		this.birthOrder = birthOrder;
	}

	@XmlElement
	public String getBirthPlace() {
		return this.birthPlace;
	}

	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}
	
	@XmlElement
	public String getSsn() {
		return this.ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	@XmlElement
	public String getDeathInd() {
		return this.deathInd;
	}

	public void setDeathInd(String dateInd) {
		this.deathInd = dateInd;
	}

	@XmlElement
	public Date getDeathTime() {
		return this.deathTime;
	}

	public void setDeathTime(Date deathTime) {
		this.deathTime = deathTime;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "person")
	@XmlElement
	public Set<PersonIdentifier> getPersonIdentifiers() {
		return this.personIdentifiers;
	}

	public void setPersonIdentifiers(Set<PersonIdentifier> personIdentifiers) {
		this.personIdentifiers = personIdentifiers;
	}		

	@Column(name = "account", length = 255)
	@XmlElement
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_identifier_domain_id", nullable = true)
	@XmlElement
	public IdentifierDomain getAccountIdentifierDomain() {
		return this.accountIdentifierDomain;
	}

	public void setAccountIdentifierDomain(IdentifierDomain accountIdentifierDomain) {
		this.accountIdentifierDomain = accountIdentifierDomain;
	}
	
	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof Person))
			return false;
		Person castOther = (Person) other;
		return new EqualsBuilder().append(personId, castOther.personId).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(personId).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("personId", personId).append("givenName", givenName).append(
				"familyName", familyName).append("personIdentifiers", personIdentifiers).toString();
	}

	public String toStringLong() {
		return new ToStringBuilder(this).append("personId", personId)
				.append("prefix", prefix).append("suffix",
						suffix).append("givenName", givenName).append(
						"middleName", middleName).append("familyName",
						familyName).append("familyName2", familyName2)
						.append("motherName", motherName)
						.append("fatherName", fatherName)
						.append("degree", degree).append("mothersMaidenName",
						mothersMaidenName).append("dateOfBirth", dateOfBirth)
				.append("address1", address1).append("address2", address2)
				.append("city", city).append("state", state).append(
						"postalCode", postalCode).append("country", country)
				.append("countryCode", countryCode)
				.append("province", province)
				.append("district", district).append("districtId", districtId)
				.append("cell", cell).append("cellId", cellId)
				.append("sector", sector).append("sectorId", sectorId)
				.append("village", village).append("villageId", villageId)
				.append("maritalStatusCode",maritalStatusCode).append("phoneCountryCode",
						phoneCountryCode)
				.append("phoneAreaCode", phoneAreaCode).append("phoneNumber",
						phoneNumber).append("phoneExt", phoneExt)
						.append("email", email).append("multipleBirthInd",multipleBirthInd)
				.append("birthOrder", birthOrder)
				.append("birthPlace", birthPlace).append("deathInd", deathInd)
						.append("ssn", ssn).append(
						"deathTime", deathTime)
						.append("account", account).append("accountIdentifierDomain", accountIdentifierDomain)
						.append("personIdentifiers",
						personIdentifiers).toString();
	}

}