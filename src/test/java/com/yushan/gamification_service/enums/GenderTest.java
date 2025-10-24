// language: java
package com.yushan.gamification_service.enums;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GenderTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void fromString_null_returnsUnknown() {
        assertEquals(Gender.UNKNOWN, Gender.fromString(null));
    }

    @Test
    void fromString_caseInsensitive() {
        assertEquals(Gender.MALE, Gender.fromString("male"));
        assertEquals(Gender.FEMALE, Gender.fromString("FeMaLe"));
        assertEquals(Gender.UNKNOWN, Gender.fromString("UnKnOwN"));
    }

    @Test
    void fromString_invalid_returnsUnknown() {
        assertEquals(Gender.UNKNOWN, Gender.fromString("not_a_gender"));
    }

    @Test
    void toString_and_jsonSerialization() throws Exception {
        // toString returns enum name
        assertEquals("MALE", Gender.MALE.toString());

        // Jackson serialization uses @JsonValue
        String json = mapper.writeValueAsString(Gender.FEMALE);
        assertEquals("\"FEMALE\"", json);

        // Jackson deserialization uses @JsonCreator (case-insensitive)
        Gender deserialized1 = mapper.readValue("\"male\"", Gender.class);
        assertEquals(Gender.MALE, deserialized1);

        Gender deserialized2 = mapper.readValue("null", Gender.class);
        assertNull(deserialized2); // Jackson will map JSON null to Java null
    }
}
