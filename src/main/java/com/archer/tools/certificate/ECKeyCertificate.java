package com.archer.tools.certificate;

import java.security.cert.X509Certificate;

public class ECKeyCertificate {
	
	private X509Certificate certificate;
	
	private ECKeyPair keyPair;
	
	public ECKeyCertificate(X509Certificate certificate, ECKeyPair keyPair) {
		this.certificate = certificate;
		this.keyPair = keyPair;
	}

	public X509Certificate getCertificate() {
		return certificate;
	}

	public ECKeyPair getKeyPair() {
		return keyPair;
	}
}
