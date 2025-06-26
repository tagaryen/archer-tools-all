package com.archer.tools.certificate;

import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.KeyUsage;

public class ECCertificate {

	static {
		BCProvider.addBCProvider(); 
	}
	
	public static ECKeyCertificate generateCaCertificate(SubjectParam param) throws CertificateException {
		try {
			ECKeyPair caKey = KeyCertificateUtil.generateECKeyPair(param.getAlgorithm());
			X509Certificate crt = Certificate.createCaCert(param.getAlgorithm(), new X500Name(param.toSubjString()), 
					caKey.getPublicKey(), caKey.getPrivateKey(), param.getBeginDate(), param.getEndDate(),
					param.getKeyUsage());
			return new ECKeyCertificate(crt, caKey);
		} catch(Exception e) {
			throw new CertificateException(e);
		}
	}
	
	
	public static ECKeyCertificate generateCertificate(ECPrivateKey issuerKey, X509Certificate issuerCrt, SubjectParam crtSubjectParam) throws CertificateException {
		try {
			ECKeyPair key = KeyCertificateUtil.generateECKeyPair(crtSubjectParam.getAlgorithm());
			X509Certificate crt = Certificate.createCert(crtSubjectParam.isIssuer(), crtSubjectParam.getAlgorithm(), new X500Name(crtSubjectParam.toSubjString()), issuerCrt, 
					key.getPublicKey(), issuerKey, crtSubjectParam.getBeginDate(), crtSubjectParam.getEndDate(),
					crtSubjectParam.getKeyUsage());
			return new ECKeyCertificate(crt, key);
		} catch(Exception e) {
			throw new CertificateException(e);
		}
	}
	

	public static ECPemKeyCertificate generatePemCaCertificate(SubjectParam param) throws CertificateException {
		try {
			ECKeyPair caKey = KeyCertificateUtil.generateECKeyPair(param.getAlgorithm());
			X509Certificate crt = Certificate.createCaCert(param.getAlgorithm(), new X500Name(param.toSubjString()), 
					caKey.getPublicKey(), caKey.getPrivateKey(), param.getBeginDate(), param.getEndDate(),
					param.getKeyUsage());
			return new ECPemKeyCertificate(KeyCertificateUtil.x509ToPem(crt), KeyCertificateUtil.privateKeyToPem(caKey.getPrivateKey()),
					KeyCertificateUtil.ecPrivateKeyToHex(caKey.getPrivateKey()), KeyCertificateUtil.ecPublicKeyToHex(caKey.getPublicKey()));
		} catch(Exception e) {
			throw new CertificateException(e);
		}
	}
	
	
	public static ECPemKeyCertificate generatePemCertificate(String issuerKeyPem, String issuerCertPem, SubjectParam crtSubjectParam) throws CertificateException {
		try {
			ECKeyPair key = KeyCertificateUtil.generateECKeyPair(crtSubjectParam.getAlgorithm());
			X509Certificate issuerCrt = KeyCertificateUtil.pemStringToX509(issuerCertPem);
			X509Certificate crt = Certificate.createCert(crtSubjectParam.isIssuer(), crtSubjectParam.getAlgorithm(), new X500Name(crtSubjectParam.toSubjString()), issuerCrt, 
					key.getPublicKey(), KeyCertificateUtil.pemStringToECPrivateKey(issuerKeyPem), crtSubjectParam.getBeginDate(), crtSubjectParam.getEndDate(),
					crtSubjectParam.getKeyUsage());
			String pemCrt = KeyCertificateUtil.x509ToPem(crt);
			if(pemCrt.charAt(pemCrt.length() - 1) == '\n') {
				pemCrt += issuerCertPem;
			} else {
				pemCrt += '\n' + issuerCertPem;
			}
			return new ECPemKeyCertificate(pemCrt, KeyCertificateUtil.privateKeyToPem(key.getPrivateKey()),
					KeyCertificateUtil.ecPrivateKeyToHex(key.getPrivateKey()), KeyCertificateUtil.ecPublicKeyToHex(key.getPublicKey()));
		} catch(Exception e) {
			throw new CertificateException(e);
		}
	}
	
	public static X509Certificate updateCertificateDate(ECPrivateKey issuerKey, X509Certificate issuerCrt, X509Certificate crt, 
			LocalDateTime begin, LocalDateTime end) 
			throws CertificateException {
		try {
			Date beginDate = Date.from(begin.atZone(ZoneId.systemDefault()).toInstant());
			Date endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());

			KeyUsage keyUsage = KeyUsage.getInstance(crt.getExtensionValue("2.5.29.15"));
			boolean isCa = crt.getBasicConstraints() >= 0;
			return Certificate.createCert(isCa, crt.getSigAlgName(), new X500Name(crt.getSubjectX500Principal().getName()), 
					issuerCrt, 
					crt.getPublicKey(), issuerKey, beginDate, endDate, keyUsage);
		} catch(Exception e) {
			throw new CertificateException(e);
		}
	}
	
	public static String updatePemCertificateDate(String issuerKeyPem, String issuerCrtPem, String crtPem, 
			LocalDateTime begin, LocalDateTime end) 
			throws CertificateException {
		try {
			ECPrivateKey parentKey = KeyCertificateUtil.pemStringToECPrivateKey(issuerKeyPem);
			X509Certificate[] crtChain = KeyCertificateUtil.pemStringToX509Chain(crtPem);
			X509Certificate parentCrt = KeyCertificateUtil.pemStringToX509(issuerCrtPem), curCrt = crtChain[0];
			if(crtChain.length > 1) {
				parentCrt = crtChain[1];
			}
			curCrt.verify(parentCrt.getPublicKey());
			Date beginDate = Date.from(begin.atZone(ZoneId.systemDefault()).toInstant());
			Date endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());

			KeyUsage keyUsage = KeyUsage.getInstance(curCrt.getExtensionValue("2.5.29.15"));
			boolean isCa = curCrt.getBasicConstraints() >= 0;
			X509Certificate newCrt = Certificate.createCert(isCa, curCrt.getSigAlgName(), new X500Name(curCrt.getSubjectX500Principal().getName()), 
					parentCrt, 
					curCrt.getPublicKey(), parentKey, beginDate, endDate, keyUsage);
			String parantCrtPem = KeyCertificateUtil.extractParentCrtPem(crtPem);
			String newCrtPem = KeyCertificateUtil.x509ToPem(newCrt);
			if(newCrtPem.charAt(newCrtPem.length() - 1) == '\n') {
				newCrtPem += parantCrtPem;
			} else {
				newCrtPem += '\n' + parantCrtPem;
			}
			return newCrtPem;
		} catch(Exception e) {
			throw new CertificateException(e);
		}
	}
}
