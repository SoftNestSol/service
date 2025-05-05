package ro.unibuc.booking.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MetricsService {

    private final MeterRegistry registry;

    private final Counter artistCreatedCounter;
    private final Counter artistRetrievedCounter;
    private final Counter artistUpdatedCounter;
    private final Counter artistDeletedCounter;
    private final Counter artistPriceSearchCounter;
    private final Counter artistErrorCounter;

    private final Counter httpRequestsCounter;
    private final Gauge   inFlightRequestsGauge;
    private final DistributionSummary payloadSizeSummary;

    private final AtomicInteger inFlight = new AtomicInteger(0);

    public MetricsService(MeterRegistry registry) {
        this.registry = registry;

        artistCreatedCounter = Counter.builder("artist.created.count")
            .description("Number of artists created")
            .register(registry);

        artistRetrievedCounter = Counter.builder("artist.retrieved.count")
            .description("Number of times an artist was retrieved")
            .register(registry);

        artistUpdatedCounter = Counter.builder("artist.updated.count")
            .description("Number of artists updated")
            .register(registry);

        artistDeletedCounter = Counter.builder("artist.deleted.count")
            .description("Number of artists deleted")
            .register(registry);

        artistPriceSearchCounter = Counter.builder("artist.price_search.count")
            .description("Number of price-under searches")
            .register(registry);

        artistErrorCounter = Counter.builder("artist.errors.count")
            .description("Number of errors in artist endpoints")
            .register(registry);

        httpRequestsCounter = Counter.builder("http.requests.count")
            .description("Total HTTP requests")
            .register(registry);

        inFlightRequestsGauge = Gauge.builder("http.inflight.requests", inFlight, AtomicInteger::get)
            .description("Current in-flight HTTP requests")
            .register(registry);

        payloadSizeSummary = DistributionSummary.builder("http.payload.size")
            .description("Distribution of HTTP request payload sizes in bytes")
            .baseUnit("bytes")
            .register(registry);
    }

    public void incArtistCreated()    { artistCreatedCounter.increment(); }
    public void incArtistRetrieved()  { artistRetrievedCounter.increment(); }
    public void incArtistUpdated()    { artistUpdatedCounter.increment(); }
    public void incArtistDeleted()    { artistDeletedCounter.increment(); }
    public void incArtistPriceSearch(){ artistPriceSearchCounter.increment(); }
    public void incArtistErrors()     { artistErrorCounter.increment(); }

    public void incHttpRequests()     { httpRequestsCounter.increment(); }
    public void incInFlight()         { inFlight.incrementAndGet(); }
    public void decInFlight()         { inFlight.decrementAndGet(); }
    public void recordPayloadSize(long bytes) { payloadSizeSummary.record(bytes); }

    /**
     * Record an HTTP request duration with tags.
     *
     * @param startNano   System.nanoTime() at the start of the request
     * @param method      HTTP method (e.g. "GET", "POST")
     * @param path        Route path template (e.g. "/artists/{id}")
     * @param statusCode  HTTP status code returned
     */
    public void recordRequestDuration(long startNano,
                                      String method,
                                      String path,
                                      int statusCode) {
        long duration = System.nanoTime() - startNano;
        registry.timer("http.requests.duration",
                "method", method,
                "path", path,
                "status", String.valueOf(statusCode)
        ).record(duration, TimeUnit.NANOSECONDS);
    }
}
