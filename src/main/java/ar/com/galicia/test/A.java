package ar.com.galicia.test;


import ar.com.galicia.log.Logear;
import ar.com.galicia.verificar.CertificateValidation;
import ar.com.galicia.verificar.ExtractEmbeddedFiles;
import ar.com.galicia.verificar.PDFBase64;
import ar.com.galicia.verificar.Respuesta;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.bouncycastle.util.encoders.Encoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * Created by l0633615 on 30/08/2017.
 */
public class A {

    public static void main(String[] args){


        Logear.logEmpresasSAS_debug("*****************************************************************************************************");
        String base64 = leerArchivo();


        Respuesta respuesta = new Respuesta();
        String pdfEstadoGeneral ="";
        ObjectMapper mapper = new ObjectMapper();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssss");
        String date = sdf.format(new Date());
        String pdfPadre="e:/ibm/bpmLogs/tmpPadre_"+date+".pdf";
        String pdfHijo="e:/ibm/bpmLogs/tmpHijo_"+date+".pdf";


        try {
            //PDFBase64 obj = mapper.readValue(req.getParameter("base64"), PDFBase64.class);




            //Verificar PADRE
            System.out.println("PDF Padre");
            CertificateValidation verificarPadre = new CertificateValidation();
            pdfEstadoGeneral = verificarPadre.verificarFirmaBase64(base64);
            System.out.println(pdfEstadoGeneral);
            Logear.logEmpresasSAS_debug(pdfEstadoGeneral);

            FileUtils.writeByteArrayToFile(new File(pdfPadre), decode(base64));

            //Verificar HIJO
            if(pdfEstadoGeneral.equalsIgnoreCase("Documento valido")){
                System.out.println("PDF Hijo");
                ExtractEmbeddedFiles eef = new ExtractEmbeddedFiles(pdfHijo);
                boolean tieneAdjuntos = eef.extraerAdjuntos(pdfPadre);

                if(tieneAdjuntos){
                    CertificateValidation verificarHijo = new CertificateValidation();
                    pdfEstadoGeneral= verificarHijo.verificarFirmaFilePath(pdfHijo);
                    Logear.logEmpresasSAS_debug(pdfEstadoGeneral);
                }else{
                    pdfEstadoGeneral= "El PDF no posee estatuto";
                }



            }else{
                System.out.println(respuesta);
                Logear.logEmpresasSAS_debug(pdfEstadoGeneral);
            }



            respuesta.setEstadoPdf(pdfEstadoGeneral);
            //http://desabpmpc01.bancogalicia.com.ar:9080/pdfverify/verificarFirma?base64={"base64" : ""}
            //Object to JSON in String
            String jsonInString = mapper.writeValueAsString(respuesta);
            System.out.println(jsonInString);
            //resp.getWriter().write("JsonService POST " + jsonInString);

        } catch (Exception e) {
            e.printStackTrace();
            Logear.logEmpresasSAS_debug("Error al verificar PDF");
        }


        Logear.logEmpresasSAS_debug("*****************************************************************************************************");
    }
    private static String leerArchivo() {

        BufferedReader br = null;
        FileReader fr = null;
        StringBuilder sb =new StringBuilder();//
        try {

            //br = new BufferedReader(new FileReader(FILENAME));
            fr = new FileReader("E:\\SAS\\PDFs\\2017090711350004_IF-2017-19191094-APN-DA#IGJ - Documento Constitutivo.txt");
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
