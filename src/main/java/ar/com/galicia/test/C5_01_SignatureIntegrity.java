package ar.com.galicia.test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.ArrayList;
 
import org.bouncycastle.jce.provider.BouncyCastleProvider;
 
import com.itextpdf.text.log.LoggerFactory;
import com.itextpdf.text.log.SysoLogger;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.security.PdfPKCS7;
 
public class C5_01_SignatureIntegrity {
    public static final String EXAMPLE1 = "e:/SAS/PDFs/tmpHijo_2017100314070013_StarGate.pdf";

	public PdfPKCS7 verifySignature(AcroFields fields, String name) throws GeneralSecurityException, IOException {
		System.out.println("Signature covers whole document: " + fields.signatureCoversWholeDocument(name));
		System.out.println("Document revision: " + fields.getRevision(name) + " of " + fields.getTotalRevisions());
        PdfPKCS7 pkcs7 = fields.verifySignature(name);
        System.out.println("Integrity check OK? " + pkcs7.verify());
        return pkcs7;
	}
 
	public void verifySignatures(String path) throws IOException, GeneralSecurityException {
		System.out.println(path);
        PdfReader reader = new PdfReader(path);
        AcroFields fields = reader.getAcroFields();
        ArrayList<String> names = fields.getSignatureNames();
		for (String name : names) {
			System.out.println("===== " + name + " =====");
			verifySignature(fields, name);
		}
		System.out.println();
	}
 
	public static void main(String[] args) throws IOException, GeneralSecurityException {
		LoggerFactory.getInstance().setLogger(new SysoLogger());
		BouncyCastleProvider provider = new BouncyCastleProvider();
		Security.addProvider(provider);
		C5_01_SignatureIntegrity app = new C5_01_SignatureIntegrity();
		app.verifySignatures(EXAMPLE1);
	}
}