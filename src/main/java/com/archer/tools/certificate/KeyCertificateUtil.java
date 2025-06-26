package com.archer.tools.certificate;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.io.pem.PemReader;

public class KeyCertificateUtil {

	static {
		BCProvider.addBCProvider(); 
	}

	private static final Map<String, CurveParam> curveParams = new ConcurrentHashMap<>();

	private static final RSAKeyGenerationParameters rsaKeyParam = new RSAKeyGenerationParameters(BigInteger.valueOf(63337), new SecureRandom(), 2048, 80);
	
	private static class CurveParam {
		ECNamedCurveParameterSpec curveParamSpec;
		ECDomainParameters domainParam;
		public CurveParam(ECNamedCurveParameterSpec curveParamSpec, ECDomainParameters domainParam) {
			this.curveParamSpec = curveParamSpec;
			this.domainParam = domainParam;
		}
	}

	/**
	 * @param ecCurve see <code>com.archer.tools.certificate.CurveName</code>
	 * */
    public static ECKeyPair generateECKeyPair(String ecCurve) throws CertificateException {
		try {
			CurveParam param = curveParams.getOrDefault(ecCurve, null);
			if(param == null) {
			    X9ECParameters ecParam = GMNamedCurves.getByName(ecCurve);
			    ECNamedCurveParameterSpec paramSpec = ECNamedCurveTable.getParameterSpec(ecCurve);
			    ECDomainParameters domainParameters = new ECDomainParameters(ecParam.getCurve(), ecParam.getG(), ecParam.getN());
			    param = new CurveParam(paramSpec, domainParameters);
			    curveParams.put(ecCurve, param);
			}
			
	        ECKeyPairGenerator ecKeyPairGenerator = new ECKeyPairGenerator();
	        ecKeyPairGenerator.init(new ECKeyGenerationParameters(param.domainParam, new SecureRandom()));
	        AsymmetricCipherKeyPair cipherKeyPair = ecKeyPairGenerator.generateKeyPair();
	        ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(((ECPublicKeyParameters) cipherKeyPair.getPublic()).getQ(), param.curveParamSpec);
	        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(((ECPrivateKeyParameters) cipherKeyPair.getPrivate()).getD(), param.curveParamSpec);
	        KeyFactory keyFactory = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
	        ECPrivateKey privateKey = (ECPrivateKey) keyFactory.generatePrivate(privateKeySpec);
	        ECPublicKey publicKey = (ECPublicKey) keyFactory.generatePublic(publicKeySpec);
	        
	        return new ECKeyPair(privateKey, publicKey);
		} catch(Exception e) {
			throw new CertificateException(e);
		}
    }
    
    public static RSAKeyPair generateRSAKeyPair() throws CertificateException {
    	try {
        	RSAKeyPairGenerator rsaKeyPairGenerator = new RSAKeyPairGenerator();
            rsaKeyPairGenerator.init(rsaKeyParam);
            AsymmetricCipherKeyPair cipherKeyPair = rsaKeyPairGenerator.generateKeyPair();
            RSAKeyParameters publicKeyParam = (RSAKeyParameters) cipherKeyPair.getPublic();
            RSAKeyParameters privateKeyParam = (RSAKeyParameters) cipherKeyPair.getPrivate();
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(publicKeyParam.getModulus(), publicKeyParam.getExponent());
            RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(privateKeyParam.getModulus(), privateKeyParam.getExponent());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
            RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
            
            return new RSAKeyPair(privateKey, publicKey);
    	} catch(Exception e) {
    		throw new CertificateException(e);
    	}
    }
    
	public static String ecPublicKeyToHex(ECPublicKey pubKey) throws IOException {        
		byte[] pubKeyBytes = ((BCECPublicKey) pubKey).getQ().getEncoded(false);
		return HexUtil.bytesToHexStr(Arrays.copyOfRange(pubKeyBytes, 1, pubKeyBytes.length));
	}
	
	public static String ecPrivateKeyToHex(ECPrivateKey priKey) throws IOException {        
		return ((BCECPrivateKey) priKey).getD().toString(16);
	}
	public static String rsaPublicKeyToHex(RSAPublicKey pubKey) throws IOException {        
		String exp = pubKey.getPublicExponent().toString(16);     
		String mod = pubKey.getModulus().toString(16);
		return exp + mod;
	}
	
	public static String rsaPrivateKeyToHex(RSAPrivateKey priKey) throws IOException {       
		String exp = priKey.getPrivateExponent().toString(16);     
		String mod = priKey.getModulus().toString(16);
		return exp + mod;
	}
	

