package ro.unibuc.booking.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "bookings")
public class Booking {

    @Id
    private String eventId;

    @Field("artist_id")
    private String artistId;

    @Field("status_contract")
    private ContractStatus statusContract;

    @Field("status_plata")
    private PaymentStatus statusPlata;

    @Field("locatie")
    private Locatie locatie;

    @Field("data")
    private LocalDateTime data;

    @Field("interval_orar")
    private String intervalOrar;

    @Field("client")
    private Client client;

    @Field("pret_event")
    private double pretEvent;

    @Field("suma_achitata")
    private double sumaAchitata;

    @Transient
    public Map<String, Enum<?>> getStatus() {
        Map<String, Enum<?>> statusMap = new HashMap<>();
        statusMap.put("status_contract", statusContract);
        statusMap.put("status_plata", statusPlata);
        return statusMap;
    }

    // Getteri, setteri

    public String getEventId() {
        return eventId;
    }
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    public String getArtistId() {
        return artistId;
    }
    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }
    public ContractStatus getStatusContract() {
        return statusContract;
    }
    public void setStatusContract(ContractStatus statusContract) {
        this.statusContract = statusContract;
    }
    public PaymentStatus getStatusPlata() {
        return statusPlata;
    }
    public void setStatusPlata(PaymentStatus statusPlata) {
        this.statusPlata = statusPlata;
    }
    public Locatie getLocatie() {
        return locatie;
    }
    public void setLocatie(Locatie locatie) {
        this.locatie = locatie;
    }
    public LocalDateTime getData() {
        return data;
    }
    public void setData(LocalDateTime data) {
        this.data = data;
    }
    public String getIntervalOrar() {
        return intervalOrar;
    }
    public void setIntervalOrar(String intervalOrar) {
        this.intervalOrar = intervalOrar;
    }
    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }
    public double getPretEvent() {
        return pretEvent;
    }
    public void setPretEvent(double pretEvent) {
        this.pretEvent = pretEvent;
    }
    public double getSumaAchitata() {
        return sumaAchitata;
    }
    public void setSumaAchitata(double sumaAchitata) {
        this.sumaAchitata = sumaAchitata;
    }
}

// enumuri pentru status

public enum ContractStatus {
    CONFIRMAT,
    ASTEPTARE,
    ANULAT
}

public enum PaymentStatus {
    AVANS,
    INTEGRAL
}