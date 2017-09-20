package ar.com.galicia.test;


import ar.com.galicia.log.Logear;
import ar.com.galicia.verificar.CertificateValidation;
import ar.com.galicia.verificar.PDFBase64;
import com.fasterxml.jackson.databind.ObjectMapper;
import sun.misc.BASE64Encoder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by l0633615 on 30/08/2017.
 */
public class A {

    public static void main(String[] args){


        Logear.logEmpresasSAS_debug("*****************************************************************************************************");
        String respuesta="";
        String certificado = "/ibm/bpmLogs/acraizra.crt";
        CertificateValidation cv = new CertificateValidation();
        try {
            //ObjectMapper mapper = new ObjectMapper();
            //PDFBase64 obj = mapper.readValue(leerArchivo(), PDFBase64.class);

            //Logear.logEmpresasSAS_debug("-----------------"+obj.getBase64());

            respuesta = cv.verificarFirma(certificado, leerArchivo());

            Logear.logEmpresasSAS_debug(respuesta);

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
}
