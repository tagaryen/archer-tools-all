package com.archer.tools.certificate;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

final class Certificate {

	protected static X509Certificate createCaCert(String signAlg, X500Name subject, PublicKey publicKey, PrivateKey privateKey, Date startDate, Date endDate, KeyUsage usage) throws Exception {
		X509v3CertificateBuilder jcaX509v3Cert =
		new JcaX509v3CertificateBuilder(subject, BigInteger.valueOf(System.currentTimeMillis()), startDate, endDate, subject, publicKey);
		JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
		jcaX509v3Cert = jcaX509v3Cert.addExtension(Extension.subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(publicKey))
				.addExtension(Extension.authorityKeyIdentifier, false, extUtils.createAuthorityKeyIdentifier(publicKey));
		jcaX509v3Cert = jcaX509v3Cert
						.addExtension(Extension.basicConstraints, false, new BasicConstraints(true))
						.addExtension(Extension.keyUsage, false, usage);
	    JcaContentSignerBuilder contentSignerBuilder = new JcaContentSignerBuilder(signAlg);
	    contentSignerBuilder.setProvider("BC");
	    return (new JcaX509CertificateConverter()).setProvider("BC").getCertificate(jcaX509v3Cert.build(contentSignerBuilder.build(privateKey)));
	}
	
	protected static X509Certificate createCert(boolean isCa, String signAlg, X500Name subject, X509Certificate issuer, PublicKey publicKey, PrivateKey privateKey, Date startDate, Date endDate, KeyUsage usage) throws Exception {
		X509v3CertificateBuilder jcaX509v3Cert =
		new JcaX509v3CertificateBuilder(issuer, BigInteger.valueOf(System.currentTimeMillis()), startDate, endDate, subject, publicKey);
		JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
		jcaX509v3Cert = jcaX509v3Cert.addExtension(Extension.subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(publicKey))
				.addExtension(Extension.authorityKeyIdentifier, false, extUtils.createAuthorityKeyIdentifier(issuer));
		jcaX509v3Cert = jcaX509v3Cert
						.addExtension(Extension.basicConstraints, false, new BasicConstraints(isCa))
						.addExtension(Extension.keyUsage, false, usage);
	    JcaContentSignerBuilder contentSignerBuilder = new JcaContentSignerBuilder(signAlg);
	    contentSignerBuilder.setProvider("BC");
	    return (new JcaX509CertificateConverter()).setProvider("BC").getCertificate(jcaX509v3Cert.build(contentSignerBuilder.build(privateKey)));
	}
	
}
