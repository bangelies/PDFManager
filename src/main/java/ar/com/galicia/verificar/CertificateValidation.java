package ar.com.galicia.verificar;

import ar.com.galicia.config.Propiedades;
import ar.com.galicia.entidades.Documento;
import ar.com.galicia.entidades.Firma;
import ar.com.galicia.log.Logear;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.security.*;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.bouncycastle.util.encoders.Encoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

public class CertificateValidation {


	private KeyStore ks;

	private boolean conFirmas=false;
	private static final Encoder encoder = new Base64Encoder();



	public Firma verifySignature(AcroFields fields, String name) throws GeneralSecurityException, IOException {
		Firma firma = new Firma();

		conFirmas=true;
//		PdfPKCS7 pkcs7 = super.verifySignature(fields, name);

		PdfPKCS7 pkcs7 = fields.verifySignature(name);
		//System.out.println("Integrity check OK? " + pkcs7.verify());
		firma.setIntegridad(pkcs7.verify());

		Certificate[] certs = pkcs7.getSignCertificateChain();
		Calendar cal = pkcs7.getSignDate();
		List<VerificationException> errors = CertificateVerification.verifyCertificates(certs, ks, cal);
		if (errors.size() == 0){
			Logear.logEmpresasSAS_debug("Certificates verified against the KeyStore");
			firma.setValidez(true);
			//System.out.println("Certificates verified against the KeyStore");
		}
		else{
			//System.out.println("No Certificates verified against the KeyStore");
			firma.setValidez(false);
			//firma.setFirmaInvalida(errors.toString());
			Logear.logEmpresasSAS_debug(errors.toString());}	//Aca tira el error de firma invalida
		for (int i = 0; i < certs.length; i++) {
			X509Certificate cert = (X509Certificate) certs[i];
			Logear.logEmpresasSAS_debug("=== Certificate " + i + " ===");
			showCertificateInfo(cert, cal.getTime(),firma);
		}
		X509Certificate signCert = (X509Certificate)certs[0];
		X509Certificate issuerCert = (certs.length > 1 ? (X509Certificate)certs[1] : null);
//		Logear.logEmpresasSAS_debug("=== Checking validity of the document at the time of signing ===");
//		checkRevocation(pkcs7, signCert, issuerCert, cal.getTime());
		Logear.logEmpresasSAS_debug("=== Checking validity of the document today ===");
		checkRevocation(pkcs7, signCert, issuerCert, new Date());

		return firma;
	}

	public void checkRevocation(PdfPKCS7 pkcs7, X509Certificate signCert, X509Certificate issuerCert, Date date) throws GeneralSecurityException, IOException {
		List<BasicOCSPResp> ocsps = new ArrayList<BasicOCSPResp>();
		if (pkcs7.getOcsp() != null)
			ocsps.add(pkcs7.getOcsp());
		OCSPVerifier ocspVerifier = new OCSPVerifier(null, ocsps);
		List<VerificationOK> verification =ocspVerifier.verify(signCert, issuerCert, date);
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
			//respuesta="fail";
		}
		else {
			for (VerificationOK v : verification) {
				Logear.logEmpresasSAS_debug("verification "+verification.size());
				Logear.logEmpresasSAS_debug(v.toString());
			}
			//respuesta="ok";
		}
	}

	public void showCertificateInfo(X509Certificate cert, Date signDate,Firma firma) {
		Logear.logEmpresasSAS_debug("Issuer: " + cert.getIssuerDN());
		Logear.logEmpresasSAS_debug("Subject: " + cert.getSubjectDN());
		firma.setNombreFirma(""+cert.getSubjectDN());
		SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
		Logear.logEmpresasSAS_debug("Valid from: " + date_format.format(cert.getNotBefore()));
		Logear.logEmpresasSAS_debug("Valid to: " + date_format.format(cert.getNotAfter()));
		try {
			cert.checkValidity(signDate);
		//	Logear.logEmpresasSAS_debug("The certificate was valid at the time of signing.");
		} catch (CertificateExpiredException e) {
		//	Logear.logEmpresasSAS_debug("The certificate was expired at the time of signing.");
		} catch (CertificateNotYetValidException e) {
		//	Logear.logEmpresasSAS_debug("The certificate wasn't valid yet at the time of signing.");
		}
		try {
			cert.checkValidity();
		//	Logear.logEmpresasSAS_debug("The certificate is still valid.");
		} catch (CertificateExpiredException e) {
		//	Logear.logEmpresasSAS_debug("The certificate has expired.");
		} catch (CertificateNotYetValidException e) {
		//	Logear.logEmpresasSAS_debug("The certificate isn't valid yet.");
		}
	}

	private void setKeyStore(KeyStore ks) {
		this.ks = ks;
	}

