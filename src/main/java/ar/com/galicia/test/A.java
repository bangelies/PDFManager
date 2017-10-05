package ar.com.galicia.test;


import ar.com.galicia.config.Propiedades;
import ar.com.galicia.entidades.Documento;
import ar.com.galicia.log.Logear;
import ar.com.galicia.verificar.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.bouncycastle.util.encoders.Encoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * Created by l0633615 on 30/08/2017.
 */
public class A {

    public static void main(String[] args) {


        System.out.println("*****************************************************************************************************");
//        String base64 = leerArchivo();

        ObjectMapper mapper = new ObjectMapper();
        String uuid = UUID.randomUUID().toString();

        //String pdfPadre= Propiedades.pdfExtractor+"tmpPadre_"+uuid+".pdf";
        String pdfPadre= "E:/SAS/PDFs/Estatuto Waykap SAS - Como adjunto certificado por IGJ.pdf";
        String pdfHijo=Propiedades.pdfExtractor+"tmpHijo_"+uuid+".pdf";
        boolean tieneAdjuntos = false;

        try {
            //PDFBase64 obj = mapper.readValue(req.getParameter("base64"), PDFBase64.class);

            //FileUtils.writeByteArrayToFile(new File(pdfPadre), decode(leerArchivo()));



            List<Documento> resultadoDelAnalisis=null;

            List<String> documentosParaAnalizar = new ArrayList<String>();
            documentosParaAnalizar.add(pdfPadre);
            ExtractEmbeddedFiles eef = new ExtractEmbeddedFiles(pdfHijo);

            if(eef.extraerAdjuntos(pdfPadre)){
                documentosParaAnalizar.add(pdfHijo);
             }


            resultadoDelAnalisis = verifcarDocumentos(documentosParaAnalizar);



            //Object to JSON in String
            String jsonInString = mapper.writeValueAsString(resultadoDelAnalisis);
            System.out.println("Resultado final:"+jsonInString);
            //resp.getWriter().write("ar.com.galicia.verificar.JsonService POST " + jsonInString);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al verificar PDF");
        }


        System.out.println("*****************************************************************************************************");
    }

    private static  List<Documento> verifcarDocumentos(List<String> documentosParaAnalizar){

        List<Documento> documentosAnalizados = new ArrayList<Documento>();
        try {
            for (String pathDocumento: documentosParaAnalizar) {
                CertificateValidation cv = new CertificateValidation();
                documentosAnalizados.add(cv.verificarFirmaFilePath(pathDocumento));
            }
        }catch(Exception e){
            System.out.println("---> El documento no tiene firmas" );
        }
        return documentosAnalizados;
    }

    private static String leerArchivo() {

        BufferedReader br = null;
        FileReader fr = null;
        StringBuilder sb =new StringBuilder();//
        try {

            //br = new BufferedReader(new FileReader(FILENAME));
            fr = new FileReader("E:\\SAS\\PDFs\\Estatuto Waykap SAS - Como adjunto certificado por IGJ.txt");
            br = new BufferedReader(fr);

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                sb.append(sCurrentLine);
            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
        return sb.toString();

    }
    public static byte[] decode(String    data)
    {
        Encoder encoder = new Base64Encoder();
        int len = data.length() / 4 * 3;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream(len);

        try{
            encoder.decode(data, bOut);
        }catch (Exception e){}

        return bOut.toByteArray();
    }
}
