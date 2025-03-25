package ro.unibuc.booking.data;

import org.springframework.data.mongodb.core.mapping.Field;

public class Client {

    @Field("nume")
    private String nume;

    @Field("prenume")
    private String prenume;

    @Field("telefon")
    private String telefon;

    @Field("email")
    private String email;

    public Client() {
    }

    public Client(String nume, String prenume, String telefon, String email) {
        this.nume = nume;
        this.prenume = prenume;
        this.telefon = telefon;
        this.email = email;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}