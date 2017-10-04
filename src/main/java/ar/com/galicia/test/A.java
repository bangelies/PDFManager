package ar.com.galicia.test;


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


/**
 * Created by l0633615 on 30/08/2017.
 */
public class A {

    public static void main(String[] args) {


        System.out.println("*****************************************************************************************************");
//        String base64 = leerArchivo();



        ObjectMapper mapper = new ObjectMapper();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssss");
        String date = sdf.format(new Date());

        String pdfHijo = "e:/ibm/bpmLogs/tmpHijo_" + date + ".pdf";
        String pdfPadre = "e:/SAS/PDFs/IF-2017-20442925-APN-DA#IGJ.pdf";
        boolean tieneAdjuntos = false;
        boolean isPadreOk=true;
        boolean isHijoOk=true;

        try {
            //PDFBase64 obj = mapper.readValue(req.getParameter("base64"), PDFBase64.class);

            //Verifico que exista el estatuto
            System.out.println("Verifico que exista adjuntos. . .");
            ExtractEmbeddedFiles eef = new ExtractEmbeddedFiles(pdfHijo);
            tieneAdjuntos = eef.extraerAdjuntos(pdfPadre);

            if (tieneAdjuntos){
                System.out.println("Ok");
                //Verificar PADRE
                System.out.println("-===< PDF Padre >===-");
                List<EstadoDocumento> padre = imprimirResultado(pdfPadre);

                for (EstadoDocumento estadofirma: padre) {
                    if(estadofirma.isIntegridad() & estadofirma.isValidez()){
                        System.out.println("PDF Padre = OK");
                    }else{
                        System.out.println("PDF Padre = NOT OK");
                        isPadreOk=false;
                    }
                }
                if(isPadreOk){
                    System.out.println("-===< PDF Hijo >===-");
                    List<EstadoDocumento> hijo = imprimirResultado(pdfHijo);

                    for (EstadoDocumento estadofirma: hijo) {
                        if(estadofirma.isIntegridad() & estadofirma.isValidez()){
                            System.out.println("PDF Hijo = OK");
                        }else{
                            System.out.println("PDF Hijo = NOT OK");
                            isHijoOk=false;
                        }
                    }
                }



            }else{
                System.out.println("No hay adjuntos, ni me molesto en continuar.");
            }
            System.out.println("Estado general Padre: "+isPadreOk);
            System.out.println("Estado general Hijo: "+isHijoOk);
//            FileUtils.writeByteArrayToFile(new File(pdf), decode(base64));

            boolean resultadoFinal= isHijoOk & isPadreOk?true:false;

            //Object to JSON in String
            String jsonInString = mapper.writeValueAsString(resultadoFinal);
            System.out.println("Resultado final:"+jsonInString);
            //resp.getWriter().write("ar.com.galicia.verificar.JsonService POST " + jsonInString);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al verificar PDF");
        }


        System.out.println("*****************************************************************************************************");
    }

    private static  List<EstadoDocumento> imprimirResultado(String pdf){
        List<EstadoDocumento> response= new ArrayList<EstadoDocumento>();
        try {
            CertificateValidation cv = new CertificateValidation();
            response = cv.verificarFirmaFilePath(pdf);
            

            for (EstadoDocumento ef : response) {
                System.out.println("---> Nombre firma: " + ef.getNombreFirma());
                System.out.println("---> Integridad: " + ef.isIntegridad());
                System.out.println("---> Validez: " + ef.isValidez());
                if(ef.isValidez()==false){
                    System.out.println("Firma invalida: "+ef.getFirmaInvalida());
                }
              }
        }catch(Exception e){
            System.out.println("---> El documento no tiene firmas" );
        }
        return response;
    }

//    private static String leerArchivo() {
//
//        BufferedReader br = null;
//        FileReader fr = null;
//        StringBuilder sb =new StringBuilder();//
//        try {
//
//            //br = new BufferedReader(new FileReader(FILENAME));
//            fr = new FileReader("E:\\SAS\\PDFs\\startgate.txt");
//            br = new BufferedReader(fr);
//
//            String sCurrentLine;
//
//            while ((sCurrentLine = br.readLine()) != null) {
//                sb.append(sCurrentLine);
//            }
//
//        } catch (IOException e) {
//
//            e.printStackTrace();
//
//        } finally {
//
//            try {
//
//                if (br != null)
//                    br.close();
//
//                if (fr != null)
//                    fr.close();
//
//            } catch (IOException ex) {
//
//                ex.printStackTrace();
//
//            }
//
//        }
//        return sb.toString();
//
//    }
//    public static byte[] decode(String    data)
//    {
//        Encoder encoder = new Base64Encoder();
//        int len = data.length() / 4 * 3;
//        ByteArrayOutputStream bOut = new ByteArrayOutputStream(len);
//
//        try{
//            encoder.decode(data, bOut);
//        }catch (Exception e){}
//
//        return bOut.toByteArray();
//    }
}
