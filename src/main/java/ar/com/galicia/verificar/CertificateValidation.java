package ar.com.galicia.verificar;

import ar.com.galicia.log.Logear;
import com.itextpdf.text.log.LoggerFactory;
import com.itextpdf.text.log.SysoLogger;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.security.*;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CertificateValidation extends SignatureIntegrity {


	private KeyStore ks;
	private String respuesta;
	private boolean integridadFirmas=true;
	private boolean conFirmas=false;



	public PdfPKCS7 verifySignature(AcroFields fields, String name)
			throws GeneralSecurityException, IOException {
		conFirmas=true;
		PdfPKCS7 pkcs7 = super.verifySignature(fields, name);
		Certificate[] certs = pkcs7.getSignCertificateChain();
		Calendar cal = pkcs7.getSignDate();
		List<VerificationException> errors = CertificateVerification.verifyCertificates(certs, ks, cal);
		if (errors.size() == 0)
			Logear.logEmpresasSAS_debug("Certificates verified against the KeyStore");
		else
			Logear.logEmpresasSAS_debug(errors.toString());
		for (int i = 0; i < certs.length; i++) {
			X509Certificate cert = (X509Certificate) certs[i];
			Logear.logEmpresasSAS_debug("=== Certificate " + i + " ===");
			showCertificateInfo(cert, cal.getTime());
		}
		X509Certificate signCert = (X509Certificate)certs[0];
		X509Certificate issuerCert = (certs.length > 1 ? (X509Certificate)certs[1] : null);
//		Logear.logEmpresasSAS_debug("=== Checking validity of the document at the time of signing ===");
//		checkRevocation(pkcs7, signCert, issuerCert, cal.getTime());
		Logear.logEmpresasSAS_debug("=== Checking validity of the document today ===");
		checkRevocation(pkcs7, signCert, issuerCert, new Date());

		if (!pkcs7.verify()){
			integridadFirmas=false;
		}

		return pkcs7;
	}
 
	public void checkRevocation(PdfPKCS7 pkcs7, X509Certificate signCert, X509Certificate issuerCert, Date date) throws GeneralSecurityException, IOException {
		List<BasicOCSPResp> ocsps = new ArrayList<BasicOCSPResp>();
		if (pkcs7.getOcsp() != null)
			ocsps.add(pkcs7.getOcsp());
		OCSPVerifier ocspVerifier = new OCSPVerifier(null, ocsps);
		List<VerificationOK> verification =
			ocspVerifier.verify(signCert, issuerCert, date);
		if (verification.size() == 0) {
			List<X509CRL> crls = new ArrayList<X509CRL>();
			if (pkcs7.getCRLs() != null) {
				for (CRL crl : pkcs7.getCRLs())
					crls.add((X509CRL)crl);
			}
			CRLVerifier crlVerifier = new CRLVerifier(null, crls);
			verification.addAll(crlVerifier.verify(signCert, issuerCert, date));
		}
		if (verification.size() == 0) {
			Logear.logEmpresasSAS_debug("The signing certificate couldn't be verified");
			respuesta="fail";
		}
		else {
			for (VerificationOK v : verification)
				Logear.logEmpresasSAS_debug(v.toString());
			respuesta="ok";
		}
	}
 
	public void showCertificateInfo(X509Certificate cert, Date signDate) {
		Logear.logEmpresasSAS_debug("Issuer: " + cert.getIssuerDN());
		Logear.logEmpresasSAS_debug("Subject: " + cert.getSubjectDN());
		SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
		Logear.logEmpresasSAS_debug("Valid from: " + date_format.format(cert.getNotBefore()));
		Logear.logEmpresasSAS_debug("Valid to: " + date_format.format(cert.getNotAfter()));
		try {
			cert.checkValidity(signDate);
			Logear.logEmpresasSAS_debug("The certificate was valid at the time of signing.");
		} catch (CertificateExpiredException e) {
			Logear.logEmpresasSAS_debug("The certificate was expired at the time of signing.");
		} catch (CertificateNotYetValidException e) {
			Logear.logEmpresasSAS_debug("The certificate wasn't valid yet at the time of signing.");
		}
		try {
			cert.checkValidity();
			Logear.logEmpresasSAS_debug("The certificate is still valid.");
		} catch (CertificateExpiredException e) {
			Logear.logEmpresasSAS_debug("The certificate has expired.");
		} catch (CertificateNotYetValidException e) {
			Logear.logEmpresasSAS_debug("The certificate isn't valid yet.");
		}
	}
 
	private void setKeyStore(KeyStore ks) {
		this.ks = ks;
	}
 
	public String verificarFirma(String cacert, String base64) throws IOException,GeneralSecurityException {
		Logear.logEmpresasSAS_debug("Inicio verificarFirma");

		//LoggerFactory.getInstance().setLogger(new SysoLogger());
		BouncyCastleProvider provider = new BouncyCastleProvider();
		Security.addProvider(provider);
		//CertificateValidation app = new CertificateValidation();
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
 
		ks.load(null, null);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		ks.setCertificateEntry("cacert",cf.generateCertificate(new FileInputStream(cacert)));
		setKeyStore(ks);
		verifySignatures(base64);

		if(conFirmas){
			Logear.logEmpresasSAS_debug("El documento tiene al menos una firma");
			if(integridadFirmas) {
				Logear.logEmpresasSAS_debug("Documento valido");
				respuesta="Documento valido";
			}else {
				respuesta="Al menos una firma no es valida";
				Logear.logEmpresasSAS_debug("Al menos una firma no es valida");
			}
		}else {
			respuesta="El documento no tiene firmas";
		}
		Logear.logEmpresasSAS_debug("Fin verificarFirma");

//		if(integridadFirmas & conFirmas){
//			Logear.logEmpresasSAS_debug("integridadFirmas: "+integridadFirmas);
//			Logear.logEmpresasSAS_debug("conFirmas: "+conFirmas);
//			respuesta="ok";
//		}else{
//
//			if(conFirmas==false){
//				integridadFirmas=false;
//				Logear.logEmpresasSAS_debug("conFirmas: "+conFirmas);
//			}else{
//				Logear.logEmpresasSAS_debug("conFirmas: "+conFirmas);
//			}
//
//			Logear.logEmpresasSAS_debug("integridadFirmas: "+integridadFirmas);
//			respuesta="fail";
//		}
//		Logear.logEmpresasSAS_debug("respuesta: "+respuesta);
//
//		Logear.logEmpresasSAS_debug("Fin verificarFirma");
		return respuesta;


	}
}