//	public List<EstadoDocumento> verificarFirmaBase64(String base64) throws IOException,GeneralSecurityException {
//		Logear.logEmpresasSAS_debug("Inicio verificarFirma *************************");
//
//		//LoggerFactory.getInstance().setLogger(new SysoLogger());
//		BouncyCastleProvider provider = new BouncyCastleProvider();
//		Security.addProvider(provider);
//		//CertificateValidation app = new CertificateValidation();
//		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
//
//		ks.load(null, null);
//		CertificateFactory cf = CertificateFactory.getInstance("X.509");
//		ks.setCertificateEntry("cacert",cf.generateCertificate(new FileInputStream(Propiedades.path+Propiedades.certificado)));
//		setKeyStore(ks);
//
//		//Verificar BASE64
//		//verifySignaturesBase64(base64);
//
//		PdfReader reader = new PdfReader(decode(base64));
//		AcroFields fields = reader.getAcroFields();
//		ArrayList<String> names = fields.getSignatureNames();
//
//		//Genero la lista de salida con el resultado de las firmas
//		List<EstadoDocumento> listaEstadoFirmas = new ArrayList<EstadoDocumento>();
//		for (String name : names) {
//			Logear.logEmpresasSAS_debug("===== " + name + " =====");
//			EstadoDocumento ef = verifySignature(fields, name);
//			//ef.setNombreFirma(name);
//			ef.setTieneFirmas(conFirmas);
//			listaEstadoFirmas.add(ef);
//		}
//
//		Logear.logEmpresasSAS_debug("Fin *************************");
//
//		return listaEstadoFirmas;
//	}
	public Documento verificarFirmaFilePath(String path) throws IOException,GeneralSecurityException, Exception {
		Logear.logEmpresasSAS_debug("************* Inicio analisis del documento *************");

		//Inicializo BouncyCastle para comenzar el analisis
		BouncyCastleProvider provider = new BouncyCastleProvider();
		Security.addProvider(provider);
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(null, null);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		ks.setCertificateEntry("cacert",cf.generateCertificate(new FileInputStream(Propiedades.pathCertificado)));
		setKeyStore(ks);


		//Por cada documento realizo los siguientes pasos para su analisis
		Documento documento = new Documento();
		List<Firma> firmas = new ArrayList<Firma>();

		PdfReader reader = new PdfReader(path);
		AcroFields fields = reader.getAcroFields();
		ArrayList<String> names = fields.getSignatureNames();

		documento.setNombrePDF(new File(path).getName());
		if(names.size()==0){
			documento.setHayFirmas(false);
		}else{
			documento.setHayFirmas(true);
			for (String name : names) {
				Logear.logEmpresasSAS_debug("===== " + name + " =====");
				firmas.add(verifySignature(fields, name));
			}
		}

		documento.setFirmas(firmas);

		Logear.logEmpresasSAS_debug("************* Finalizo analisis del documento *************");

		return documento;
	}
//	public byte[] decode(String    data)
//	{
//		int len = data.length() / 4 * 3;
//		ByteArrayOutputStream bOut = new ByteArrayOutputStream(len);
//
//		try	{
//			encoder.decode(data, bOut);
//		}catch (Exception e)
//		{
//			Logear.logEmpresasSAS_error("Error al decodear el PDF");
//		}
//		return bOut.toByteArray();
//	}

}