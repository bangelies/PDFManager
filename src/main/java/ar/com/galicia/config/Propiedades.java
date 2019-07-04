package ar.com.galicia.config;


import java.io.FileInputStream;
import java.util.Properties;


public class Propiedades {
    private static Properties prop = new Properties();

    private static String propertiesPath ="/was/JavaAppsConfig/coe/config/config.properties"; //Para los servidores
//    private static String propertiesPath ="C:\\CoE\\PDFManager\\config\\config.properties"; //Para local


    public static String getPropiedadesValor(String key) {

        try {
            prop.load(new FileInputStream(propertiesPath));
        } catch (Exception e) {
            System.out.println("Error: getPropiedadesValor");
        }
        return prop.getProperty(key);
    }


}