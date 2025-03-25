package ro.unibuc.booking.data;

import org.springframework.data.mongodb.core.mapping.Field;

public class Locatie {

    @Field("locatie_oras")
    private String locatieOras;

    @Field("locatie_x")
    private Double locatieX;

    @Field("locatie_y")
    private Double locatieY;

    public Locatie() {
    }

    public Locatie(String locatieOras, Double locatieX, Double locatieY) {
        this.locatieOras = locatieOras;
        this.locatieX = locatieX;
        this.locatieY = locatieY;
    }

    public String getLocatieOras() {
        return locatieOras;
    }

    public void setLocatieOras(String locatieOras) {
        this.locatieOras = locatieOras;
    }

    public Double getlocatieX() {
        return locatieX;
    }

    public void setlocatieX(Double locatieX) {
        this.locatieX = locatieX;
    }

    public Double getlocatieY() {
        return locatieY;
    }

    public void setlocatieY(Double locatieY) {
        this.locatieY = locatieY;
    }
}