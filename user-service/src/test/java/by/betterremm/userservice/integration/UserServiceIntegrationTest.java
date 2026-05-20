package by.betterremm.userservice.integration;

import by.betterremm.userservice.dto.request.PaymentCardCreateRequest;
import by.betterremm.userservice.dto.request.UserCreateRequest;
import by.betterremm.userservice.dto.request.UserUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("user_service_test").withUsername("test").withPassword("test");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("DB_URL", postgres::getJdbcUrl);
        r.add("DB_USERNAME", postgres::getUsername);
        r.add("DB_PASSWORD", postgres::getPassword);
        r.add("REDIS_HOST", redis::getHost);
        r.add("REDIS_PORT", () -> redis.getMappedPort(6379).toString());
    }

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void userFlow() throws Exception {
        var userReq = new UserCreateRequest("Alice", "Smith", LocalDate.of(1995, 5, 15), "alice@example.com");
        var create = mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userReq)))
                .andExpect(status().isCreated()).andReturn();
        long userId = objectMapper.readTree(create.getResponse().getContentAsString()).get("id").asLong();

        var cardReq = new PaymentCardCreateRequest("4111-1111-1111-1111", "Alice", LocalDate.now().plusYears(2));
        mockMvc.perform(post("/api/v1/users/{id}/cards", userId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardReq)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/users/{id}", userId)).andExpect(status().isOk()).andExpect(jsonPath("$.cards.length()").value(1));

        var update = new UserUpdateRequest("Alicia", "Smith", LocalDate.of(1995, 5, 15), "alicia@example.com");
        mockMvc.perform(put("/api/v1/users/{id}", userId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Alicia"));

        mockMvc.perform(delete("/api/v1/users/{id}", userId)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/v1/users/{id}", userId)).andExpect(status().isNotFound());
    }
}
