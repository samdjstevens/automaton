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
            .isMention(true)
            .build();

        assertNotNull(payload);
        assertSame("the-channel", payload.getChannel());
        assertSame("Hello, world!", payload.getMessage());
        assertSame("MrUser", payload.getSender());
        assertSame("direct_message", payload.getType());
        assertSame(now, payload.getTimestamp());
        assertSame(true, payload.isMention());
    }

    @Test(expected = NullPointerException.class)
    public void testMessageCannotBeExcluded() {
        Payload.builder()
            .channel("the-channel")
            .sender("MrUser")
            .type("direct_message")
            .build();
    }

    @Test(expected = NullPointerException.class)
    public void testMessageCannotBeNull() {
        Payload.builder()
                .channel("the-channel")
                .sender("MrUser")
                .type("direct_message")
                .message(null)
                .build();
    }

    @Test
    public void testIsMentionIsFalseByDefault() {
        Payload payload = Payload.builder().message("test").build();

        assertFalse(payload.isMention());
    }
}
