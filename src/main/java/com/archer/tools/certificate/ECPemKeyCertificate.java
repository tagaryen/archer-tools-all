package com.archer.tools.certificate;

public class ECPemKeyCertificate {
	
	private String crt;
	
	private String key;
	
	private String privateKeyHex;
	
	private String publicKeyHex;

	protected ECPemKeyCertificate(String crt, String key, String privateKeyHex, String publicKeyHex) {
		this.crt = crt;
		this.key = key;
		this.privateKeyHex = privateKeyHex;
		this.publicKeyHex = publicKeyHex;
	}

	public String getPrivateKeyHex() {
		return privateKeyHex;
	}

	public String getPublicKeyHex() {
		return publicKeyHex;
	}

	public void setPrivateKeyHex(String privateKeyHex) {
		this.privateKeyHex = privateKeyHex;
	}

	public void setPublicKeyHex(String publicKeyHex) {
		this.publicKeyHex = publicKeyHex;
	}

	public String getCrt() {
		return crt;
	}

	public String getKey() {
		return key;
	}

	public void setCrt(String crt) {
		this.crt = crt;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
}
