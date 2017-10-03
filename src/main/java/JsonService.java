import ar.com.galicia.config.Propiedades;
import ar.com.galicia.log.Logear;
import ar.com.galicia.verificar.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
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
import java.util.Date;
import java.util.UUID;

/**
 * Created by l0633615 on 12/09/2017.
 */

@WebServlet(name = "pdf", urlPatterns = "/verificarFirma")


public class JsonService extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write("JsonService GET");

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Logear.logEmpresasSAS_debug("*****************************************************************************************************");
        //String base64 = leerArchivo();


        Respuesta respuesta = new Respuesta();
        int estadoFirma =1;
        int estadoArchivo=1;
        ObjectMapper mapper = new ObjectMapper();


        String uuid = UUID.randomUUID().toString();
        String pdfPadre= Propiedades.pdfExtractor+"tmpPadre_"+uuid+".pdf";
        String pdfHijo=Propiedades.pdfExtractor+"tmpHijo_"+uuid+".pdf";

        try {
            PDFBase64 obj = mapper.readValue(req.getParameter("base64"), PDFBase64.class);




            //Verificar PADRE
            Logear.logEmpresasSAS_debug("PDF Padre inicio ----------");
            CertificateValidation verificarPadre = new CertificateValidation();
            estadoFirma = verificarPadre.verificarFirmaBase64(obj.getBase64());
            Logear.logEmpresasSAS_debug("pdfEstadoGeneral "+estadoFirma);

            FileUtils.writeByteArrayToFile(new File(pdfPadre), decode(obj.getBase64()));
            Logear.logEmpresasSAS_debug("PDF Padre fin ----------");
            //Verificar HIJO
            if(estadoFirma==1){
                Logear.logEmpresasSAS_debug("PDF Hijo inicio ----------");
                ExtractEmbeddedFiles eef = new ExtractEmbeddedFiles(pdfHijo);
                boolean tieneAdjuntos = eef.extraerAdjuntos(pdfPadre);

                if(tieneAdjuntos){
                    CertificateValidation verificarHijo = new CertificateValidation();
                    estadoFirma= verificarHijo.verificarFirmaFilePath(pdfHijo);
                    Logear.logEmpresasSAS_debug("pdfEstadoGeneral "+estadoFirma);
                }else{
                    estadoArchivo= 2;
                }



                Logear.logEmpresasSAS_debug("PDF Hijo fin ----------");

            }else{
                //System.out.println(respuesta);
                Logear.logEmpresasSAS_debug("Error al verificar firmas");
            }


            respuesta.setEstadoArchivo(estadoArchivo);
            respuesta.setEstadoFirma(estadoFirma);
            //http://desabpmpc01.bancogalicia.com.ar:9080/pdfverify/verificarFirma?base64={"base64" : ""}
            //Object to JSON in String
            String jsonInString = mapper.writeValueAsString(respuesta);

            Logear.logEmpresasSAS_debug(jsonInString);
            resp.getWriter().write(jsonInString);

        } catch (Exception e) {
            e.printStackTrace();
            Logear.logEmpresasSAS_debug("Error al verificar PDF");
        }


        Logear.logEmpresasSAS_debug("*****************************************************************************************************");
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
