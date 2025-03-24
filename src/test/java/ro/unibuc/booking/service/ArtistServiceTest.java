package ro.unibuc.booking.service;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ro.unibuc.booking.data.ArtistEntity;
import ro.unibuc.booking.data.ArtistRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
class ArtistServicePhotoUploadTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private Bucket bucket;

    @InjectMocks
    private ArtistService artistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testCreateNewArtist_withValidPhoto() throws Exception {
        
        ArtistEntity artist = new ArtistEntity();
        artist.setName("Test Artist");

        MockMultipartFile validPhoto = new MockMultipartFile(
                "photos",
                "test.jpg",
                "image/jpeg",
                "dummy image data".getBytes()
        );
        MultipartFile[] photos = new MultipartFile[] { validPhoto };

        when(bucket.getName()).thenReturn("test-bucket");
        Blob blob = mock(Blob.class);
        when(bucket.create(anyString(), any(byte[].class), anyString())).thenReturn(blob);

        when(artistRepository.save(any(ArtistEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArtistEntity savedArtist = artistService.createNewArtist(artist, photos);


        assertNotNull(savedArtist.getPhotos(), "Photos list should not be null");
        assertEquals(1, savedArtist.getPhotos().size(), "There should be one photo URL");
        String uploadedUrl = savedArtist.getPhotos().get(0);
        assertTrue(uploadedUrl.startsWith("https://storage.googleapis.com/test-bucket/"),
                "The URL should start with the expected bucket URL prefix");
    }

    @Test
    void testCreateNewArtist_withInvalidPhoto() {

        ArtistEntity artist = new ArtistEntity();
        artist.setName("Test Artist");

        MockMultipartFile invalidPhoto = new MockMultipartFile(
                "photos",
                "test.gif",
                "image/gif",
                "dummy image data".getBytes()
        );
        MultipartFile[] photos = new MultipartFile[] { invalidPhoto };

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            artistService.createNewArtist(artist, photos);
        });
        assertTrue(exception.getMessage().contains("Invalid file extension"),
                "Exception message should indicate an invalid file extension");
    }
}
