package com.archer.tools.certificate;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class RSAKeyPair {

	private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    
	public RSAKeyPair(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}
	
	public RSAPrivateKey getPrivateKey() {
		return privateKey;
	}
	
	public RSAPublicKey getPublicKey() {
		return publicKey;
	}
}
