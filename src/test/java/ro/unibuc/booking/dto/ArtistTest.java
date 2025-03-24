package ro.unibuc.booking.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ArtistTest {

    Artist artist = new Artist("1", Arrays.asList("photo1.jpg", "photo2.jpg"), "Test Description", new HashMap<>(), Arrays.asList("event1.jpg", "event2.mp4"));

    @Test
    void test_photos(){
        List<String> photos = Arrays.asList("photo1.jpg", "photo2.jpg");
        Assertions.assertEquals(photos, artist.getPhotos());
    }

    @Test
    void test_description(){
        Assertions.assertEquals("Test Description", artist.getDescription());
    }

    @Test
    void test_events(){
        List<String> events = Arrays.asList("event1.jpg", "event2.mp4");
        Assertions.assertEquals(events, artist.getEventsContent());
    }



}