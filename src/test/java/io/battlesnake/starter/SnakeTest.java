package io.battlesnake.starter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.nettogrof.battlesnake.snakes.Snake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SnakeTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static {
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    private Snake.Handler handler;

    @BeforeEach
    void setUp() {
        handler = new Snake.Handler();
    }

    @Test
    void pingTest() throws IOException {
        final Map<String, String> response = handler.ping();
        assertEquals("{}", response.toString());
    }

    @Test
    void startTest() throws IOException {
    	final JsonNode startRequest = OBJECT_MAPPER.readTree("{}");
        final Map<String, String> response = handler.start(startRequest);
        assertEquals("#ff00ff", response.get("color"));
    }

    @Test
    void moveTest() throws IOException {
    	final JsonNode moveRequest = OBJECT_MAPPER.readTree("{}");
        final Map<String, String> response = handler.move(moveRequest);
        assertEquals("right", response.get("move"));
    }

    @Test
    void endTest() throws IOException {
    	final JsonNode endRequest = OBJECT_MAPPER.readTree("{}");
        final Map<String, String> response = handler.end(endRequest);
        assertEquals(0, response.size());
    }
}