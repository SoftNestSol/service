package ro.unibuc.booking.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
// Import SLF4J for better logging (add dependency if needed)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream; // Use InputStream for resource handling

@Configuration
public class FirebaseConfig {

    // Use SLF4J Logger is recommended over System.out.println
    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    // Define the system property key consistently
    private static final String FIREBASE_KEY_PATH_PROPERTY = "firebase.key.path";

    // Keep @Value for the bucket name - this is less likely to change per environment like the key path
    @Value("${firebase.storage.bucket:booking-vtm.firebasestorage.app}")
    private String firebaseStorageBucket;

    @Bean
    public Bucket initFirebase() {
        InputStream serviceAccountStream = null;
        String actualKeyPath = null; // Variable to hold the path being used

        try {
            // --- Directly read the system property ---
            actualKeyPath = System.getProperty(FIREBASE_KEY_PATH_PROPERTY);
            log.info("Attempting Firebase init. Reading system property: '{}'", FIREBASE_KEY_PATH_PROPERTY);

            // --- Check if the property was actually found ---
            if (actualKeyPath == null || actualKeyPath.trim().isEmpty()) {
                log.error("Required system property '{}' for Firebase key path is not set or empty!", FIREBASE_KEY_PATH_PROPERTY);
                throw new IllegalStateException("System property '" + FIREBASE_KEY_PATH_PROPERTY + "' must be set to the Firebase key file path.");
            }

            log.info("Using Firebase key path from system property: {}", actualKeyPath);
            serviceAccountStream = new FileInputStream(actualKeyPath); // Use the path read directly

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .setStorageBucket(firebaseStorageBucket) // Use the @Value injected bucket name
                    .build();

            FirebaseApp firebaseApp;
            if (FirebaseApp.getApps().isEmpty()) {
                firebaseApp = FirebaseApp.initializeApp(options);
                log.info("✅ Firebase Admin SDK initialized successfully.");
            } else {
                // If already initialized (e.g., in some test context scenarios), get the existing app
                firebaseApp = FirebaseApp.getInstance();
                log.warn("Firebase Admin SDK was already initialized. Using existing instance.");
            }

            // Get the bucket using the initialized app
            Bucket bucket = StorageClient.getInstance(firebaseApp).bucket(firebaseStorageBucket);
            if (bucket == null) {
                 log.error("Failed to get Firebase Storage Bucket with name: {}", firebaseStorageBucket);
                 throw new RuntimeException("Could not retrieve bucket '" + firebaseStorageBucket + "' from Firebase Storage.");
            }
            log.info("Successfully obtained Firebase Storage Bucket reference: {}", bucket.getName());
            return bucket;

        } catch (FileNotFoundException e) {
            // Log the path that failed
            log.error("❌ Failed to find Firebase key file at path specified by system property '{}': Path='{}'",
                      FIREBASE_KEY_PATH_PROPERTY, actualKeyPath, e);
            throw new RuntimeException("❌ Failed to initialize Firebase Admin SDK - Key file not found at: " + actualKeyPath, e);
        } catch (IOException e) {
            // Log the path that failed
            log.error("❌ IOException during Firebase initialization (reading key file or initializing SDK). Path='{}'", actualKeyPath, e);
            throw new RuntimeException("❌ Failed to initialize Firebase Admin SDK - IO error for path: " + actualKeyPath, e);
        } catch (IllegalStateException e) {
            // Catch the specific error for missing property
             log.error(e.getMessage()); // Error details already logged
            throw e; // Re-throw
        } catch (Exception e) { // Catch any other unexpected errors
             log.error("❌ An unexpected error occurred during Firebase Admin SDK initialization. Path='{}'", actualKeyPath, e);
            throw new RuntimeException("❌ Failed to initialize Firebase Admin SDK - Unexpected error for path: " + actualKeyPath, e);
        } finally {
            // Ensure the InputStream is closed
            if (serviceAccountStream != null) {
                try {
                    serviceAccountStream.close();
                } catch (IOException e) {
                    log.warn("Failed to close Firebase service account stream.", e);
                }
            }
        }
    }
}