package ro.unibuc.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ro.unibuc.booking.dto.Greeting;
import ro.unibuc.booking.service.GreetingsService;

import java.time.Duration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Tag("IntegrationTest")
public class GreetingsControllerIntegrationTest {

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.5")
            .withExposedPorts(27017)
            .withEnv("MONGO_INITDB_ROOT_USERNAME", "root")
            .withEnv("MONGO_INITDB_ROOT_PASSWORD", "example")
            .withEnv("MONGO_INITDB_DATABASE", "testdb")
            .withCommand("--auth")
            .withStartupTimeout(Duration.ofSeconds(60)); // Increase startup timeout

    @BeforeAll
    public static void setUp() throws Exception {
        mongoDBContainer.start();
        // Manually initialize the replica set if needed
        ExecResult result = mongoDBContainer.execInContainer("mongo", "--eval", "rs.initiate()");
        System.out.println("Replica set initiation output: " + result.getStdout());
    }

    @AfterAll
    public static void tearDown() {
        mongoDBContainer.stop();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        final String MONGO_URL = "mongodb://root:example@localhost:";
        final String PORT = String.valueOf(mongoDBContainer.getMappedPort(27017));
        registry.add("mongodb.connection.url", () -> MONGO_URL + PORT);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GreetingsService greetingsService;

    @BeforeEach
    public void cleanUpAndAddTestData() {
        greetingsService.deleteAllGreetings();
        
        Greeting greeting1 = new Greeting("1", "Hello 1");
        Greeting greeting2 = new Greeting("2", "Hello 2");

        greetingsService.saveGreeting(greeting1);
        greetingsService.saveGreeting(greeting2);
    }

    @Test
    public void testGetAllGreetings() throws Exception {
        mockMvc.perform(get("/greetings"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].content").value("Hello 1"))
            .andExpect(jsonPath("$[1].content").value("Hello 2"));
    }

    @Test
    public void testCreateGreeting() throws Exception {
        Greeting greeting = new Greeting("3", "Hello New");

        mockMvc.perform(post("/greetings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(greeting)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("3"))
                .andExpect(jsonPath("$.content").value("Hello New"));

        mockMvc.perform(get("/greetings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    public void testUpdateGreeting() throws Exception {
        Greeting greeting = new Greeting("1", "Hello Updated");

        mockMvc.perform(put("/greetings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(greeting)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.content").value("Hello Updated"));

        mockMvc.perform(get("/greetings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("Hello Updated"))
                .andExpect(jsonPath("$[1].content").value("Hello 2"));
    }

    @Test
    public void testDeleteGreeting() throws Exception {

        mockMvc.perform(delete("/greetings/1"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/greetings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("Hello 2"));
    }
}
