package ro.unibuc.booking.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.key.path:/secrets/firebase-key}")
    private String firebaseKeyPath;

    @Value("${firebase.storage.bucket:booking-vtm.firebasestorage.app}")
    private String firebaseStorageBucket;

    @Bean
    public Bucket initFirebase() {
        try {
            System.out.println("Using Firebase key path: " + firebaseKeyPath);

            FileInputStream serviceAccount = new FileInputStream(firebaseKeyPath);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket(firebaseStorageBucket)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            System.out.println("✅ Firebase Admin SDK initialized successfully.");

            return StorageClient.getInstance(FirebaseApp.getInstance()).bucket(firebaseStorageBucket);

        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to initialize Firebase Admin SDK", e);
        }
    }
}
