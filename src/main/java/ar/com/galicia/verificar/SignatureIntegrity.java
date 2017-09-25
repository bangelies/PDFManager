package ar.com.galicia.verificar;

import ar.com.galicia.log.Logear;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.security.PdfPKCS7;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.bouncycastle.util.encoders.Encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class SignatureIntegrity {
	private static final Encoder encoder = new Base64Encoder();

	public PdfPKCS7 verifySignature(AcroFields fields, String name) throws GeneralSecurityException, IOException {
		Logear.logEmpresasSAS_debug("Signature covers whole document: " + fields.signatureCoversWholeDocument(name));
		Logear.logEmpresasSAS_debug("Document revision: " + fields.getRevision(name) + " of " + fields.getTotalRevisions());
		PdfPKCS7 pkcs7 = fields.verifySignature(name);
		Logear.logEmpresasSAS_debug("Integrity check OK? " + pkcs7.verify());
		return pkcs7;
	}

	public void verifySignaturesBase64(String pdf) throws IOException, GeneralSecurityException {
		PdfReader reader = new PdfReader(decode(pdf));
		AcroFields fields = reader.getAcroFields();
		ArrayList<String> names = fields.getSignatureNames();
		for (String name : names) {
			Logear.logEmpresasSAS_debug("===== " + name + " =====");
			verifySignature(fields, name);
		}
	}
	public void verifySignaturesFilePath(String pdf) throws IOException, GeneralSecurityException {
		PdfReader reader = new PdfReader(pdf);
		AcroFields fields = reader.getAcroFields();
		ArrayList<String> names = fields.getSignatureNames();
		for (String name : names) {
			Logear.logEmpresasSAS_debug("===== " + name + " =====");
			verifySignature(fields, name);
		}
	}




	public byte[] decode(String    data)
	{
		int len = data.length() / 4 * 3;
		ByteArrayOutputStream bOut = new ByteArrayOutputStream(len);

		try
		{
			encoder.decode(data, bOut);
		}
		catch (Exception e)
		{
		}

		return bOut.toByteArray();
	}
}