package ro.unibuc.booking.controller;

import ro.unibuc.booking.metrics.MetricsService;
import ro.unibuc.booking.data.ArtistEntity;
import ro.unibuc.booking.service.ArtistService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/artists")
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MetricsService metrics;

    @PostMapping(consumes = {"multipart/form-data"})
    public ArtistEntity createArtist(
            @RequestParam("artist") String artistJson,
            @RequestParam(value = "photos", required = false) MultipartFile[] photos,
            HttpServletRequest httpReq) throws IOException {

        long start = System.nanoTime();
        metrics.incInFlight();
        metrics.incHttpRequests();
        metrics.recordPayloadSize(httpReq.getContentLengthLong());

        try {
            ArtistEntity artist = objectMapper.readValue(artistJson, ArtistEntity.class);
            ArtistEntity created = artistService.createNewArtist(artist, photos);
            metrics.incArtistCreated();
            return created;

        } catch (Exception ex) {
            metrics.incArtistErrors();
            throw ex;

        } finally {
            metrics.recordRequestDuration(start, "POST", "/artists", httpReq.getContentLength());
            metrics.decInFlight();
        }
    }

    @GetMapping("/{id}")
    public ArtistEntity getArtistById(
            @PathVariable String id,
            HttpServletRequest httpReq) {

        long start = System.nanoTime();
        metrics.incInFlight();
        metrics.incHttpRequests();

        try {
            ArtistEntity artist = artistService.getArtistById(id);
            metrics.incArtistRetrieved();
            return artist;

        } catch (Exception ex) {
            metrics.incArtistErrors();
            throw ex;

        } finally {
            metrics.recordRequestDuration(start, "GET", "/artists/{id}", 0);
            metrics.decInFlight();
        }
    }

    @GetMapping("/search")
    public ArtistEntity getArtistByName(
            @RequestParam String name,
            HttpServletRequest httpReq) {

        long start = System.nanoTime();
        metrics.incInFlight();
        metrics.incHttpRequests();

        try {
            ArtistEntity artist = artistService.getArtistByName(name);
            metrics.incArtistRetrieved();
            return artist;

        } catch (Exception ex) {
            metrics.incArtistErrors();
            throw ex;

        } finally {
            metrics.recordRequestDuration(start, "GET", "/artists/search", 0);
            metrics.decInFlight();
        }
    }

    @GetMapping
    public List<ArtistEntity> getAllArtists(HttpServletRequest httpReq) {
        long start = System.nanoTime();
        metrics.incInFlight();
        metrics.incHttpRequests();

        try {
            List<ArtistEntity> list = artistService.getAllArtists();
            metrics.incArtistRetrieved();
            return list;

        } catch (Exception ex) {
            metrics.incArtistErrors();
            throw ex;

        } finally {
            metrics.recordRequestDuration(start, "GET", "/artists", 0);
            metrics.decInFlight();
        }
    }

    @PutMapping("/{id}")
    public ArtistEntity updateArtist(
            @PathVariable String id,
            @RequestParam("artist") String artistJson,
            @RequestParam(value = "photos", required = false) MultipartFile[] photos,
            HttpServletRequest httpReq) throws IOException {

        long start = System.nanoTime();
        metrics.incInFlight();
        metrics.incHttpRequests();
        metrics.recordPayloadSize(httpReq.getContentLengthLong());

        try {
            ArtistEntity updatedArtist = objectMapper.readValue(artistJson, ArtistEntity.class);
            ArtistEntity result = artistService.updateArtist(id, updatedArtist, photos);
            metrics.incArtistUpdated();
            return result;

        } catch (Exception ex) {
            metrics.incArtistErrors();
            throw ex;

        } finally {
            metrics.recordRequestDuration(start, "PUT", "/artists/{id}", httpReq.getContentLength());
            metrics.decInFlight();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteArtist(
            @PathVariable String id,
            HttpServletRequest httpReq) {

        long start = System.nanoTime();
        metrics.incInFlight();
        metrics.incHttpRequests();

        try {
            artistService.deleteArtistById(id);
            metrics.incArtistDeleted();

        } catch (Exception ex) {
            metrics.incArtistErrors();
            throw ex;

        } finally {
            metrics.recordRequestDuration(start, "DELETE", "/artists/{id}", 0);
            metrics.decInFlight();
        }
    }

    @GetMapping("/price-under")
    public List<ArtistEntity> getArtistsWithPriceUnder(
            @RequestParam String key,
            @RequestParam Number maxPrice,
            HttpServletRequest httpReq) {

        long start = System.nanoTime();
        metrics.incInFlight();
        metrics.incHttpRequests();

        try {
            List<ArtistEntity> list = artistService.getArtistsWithPriceBelow(key, maxPrice);
            metrics.incArtistPriceSearch();
            return list;

        } catch (Exception ex) {
            metrics.incArtistErrors();
            throw ex;

        } finally {
            metrics.recordRequestDuration(start, "GET", "/artists/price-under", 0);
            metrics.decInFlight();
        }
    }
}
