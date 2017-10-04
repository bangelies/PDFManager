package ar.com.galicia.verificar;

/**
 * Created by l0633615 on 03/10/2017.
 */
public class EstadoDocumento {

    private boolean integridad;
    private boolean validez;
    private String nombreFirma;
    private String firmaInvalida;
    private boolean hayFirmas;

    public EstadoDocumento() {
    }

    public EstadoDocumento(boolean integridad, boolean validez) {
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


    public String getFirmaInvalida() {

        return firmaInvalida;
    }

    public void setFirmaInvalida(String firmaInvalida) {

        this.firmaInvalida = firmaInvalida;
    }

    public boolean isHayFirmas() {
        return hayFirmas;
    }

    public void setHayFirmas(boolean hayFirmas) {
        this.hayFirmas = hayFirmas;
    }
}
