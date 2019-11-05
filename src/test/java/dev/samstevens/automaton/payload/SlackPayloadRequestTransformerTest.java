package dev.samstevens.automaton.payload;

import com.google.gson.Gson;
import dev.samstevens.automaton.payload.driver.SlackPayloadRequestTransformer;
import dev.samstevens.automaton.payload.driver.SlackRequestSignatureValidator;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.time.Instant;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

public class SlackPayloadRequestTransformerTest {

    @Test
    public void testIfShouldTransformRequest() throws IOException {
        PayloadRequestTransformer payloadRequestTransformer = getSlackPayloadRequestTransformer();

        assertTrue(payloadRequestTransformer.shouldTransformRequest(getSlackRequest()));
        assertFalse(payloadRequestTransformer.shouldTransformRequest(getOtherRequest()));
    }

    @Test
    public void testParsesRequestCorrectly() throws PayloadRequestTransformingException, IOException {
        PayloadRequestTransformer payloadRequestTransformer = getSlackPayloadRequestTransformer();

        Payload payload = payloadRequestTransformer.transformRequest(getSlackRequest());

        assertEquals("message", payload.getType());
        assertEquals("C2147483705", payload.getChannel());
        assertEquals("U2147483697", payload.getSender());
        assertEquals("Hello world", payload.getMessage());
        assertEquals(Instant.ofEpochSecond(1355517523), payload.getTimestamp());
    }

    @Test(expected = PayloadRequestTransformingException.class)
    public void testMalformedRequestThrowsException() throws PayloadRequestTransformingException, IOException {
        PayloadRequestTransformer payloadRequestTransformer = getSlackPayloadRequestTransformer();

        payloadRequestTransformer.transformRequest(getBadRequest());
    }

    @Test
    public void testMentionDetection() throws IOException, PayloadRequestTransformingException {
        PayloadRequestTransformer payloadRequestTransformer = getSlackPayloadRequestTransformer();

        Payload payload = payloadRequestTransformer.transformRequest(getSlackRequest());
        assertFalse(payload.isMention());

        payload = payloadRequestTransformer.transformRequest(getSlackRequestWithBotMention());
        assertTrue(payload.isMention());
    }

    private HttpServletRequest getSlackRequest() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getInputStream()).thenReturn(readRequestStubIntoStream("/payload/slack_request_body.json"));
        when(request.getHeader("X-Slack-Request-Timestamp")).thenReturn(Instant.now().toString());

        return request;
    }

    private HttpServletRequest getSlackRequestWithBotMention() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getInputStream()).thenReturn(readRequestStubIntoStream("/payload/slack_bot_mention_request_body.json"));
        when(request.getHeader("X-Slack-Request-Timestamp")).thenReturn(Instant.now().toString());

        return request;
    }

    private PayloadRequestTransformer getSlackPayloadRequestTransformer() {
        SlackRequestSignatureValidator validator = mock(SlackRequestSignatureValidator.class);
        when(validator.isValid(any(),any(), any())).thenReturn(true);

        return new SlackPayloadRequestTransformer(new Gson(), validator);
    }

    private HttpServletRequest getBadRequest() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getInputStream()).thenReturn(readRequestStubIntoStream("/payload/malformed_request_body.json"));
        when(request.getHeader("X-Slack-Request-Timestamp")).thenReturn(Instant.now().toString());

        return request;
    }

    private HttpServletRequest getOtherRequest() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getInputStream()).thenReturn(readRequestStubIntoStream("/payload/other_request_body.json"));

        return request;
    }

    /**
     * Helper method to read the stub file request JSON.
     */
    private ServletInputStream readRequestStubIntoStream(String filename) throws IOException {
        InputStream stream = this.getClass().getResourceAsStream(filename);

        byte[] body;
        try {
            body = IOUtils.toByteArray(stream);
        } catch (IOException e) {
            throw new RuntimeException("Could not read request InputStream.", e);
        }

        return new ServletInputStream() {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(body);

            @Override
            public int read() throws IOException {
                return inputStream.read();
            }
        };
    }
}
