import ar.com.galicia.log.Logear;
import ar.com.galicia.verificar.CertificateValidation;
import ar.com.galicia.verificar.PDFBase64;
import ar.com.galicia.verificar.Respuesta;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        Logear.logEmpresasSAS_debug("*****************************************************************************************************");
        String certificado = "/ibm/bpmLogs/acraizra.crt";
        CertificateValidation cv = new CertificateValidation();
        try {
            ObjectMapper mapper = new ObjectMapper();
            PDFBase64 obj = mapper.readValue(req.getParameter("base64"), PDFBase64.class);

            Logear.logEmpresasSAS_debug("-----------------"+obj.getBase64());

            Respuesta respuesta = new Respuesta();

            respuesta.setEstadoPdf(cv.verificarFirma(certificado,obj.getBase64()));


            //http://desabpmpc01.bancogalicia.com.ar:9080/pdfverify/verificarFirma?base64={"base64" : ""}
            //Object to JSON in String
            String jsonInString = mapper.writeValueAsString(respuesta);


            resp.getWriter().write("JsonService POST " + jsonInString);

        } catch (Exception e) {
            e.printStackTrace();
            Logear.logEmpresasSAS_debug("Error al verificar PDF");
        }


        Logear.logEmpresasSAS_debug("*****************************************************************************************************");


    }
}
