package ar.com.galicia.entidades;

/**
 * Created by l0633615 on 05/10/2017.
 */
public class Firma {

    private boolean integridad;
    private boolean validez;
    private String nombreFirma;

    public Firma() {
    }

    public boolean isIntegridad() {
        return integridad;
    }

    public void setIntegridad(boolean integridad) {
        this.integridad = integridad;
    }

    public boolean isValidez() {
        return validez;
    }

    public void setValidez(boolean validez) {
        this.validez = validez;
    }

    public String getNombreFirma() {
        return nombreFirma;
    }

    public void setNombreFirma(String nombreFirma) {
        this.nombreFirma = nombreFirma;
    }
}
