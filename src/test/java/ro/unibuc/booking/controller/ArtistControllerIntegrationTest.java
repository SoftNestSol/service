package ro.unibuc.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ro.unibuc.booking.data.ArtistEntity;
import ro.unibuc.booking.service.ArtistService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Tag("IntegrationTest")
public class ArtistControllerIntegrationTest {

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.20")
            .withExposedPorts(27017)
            .withSharding();

     @BeforeAll
    public static void setUp() {
        mongoDBContainer.start();
    }

    @AfterAll
    public static void tearDown() {
        mongoDBContainer.stop();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        final String MONGO_URL = "mongodb://host.docker.internal:";
        final String PORT = String.valueOf(mongoDBContainer.getMappedPort(27017));
        registry.add("mongodb.connection.url", () -> MONGO_URL + PORT);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void addTestData() throws Exception {
        ArtistEntity artist1 = new ArtistEntity("1", "Artist 1", new ArrayList<>(), "Description 1", Map.of("default", 100.0), new ArrayList<>());
        ArtistEntity artist2 = new ArtistEntity("2", "Artist 2", new ArrayList<>(), "Description 2", Map.of("default", 200.0), new ArrayList<>());

        // Save the artists using the service; here we pass an empty array for photos in the request
        artistService.createNewArtist(artist1, new MockMultipartFile[]{});
        artistService.createNewArtist(artist2, new MockMultipartFile[]{});
    }

    @Test
    public void testGetAllArtists() throws Exception {
        mockMvc.perform(get("/artists"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Artist 1"))
            .andExpect(jsonPath("$[1].name").value("Artist 2"));
    }

    @Test
    public void testCreateArtist() throws Exception {
        ArtistEntity newArtist = new ArtistEntity("3", "Artist New", new ArrayList<>(), "New Artist Description", Map.of("default", 150.0), new ArrayList<>());

        // Create multipart files: one for the JSON part and one as a dummy photo.
        String artistPart = objectMapper.writeValueAsString(newArtist);
        MockMultipartFile photo = new MockMultipartFile("photos", "photo.jpg", "image/jpeg", "dummy image content".getBytes());

        mockMvc.perform(multipart("/artists")
                .file(photo)
                .param("artist", artistPart))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("3"))
                .andExpect(jsonPath("$.name").value("Artist New"))
                .andExpect(jsonPath("$.prices.default").value(150.0));

        mockMvc.perform(get("/artists"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    public void testGetArtistById() throws Exception {
        mockMvc.perform(get("/artists/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("Artist 1"))
            .andExpect(jsonPath("$.prices.default").value(100.0));
    }

    @Test
    public void testGetArtistByName() throws Exception {
        mockMvc.perform(get("/artists/search")
                .param("name", "Artist 1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("Artist 1"))
            .andExpect(jsonPath("$.prices.default").value(100.0));
    }

    @Test
    public void testUpdateArtist() throws Exception {
        ArtistEntity updatedArtist = new ArtistEntity("1", "Artist Updated", new ArrayList<>(), "Updated Description", Map.of("default", 120.0), new ArrayList<>());
        String updatedArtistJson = objectMapper.writeValueAsString(updatedArtist);

        MockMultipartFile updatedPhoto = new MockMultipartFile("photos", "updated.jpg", MediaType.IMAGE_JPEG_VALUE, "updated image content".getBytes());

        mockMvc.perform(multipart("/artists/1")
                .file(updatedPhoto)
                .param("artist",updatedArtistJson)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Artist Updated"))
                .andExpect(jsonPath("$.prices.default").value(120.0));

        // Verify update via GET.
        mockMvc.perform(get("/artists/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Artist Updated"))
                .andExpect(jsonPath("$.prices.default").value(120.0));
    }

    @Test
    public void testDeleteArtist() throws Exception {
        mockMvc.perform(delete("/artists/1"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/artists"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value("2"));
    }


}