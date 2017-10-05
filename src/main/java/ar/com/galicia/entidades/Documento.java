package ar.com.galicia.entidades;

import java.util.List;

/**
 * Created by l0633615 on 05/10/2017.
 */
public class Documento {
    private String nombrePDF;
    private boolean hayFirmas;
    private List<Firma> firmas;

    public Documento() {
    }

    public boolean isHayFirmas() {
        return hayFirmas;
    }

    public void setHayFirmas(boolean hayFirmas) {
        this.hayFirmas = hayFirmas;
    }

    public List<Firma> getFirmas() {
        return firmas;
    }

    public void setFirmas(List<Firma> firmas) {
        this.firmas = firmas;
    }

    public String getNombrePDF() {
        return nombrePDF;
    }

    public void setNombrePDF(String nombrePDF) {
        this.nombrePDF = nombrePDF;
    }
}
