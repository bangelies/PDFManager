package ar.com.galicia.verificar;


import ar.com.galicia.log.Logear;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        String respuesta = "";
        String certificado = "/ibm/bpmLogs/acraizra.crt";
        CertificateValidation cv = new CertificateValidation();
        try {
            ObjectMapper mapper = new ObjectMapper();
            PDFBase64 obj = mapper.readValue(leerArchivo(), PDFBase64.class);

            Logear.logEmpresasSAS_debug("-----------------"+obj.getBase64());

            respuesta = cv.verificarFirma(certificado, obj.getBase64());


        } catch (Exception e) {
            e.printStackTrace();
            Logear.logEmpresasSAS_debug("Error al verificar PDF");
        }

    }
    private static String leerArchivo() {

        BufferedReader br = null;
        FileReader fr = null;
        String salida="";
        try {

            //br = new BufferedReader(new FileReader(FILENAME));
            fr = new FileReader("E:\\SAS\\PDFs\\json.txt");
            br = new BufferedReader(fr);

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                salida+=sCurrentLine;
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
        return salida;

    }
}
