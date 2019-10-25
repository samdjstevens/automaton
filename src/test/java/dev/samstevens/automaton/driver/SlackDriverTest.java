package dev.samstevens.automaton.driver;

import dev.samstevens.automaton.message.MessageSender;
import dev.samstevens.automaton.payload.PayloadRequestTransformer;
import org.junit.Test;
import static org.junit.Assert.*;

public class SlackDriverTest {

    @Test
    public void testGetPayloadRequestTransformer() {
        Driver slackDriver = getSlackDriver();
        PayloadRequestTransformer transformer = slackDriver.getPayloadRequestTransformer();

        assertNotNull(transformer);
    }

    @Test
    public void testCreateMessageSender() {
        Driver slackDriver = getSlackDriver();

        MessageSender messageSender = slackDriver.createMessageSender("channel");

        assertNotNull(messageSender);
        assertEquals("channel", messageSender.getDefaultChannel());
    }

    private Driver getSlackDriver() {
        return new SlackDriver("automaton", "my-secret-token");
    }
}
