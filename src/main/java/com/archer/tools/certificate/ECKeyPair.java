package com.archer.tools.certificate;

import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

public class ECKeyPair {
	
	private ECPrivateKey privateKey;
    private ECPublicKey publicKey;
    
	public ECKeyPair(ECPrivateKey privateKey, ECPublicKey publicKey) {
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}
	
	public ECPrivateKey getPrivateKey() {
		return privateKey;
	}
	public ECPublicKey getPublicKey() {
		return publicKey;
	}
}
