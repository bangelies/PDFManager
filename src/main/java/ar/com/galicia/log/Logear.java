package ar.com.galicia.log;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by l0633615 on 14/09/2017.
 */
public class Logear {


    private static String propertiesPath = "/ibm/bpmLogs/config/log4j.properties";//Para los servidores
    private static Properties p = new Properties();
    private static Logger loggerEmpresasSAS = Logger.getLogger("loggerEmpresasSAS");
    private static boolean isConfigured = true;


    private static void configurarLogger(){
        if(isConfigured){
            try{
                p.load(new FileInputStream(propertiesPath));
                PropertyConfigurator.configure(p);
                isConfigured=false;
            }catch (Exception e){
                System.out.println("Logj4 Error: Error al inicializar log4j");
            }
        }
    }


    /****************************
     * LOG Empresas SAS
     ****************************/

    public static void logEmpresasSAS_info(String value){
        configurarLogger();
        loggerEmpresasSAS.info(value);
    }
    public static void logEmpresasSAS_error(String value){
        configurarLogger();
        loggerEmpresasSAS.error(value);
    }
    public static void logEmpresasSAS_debug(String value){
        configurarLogger();
        loggerEmpresasSAS.debug(value);
    }
}
