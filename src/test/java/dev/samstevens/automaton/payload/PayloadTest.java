package dev.samstevens.automaton.payload;

import org.junit.Test;
import java.time.Instant;
import static org.junit.Assert.*;

public class PayloadTest {

    @Test
    public void testBuilder() {
        Instant now = Instant.now();
        Payload payload = Payload.builder()
            .channel("the-channel")
            .message("Hello, world!")
            .sender("MrUser")
            .type("direct_message")
            .timestamp(now)
            .build();

        assertNotNull(payload);
        assertSame("the-channel", payload.getChannel());
        assertSame("Hello, world!", payload.getMessage());
        assertSame("MrUser", payload.getSender());
        assertSame("direct_message", payload.getType());
        assertSame(now, payload.getTimestamp());
    }

    @Test(expected = NullPointerException.class)
    public void testMessageCannotBeNull() {
        Payload.builder()
            .channel("the-channel")
            .sender("MrUser")
            .type("direct_message")
            .build();
    }
}
