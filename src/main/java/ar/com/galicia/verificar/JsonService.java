package ar.com.galicia.verificar;

import ar.com.galicia.config.Propiedades;
import ar.com.galicia.entidades.Documento;
import ar.com.galicia.entidades.Firma;
import ar.com.galicia.log.Logear;
import ar.com.galicia.verificar.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.bouncycastle.util.encoders.Encoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by l0633615 on 12/09/2017.
 */

@WebServlet(name = "pdf", urlPatterns = "/verificarFirma")


public class JsonService extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write("ar.com.galicia.verificar.JsonService GET");

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Logear.logEmpresasSAS_debug("*****************************************************************************************************");
        //String base64 = leerArchivo();




        ObjectMapper mapper = new ObjectMapper();

        String uuid = UUID.randomUUID().toString();
        String jsonInString="";

        String pdfPadre= Propiedades.getPropiedadesValor("docPath")+"tmpPlancheta_"+uuid+".pdf";
        String pdfHijo=Propiedades.getPropiedadesValor("docPath")+"tmpEstatuto_"+uuid+".pdf";

        String base64=IOUtils.toString(req.getReader());
        //Logear.logEmpresasSAS_debug(base64);

        try {

            FileUtils.writeByteArrayToFile(new File(pdfPadre), decode(base64));


            //Verifico las firmas
            List<Documento> resultadoDelAnalisis=null;

            List<String> documentosParaAnalizar = new ArrayList<String>();
            documentosParaAnalizar.add(pdfPadre);


            try {
                ExtractEmbeddedFiles eef = new ExtractEmbeddedFiles(pdfHijo);
                if(eef.extraerAdjuntos(pdfPadre)){
                    documentosParaAnalizar.add(pdfHijo);
                }
                resultadoDelAnalisis = verifcarDocumentos(documentosParaAnalizar);
                //Object to JSON in String
                jsonInString = mapper.writeValueAsString(resultadoDelAnalisis);
            }catch(Exception e){
                Logear.logEmpresasSAS_error("Documento no valido");
                jsonInString="Documento no valido";
            }
            // -------------------------------- HASTA ACA ------------------------------------

            Logear.logEmpresasSAS_debug("Resultado final:"+jsonInString);

            Logear.logEmpresasSAS_debug(jsonInString);

            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(jsonInString);

        } catch (Exception e) {
            e.printStackTrace();
            Logear.logEmpresasSAS_debug("Error al verificar PDF");
        }


        Logear.logEmpresasSAS_debug("*****************************************************************************************************");
    }
    private static  List<Documento> verifcarDocumentos(List<String> documentosParaAnalizar){

        List<Documento> documentosAnalizados = new ArrayList<Documento>();
        try {
            for (String pathDocumento: documentosParaAnalizar) {
                CertificateValidation cv = new CertificateValidation();
                documentosAnalizados.add(cv.verificarFirmaFilePath(pathDocumento,"cer"));
            }
        }catch(Exception e){
            System.out.println("---> El documento no tiene firmas" );
        }
        return documentosAnalizados;
    }

    private byte[] decode(String data)
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
