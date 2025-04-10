package ro.unibuc.booking.data;
import org.springframework.data.annotation.Id;

public class ContactInfoEntity {

    @Id
    private String id;
    private  String phoneNumber;
    private  String email;  
    private  String address;
    private  String instagram;
    private  String facebook;
    private  String youtube;
    private  String tiktok;


    public ContactInfoEntity() {
    }

    public ContactInfoEntity(String phoneNumber, String email, String address, String instagram, String facebook, String youtube, String tiktok) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.instagram = instagram;
        this.facebook = facebook;
        this.youtube = youtube;
        this.tiktok = tiktok;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getTiktok() {
        return tiktok;
    }

    public void setTiktok(String tiktok) {
        this.tiktok = tiktok;
    }

    @Override 
    public String toString() {
        return String.format(
                "ContactInfo[phoneNumber='%s', email='%s', address='%s', instagram='%s', facebook='%s', youtube='%s', tiktok='%s']",
                phoneNumber, email, address, instagram, facebook, youtube, tiktok);
    }
}