	public static String privateKeyToPem(PrivateKey key) {
	    byte[] buffer = Base64.getEncoder().encode(key.getEncoded());
	    String head = "-----BEGIN PRIVATE KEY-----\n";
	    String tail = "-----END PRIVATE KEY-----\n";
	    StringBuilder sb = new StringBuilder(head);
	    int begin = 0, offset = 64;
	    byte[] line = new byte[offset];
	    while (begin + offset <= buffer.length) {
	        System.arraycopy(buffer, begin, line, 0, offset);
	        sb.append(new String(line, StandardCharsets.US_ASCII))
	          .append("\n");
	        begin += offset;
	    } 
	    if (begin < buffer.length) {
	        line = new byte[buffer.length - begin];
	        System.arraycopy(buffer, begin, line, 0, line.length);
	        sb.append(new String(line, StandardCharsets.UTF_8))
	          .append("\n");
	    } 
	    sb.append(tail);
	    return sb.toString();
	}
	
	public static String publicKeyToPem(PublicKey key) throws IOException {
		StringWriter stringWriter = new StringWriter();
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
            pemWriter.writeObject(key);
        }
        return stringWriter.toString();
	}
	
	public static String x509ToPem(X509Certificate crt) throws IOException {
		StringWriter writer = new StringWriter();
	    try (JcaPEMWriter pw = new JcaPEMWriter(writer)) {
		    pw.writeObject(crt);
		    pw.flush();
		    return writer.getBuffer().toString();
	    }
	}
	
	public static X509Certificate pemStringToX509(String pemStr) throws CertificateException {
		StringReader reader = new StringReader(pemStr);
		PemReader pemReader = new PemReader(reader);
		PEMParser pemParser = new PEMParser(pemReader);
		try {
			Object object = pemParser.readObject();
			if (object instanceof X509CertificateHolder) {
			      return (new JcaX509CertificateConverter()).setProvider("BC")
			        .getCertificate((X509CertificateHolder)object); 
			}
			throw new IllegalArgumentException("invalid cert content, " + pemStr);
		} catch(Exception e) {
			throw new CertificateException(e);
		} finally {
			try {
				pemParser.close();
			} catch (IOException e) {
				throw new CertificateException(e);
			}
		}
	}
	
	protected static X509Certificate[] pemStringToX509Chain(String pemStr) throws Exception {
		int off = 0;
		X509Certificate[] chain = new X509Certificate[1024];
		String[] lines = pemStr.split("\n");
		StringBuilder crtPem = new StringBuilder(1024);
		int state = 0;
		for(int i = 0; i < lines.length; i++) {
			if(lines[i].charAt(0) == '-') {
				if(state == 0) {
					state = 1;
				} else if(state == 1) {
					state = 2;
				}
			}
			crtPem.append(lines[i]).append('\n');
			if(state == 2) {
				chain[off++] = pemStringToX509(crtPem.toString());
				crtPem = new StringBuilder(1024);
				state = 0;
			}
		}
		return Arrays.copyOfRange(chain, 0, off);
	}
	
	public static X500Name x509ToX500Name(X509Certificate crt) {
		return new X500Name(crt.getSubjectX500Principal().getName());
	}
	

	public static ECPrivateKey pemStringToECPrivateKey(String pemStr) throws CertificateException {
		try {
			return (ECPrivateKey)pemStringToECPrivateKey(pemStr, "EC");
		} catch (Exception e) {
			throw new CertificateException(e);
		}
	}
	
	public static RSAPrivateKey pemStringToRSAPrivateKey(String pemStr) throws CertificateException {
		try {
			return (RSAPrivateKey)pemStringToECPrivateKey(pemStr, "RSA");
		} catch (Exception e) {
			throw new CertificateException(e);
		}
	}
	
	private static PrivateKey pemStringToECPrivateKey(String pemStr, String alg) throws Exception {
        StringBuilder sb = new StringBuilder();
        String[] lines = pemStr.split("\n");
        int l = lines.length;
        if(lines[l - 1].trim().isEmpty()) {
        	--l;
        }
        for(int i = 1; i < l - 1; i++) {
            sb.append(lines[i]);
        }
        byte[] buffer = Base64.getDecoder().decode(sb.toString().getBytes(StandardCharsets.US_ASCII));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
        KeyFactory keyFactory = KeyFactory.getInstance(alg, new BouncyCastleProvider());
        return keyFactory.generatePrivate(keySpec);
	} 
	
	
	protected static String extractParentCrtPem(String pemStr) {
        String sep = "-----END CERTIFICATE-----";
        if(!pemStr.contains(sep)) {
            throw new IllegalArgumentException("invalid certificat: \n" + pemStr);
        }
        int index = pemStr.indexOf(sep) + sep.length();
        if(index < pemStr.length()) {
        	index++;
        }
        return pemStr.substring(index).trim();
	}
}
