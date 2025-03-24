package ro.unibuc.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.unibuc.booking.data.ArtistEntity;
import ro.unibuc.booking.service.ArtistService;
import java.lang.reflect.Field;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ArtistControllerTest {

    @Mock
    private ArtistService artistService;

    @InjectMocks
    private ArtistController artistController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(artistController).build();
    
        Field mapperField = ArtistController.class.getDeclaredField("objectMapper");
        mapperField.setAccessible(true);
        mapperField.set(artistController, objectMapper);
    }
    

    @Test
    void test_createArtist_withValidPhoto() throws Exception {
        ArtistEntity artist = new ArtistEntity();
        artist.setName("Test Artist");
        artist.setPhotos(Arrays.asList("https://storage.googleapis.com/test-bucket/photo.jpg"));

        String artistJson = objectMapper.writeValueAsString(artist);

        MockMultipartFile photoPart = new MockMultipartFile(
                "photos", "photo.jpg", "image/jpeg", "dummy image data".getBytes());

        when(artistService.createNewArtist(any(ArtistEntity.class), any()))
                .thenReturn(artist);

        mockMvc.perform(multipart("/artists")
                        .file(photoPart)
                        .param("artist", artistJson))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Test Artist"))
               .andExpect(jsonPath("$.photos[0]").value("https://storage.googleapis.com/test-bucket/photo.jpg"));
    }

    /* 
    @Test
    void test_createArtist_withInvalidPhoto() throws Exception {
        ArtistEntity artist = new ArtistEntity();
        artist.setName("Test Artist");

        String artistJson = objectMapper.writeValueAsString(artist);

        MockMultipartFile photoPart = new MockMultipartFile(
                "photos", "photo.gif", "image/gif", "dummy image data".getBytes());

        when(artistService.createNewArtist(any(ArtistEntity.class), any()))
                .thenThrow(new IllegalArgumentException("Invalid file extension: photo.gif"));

        mockMvc.perform(multipart("/artists")
                        .file(photoPart)
                        .param("artist", artistJson))
               .andExpect(status().isInternalServerError());
    }
               */
}
