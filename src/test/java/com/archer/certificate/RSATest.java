package com.archer.certificate;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import com.archer.tools.certificate.CertificateException;
import com.archer.tools.certificate.KeyCertificateUtil;
import com.archer.tools.certificate.RSACertificate;
import com.archer.tools.certificate.RSAKeyCertificate;
import com.archer.tools.certificate.RSAKeyPair;
import com.archer.tools.certificate.RSAPemKeyCertificate;
import com.archer.tools.certificate.SubjectParam;

public class RSATest {

	public static void genPemCertificate() {

		try {
			SubjectParam p = new SubjectParam();
			p.setCommonName("超级宇宙公司.com");
			p.setOrganization("超级宇宙公司");
			p.setCountry("CN");
			p.setBeginDate(LocalDateTime.now());
			p.setEndDate(LocalDateTime.now().plusYears(10));
			
			RSAPemKeyCertificate crts = RSACertificate.generatePemCaCertificate(p);
			
			Files.write(Paths.get("d:/ca.crt"), crts.getCrt().getBytes(), StandardOpenOption.CREATE);
			Files.write(Paths.get("d:/ca.key"), crts.getKey().getBytes(), StandardOpenOption.CREATE);
			Files.write(Paths.get("d:/caPublickey.pem"), KeyCertificateUtil.publicKeyToPem(KeyCertificateUtil.pemStringToX509(crts.getCrt()).getPublicKey()).getBytes(), StandardOpenOption.CREATE);
			

			p = new SubjectParam();
			p.setCommonName("宇宙公司.com");
			p.setOrganization("宇宙公司");
			p.setCountry("CN");
			p.setBeginDate(LocalDateTime.now());
			p.setEndDate(LocalDateTime.now().plusYears(10));
			
			RSAPemKeyCertificate serverCrts = RSACertificate.generatePemCertificate(crts.getCrt(), crts.getKey(), p);
			Files.write(Paths.get("d:/server.crt"), serverCrts.getCrt().getBytes(), StandardOpenOption.CREATE);
			Files.write(Paths.get("d:/server.key"), serverCrts.getKey().getBytes(), StandardOpenOption.CREATE);
			Files.write(Paths.get("d:/serverPublickey.pem"), KeyCertificateUtil.publicKeyToPem(KeyCertificateUtil.pemStringToX509(serverCrts.getCrt()).getPublicKey()).getBytes(), StandardOpenOption.CREATE);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void genCertificate() {

		try {
			SubjectParam p = new SubjectParam();
			p.setCommonName("超级宇宙公司.com");
			p.setOrganization("超级宇宙公司");
			p.setCountry("CN");
			p.setBeginDate(LocalDateTime.now());
			p.setEndDate(LocalDateTime.now().plusYears(10));
			
			RSAKeyCertificate crts = RSACertificate.generateCaCertificate(p);
			
			Files.write(Paths.get("d:/ca.crt"), KeyCertificateUtil.x509ToPem(crts.getCertificate()).getBytes(), StandardOpenOption.CREATE);
			Files.write(Paths.get("d:/ca.key"), KeyCertificateUtil.privateKeyToPem(crts.getKeyPair().getPrivateKey()).getBytes(), StandardOpenOption.CREATE);
			Files.write(Paths.get("d:/caPublickey.pem"), KeyCertificateUtil.publicKeyToPem(crts.getKeyPair().getPublicKey()).getBytes(), StandardOpenOption.CREATE);
			

			p = new SubjectParam();
			p.setCommonName("宇宙公司.com");
			p.setOrganization("宇宙公司");
			p.setCountry("CN");
			p.setBeginDate(LocalDateTime.now());
			p.setEndDate(LocalDateTime.now().plusYears(10));
			
			RSAKeyCertificate serverCrts = RSACertificate.generateCertificate(crts.getKeyPair().getPrivateKey(), crts.getCertificate(), p);
			Files.write(Paths.get("d:/server.crt"), KeyCertificateUtil.x509ToPem(serverCrts.getCertificate()).getBytes(), StandardOpenOption.CREATE);
			Files.write(Paths.get("d:/server.key"), KeyCertificateUtil.privateKeyToPem(serverCrts.getKeyPair().getPrivateKey()).getBytes(), StandardOpenOption.CREATE);
			Files.write(Paths.get("d:/serverPublickey.pem"), KeyCertificateUtil.publicKeyToPem(serverCrts.getKeyPair().getPublicKey()).getBytes(), StandardOpenOption.CREATE);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


	public static void main(String args[]) {
		
	}
}
