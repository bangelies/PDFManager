package ar.com.galicia.verificar;

/**
 * Created by l0633615 on 03/10/2017.
 */
public class EstadoFirma {

    private boolean integridad;
    private boolean validez;
    private boolean tieneFirmas;
    private String nombreFirma;
    private String firmaInvalida;

    public EstadoFirma() {
    }

    public EstadoFirma(boolean integridad, boolean validez) {
        this.integridad = integridad;
        this.validez = validez;
    }

    public String getNombreFirma() {
        return nombreFirma;
    }
    public void setNombreFirma(String nombreFirma) {
        this.nombreFirma = nombreFirma;
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

    public boolean isTieneFirmas() {
        return tieneFirmas;
    }

    public void setTieneFirmas(boolean tieneFirmas) {
        this.tieneFirmas = tieneFirmas;
    }

    public String getFirmaInvalida() {
        return firmaInvalida;
    }

    public void setFirmaInvalida(String firmaInvalida) {
        this.firmaInvalida = firmaInvalida;
    }
}
