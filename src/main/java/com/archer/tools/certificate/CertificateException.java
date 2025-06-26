package com.archer.tools.certificate;

public class CertificateException extends Exception {

	private static final long serialVersionUID = 21897482348724823L;

	public CertificateException(Throwable cause) {
		super(cause);
	}
	
	public CertificateException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CertificateException(String message) {
		super(message);
	}
}
