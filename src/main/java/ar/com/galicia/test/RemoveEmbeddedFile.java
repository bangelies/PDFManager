package ar.com.galicia.test;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
 
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RemoveEmbeddedFile {
 
    public static final String SRC = "E:\\SAS\\PDFs\\2017090711350004_IF-2017-19191094-APN-DA#IGJ - Documento Constitutivo.pdf";
    public static final String DEST ="E:\\solo.pdf";
 
    public static void main(String[] args) throws IOException, DocumentException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new RemoveEmbeddedFile().manipulatePdf(SRC, DEST);
    }
    public void manipulatePdf(String src, String dest) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(src);
        PdfDictionary root = reader.getCatalog();
        PdfDictionary names = root.getAsDict(PdfName.NAMES);
        PdfDictionary embeddedFiles = names.getAsDict(PdfName.EMBEDDEDFILES);
        PdfArray namesArray = embeddedFiles.getAsArray(PdfName.NAMES);
        namesArray.remove(0);
        namesArray.remove(0);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        stamper.close();
    }
}