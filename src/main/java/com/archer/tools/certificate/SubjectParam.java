package com.archer.tools.certificate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.bouncycastle.asn1.x509.KeyUsage;

public class SubjectParam {
	
	private boolean issuer;
	
	private String commonName;
	
	private String organization;
	
	private String organizationUnit;

	private String locality;
	
	private String state;
	
	private String country;
	
	private LocalDateTime beginDate;
	
	private LocalDateTime endDate;
	
	private String algorithm;
	
	private KeyUsage keyUsage;

	
	public SubjectParam() {}

	public SubjectParam(boolean issuer, String commonName, String organization, String organizationUnit, String locality, String state,
			String country, LocalDateTime beginDate, LocalDateTime endDate, String algorithm, KeyUsage keyUsage) {
		this.commonName = commonName;
		this.organization = organization;
		this.organizationUnit = organizationUnit;
		this.locality = locality;
		this.state = state;
		this.country = country;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.algorithm = algorithm;
		this.keyUsage = keyUsage;
	}
	

	public boolean isIssuer() {
		return issuer;
	}

	public void setIssuer(boolean issuer) {
		this.issuer = issuer;
	}

	public String getCommonName() {
		return commonName;
	}

	public String getOrganization() {
		return organization;
	}

	public String getOrganizationUnit() {
		return organizationUnit;
	}

	public String getLocality() {
		return locality;
	}

	public String getState() {
		return state;
	}

	public String getCountry() {
		return country;
	}
	
	public KeyUsage getKeyUsage() {
		if(keyUsage == null) {
			keyUsage = new KeyUsage(KeyUsage.cRLSign | KeyUsage.keyCertSign | KeyUsage.keyAgreement);
		}
		return keyUsage;
	}

	public void setKeyUsage(KeyUsage keyUsage) {
		this.keyUsage = keyUsage;
	}

	public Date getBeginDate() {
		Date begin = null;
		if(beginDate == null) {
			System.out.println("beginDate is null");
			begin = new Date();
		} else {
			begin = Date.from(beginDate.atZone(ZoneId.systemDefault()).toInstant());
		}
		return begin;
	}

	public Date getEndDate() {
		Date end = null;
		if(endDate == null) {
			System.out.println("endDate is null");
			end = new Date();
		} else {
			end = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
		}
		return end;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public void setOrganizationUnit(String organizationUnit) {
		this.organizationUnit = organizationUnit;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setBeginDate(LocalDateTime beginDate) {
		this.beginDate = beginDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}
	
	public String getAlgorithm() {
		if(algorithm == null) {
			algorithm = AlgorithmName.SHA256WITHRSA;
		}
		return algorithm;
	}

	/**
	 * @param algorithm see <code>com.archer.tools.certificate.AlgorithmName</code>
	 * */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String toSubjString() {
		StringBuilder builder = new StringBuilder();
	    if (this.commonName != null) {
	    	builder.append("CN=").append(this.commonName); 
	    } else {
	    	System.out.println("commonName is null");
	    	builder.append("CN=*.xy.com"); 
	    }
	    if (this.organization != null) {
	    	builder.append(",O=").append(this.organization);
	    } else {
	    	System.out.println("organization is null");
	    	builder.append(",O=徐氏有限责任公司"); 
	    }
	    if (this.organizationUnit != null) {
	        builder.append(",OU=").append(this.organizationUnit);
	    } else {
	    	System.out.println("organizationUnit is null");
	    }
	    if (this.locality != null) {
	        builder.append(",L=").append(this.locality); 
	    } else {
	    	System.out.println("locality is null");
	    }
	    if (this.state != null) {
	        builder.append(",ST=").append(this.state); 
	    } else {
	    	System.out.println("state is null");
	    }
	    if (this.country != null) {
	    	if(this.country.getBytes().length > 2) {
	    		throw new RuntimeException("the length of country is too long");
	    	}
	        builder.append(",C=").append(this.country); 
	    } else {
	    	System.out.println("country is null");
	    }
	    return builder.toString();
	}
	
}
