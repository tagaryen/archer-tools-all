package com.archer.tools.certificate;

import java.security.Provider;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class BCProvider {

	private static volatile boolean loaded = false;
	
	public static void addBCProvider() {
		if(!loaded) {
			loaded = true;
			Security.addProvider((Provider)new BouncyCastleProvider()); 
		}
	}
}
