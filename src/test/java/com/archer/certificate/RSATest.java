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
import com.archer.tools.certificate.SubjectParam;

public class RSATest {


	public static void main(String args[]) {

		try {
			SubjectParam p = new SubjectParam();
			p.setBeginDate(LocalDateTime.now());
			p.setEndDate(LocalDateTime.now().plusMonths(12));
			
			RSAKeyCertificate crts = RSACertificate.generateCaCertificate(p);
			Files.write(Paths.get("d:/tmp.crt"), KeyCertificateUtil.x509ToPem(crts.getCertificate()).getBytes(), StandardOpenOption.CREATE);
			Files.write(Paths.get("d:/tmp.key"), KeyCertificateUtil.privateKeyToPem(crts.getKeyPair().getPrivateKey()).getBytes(), StandardOpenOption.CREATE);
			Files.write(Paths.get("d:/tmp.pem"), KeyCertificateUtil.publicKeyToPem(crts.getKeyPair().getPublicKey()).getBytes(), StandardOpenOption.CREATE);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
