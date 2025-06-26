package com.archer.tools.certificate;

import java.security.cert.X509Certificate;

public class RSAKeyCertificate {
	private X509Certificate certificate;
	
	private RSAKeyPair keyPair;
	
	public RSAKeyCertificate(X509Certificate certificate, RSAKeyPair keyPair) {
		this.certificate = certificate;
		this.keyPair = keyPair;
	}

	public X509Certificate getCertificate() {
		return certificate;
	}

	public RSAKeyPair getKeyPair() {
		return keyPair;
	}
}
